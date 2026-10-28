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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JWTutil jwtUtil;

    @PostMapping("/addCoupon")
    public ResponseEntity<?> addCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody Coupon coupon)
            throws LoginException, TokenException,
            CouponException, CompanyException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.addCoupon(companyId, coupon);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(coupon.getTitle() + " added");
    }

    @GetMapping("/allCoupons")
    public ResponseEntity<?> getAllCoupons(
            @RequestHeader("Authorization") String token)
            throws LoginException, TokenException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getAllCompanyCoupons(companyId));
    }

    @GetMapping("/getOneCoupon/{id}")
    public ResponseEntity<?> getOneCompanyCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable int id)
            throws LoginException, TokenException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        companyService.getOneCompanyCoupon(companyId, id)
                );
    }

    @GetMapping("/allCouponsByCategory/{category}")
    public ResponseEntity<?> getAllCompanyCouponsByCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Category category)
            throws LoginException, TokenException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        companyService.getAllCompanyCouponsByCategory(
                                companyId, category
                        )
                );
    }

    @GetMapping("/allCouponsByMaxPrice/{maxPrice}")
    public ResponseEntity<?> getAllCompanyCouponsByMaxPrice(
            @RequestHeader("Authorization") String token,
            @PathVariable double maxPrice)
            throws LoginException, TokenException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        companyService.getAllCompanyCouponsByMaxPrice(
                                companyId, maxPrice
                        )
                );
    }

    @GetMapping("/companyDetails")
    public ResponseEntity<?> getCompanyDetails(
            @RequestHeader("Authorization") String token)
            throws LoginException, TokenException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(companyService.getCompanyDetails(companyId));
    }

    @PutMapping("/updateCoupon")
    public ResponseEntity<?> updateCoupon(
            @RequestHeader("Authorization") String token,
            @RequestBody Coupon coupon)
            throws LoginException, TokenException,
            CouponException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.updateCoupon(companyId, coupon);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(coupon.getTitle() + " updated.");
    }

    @PutMapping("/updateDetails")
    public ResponseEntity<?> updateDetails(
            @RequestHeader("Authorization") String token,
            @RequestBody Company company)
            throws LoginException, TokenException,
            CompanyException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.updateCompanyDetails(companyId, company);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Company details updated successfully.");
    }

    @DeleteMapping("/deleteCoupon/{id}")
    public ResponseEntity<?> deleteCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable int id)
            throws LoginException, TokenException, ExistsException {

        int companyId = jwtUtil.checkUser(token, UserType.COMPANY);
        companyService.deleteCoupon(companyId, id);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Coupon deleted.");
    }
}