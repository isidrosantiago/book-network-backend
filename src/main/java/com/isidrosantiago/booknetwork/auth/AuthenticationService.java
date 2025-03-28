package com.isidrosantiago.booknetwork.auth;

import com.isidrosantiago.booknetwork.exception.AccountActivationException;
import com.isidrosantiago.booknetwork.exception.EmailAlreadyExistsException;
import com.isidrosantiago.booknetwork.role.RoleRepository;
import com.isidrosantiago.booknetwork.security.JwtService;
import com.isidrosantiago.booknetwork.user.Token;
import com.isidrosantiago.booknetwork.user.TokenRepository;
import com.isidrosantiago.booknetwork.user.User;
import com.isidrosantiago.booknetwork.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public void signUp(SignUpRequest request) throws MessagingException {
        var role = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role USER was not initialized"));

        var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).accountLocked(false).enabled(false).roles(List.of(role)).build();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("The email is already registered to another user.");
        }

        userRepository.save(user);
        tokenService.sendValidationEmail(user);
    }

    public SignInResponse signIn(LogInRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Sign in credentials are incorrect.");
        }

        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var claims = new HashMap<String, Object>();
        var authUser = (User) auth.getPrincipal();
        claims.put("fullname", authUser.fullName());

        String jwt = jwtService.generateToken(authUser, claims);

        return SignInResponse.builder()
            .name(authUser.fullName())
            .email(authUser.getEmail())
            .token(jwt)
            .build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new AccountActivationException("INVALID_TOKEN"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            tokenService.sendValidationEmail(savedToken.getUser());
            throw new AccountActivationException("EXPIRED_TOKEN");
        }

        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public Boolean verifyEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
