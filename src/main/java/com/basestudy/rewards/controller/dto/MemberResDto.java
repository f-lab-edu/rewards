package com.basestudy.rewards.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "사용자")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResDto{
    @Schema(description = "사용자고유번호")
    @NotEmpty
    private long id;
    @Schema(description = "이메일", nullable = false, example = "steven@netmail.com")
    @NotBlank
    private String email;
    @Schema(description = "이름", nullable = false)
    @NotBlank
    private String name;
    @Schema(description = "전화번호", nullable = false)
    @NotBlank
    private String phone;
}
