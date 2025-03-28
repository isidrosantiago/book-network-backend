package com.isidrosantiago.booknetwork.auth;

import lombok.*;

@Data
@Builder
public class SignInResponse {
    private String name;
    private String email;
    private String token;
}
