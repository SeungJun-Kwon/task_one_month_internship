package com.sparta.task_one_month_internship.global.security;

import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRole;
import com.sparta.task_one_month_internship.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new UserDetailsImpl(user);
        }

        throw new UsernameNotFoundException("Not Found " + username);
    }

    // Token 에 담겨 있는 정보(Claims)를 바탕으로 User 객체 생성
    public UserDetails loadUserByClaims(Claims info) {
        User user = new User(info.getSubject(),
            UserRole.valueOf(info.get("authority").toString()));
        user.setUserId(info.get("userId", Long.class));

        return new UserDetailsImpl(user);
    }
}
