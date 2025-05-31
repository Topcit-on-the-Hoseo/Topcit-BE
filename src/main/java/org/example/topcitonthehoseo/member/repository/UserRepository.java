package org.example.topcitonthehoseo.member.repository;

import java.util.Optional;

import org.example.topcitonthehoseo.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 사용자 찾기 (구글 로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 학번으로 사용자 찾기 (일반 로그인 시 사용)
    Optional<User> findByStudentId(String studentId);

    // 닉네임 중복 확인 등도 필요시 추가 가능
    boolean existsByNickname(String nickname);
}
