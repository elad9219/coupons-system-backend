package com.jb.spring_coupons_project;

import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.service.AdminService;
import com.jb.spring_coupons_project.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:coupons_context_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.sql.init.mode=never",
		"COUPONS_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
class SpringCouponsProjectApplicationTests {

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoadsAndSeedsDemoData() {
		assertEquals(15L, companyRepository.count());
		assertEquals(21L, customerRepository.count());
		assertEquals(50L, couponRepository.count());

		Customer demoCustomer = getDemoCustomer();

		assertEquals(
				2,
				couponRepository.findAllCustomerCoupons(
						demoCustomer.getId()
				).size()
		);
	}

	@Test
	@Transactional
	void deletingCustomerPreservesSharedCouponsAndOtherOwners()
			throws Exception {

		Customer customer = getDemoCustomer();

		List<Coupon> purchasedCoupons =
				couponRepository.findAllCustomerCoupons(
						customer.getId()
				);

		assertEquals(2, purchasedCoupons.size());

		Customer anotherCustomer = customerRepository.findAll()
				.stream()
				.filter(c -> c.getId() != customer.getId())
				.findFirst()
				.orElseThrow(() ->
						new AssertionError("Another customer not found")
				);

		int couponId = purchasedCoupons.get(0).getId();

		// Give another customer ownership of the same coupon.
		couponRepository.addPurchasedCoupon(
				anotherCustomer.getId(),
				couponId
		);

		adminService.deleteCustomer(customer.getId());

		assertEquals(
				0,
				countRows(
						"SELECT COUNT(*) FROM customers " +
								"WHERE customer_id = ?",
						customer.getId()
				)
		);

		assertEquals(
				0,
				countRows(
						"SELECT COUNT(*) FROM customers_vs_coupons " +
								"WHERE customer_id = ?",
						customer.getId()
				)
		);

		for (Coupon coupon : purchasedCoupons) {
			assertEquals(
					1,
					countRows(
							"SELECT COUNT(*) FROM coupons " +
									"WHERE coupon_id = ?",
							coupon.getId()
					)
			);
		}

		assertEquals(
				1,
				countRows(
						"SELECT COUNT(*) FROM customers_vs_coupons " +
								"WHERE customer_id = ? AND coupon_id = ?",
						anotherCustomer.getId(),
						couponId
				),
				"Another customer's purchase must remain intact"
		);
	}

	@Test
	@Transactional
	void deletingPurchasedCouponPreservesCustomer()
			throws Exception {

		Customer customer = getDemoCustomer();

		List<Coupon> purchasedCoupons =
				couponRepository.findAllCustomerCoupons(
						customer.getId()
				);

		assertEquals(2, purchasedCoupons.size());

		Coupon coupon = purchasedCoupons.get(0);

		companyService.deleteCoupon(
				coupon.getCompanyId(),
				coupon.getId()
		);

		couponRepository.flush();

		assertEquals(
				0,
				countRows(
						"SELECT COUNT(*) FROM coupons " +
								"WHERE coupon_id = ?",
						coupon.getId()
				)
		);

		assertEquals(
				1,
				countRows(
						"SELECT COUNT(*) FROM customers " +
								"WHERE customer_id = ?",
						customer.getId()
				)
		);

		assertEquals(
				0,
				countRows(
						"SELECT COUNT(*) FROM customers_vs_coupons " +
								"WHERE coupon_id = ?",
						coupon.getId()
				)
		);

		assertTrue(
				couponRepository.existsById(
						purchasedCoupons.get(1).getId()
				)
		);
	}

	private Customer getDemoCustomer() {
		return customerRepository
				.findByEmail("kobi@gmail.com")
				.orElseThrow(() ->
						new AssertionError(
								"Demo customer was not seeded"
						)
				);
	}

	private int countRows(String sql, Object... args) {
		Integer result = jdbcTemplate.queryForObject(
				sql,
				Integer.class,
				args
		);

		if (result == null) {
			throw new AssertionError("Count query returned null");
		}

		return result;
	}
}