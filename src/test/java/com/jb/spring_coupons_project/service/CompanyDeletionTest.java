
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:company_deletion_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "COUPONS_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
class CompanyDeletionTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void deletingCompanyWithPurchasedCouponsPreservesCustomers()
            throws Exception {

        Customer customer = customerRepository
                .findByEmail("kobi@gmail.com")
                .orElseThrow();

        List<Coupon> purchases =
                couponRepository.findAllCustomerCoupons(
                        customer.getId()
                );

        assertFalse(purchases.isEmpty());

        Coupon purchasedCoupon = purchases.get(0);
        int companyId = purchasedCoupon.getCompanyId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow();

        assertNotNull(company.getCoupons());

        List<Integer> couponIds = couponRepository
                .getAllCompanyCoupons(companyId)
                .stream()
                .map(Coupon::getId)
                .toList();

        assertFalse(couponIds.isEmpty());

        Coupon unrelatedCoupon = couponRepository.findAll()
                .stream()
                .filter(c -> c.getCompanyId() != companyId)
                .findFirst()
                .orElseThrow();

        Customer otherCustomer = customerRepository.findAll()
                .stream()
                .filter(c -> c.getId() != customer.getId())
                .findFirst()
                .orElseThrow();

        // Both customers have a purchase of the coupon
        // belonging to the company being deleted.
        couponRepository.addPurchasedCoupon(
                otherCustomer.getId(),
                purchasedCoupon.getId()
        );

        // The other customer also owns an unrelated coupon.
        couponRepository.addPurchasedCoupon(
                otherCustomer.getId(),
                unrelatedCoupon.getId()
        );

        adminService.deleteCompany(companyId);

        assertEquals(
                0,
                count(
                        "SELECT COUNT(*) FROM companies WHERE id = ?",
                        companyId
                )
        );

        assertEquals(
                0,
                count(
                        "SELECT COUNT(*) FROM coupons WHERE company_id = ?",
                        companyId
                )
        );

        for (int couponId : couponIds) {
            assertEquals(
                    0,
                    count(
                            "SELECT COUNT(*) FROM customers_vs_coupons " +
                                    "WHERE coupon_id = ?",
                            couponId
                    )
            );
        }

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM customers " +
                                "WHERE customer_id = ?",
                        customer.getId()
                )
        );

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM customers " +
                                "WHERE customer_id = ?",
                        otherCustomer.getId()
                )
        );

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM coupons " +
                                "WHERE coupon_id = ?",
                        unrelatedCoupon.getId()
                )
        );

        assertEquals(
                1,
                count(
                        "SELECT COUNT(*) FROM customers_vs_coupons " +
                                "WHERE customer_id = ? AND coupon_id = ?",
                        otherCustomer.getId(),
                        unrelatedCoupon.getId()
                )
        );
    }

    @Test
    @Transactional
    void deletingMissingCompanyDoesNotModifyTheDatabase() {

        long companyCount = companyRepository.count();
        long couponCount = couponRepository.count();

        assertThrows(
                ExistsException.class,
                () -> adminService.deleteCompany(Integer.MAX_VALUE)
        );

        assertEquals(companyCount, companyRepository.count());
        assertEquals(couponCount, couponRepository.count());
    }

    private int count(String sql, Object... parameters) {
        Integer result = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                parameters
        );

        assertNotNull(result);
        return result;
    }
}
