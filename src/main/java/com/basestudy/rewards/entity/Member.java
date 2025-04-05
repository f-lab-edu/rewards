package com.basestudy.rewards.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name="member")
@EqualsAndHashCode(callSuper = false , of = "id")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor //생성자가 있어서 기본생성자 추가해줘야함, lombok @NoArgsContructor(access = AccessLevel.PROTECTED) 사용
public class Member implements UserDetails{
    
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //지연로딩 불가, DB의 auto increment에 키생성 위임
    private long id;

    @Column(nullable = false, length = 200)
    @Email
    // @Length(min = 1, max = 200)
    // @NotBlank (message = "이메일은 필수값입니다.") -> service validsation check 함수로 따로 빼기
    private String email;

    @Column(nullable = false, length = 11)
    // @Length(min = 1, max = 11)
    // @NotBlank (message = "핸드폰번호는 필수값입니다.")
    private String phone;

    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(nullable = false, length = 200)
    // @NotBlank (message = "비밀번호는 필수값입니다.")
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("role")); //TODO: role구분

        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
