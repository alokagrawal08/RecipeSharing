package com.example.RecipeSharing.payloads.requests;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String email;
    private String username;
    private String password;
}
