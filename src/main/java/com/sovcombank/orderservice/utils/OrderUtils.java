package com.sovcombank.orderservice.utils;

import java.util.Set;

public class OrderUtils {
    public static boolean access(String userId, Set<String> hrbpIds){
        return hrbpIds.stream().anyMatch(hrbp -> hrbp.equals(userId));
    }
}
