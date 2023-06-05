package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService extends ClientService {

    private int customer_id;


    @Override
    public boolean login(String email, String password) {
        Optional<Customer> customer = customerRepository.findByEmailAndPassword(email, password);
        if (customer.isPresent()) {
            this.customer_id = customer.get().getId();
            System.out.println("Customer connected");
            return true;
        }
        return false;
    }




    public void purchaseCoupon(int coupon_id) throws CouponException, ExistsException, TokenException {
        if (!couponRepository.existsById(coupon_id)) {
            throw new ExistsException("Sorry, coupon not exists.");
        }
        Coupon coupon = couponRepository.findById(coupon_id).get();
        if (coupon.getAmount() == 0) {
            throw new CouponException("This coupon is out of stock, sorry.");
        }
        if (couponRepository.checkCustomerSameCoupon(this.customer_id, coupon_id) > 0) {
            throw new CouponException("You already have this coupon You may not buy more than 1");
        }
        couponRepository.addPurchasedCoupon(this.customer_id, coupon_id);
        coupon.setAmount(coupon.getAmount() - 1);
        couponRepository.save(coupon); //To save after the new amount
        System.out.println("Coupon " + coupon.getTitle() + " purchased.");
    }



    public List<Coupon> getCustomerCoupons() throws ExistsException, TokenException {
        List<Coupon> couponList = couponRepository.findAllCustomerCoupons(this.customer_id);
        if (couponList.isEmpty()) {
            throw new ExistsException("There are no coupons exist");
        }
        return couponList;
    }


    public List<Coupon> getCustomerCouponsByCategory(Category category) throws ExistsException, TokenException {
        List<Coupon> couponList = new ArrayList<>();
        for (Coupon coupon : getCustomerCoupons()) {
            if (coupon.getCategory().equals(category)) {
                couponList.add(coupon);
            }
        }
        if (couponList.isEmpty()) {
            throw new ExistsException("No coupon exists in this category");
        }
        return couponList;
    }


    public List<Coupon> getCustomerCouponsByMaxPrice(double maxPrice) throws ExistsException, TokenException {
        List<Coupon> couponList = couponRepository.findAllCustomerCouponsByMaxPrice(this.customer_id, maxPrice);
        if (couponList.isEmpty()) {
            throw new ExistsException("No coupon exists for this maximum price");
        }
        return couponList;
    }


    public Customer getCustomerDetails() throws ExistsException, TokenException {
        if (customerRepository.existsById(this.customer_id)) {
            return customerRepository.findById(this.customer_id).get();
        } else {
            throw new ExistsException("Customer not exists.");
        }
    }
}

