package com.jb.spring_coupons_project.controller;


import com.jb.spring_coupons_project.beans.UserDetails;
import com.jb.spring_coupons_project.exception.LoginException;
import com.jb.spring_coupons_project.security.JWTutil;
import com.jb.spring_coupons_project.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JWTutil jwtUtil;


    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody UserDetails userDetails) throws LoginException {
        String login = loginService.login(userDetails.getEmail(), userDetails.getPassword(), userDetails.getUserType()).toString();
        return  ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(userDetails))
                .body(userDetails.getUserType() + " connected successfully");
    }

}



