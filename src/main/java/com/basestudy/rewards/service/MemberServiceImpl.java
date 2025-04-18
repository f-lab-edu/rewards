package com.basestudy.rewards.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basestudy.rewards.ApiResponseWrapper;
import com.basestudy.rewards.controller.dto.SignUpDto;
import com.basestudy.rewards.domain.Member;
import com.basestudy.rewards.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService{
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Member member =  memberRepository.findByEmail(email)
                            .orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return member; //member가 UserDetails를 상속, security의 User를 사용하지 않아도 됨
    }

    @Transactional
    public ApiResponseWrapper<?> signUp(SignUpDto memberDto) throws RuntimeException{
        this.getMemberWithEmail(memberDto.getEmail()).ifPresent(item -> {
            //TODO: exception handler로 처리필요
            throw new RuntimeException("이미 가입되었습니다.");
        });
        
        Member member = memberDto.toEntity();
        
        memberRepository.save(member);
        return ApiResponseWrapper.createSuccess("저장되었습니다.");
    }
    
    @Transactional(readOnly = true)
    public Optional<Member> getMemberWithEmail(String email){
        return memberRepository.findByEmail(email);
    }
    //TODO: validation 추가
}
