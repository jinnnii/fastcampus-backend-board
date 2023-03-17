package com.fastcampus.backendboard.repository;

import com.fastcampus.backendboard.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends
        JpaRepository<UserAccount,String> { }
