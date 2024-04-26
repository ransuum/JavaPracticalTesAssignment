package com.example.javapracticaltesassignment.controller;

import com.example.javapracticaltesassignment.mapper.UserMapper;
import com.example.javapracticaltesassignment.model.dto.UserDto;
import com.example.javapracticaltesassignment.model.entity.Jwt;
import com.example.javapracticaltesassignment.model.entity.SignIn;
import com.example.javapracticaltesassignment.model.entity.SignUp;
import com.example.javapracticaltesassignment.model.entity.Users;
import com.example.javapracticaltesassignment.security.TokenProvider;
import com.example.javapracticaltesassignment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private AuthenticationManager authenticationManager;

    private UserService service;

    private TokenProvider tokenService;
    private UserMapper userMapper;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserService service,
                          TokenProvider tokenService, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.service = service;
        this.tokenService = tokenService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUp signUp) {
        return new ResponseEntity<>(userMapper.toDto((Users) service.signUp(signUp)), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<Jwt> signIn(@RequestBody @Valid SignIn data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var authUser = authenticationManager.authenticate(usernamePassword);
        var accessToken = tokenService.generateAccessToken((Users) authUser.getPrincipal());
        return ResponseEntity.ok(new Jwt(accessToken));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestParam UUID id, @RequestParam(required = false) String email,
                                              @RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName,
                                              @RequestParam(required = false) String birthDate, @RequestParam(required = false) String address,
                                              @RequestParam(required = false) String phone) {
        return new ResponseEntity<>(userMapper.toDto(service.updateUser(id, email, firstName, lastName, birthDate, address, phone)), HttpStatus.OK);
    }

    @GetMapping("/after/{date}")
    public ResponseEntity<List<UserDto>> getUsersByDateAfter(@PathVariable String date) {
        return new ResponseEntity<>(service.findUsersAfter(date).stream()
                .map(userMapper::toDto).collect(Collectors.toList()), HttpStatus.FOUND);
    }
    @GetMapping("/before/{date}")
    public ResponseEntity<List<UserDto>> getUsersByDateBefore(@PathVariable String date) {
        return new ResponseEntity<>(service.findUsersBefore(date).stream()
                .map(userMapper::toDto).collect(Collectors.toList()), HttpStatus.FOUND);
    }
}
