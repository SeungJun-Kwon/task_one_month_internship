package com.sparta.task_one_month_internship.domain.user.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpResponse {

    private String username;
    private String nickname;
    private List<AuthorityResponse> authorities;

}
