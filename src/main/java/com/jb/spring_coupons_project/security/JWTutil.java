package com.jb.spring_coupons_project.security;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.beans.UserDetails;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.exception.TokenException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.LoginException;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTutil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final long TOKEN_DURATION_MINUTES = 30;

    private final Key signingKey;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    public JWTutil(
            @Value("${COUPONS_JWT_SECRET_BASE64:}") String secretBase64,
            CompanyRepository companyRepository,
            CustomerRepository customerRepository) {

        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;

        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException(
                    "COUPONS_JWT_SECRET_BASE64 must be configured"
            );
        }

        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(secretBase64.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "JWT secret must be valid Base64", ex
            );
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT signing key must contain at least 32 bytes"
            );
        }

        this.signingKey = new SecretKeySpec(
                keyBytes,
                SignatureAlgorithm.HS256.getJcaName()
        );
    }

    public String generateToken(UserDetails userDetails) {
        if (userDetails == null ||
                userDetails.getEmail() == null ||
                userDetails.getUserType() == null) {
            throw new IllegalArgumentException("Invalid user details");
        }

        int userId = resolveUserId(userDetails);

        return createToken(
                userDetails.getEmail(),
                userDetails.getUserType(),
                userId
        );
    }

    public String generateToken(String existingToken) {
        Claims claims = extractAllClaims(existingToken);

        UserType userType = UserType.valueOf(
                claims.get("userType", String.class)
        );

        int userId = extractUserId(claims);

        String email;

        switch (userType) {
            case ADMIN:
                if (userId != 0 ||
                        !ADMIN_EMAIL.equals(claims.getSubject())) {
                    throw new IllegalArgumentException(
                            "Invalid administrator identity"
                    );
                }
                email = ADMIN_EMAIL;
                break;

            case COMPANY:
                email = companyRepository.findEmailForAuthById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Company account not found"
                                )
                        );
                break;

            case CUSTOMER:
                email = customerRepository.findEmailForAuthById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer account not found"
                                )
                        );
                break;

            default:
                throw new IllegalArgumentException("Unsupported user type");
        }

        return createToken(email, userType, userId);
    }

    private int resolveUserId(UserDetails userDetails) {
        switch (userDetails.getUserType()) {
            case ADMIN:
                if (!ADMIN_EMAIL.equals(userDetails.getEmail())) {
                    throw new IllegalArgumentException(
                            "Invalid administrator email"
                    );
                }
                return 0;

            case COMPANY:
                Company company = companyRepository.findByEmail(
                        userDetails.getEmail()
                );

                if (company == null) {
                    throw new IllegalArgumentException(
                            "Company account not found"
                    );
                }

                return company.getId();

            case CUSTOMER:
                Customer customer = customerRepository
                        .findByEmail(userDetails.getEmail())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer account not found"
                                )
                        );

                return customer.getId();

            default:
                throw new IllegalArgumentException("Unsupported user type");
        }
    }

    private String createToken(
            String email,
            UserType userType,
            int userId) {

        Instant now = Instant.now();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType.name());
        claims.put("userId", userId);

        return BEARER_PREFIX +
                Jwts.builder()
                        .setClaims(claims)
                        .setSubject(email)
                        .setIssuedAt(Date.from(now))
                        .setExpiration(Date.from(
                                now.plus(
                                        TOKEN_DURATION_MINUTES,
                                        ChronoUnit.MINUTES
                                )
                        ))
                        .signWith(signingKey, SignatureAlgorithm.HS256)
                        .compact();
    }

    public Claims extractAllClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Missing token");
        }

        String rawToken = token.trim();

        if (rawToken.startsWith(BEARER_PREFIX)) {
            rawToken = rawToken.substring(BEARER_PREFIX.length()).trim();
        }

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(rawToken)
                .getBody();
    }

    private int extractUserId(Claims claims) {
        Object value = claims.get("userId");

        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Missing user identity");
        }

        return ((Number) value).intValue();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpirationDate(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        try {
            extractAllClaims(token);
            return false;
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public String getUserType(String token) {
        return extractAllClaims(token).get("userType", String.class);
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        if (userDetails == null ||
                userDetails.getUserType() == null) {
            return false;
        }

        Claims claims = extractAllClaims(token);

        return claims.getSubject().equals(userDetails.getEmail()) &&
                userDetails.getUserType().name().equals(
                        claims.get("userType", String.class)
                );
    }

    public boolean validateToken(String token) {
        extractAllClaims(token);
        return true;
    }

    public int checkUser(String token, UserType expectedType)
            throws LoginException, TokenException {

        Claims claims;

        try {
            claims = extractAllClaims(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenException("Invalid or expired token");
        }

        String actualType = claims.get("userType", String.class);

        if (!expectedType.name().equals(actualType)) {
            throw new LoginException("User not allowed");
        }

        String email = claims.getSubject();
        int userId;

        try {
            userId = extractUserId(claims);
        } catch (IllegalArgumentException ex) {
            throw new TokenException("Invalid user identity");
        }

        if (email == null || email.isBlank()) {
            throw new TokenException("Missing token subject");
        }

        if (expectedType == UserType.ADMIN) {
            if (userId != 0 || !ADMIN_EMAIL.equals(email)) {
                throw new TokenException(
                        "Invalid administrator identity"
                );
            }

            return 0;
        }

        if (userId <= 0) {
            throw new TokenException("Invalid account identity");
        }

        boolean accountMatches;

        if (expectedType == UserType.COMPANY) {
            accountMatches = companyRepository.existsByIdAndEmail(
                    userId, email
            );
        } else {
            accountMatches = customerRepository.existsByIdAndEmail(
                    userId, email
            );
        }

        if (!accountMatches) {
            throw new TokenException(
                    "Account not found or token is no longer valid"
            );
        }

        return userId;
    }
}