package com.memopet.memopet.domain.oauth2.service;


import com.memopet.memopet.domain.member.entity.Member;
import com.memopet.memopet.domain.member.entity.MemberSocial;
import com.memopet.memopet.domain.member.entity.MemberStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private final MemberSocial member;

    public OAuth2UserPrincipal(MemberSocial userInfo) {
        this.member = userInfo;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return member.getMemberStatus().equals(MemberStatus.NORMAL) ? false : true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays
                .stream(member
                        .getRoles()
                        .split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getName() {
        return member.getEmail();
    }
}
