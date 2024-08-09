package com.sparta.task_one_month_internship.domain.user.repository;

import com.sparta.task_one_month_internship.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
