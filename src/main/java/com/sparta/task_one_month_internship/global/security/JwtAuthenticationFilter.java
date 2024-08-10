package com.sparta.task_one_month_internship.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.task_one_month_internship.domain.user.dto.UserSignRequest;
import com.sparta.task_one_month_internship.domain.user.dto.UserSignResponse;
import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRole;
import com.sparta.task_one_month_internship.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/v1/users/sign");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        try {
            UserSignRequest signRequest = objectMapper.readValue(request.getInputStream(),
                UserSignRequest.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    signRequest.getUsername(),
                    signRequest.getPassword(),
                    null
                )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        UserRole role;

        if (userDetails.getUser() != null) {
            role = userDetails.getUser().getAuthorityName();
        } else {
            throw new IllegalStateException("User or Admin not found");
        }

        // Token 생성
        String token = jwtUtil.createToken(userDetails.getUser());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        // 응답에 map 형식으로
        setTokenResponse(response, token, userDetails.getUser());
    }

    private void setTokenResponse(HttpServletResponse response, String token, User user)
        throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        UserSignResponse signInResponse = new UserSignResponse(token);

        response.getWriter().println(objectMapper.writeValueAsString(signInResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
}
