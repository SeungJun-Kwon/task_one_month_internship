package com.sparta.task_one_month_internship.domain.user.dto;

import lombok.Getter;

@Getter
public class UserSignUpRequest {

    private String username;
    private String password;
    private String nickname;
}
