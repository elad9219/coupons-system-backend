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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService extends ClientService {

    private static final ZoneId BUSINESS_ZONE =
            ZoneId.of("Asia/Jerusalem");

    @Override
    public boolean login(String email, String password) {
        return customerRepository
                .findByEmailAndPassword(email, password)
                .isPresent();
    }

    @Transactional(rollbackFor = Exception.class)
    public void purchaseCoupon(int customerId, int couponId)
            throws CouponException, ExistsException {

        if (!customerRepository.existsById(customerId)) {
            throw new ExistsException("Customer not found");
        }

        // Lock the coupon row until the purchase transaction completes.
        // Concurrent purchases of the same coupon are serialized.
        Coupon coupon = couponRepository
                .findByIdForUpdate(couponId)
                .orElseThrow(() ->
                        new ExistsException("Coupon not exists")
                );

        LocalDate today = LocalDate.now(BUSINESS_ZONE);

        if (coupon.isExpired() ||
                (coupon.getEnd_date() != null &&
                        coupon.getEnd_date().isBefore(today))) {
            throw new CouponException(
                    "This coupon has expired and cannot be purchased"
            );
        }

        if (coupon.getStart_date() != null &&
                coupon.getStart_date().isAfter(today)) {
            throw new CouponException(
                    "This coupon is not available for purchase yet"
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

        // Flush the stock update before writing directly to the
        // customer-coupon join table.
        coupon.setAmount(coupon.getAmount() - 1);
        couponRepository.saveAndFlush(coupon);

        couponRepository.addPurchasedCoupon(
                customerId,
                couponId
        );
    }

    public List<Coupon> getCustomerCoupons(int customerId)
            throws ExistsException {

        List<Coupon> coupons = couponRepository
                .findAllCustomerCoupons(customerId);

        if (coupons.isEmpty()) {
            throw new ExistsException(
                    "No purchased coupons exist"
            );
        }

        return coupons;
    }

    public List<Coupon> getCustomerCouponsByCategory(
            int customerId,
            Category category) throws ExistsException {

        List<Coupon> coupons = getCustomerCoupons(customerId)
                .stream()
                .filter(coupon ->
                        coupon.getCategory() == category
                )
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
            if (customerRepository.existsByEmail(
                    incoming.getEmail()
            )) {
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