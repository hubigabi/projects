package com.project.model;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserRole {
    STUDENT("Student", "USER"),
    LECTURER("Prowadzący zajęcia", "USER", "ADMIN");

    @Getter
    String friendlyName;
    @Getter
    List<SimpleGrantedAuthority> authorities;

    UserRole(String friendlyName, String... authorities) {
        this.friendlyName = friendlyName;
        this.authorities = Arrays.stream(authorities)
                .map(s -> new SimpleGrantedAuthority("ROLE_" + s))
                .collect(Collectors.toList());
    }
}
