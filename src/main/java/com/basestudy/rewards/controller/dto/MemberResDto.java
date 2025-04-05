package com.basestudy.rewards.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResDto {
    @NotEmpty
    private long id;
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
}
