package com.sovcombank.orderservice.security;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public enum Roles {

    CANDIDATE("CANDIDATE"),
    HR("HR"),
    HRBP("HRBP"),
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER");



    private final String val;

    Roles(String val){
        this.val = val;
    }
    public static boolean isALlowed(Collection<? extends GrantedAuthority> authorities, Roles role){
        return authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(role.val));
    }
}
