package com.sparta.task_one_month_internship.global.jwt;

import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRefreshToken;
import com.sparta.task_one_month_internship.domain.user.entity.UserRole;
import com.sparta.task_one_month_internship.domain.user.repository.UserRefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L; // 30분
    public final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final UserRefreshTokenRepository userRefreshTokenRepository;

    @Autowired
    public JwtUtil(UserRefreshTokenRepository userRefreshTokenRepository) {
        this.userRefreshTokenRepository = userRefreshTokenRepository;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_TIME);
    }

    public String createAccessTokenFromClaims(Claims userInfo) {
        User user = User
            .builder()
            .username(userInfo.getSubject())
            .userId((Long) userInfo.get("userId"))
            .nickname((String) userInfo.get("nickname"))
            .authorityName((UserRole) userInfo.get("authority"))
            .build();

        return createAccessToken(user);
    }

    @Transactional
    public String createRefreshToken(User user) {
        String token = createToken(user, REFRESH_TOKEN_TIME);

        UserRefreshToken userRefreshToken;

        userRefreshToken = userRefreshTokenRepository.findUserRefreshTokenByUser(user);

        if (userRefreshToken != null) {
            userRefreshToken.setToken(token);
        } else {
            userRefreshToken = UserRefreshToken
                .builder()
                .token(token)
                .user(user)
                .build();

            userRefreshTokenRepository.save(userRefreshToken);
        }

        return token;
    }

    public String createToken(User user, long expireTime) {
        Date date = new Date();

        return Jwts.builder()
            .setSubject(user.getUsername()) // 사용자 식별자값(Username)
            .claim("userId", user.getUserId())
            .claim("nickname", user.getNickname())
            .claim(("authority"), user.getAuthorityName())
            .setExpiration(new Date(date.getTime() + expireTime)) // expireTime 에 따른 만료 시간
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm) // 암호화 알고리즘
            .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(token)) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getRefreshTokenByUserId(Long userId) {
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findUserRefreshTokenByUserId(
            userId);

        if (userRefreshToken == null) {
            return null;
        }

        return userRefreshToken.getToken();
    }

    // 토큰 만료 검증(AccessToken)
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
