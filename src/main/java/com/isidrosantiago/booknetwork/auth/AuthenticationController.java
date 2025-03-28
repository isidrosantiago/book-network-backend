package com.isidrosantiago.booknetwork.auth;

import com.isidrosantiago.booknetwork.response.CustomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) throws MessagingException {
        service.signUp(request);
        CustomResponse<?> response = CustomResponse.builder()
            .status("success")
            .message("User has been created")
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody LogInRequest request) {
        SignInResponse signInResp = service.signIn(request);
        CustomResponse<SignInResponse> response = CustomResponse.<SignInResponse>builder()
            .status("success")
            .data(signInResp)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate-account")
    public ResponseEntity<?> confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        CustomResponse<?> response = CustomResponse.builder()
            .status("success")
            .message("Account has been activated")
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        var exists = service.verifyEmail(email);
        var response = CustomResponse.builder()
            .status(exists ? "fail" : "success")
            .data(exists)
            .build();
        return ResponseEntity.ok(response);
    }
}
