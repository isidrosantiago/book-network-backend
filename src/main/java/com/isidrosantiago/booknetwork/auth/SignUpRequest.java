package com.isidrosantiago.booknetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest {
    @NotEmpty(message = "First name is required")
    @NotBlank(message = "First name is required")
    private String firstname;
    @NotEmpty(message = "Last name is required")
    @NotBlank(message = "Last name is required")
    private String lastname;
    @Email(message = "Email requires a valid format")
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;
    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password should be at least 5 character long")
    private String password;
}
