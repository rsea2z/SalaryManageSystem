package com.example.salarymanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "User ID cannot be blank")
    private String userid; // Corresponds to Teacher_number

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
