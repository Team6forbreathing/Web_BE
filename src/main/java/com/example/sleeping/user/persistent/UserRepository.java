package com.example.sleeping.user.persistent;

import com.example.sleeping.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUserId(String userId);

    Optional<User> findByUserId(String userId);

    void deleteByUserId(String userId);

    Page<User> findAll(Pageable pageable);

    Optional<User> findTopByOrderByIdDesc();
}
