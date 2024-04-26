package com.example.javapracticaltesassignment.controller;

import static org.junit.jupiter.api.Assertions.*;
import com.example.javapracticaltesassignment.enums.UserRoles;
import com.example.javapracticaltesassignment.exception.InvalidJwtException;
import com.example.javapracticaltesassignment.mapper.UserMapper;
import com.example.javapracticaltesassignment.model.dto.UserDto;
import com.example.javapracticaltesassignment.model.entity.Jwt;
import com.example.javapracticaltesassignment.model.entity.SignIn;
import com.example.javapracticaltesassignment.model.entity.SignUp;
import com.example.javapracticaltesassignment.model.entity.Users;
import com.example.javapracticaltesassignment.security.TokenProvider;
import com.example.javapracticaltesassignment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private Users testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .id(UUID.randomUUID())
                .address("zp")
                .email("test@example.com")
                .password(new BCryptPasswordEncoder().encode("password"))
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(20))
                .phone("+1234567890")
                .role(UserRoles.USER)
                .build();

        testUserDto = UserDto.builder()
                .id(testUser.getId())
                .email(testUser.getEmail())
                .firstName(testUser.getFirstName())
                .lastName(testUser.getLastName())
                .birthDate(testUser.getBirthDate())
                .address(testUser.getAddress())
                .phone(testUser.getPhone())
                .role(testUser.getRole())
                .password(testUser.getPassword())
                .build();
    }

    @Test
    void signUp() throws InvalidJwtException {
        SignUp signUpData = new SignUp(testUser.getEmail(), "password", testUser.getFirstName(), testUser.getLastName(),
                testUser.getBirthDate().toString(), testUser.getAddress(), testUser.getPhone());

        when(userService.signUp(signUpData)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        ResponseEntity<UserDto> response = userController.signUp(signUpData);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUserDto, response.getBody());
    }

    @Test
    void signIn() {
        SignIn signInData = new SignIn(testUser.getEmail(), "password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null, null);
        String accessToken = "access_token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateAccessToken(testUser)).thenReturn(accessToken);

        ResponseEntity<Jwt> response = userController.signIn(signInData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new Jwt(accessToken), response.getBody());
    }

    @Test
    void updateUser() {
        UUID userId = testUser.getId();
        String newEmail = "newemail@example.com";
        String newFirstName = "NewFirstName";
        String newLastName = "NewLastName";
        String newBirthDate = LocalDate.now().minusYears(25).toString();
        String newAddress = "New Address";
        String newPhone = "+9876543210";

        Users updatedUser = Users.builder()
                .id(userId)
                .email(newEmail)
                .firstName(newFirstName)
                .lastName(newLastName)
                .birthDate(LocalDate.parse(newBirthDate))
                .address(newAddress)
                .phone(newPhone)
                .role(testUser.getRole())
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .birthDate(updatedUser.getBirthDate())
                .address(updatedUser.getAddress())
                .phone(updatedUser.getPhone())
                .role(updatedUser.getRole())
                .build();

        when(userService.updateUser(userId, newEmail, newFirstName, newLastName, newBirthDate, newAddress, newPhone))
                .thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedUserDto);

        ResponseEntity<UserDto> response = userController.updateUser(userId, newEmail, newFirstName, newLastName, newBirthDate, newAddress, newPhone);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUserDto, response.getBody());
    }

    @Test
    void getUsersByDateAfter() {
        LocalDate date = LocalDate.now().minusYears(25);
        String dateString = date.toString();
        List<Users> usersAfterDate = Collections.singletonList(testUser);
        List<UserDto> userDtosAfterDate = Collections.singletonList(testUserDto);

        when(userService.findUsersAfter(dateString)).thenReturn(usersAfterDate);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        ResponseEntity<List<UserDto>> response = userController.getUsersByDateAfter(dateString);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(userDtosAfterDate, response.getBody());
    }

    @Test
    void getUsersByDateBefore() {
        LocalDate date = LocalDate.now().minusYears(25);
        String dateString = date.toString();
        List<Users> usersBeforeDate = Collections.singletonList(testUser);
        List<UserDto> userDtosBeforeDate = Collections.singletonList(testUserDto);

        when(userService.findUsersBefore(dateString)).thenReturn(usersBeforeDate);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        ResponseEntity<List<UserDto>> response = userController.getUsersByDateBefore(dateString);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(userDtosBeforeDate, response.getBody());
    }
}