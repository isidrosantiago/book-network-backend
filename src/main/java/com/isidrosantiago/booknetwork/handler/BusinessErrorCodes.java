package com.isidrosantiago.booknetwork.handler;

import lombok.Getter;

@Getter
public enum BusinessErrorCodes {
    NO_CODE("NO_CODE", "No code"),
    INCORRECT_CURRENT_PASSWORD("INCORRECT_PASSWORD", "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH("PASSWORD_NOT_MATCH", "The new password does not match"),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "User account is locked"),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "User account is disabled"),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Sign in credentials are incorrect"),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "The email is already registered to another user.");;

    private final String internalErrorCode;
    private final String message;

    BusinessErrorCodes(String internalErrorCode, String message) {
        this.internalErrorCode = internalErrorCode;
        this.message = message;
    }
}
