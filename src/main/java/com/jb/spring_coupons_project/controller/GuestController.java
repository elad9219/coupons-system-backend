package com.jb.spring_coupons_project.controller;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest")   //http://localhost:8080/guest
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    @GetMapping("/allSystemCoupons")
    public ResponseEntity<?> getAllCoupons() throws ExistsException {
        return ResponseEntity.ok()
                .body(guestService.getAllSystemCoupons());
    }


    @GetMapping("/allCouponsByCategory/{category}")
    public ResponseEntity<?> getAllCompanyCouponsByCategory(@PathVariable Category category) throws  ExistsException {
        return ResponseEntity.ok()
                .body(guestService.getAllCouponsByCategory(category));
    }


    @GetMapping("/allCouponsByMaxPrice/{maxPrice}")
    public ResponseEntity<?> getAllCompanyCouponsByMaxPrice(@PathVariable double maxPrice) throws  ExistsException {
        return ResponseEntity.ok()
                .body(guestService.getAllCouponsByMaxPrice(maxPrice));
    }

    // Added endpoint for customer registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) throws CustomerException {
        guestService.register(customer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Customer registered successfully");
    }
}