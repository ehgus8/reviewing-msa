package com.playdata.userservice.user.repository;

import com.playdata.userservice.common.auth.Role;
import com.playdata.userservice.user.dto.UserSaveReqDto;
import com.playdata.userservice.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 테스트")
    void insertTest() {

        UserSaveReqDto dto = UserSaveReqDto.builder()
                .email("abc@naver.com")
                .password("123456789")
                .nickName("김테스트")
                .role(Role.USER)
                .build();

        User user = dto.toEntity(passwordEncoder);

        userRepository.save(user);
    }


}