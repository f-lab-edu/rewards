package com.basestudy.rewards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basestudy.rewards.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
    // 기본적으로 save, findone, findall, count, delete 기능 제공
    // 그외는 키워드를 이용하여 정의할 수 있다
    Optional<Member> findByEmail(String email);
    
}