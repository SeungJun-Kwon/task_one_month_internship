package com.sparta.task_one_month_internship.global.security;

import com.sparta.task_one_month_internship.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(token)) {
            if (!jwtUtil.validateToken(token)) {
                log.error("Token Error");
                return;
            } else if (jwtUtil.isTokenExpired(token)) {  // Access 토큰 만료 검증
                // Token 에 있는 유저 정보로 RefreshToken 조회
                Claims userInfo = jwtUtil.getUserInfoFromToken(token);
                String refreshToken = jwtUtil.getRefreshTokenByUserId(
                    (Long) userInfo.get("userId"));

                if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)
                    && jwtUtil.isTokenExpired(refreshToken)) { // RefreshToken 검증
                    // Claims 에 있는 유저 정보로 Access 토큰 재생성
                    token = jwtUtil.createAccessTokenFromClaims(userInfo);
                    // 새로운 AccessToken 을 Header 에 갱신
                    response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
                } else {
                    log.error("Token Invalid");
                    return;
                }
            }

            Claims info = jwtUtil.getUserInfoFromToken(token);

            try {
                setAuthentication(info);
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(Claims info) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(info);
        context.setAuthentication(authentication);
        log.info("인증처리");
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(Claims info) {
        UserDetails userDetails = userDetailsService.loadUserByClaims(info);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }
}
