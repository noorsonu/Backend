package com.main.security;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlackList {
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();

    public void blacklist(String token) { tokens.add(token); }
    public boolean isBlacklisted(String token) { return tokens.contains(token); }
}
