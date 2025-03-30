package com.basestudy.rewards.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class MemberResDto {
    @NotEmpty
    private long id;
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;

    public MemberResDto(){}

    public MemberResDto(long id, String email, String name, String phone){
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
}
