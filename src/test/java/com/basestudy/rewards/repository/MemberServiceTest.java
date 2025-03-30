package com.basestudy.rewards.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.SignUpDto;
import com.basestudy.rewards.service.MemberServiceImpl;

@ExtendWith(MockitoExtension.class) //의존중인 객체를 가짜 객체를 생성해 처리
public class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberServiceImpl memberServiceImpl;

    public SignUpDto setUpMemberDto() {
        return SignUpDto.builder()
                    .email("steven@netmail.com")
                    .phone("01012341234")
                    .name("steven")
                    .password("steven11")
                    .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void signUp(){
        SignUpDto signUpMemberDto = setUpMemberDto();
		
		when(memberRepository.save(any())).thenReturn(signUpMemberDto.toEntity());
		
		//when
		ApiResponseWrapper<?> apiResponseWrapper  =  memberServiceImpl.signUp(signUpMemberDto);
		
		//then
		assertEquals(apiResponseWrapper.isSuccess(), true);
    }
}
