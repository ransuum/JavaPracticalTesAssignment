package com.example.javapracticaltesassignment.mapper;

import com.example.javapracticaltesassignment.model.dto.UserDto;
import com.example.javapracticaltesassignment.model.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(Users user) {
        return UserDto.builder()
                .id(user.getId())
                .address(user.getAddress())
                .phone(user.getPhone())
                .role(user.getRole())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .build();
    }
}
