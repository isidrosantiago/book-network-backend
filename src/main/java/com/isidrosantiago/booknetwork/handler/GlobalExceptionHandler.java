package com.isidrosantiago.booknetwork.handler;

import com.isidrosantiago.booknetwork.exception.AccountActivationException;
import com.isidrosantiago.booknetwork.exception.EmailAlreadyExistsException;
import com.isidrosantiago.booknetwork.exception.OperationNotPermittedException;
import com.isidrosantiago.booknetwork.response.CustomResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.isidrosantiago.booknetwork.handler.BusinessErrorCodes.*;
import static com.isidrosantiago.booknetwork.handler.BusinessErrorCodes.BAD_CREDENTIALS;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<CustomResponse<?>> handleException(LockedException ex) {
        return ResponseEntity
            .status(UNAUTHORIZED)
            .body(
                CustomResponse.builder()
                    .internalErrorCode(ACCOUNT_LOCKED.getInternalErrorCode())
                    .status("error")
                    .message(ACCOUNT_LOCKED.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<CustomResponse<?>> handleException(DisabledException ex) {
        return ResponseEntity
            .status(UNAUTHORIZED)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .internalErrorCode(ACCOUNT_DISABLED.getInternalErrorCode())
                    .message(ACCOUNT_DISABLED.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomResponse<?>> handleException(BadCredentialsException ex) {
        return ResponseEntity
            .status(UNAUTHORIZED)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .internalErrorCode(BAD_CREDENTIALS.getInternalErrorCode())
                    .message(BAD_CREDENTIALS.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<CustomResponse<?>> handleException(EmailAlreadyExistsException ex) {
        return ResponseEntity
            .status(UNAUTHORIZED)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .internalErrorCode(EMAIL_ALREADY_EXISTS.getInternalErrorCode())
                    .message(EMAIL_ALREADY_EXISTS.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<CustomResponse<?>> handleException(MessagingException ex) {
        return ResponseEntity
            .status(INTERNAL_SERVER_ERROR)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .message(ex.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<CustomResponse<?>> handleException(OperationNotPermittedException ex) {
        return ResponseEntity
            .status(BAD_REQUEST)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .message(ex.getMessage())
                    .build()
            );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
            .status(BAD_REQUEST)
            .body(
                CustomResponse.<Map<String, String>>builder()
                    .status("fail")
                    .data(errors)
                    .build()
            );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<?>> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
            .status(INTERNAL_SERVER_ERROR)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .message("Internal error, contact the admin")
                    .internalErrorCode("INTERNAL_SERVER_ERROR")
                    .build()
            );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomResponse<?>> handleException(UsernameNotFoundException ex) {
        return ResponseEntity
            .status(NOT_FOUND)
            .body(
                CustomResponse.builder()
                    .status("fail")
                    .message(ex.getMessage())
                    .internalErrorCode("USER_NOT_FOUND")
                    .build()
            );
    }

    @ExceptionHandler(AccountActivationException.class)
    public ResponseEntity<CustomResponse<?>> handleException(AccountActivationException ex) {
        var message = (ex.getMessage().equals("INVALID_TOKEN"))
            ? "Account activation token is invalid"
            : "Activation has expired. New token has been sent.";

        return ResponseEntity
            .status(BAD_REQUEST)
            .body(
                CustomResponse.builder()
                    .status("error")
                    .internalErrorCode(ex.getMessage())
                    .message(message)
                    .build()
            );
    }
}
