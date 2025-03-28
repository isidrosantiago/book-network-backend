package com.isidrosantiago.booknetwork.auth;

import com.isidrosantiago.booknetwork.email.EmailService;
import com.isidrosantiago.booknetwork.email.EmailTemplateName;
import com.isidrosantiago.booknetwork.user.Token;
import com.isidrosantiago.booknetwork.user.TokenRepository;
import com.isidrosantiago.booknetwork.user.User;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void sendValidationEmail(User user) throws MessagingException {
        var token = generateAndSaveActivationToken(user);

        emailService.sendEmail(
            user.getEmail(),
            user.fullName(),
            EmailTemplateName.ACTIVATE_ACCOUNT,
            activationUrl,
            token,
            "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);

        var token = Token.builder()
            .token(generatedToken)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(1))
            .user(user)
            .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
