package com.basestudy.rewards.controller.dto;

import com.basestudy.rewards.entity.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class SignUpDto { 
    private String email;
    private String phone;
    private String name;
    private String password;

    public SignUpDto(){}

    public SignUpDto(String email, String phone, String name, String password){
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.password = password;
    }

    public Member toEntity(){
        return Member.builder()
                    .email(this.email)
                    .phone(this.phone)
                    .name(this.name)
                    .password(this.password)
                    .build();
    }
}
