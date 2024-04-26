package com.example.javapracticaltesassignment.repo;

import com.example.javapracticaltesassignment.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
@EnableJpaRepositories
public interface UserRepo extends JpaRepository<Users, UUID> {
    UserDetails findByEmail(String email);
    List<Users> findAllByBirthDateAfter(LocalDate localDateTime);
    List<Users> findAllByBirthDateBefore(LocalDate localDateTime);
}
