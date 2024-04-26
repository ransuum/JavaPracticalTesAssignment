package com.example.javapracticaltesassignment.model.entity;

import java.time.LocalDateTime;

public record SignUp(String email, String password,
                     String firstName, String lastName,
                     String birthDate,
                     String address, String phone) {
}
