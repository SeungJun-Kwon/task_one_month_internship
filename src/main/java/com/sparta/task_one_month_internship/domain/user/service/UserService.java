package com.sparta.task_one_month_internship.domain.user.service;

import com.sparta.task_one_month_internship.domain.user.dto.UserSignUpRequest;
import com.sparta.task_one_month_internship.domain.user.dto.UserSignUpResponse;
import com.sparta.task_one_month_internship.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserSignUpResponse signUp(UserSignUpRequest request) {
        UserSignUpResponse response = null;

        return response;
    }
}
