package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.CouponException;
import com.jb.spring_coupons_project.exception.CustomerException;
import com.jb.spring_coupons_project.exception.ExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService extends ClientService {

    @Override
    public boolean login(String email, String password) {
        return customerRepository
                .findByEmailAndPassword(email, password)
                .isPresent();
    }

    @Transactional
    public void purchaseCoupon(int customerId, int couponId)
            throws CouponException, ExistsException {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() ->
                        new ExistsException("Coupon not exists")
                );

        if (coupon.isExpired() ||
                (coupon.getEnd_date() != null &&
                        coupon.getEnd_date().isBefore(
                                java.time.LocalDate.now()
                        ))) {
            throw new CouponException(
                    "This coupon has expired and cannot be purchased"
            );
        }

        if (coupon.getAmount() <= 0) {
            throw new CouponException(
                    "This coupon is out of stock"
            );
        }

        if (couponRepository.checkCustomerSameCoupon(
                customerId, couponId) > 0) {
            throw new CouponException(
                    "You already own this coupon"
            );
        }

        couponRepository.addPurchasedCoupon(customerId, couponId);

        coupon.setAmount(coupon.getAmount() - 1);
        couponRepository.save(coupon);
    }

    public List<Coupon> getCustomerCoupons(int customerId)
            throws ExistsException {

        List<Coupon> coupons = couponRepository
                .findAllCustomerCoupons(customerId);

        if (coupons.isEmpty()) {
            throw new ExistsException("No purchased coupons exist");
        }

        return coupons;
    }

    public List<Coupon> getCustomerCouponsByCategory(
            int customerId,
            Category category) throws ExistsException {

        List<Coupon> coupons = getCustomerCoupons(customerId)
                .stream()
                .filter(coupon -> coupon.getCategory() == category)
                .collect(Collectors.toList());

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No coupons exist in this category"
            );
        }

        return coupons;
    }

    public List<Coupon> getCustomerCouponsByMaxPrice(
            int customerId,
            double maxPrice) throws ExistsException {

        List<Coupon> coupons = couponRepository
                .findAllCustomerCouponsByMaxPrice(
                        customerId, maxPrice
                );

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No coupons exist for this maximum price"
            );
        }

        return coupons;
    }

    public Customer getCustomerDetails(int customerId)
            throws ExistsException {

        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ExistsException("Customer not found")
                );
    }

    @Transactional
    public void updateCustomerDetails(
            int customerId,
            Customer incoming)
            throws CustomerException, ExistsException {

        Customer existing = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ExistsException("Customer not found")
                );

        if (!existing.getEmail().equals(incoming.getEmail())) {
            if (customerRepository.existsByEmail(incoming.getEmail())) {
                throw new CustomerException("Email already exists");
            }

            existing.setEmail(incoming.getEmail());
        }

        existing.setFirst_name(incoming.getFirst_name());
        existing.setLast_name(incoming.getLast_name());
        existing.setPassword(incoming.getPassword());

        customerRepository.saveAndFlush(existing);
    }
}