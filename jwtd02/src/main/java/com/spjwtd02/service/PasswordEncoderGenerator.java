package com.spjwtd02.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Admin: " + encoder.encode("admin123"));
        System.out.println("User: " + encoder.encode("user123"));
        System.out.println("Manager: " + encoder.encode("manager123"));
    }
}