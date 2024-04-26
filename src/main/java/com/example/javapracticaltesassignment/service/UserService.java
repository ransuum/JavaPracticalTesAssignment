package com.example.javapracticaltesassignment.service;

import com.example.javapracticaltesassignment.enums.UserRoles;
import com.example.javapracticaltesassignment.exception.DateBiggerThanToday;
import com.example.javapracticaltesassignment.exception.InvalidJwtException;
import com.example.javapracticaltesassignment.exception.LessThanMinAgeException;
import com.example.javapracticaltesassignment.exception.NotFoundException;
import com.example.javapracticaltesassignment.model.entity.SignUp;
import com.example.javapracticaltesassignment.model.entity.Users;
import com.example.javapracticaltesassignment.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    @Value("${min.age.user}")
    private int age;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email);
    }

    public UserDetails signUp(SignUp signUp) throws InvalidJwtException {
        if (userRepo.findByEmail(signUp.email()) != null) {
            throw new InvalidJwtException("Email already exists");
        }
        LocalDate localDate = LocalDate.parse(signUp.birthDate(), formatter);
        String encryptedPassword = new BCryptPasswordEncoder().encode(signUp.password());
        Users user = Users.builder()
                .email(signUp.email())
                .password(encryptedPassword)
                .address(signUp.address())
                .phone(signUp.phone())
                .lastName(signUp.lastName())
                .firstName(signUp.firstName())
                .birthDate(localDate)
                .build();
        if ((LocalDateTime.now().getYear() - user.getBirthDate().getYear()) < age)
            throw new LessThanMinAgeException((LocalDateTime.now().getYear() - user.getBirthDate().getYear()) + " less than 18 years");
        if (signUp.email().trim().equals("admin@admin.com")) user.setRole(UserRoles.ADMIN);
        else user.setRole(UserRoles.USER);
        return userRepo.save(user);
    }

    public Users updateUser(UUID id, String email, String firstName, String lastName, String birthDate,
                           String address, String phone) {
        Users user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("user with id:" + id + " not found"));
        if (email != null && !email.equals(user.getEmail())) user.setEmail(email);
        if (firstName != null && !firstName.equals(user.getFirstName())) user.setFirstName(firstName);
        if (lastName != null && !lastName.equals(user.getLastName())) user.setLastName(lastName);
        if (birthDate != null && !LocalDate.parse(birthDate, formatter).equals(user.getBirthDate())) {
            LocalDate localDate = LocalDate.parse(birthDate, formatter);
            if ((LocalDate.now().getYear() - localDate.getYear()) < age)
                throw new LessThanMinAgeException((LocalDateTime.now().getYear() - user.getBirthDate().getYear()) + "less than 18 years");
            user.setBirthDate(localDate);
        }
        if (address != null && !address.equals(user.getAddress())) user.setAddress(address);
        if (phone != null && !phone.equals(user.getPhone())) user.setPhone(phone);
        return userRepo.save(user);
    }
    public void deleteUser(UUID id){
        userRepo.findById(id).orElseThrow(()-> new NotFoundException("user with this id not found"));
        userRepo.deleteById(id);
    }
    public List<Users> findUsersAfter(String localDateTime){
        if (LocalDate.parse(localDateTime, formatter).isAfter(LocalDate.now())){
            throw new DateBiggerThanToday(LocalDate.parse(localDateTime, formatter) + " not found, because now is " + LocalDate.now());
        }
        return userRepo.findAllByBirthDateAfter(LocalDate.parse(localDateTime, formatter));
    }
    public List<Users> findUsersBefore(String localDateTime){
        if (LocalDate.parse(localDateTime, formatter).isAfter(LocalDate.now())){
            throw new DateBiggerThanToday(LocalDate.parse(localDateTime, formatter) + " not found, because now is " + LocalDate.now());
        }
        return userRepo.findAllByBirthDateBefore(LocalDate.parse(localDateTime, formatter));
    }
}
