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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/customer")   //http://localhost:8080/customer
@RequiredArgsConstructor
public class CustomerController {

    private final JWTutil jwtUtil;
    private final CustomerService customerService;

    @PostMapping("/purchaseCoupon/{coupon_id}")
    public ResponseEntity<?> purchaseCoupon(@RequestHeader(name = "Authorization") String token, @PathVariable int coupon_id) throws LoginException, TokenException, CouponException, ExistsException, DataIntegrityViolationException, SQLException {
        jwtUtil.checkUser(token, UserType.CUSTOMER);
        customerService.purchaseCoupon(coupon_id);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Coupon purchased.");
    }

    @GetMapping("/customerCoupons")
    public ResponseEntity<?> getAllCustomerCoupons(@RequestHeader(name = "Authorization") String token) throws LoginException, TokenException, ExistsException {
        jwtUtil.checkUser(token, UserType.CUSTOMER);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customerService.getCustomerCoupons());
    }

    @GetMapping("/customerCouponsByCategory/{category}")
    public ResponseEntity<?> getCustomerCouponsByCategory(@RequestHeader(name = "Authorization") String token, @PathVariable Category category) throws LoginException, TokenException, CouponException, ExistsException {
        jwtUtil.checkUser(token, UserType.CUSTOMER);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customerService.getCustomerCouponsByCategory(category));
    }

    @GetMapping("/customerCouponsByMaxPrice/{maxPrice}")
    public ResponseEntity<?> getCustomerCouponsByMaxPrice(@RequestHeader(name = "Authorization") String token, @PathVariable double maxPrice) throws LoginException, TokenException, CouponException, ExistsException {
        jwtUtil.checkUser(token, UserType.CUSTOMER);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customerService.getCustomerCouponsByMaxPrice(maxPrice));
    }

    @GetMapping("/customerDetails")
    public ResponseEntity<?> getCustomerDetails(@RequestHeader(name = "Authorization") String token) throws LoginException, TokenException, CouponException, CustomerException, ExistsException {
        jwtUtil.checkUser(token, UserType.CUSTOMER);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body(customerService.getCustomerDetails());
    }

    // NEW ENDPOINT FOR CUSTOMER UPDATE
    @PutMapping("/updateDetails")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateDetails(@RequestHeader(name = "Authorization") String token, @RequestBody Customer customer) throws LoginException, TokenException, CustomerException, ExistsException {
        jwtUtil.checkUser(token, UserType.CUSTOMER);
        customerService.updateCustomerDetails(customer);
        return ResponseEntity.ok()
                .header("Authorization", jwtUtil.generateToken(token))
                .body("Customer details updated successfully.");
    }
}