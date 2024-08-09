package com.sparta.task_one_month_internship.domain.user.controller;

import com.sparta.task_one_month_internship.domain.user.dto.UserSignUpRequest;
import com.sparta.task_one_month_internship.domain.user.dto.UserSignUpResponse;
import com.sparta.task_one_month_internship.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signup")
    private UserSignUpResponse signUp(@RequestBody UserSignUpRequest request) {
        return userService.signUp(request);
    }
}
