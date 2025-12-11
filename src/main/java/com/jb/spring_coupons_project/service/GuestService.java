package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService extends ClientService {

    public List<Coupon> getAllSystemCoupons() throws ExistsException {
        List<Coupon> couponsList = couponRepository.getAllSystemCoupons();
        if (couponsList.isEmpty()) {
            throw new ExistsException("No coupons exists");
        }
        return couponsList;
    }


    public List<Coupon> getAllCouponsByCategory(Category category) throws ExistsException {
        List<Coupon> couponList = new ArrayList<>();
        for (Coupon coupon : getAllSystemCoupons()) {
            if (coupon.getCategory().equals(category)) {
                couponList.add(coupon);
            }
        }
        if (couponList.isEmpty()) {
            throw new ExistsException("No coupon exists in this category");
        }
        return couponList;
    }


    public List<Coupon> getAllCouponsByMaxPrice(double maxPrice) throws ExistsException {
        List<Coupon> couponList = couponRepository.getAllSystemCouponsByMaxPrice(maxPrice);
        if (couponList.isEmpty()) {
            throw new ExistsException("The company has no coupons exists for this max price.");
        }
        return couponList;
    }

    // Added register method for guests
    public void register(Customer customer) throws CustomerException {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerException("Email already exists");
        }
        customerRepository.save(customer);
        System.out.println("New customer registered via GuestService: " + customer.getEmail());
    }

    @Override
    public boolean login(String email, String password) {
        return false;
    }
}