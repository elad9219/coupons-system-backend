
package com.jb.spring_coupons_project.controller;

import com.jb.spring_coupons_project.beans.UserDetails;
import com.jb.spring_coupons_project.exception.LoginException;
import com.jb.spring_coupons_project.security.JWTutil;
import com.jb.spring_coupons_project.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JWTutil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody UserDetails userDetails
    ) throws LoginException {

        if (userDetails == null ||
                userDetails.getEmail() == null ||
                userDetails.getEmail().isBlank() ||
                userDetails.getPassword() == null ||
                userDetails.getPassword().isBlank() ||
                userDetails.getUserType() == null) {

            throw new LoginException(
                    "Email, password and user type are required"
            );
        }

        loginService.login(
                userDetails.getEmail(),
                userDetails.getPassword(),
                userDetails.getUserType()
        );

        return ResponseEntity.ok()
                .header(
                        "Authorization",
                        jwtUtil.generateToken(userDetails)
                )
                .body(
                        userDetails.getUserType() +
                                " connected successfully"
                );
    }
}
