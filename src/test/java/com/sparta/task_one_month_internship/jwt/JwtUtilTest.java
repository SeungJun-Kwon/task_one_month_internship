package com.sparta.task_one_month_internship.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRole;
import com.sparta.task_one_month_internship.domain.user.repository.UserRefreshTokenRepository;
import com.sparta.task_one_month_internship.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private UserRefreshTokenRepository userRefreshTokenRepository;

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        MockitoAnnotations.openMocks(this);
        // JwtUtil 생성
        jwtUtil = new JwtUtil(userRefreshTokenRepository);
        // 테스트용 Secret Key 설정
        ReflectionTestUtils.setField(jwtUtil, "secretKey",
            "testSecretKeytestSecretKeytestSecretKeytestSecretKey");
        jwtUtil.init();
    }

    @Test
    void createAccessToken() {
        User user = new User("testUser", UserRole.USER);
        user.setUserId(1L);
        user.setNickname("TestNickname");

        String token = jwtUtil.createAccessToken(user);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));
        Claims userInfoFromToken = jwtUtil.getUserInfoFromToken(token);
        assertEquals(user.getUsername(), userInfoFromToken.getSubject());
        assertEquals(user.getUserId(),
            Long.valueOf(String.valueOf(userInfoFromToken.get("userId"))));
        assertEquals(user.getNickname(), userInfoFromToken.get("nickname"));
        assertEquals(String.valueOf(user.getAuthorityName()),
            String.valueOf(userInfoFromToken.get("authority")));
        System.out.println(token);
    }

    @Test
    void createAccessTokenFromClaims() {
        Claims userInfo = mock(Claims.class);
        given(userInfo.getSubject()).willReturn("testUser");
        given(userInfo.get("userId")).willReturn(1L);
        given(userInfo.get("nickname")).willReturn("TestNickname");
        given(userInfo.get("authority")).willReturn(UserRole.USER);

        String token = jwtUtil.createAccessTokenFromClaims(userInfo);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));
        Claims userInfoFromToken = jwtUtil.getUserInfoFromToken(token);
        assertEquals(userInfo.getSubject(), userInfoFromToken.getSubject());
        assertEquals(Long.valueOf(String.valueOf(userInfo.get("userId"))),
            Long.valueOf(String.valueOf(userInfoFromToken.get("userId"))));
        assertEquals(userInfo.get("nickname"), userInfoFromToken.get("nickname"));
        assertEquals(String.valueOf(userInfo.get("authority")),
            String.valueOf(userInfoFromToken.get("authority")));
        System.out.println(token);
    }

    @Test
    void validateToken() {
        User user = new User("testUser", UserRole.USER);
        user.setUserId(1L);
        user.setNickname("TestNickname");

        String token = jwtUtil.createAccessToken(user);
        assertTrue(jwtUtil.validateToken(token));

        String invalidToken = token + "invalid";
        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    void getUserInfoFromToken() {
        User user = new User("testUser", UserRole.USER);
        user.setUserId(1L);
        user.setNickname("TestNickname");

        String token = jwtUtil.createAccessToken(user);
        Claims claims = jwtUtil.getUserInfoFromToken(token);

        assertEquals("testUser", claims.getSubject());
        assertEquals(1L, Long.valueOf(String.valueOf(claims.get("userId"))));
        assertEquals("TestNickname", claims.get("nickname"));
        assertEquals("USER", claims.get("authority"));
    }

    @Test
    void isTokenExpiredFalse() {
        User user = new User("testUser", UserRole.USER);
        user.setUserId(1L);
        user.setNickname("TestNickname");

        String token = jwtUtil.createAccessToken(user);
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void isTokenExpiredWithShortExpirationTime() throws Exception {
        User user = new User("testUser", UserRole.USER);
        user.setUserId(1L);
        user.setNickname("TestNickname");

        String token = jwtUtil.createToken(user, 1L);

        // 잠시 대기
        Thread.sleep(10);

        assertTrue(jwtUtil.isTokenExpired(token));
    }
}