package com.sovcombank.orderservice.utils;

import com.sovcombank.orderservice.security.Roles;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.List;

public class UserUtils {
    public static String getUserId(Principal principal){
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return (String) token.getTokenAttributes().get("sub");
    }
    public static boolean isAllowed(Principal principal, List<Roles> accessRoles){
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return accessRoles.stream().anyMatch(role -> Roles.isALlowed(token.getAuthorities(), role));
    }
}
