package com.example.javapracticaltesassignment.service;

import com.example.javapracticaltesassignment.enums.UserRoles;
import com.example.javapracticaltesassignment.exception.InvalidJwtException;
import com.example.javapracticaltesassignment.model.entity.SignUp;
import com.example.javapracticaltesassignment.model.entity.Users;
import com.example.javapracticaltesassignment.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private Users testUser;
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "age", 18);
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
    }
    @Test
    void signUp() throws InvalidJwtException {
        when(userRepo.findByEmail(any())).thenReturn(null);
        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        SignUp signUp = new SignUp("test@example.com", "password", "John", "Doe", testUser.getBirthDate().toString(), "123 Main St", "+1234567890");
        UserDetails savedUser = userService.signUp(signUp);

        assertNotNull(savedUser);
        assertEquals(testUser.getEmail(), savedUser.getUsername());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
        assertEquals(testUser.getRole(), UserRoles.USER);
    }
    @Test
    void updateUser() {
        when(userRepo.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(userRepo.save(any(Users.class))).thenReturn(testUser);

        Users updatedUser = userService.updateUser(testUser.getId(), "newemail@example.com", "NewFirstName", "NewLastName", "2003-11-03", "New Address", "+9876543210", new BCryptPasswordEncoder().encode("1234"));

        assertNotNull(updatedUser);
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertEquals("New Address", updatedUser.getAddress());
        assertEquals("+9876543210", updatedUser.getPhone());
    }
    @Test
    void findUsersAfter() {
        LocalDate testDate = LocalDate.now().minusYears(25);
        LocalDate userBirthDate1 = testDate.minusYears(1);
        LocalDate userBirthDate2 = testDate.plusYears(1);

        Users user1 = new Users();
        user1.setBirthDate(userBirthDate1);

        Users user2 = new Users();
        user2.setBirthDate(userBirthDate2);

        when(userRepo.findAllByBirthDateAfter(testDate)).thenReturn(List.of(user2));

        List<Users> usersAfter = userService.findUsersAfter(testDate.toString());

        assertEquals(1, usersAfter.size());
        assertEquals(user2, usersAfter.get(0));
    }
    @Test
    void findUsersBefore() {
        LocalDate testDate = LocalDate.now().minusYears(25);
        LocalDate userBirthDate1 = testDate.minusYears(1);
        LocalDate userBirthDate2 = testDate.plusYears(1);

        Users user1 = new Users();
        user1.setBirthDate(userBirthDate1);

        Users user2 = new Users();
        user2.setBirthDate(userBirthDate2);

        when(userRepo.findAllByBirthDateBefore(testDate)).thenReturn(List.of(user1));

        List<Users> usersBefore = userService.findUsersBefore(testDate.toString());

        assertEquals(1, usersBefore.size());
        assertEquals(user1, usersBefore.get(0));
    }
}