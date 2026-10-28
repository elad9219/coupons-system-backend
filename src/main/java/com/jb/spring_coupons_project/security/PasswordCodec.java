package com.jb.spring_coupons_project.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class PasswordCodec {

    private static final BCryptPasswordEncoder ENCODER =
            new BCryptPasswordEncoder(10);

    private static final Pattern BCRYPT_PATTERN = Pattern.compile(
            "^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$"
    );

    private PasswordCodec() {
    }

    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Password must not be blank"
            );
        }

        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "Password must not exceed 72 UTF-8 bytes"
            );
        }

        return ENCODER.encode(rawPassword);
    }

    public static boolean matches(
            String rawPassword,
            String encodedPassword) {

        return rawPassword != null &&
                encodedPassword != null &&
                isEncoded(encodedPassword) &&
                ENCODER.matches(rawPassword, encodedPassword);
    }

    public static boolean isEncoded(String password) {
        return password != null &&
                BCRYPT_PATTERN.matcher(password).matches();
    }
}
