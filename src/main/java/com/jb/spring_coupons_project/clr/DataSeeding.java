package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeding implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeding.class);

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Never replace existing records, including partially populated databases.
        if (companyRepository.count() > 0 || customerRepository.count() > 0 ||
                couponRepository.count() > 0) {
            log.info("Database contains data. Skipping demo seeding.");
            return;
        }

        LocalDate today = LocalDate.now();
        List<Company> companies = companyRepository.saveAll(DemoCatalog.companies());
        List<Customer> customers = customerRepository.saveAll(DemoCatalog.customers());
        List<Coupon> coupons = couponRepository.saveAll(DemoCatalog.coupons(companies, today));

        Customer demoCustomer = customers.get(0);
        List<Coupon> purchasedCoupons = List.of(coupons.get(0), coupons.get(1));

        for (Coupon coupon : purchasedCoupons) {
            coupon.setAmount(coupon.getAmount() - 1);
        }

        // Finish entity saves before writing directly to the purchase join table.
        // A later merge could overwrite those purchase relationships.
        couponRepository.saveAll(purchasedCoupons);
        for (Coupon coupon : purchasedCoupons) {
            couponRepository.addPurchasedCoupon(demoCustomer.getId(), coupon.getId());
        }

        log.info("Demo data seeded: {} companies, {} customers, {} coupons, {} purchases",
                companies.size(), customers.size(), coupons.size(), purchasedCoupons.size());
    }
}