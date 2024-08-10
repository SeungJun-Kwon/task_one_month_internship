package com.sparta.task_one_month_internship.domain.user.service;

import com.sparta.task_one_month_internship.domain.user.dto.UserSignUpRequest;
import com.sparta.task_one_month_internship.domain.user.dto.UserSignUpResponse;
import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRole;
import com.sparta.task_one_month_internship.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSignUpResponse signUp(UserSignUpRequest request) {
        validateSignUp(request.getUsername());

        User user = User.builder()
            .username(request.getUsername())
            .nickname(request.getNickname())
            .password(passwordEncoder.encode(request.getPassword()))
            .authorityName(UserRole.USER)
            .build();

        User savedUser = userRepository.save(user);

        return new UserSignUpResponse(savedUser);
    }

    private void validateSignUp(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is empty");
        }

        User user = userRepository.findByUsername(username);

        if (user != null) {
            throw new DuplicateKeyException("Username is already");
        }
    }
}
