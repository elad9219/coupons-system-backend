package com.jb.spring_coupons_project.controller;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
import com.jb.spring_coupons_project.security.JWTutil;
import com.jb.spring_coupons_project.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/company")   //http://localhost:8080/company
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JWTutil jwtUtil;

    //CREATE
    @PostMapping("/addCoupon")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<?> addCoupon(@RequestHeader(name = "Authorization") String token, @RequestBody Coupon coupon) throws LoginException, TokenException, CouponException, CompanyException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.addCoupon(coupon);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(coupon.getTitle() + " added");
    }

    //READ
    @GetMapping("/allCoupons")
    public ResponseEntity<?> getAllCoupons(@RequestHeader(name = "Authorization") String token) throws LoginException, TokenException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getAllCompanyCoupons());
    }

    @GetMapping("/getOneCoupon/{id}")
    public ResponseEntity<?> getOneCompanyCoupon(@RequestHeader(name = "Authorization") String token, @PathVariable int id) throws LoginException, TokenException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getOneCompanyCoupon(id));
    }

    @GetMapping("/allCouponsByCategory/{category}")
    public ResponseEntity<?> getAllCompanyCouponsByCategory(@RequestHeader(name = "Authorization") String token, @PathVariable Category category) throws LoginException, TokenException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getAllCompanyCouponsByCategory(category));
    }

    @GetMapping("/allCouponsByMaxPrice/{maxPrice}")
    public ResponseEntity<?> getAllCompanyCouponsByMaxPrice(@RequestHeader(name = "Authorization") String token, @PathVariable double maxPrice) throws LoginException, TokenException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getAllCompanyCouponsByMaxPrice(maxPrice));
    }

    @GetMapping("/companyDetails")
    public ResponseEntity<?> getCompanyDetails(@RequestHeader(name = "Authorization") String token) throws ExistsException, TokenException, LoginException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        return  ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getCompanyDetails());
    }

    //UPDATE
    @PutMapping("/updateCoupon")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateCoupon(@RequestHeader(name = "Authorization") String token, @RequestBody Coupon coupon) throws LoginException, TokenException, CouponException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.updateCoupon(coupon);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(coupon.getTitle() + " updated.");
    }

    // NEW ENDPOINT FOR COMPANY UPDATE
    @PutMapping("/updateDetails")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateDetails(@RequestHeader(name = "Authorization") String token, @RequestBody Company company) throws LoginException, TokenException, CompanyException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.updateCompanyDetails(company);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Company details updated successfully.");
    }

    //DELETE
    @DeleteMapping("/deleteCoupon/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<?> deleteCoupon(@RequestHeader(name = "Authorization") String token, @PathVariable int id) throws LoginException, TokenException, CouponException, ExistsException {
        jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.deleteCoupon(id);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Coupon deleted.");
    }
}