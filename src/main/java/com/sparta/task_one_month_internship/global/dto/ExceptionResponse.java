package com.sparta.task_one_month_internship.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse {

    String msg;
    int httpCode;
}
