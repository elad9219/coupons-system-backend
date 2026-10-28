
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.security.PasswordCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService extends ClientService {

    public List<Coupon> getAllSystemCoupons()
            throws ExistsException {

        List<Coupon> coupons = couponRepository.getAllSystemCoupons();

        if (coupons.isEmpty()) {
            throw new ExistsException("No coupons exists");
        }

        return coupons;
    }

    public List<Coupon> getAllCouponsByCategory(Category category)
            throws ExistsException {

        List<Coupon> coupons = new ArrayList<>();

        for (Coupon coupon : getAllSystemCoupons()) {
            if (coupon.getCategory() == category) {
                coupons.add(coupon);
            }
        }

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No coupon exists in this category"
            );
        }

        return coupons;
    }

    public List<Coupon> getAllCouponsByMaxPrice(double maxPrice)
            throws ExistsException {

        List<Coupon> coupons = couponRepository
                .getAllSystemCouponsByMaxPrice(maxPrice);

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No coupons exist for this maximum price"
            );
        }

        return coupons;
    }

    public void register(Customer customer)
            throws CustomerException {

        if (customer == null ||
                customer.getEmail() == null ||
                customer.getEmail().isBlank()) {
            throw new CustomerException("Email is required");
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerException("Email already exists");
        }

        try {
            customer.setPassword(
                    PasswordCodec.encode(customer.getPassword())
            );
        } catch (IllegalArgumentException ex) {
            throw new CustomerException(ex.getMessage());
        }

        customer.setId(0);
        customer.setCoupons(null);

        customerRepository.save(customer);
    }

    @Override
    public boolean login(String email, String password) {
        return false;
    }
}
