package com.basestudy.rewards.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.basestudy.rewards.domain.Member;

// @SpringBootTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //test 기본은 내장db로 연결하게 되어있어서 내장디비 쓰지말라고 설정
public class MemberRepositoryTest {
    
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("member DB저장 확인")
    void jpaSave() {
        //id직접설정시 jpa save 실행결과는 merge가 실행되고 새로운 객체를 반환한다.
        //generate id를 사용하면 들어간 entity가 그대로 반환됨
        Member member = Member.builder()
                                .email("steven11@netmail.com")
                                .phone("01012341234")
                                .name("steven11")
                                .password("steven11")
                                .build();
        Member saveMember = memberRepository.save(member);
        Assertions.assertThat(saveMember).isSameAs(member);
        Assertions.assertThat(memberRepository.findByEmail("steven11@netmail.com").get().getEmail()).isEqualTo(member.getEmail());
    }


}
