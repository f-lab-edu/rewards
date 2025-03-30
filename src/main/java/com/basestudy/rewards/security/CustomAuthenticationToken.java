package com.basestudy.rewards.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

public class CustomAuthenticationToken extends AbstractAuthenticationToken{
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final Object principal;
    private Object credentials;

    //인증전
    public CustomAuthenticationToken(Object principal, Object credentials){
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }
    //인증후
    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials(){
        return this.credentials;
    }
    @Override
    public Object getPrincipal(){
        return this.principal;
    }
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException{
        Assert.isTrue(!isAuthenticated, "토큰 신뢰불가");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials(){
        super.eraseCredentials();
        this.credentials = null;
    }
}
