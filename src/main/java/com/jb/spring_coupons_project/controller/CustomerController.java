package com.jb.spring_coupons_project.controller;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.beans.UserType;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
import com.jb.spring_coupons_project.security.JWTutil;
import com.jb.spring_coupons_project.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JWTutil jwtUtil;
    private final CustomerService customerService;

    @PostMapping("/purchaseCoupon/{coupon_id}")
    public ResponseEntity<?> purchaseCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable int coupon_id)
            throws LoginException, TokenException,
            CouponException, ExistsException {

        int customerId = jwtUtil.checkUser(token, UserType.CUSTOMER);
        customerService.purchaseCoupon(customerId, coupon_id);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Coupon purchased.");
    }

    @GetMapping("/customerCoupons")
    public ResponseEntity<?> getAllCustomerCoupons(
            @RequestHeader("Authorization") String token)
            throws LoginException, TokenException, ExistsException {

        int customerId = jwtUtil.checkUser(token, UserType.CUSTOMER);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customerService.getCustomerCoupons(customerId));
    }

    @GetMapping("/customerCouponsByCategory/{category}")
    public ResponseEntity<?> getCustomerCouponsByCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Category category)
            throws LoginException, TokenException, ExistsException {

        int customerId = jwtUtil.checkUser(token, UserType.CUSTOMER);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        customerService.getCustomerCouponsByCategory(
                                customerId, category
                        )
                );
    }

    @GetMapping("/customerCouponsByMaxPrice/{maxPrice}")
    public ResponseEntity<?> getCustomerCouponsByMaxPrice(
            @RequestHeader("Authorization") String token,
            @PathVariable double maxPrice)
            throws LoginException, TokenException, ExistsException {

        int customerId = jwtUtil.checkUser(token, UserType.CUSTOMER);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(
                        customerService.getCustomerCouponsByMaxPrice(
                                customerId, maxPrice
                        )
                );
    }

    @GetMapping("/customerDetails")
    public ResponseEntity<?> getCustomerDetails(
            @RequestHeader("Authorization") String token)
            throws LoginException, TokenException, ExistsException {

        int customerId = jwtUtil.checkUser(token, UserType.CUSTOMER);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customerService.getCustomerDetails(customerId));
    }

    @PutMapping("/updateDetails")
    public ResponseEntity<?> updateDetails(
            @RequestHeader("Authorization") String token,
            @RequestBody Customer customer)
            throws LoginException, TokenException,
            CustomerException, ExistsException {

        int customerId = jwtUtil.checkUser(token, UserType.CUSTOMER);
        customerService.updateCustomerDetails(customerId, customer);

        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Customer details updated successfully.");
    }
}