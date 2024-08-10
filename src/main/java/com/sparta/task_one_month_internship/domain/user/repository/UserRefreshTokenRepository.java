package com.sparta.task_one_month_internship.domain.user.repository;

import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    public UserRefreshToken findUserRefreshTokenByUser(User user);

    @Query("select urt from UserRefreshToken urt where urt.user.userId = ?1")
    public UserRefreshToken findUserRefreshTokenByUserId(Long userId);
}
