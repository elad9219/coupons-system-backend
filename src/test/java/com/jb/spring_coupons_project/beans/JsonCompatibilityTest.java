
package com.jb.spring_coupons_project.beans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:json_compatibility_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "COUPONS_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
class JsonCompatibilityTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void createsCompanyWithoutClientSuppliedId() throws Exception {
        String json = """
                {
                  "name": "Test Company",
                  "email": "company@example.com",
                  "password": "test-password"
                }
                """;

        Company company = jsonMapper.readValue(
                json, Company.class
        );

        assertEquals(0, company.getId());
        assertEquals("Test Company", company.getName());
        assertEquals("company@example.com", company.getEmail());
        assertEquals("test-password", company.getPassword());
    }

    @Test
    void createsCustomerWithoutClientSuppliedId() throws Exception {
        String json = """
                {
                  "first_name": "Test",
                  "last_name": "Customer",
                  "email": "customer@example.com",
                  "password": "test-password"
                }
                """;

        Customer customer = jsonMapper.readValue(
                json, Customer.class
        );

        assertEquals(0, customer.getId());
        assertEquals("Test", customer.getFirst_name());
        assertEquals("Customer", customer.getLast_name());
        assertEquals("customer@example.com", customer.getEmail());
    }

    @Test
    void createsCouponWithoutIdOrCompanyId() throws Exception {
        String json = """
                {
                  "category": "FOOD",
                  "title": "Test Coupon",
                  "description": "Regression test coupon",
                  "start_date": "2026-10-01",
                  "end_date": "2026-12-31",
                  "amount": 10,
                  "price": 99.5,
                  "image": "https://example.com/image.jpg"
                }
                """;

        Coupon coupon = jsonMapper.readValue(
                json, Coupon.class
        );

        assertEquals(0, coupon.getId());
        assertEquals(0, coupon.getCompanyId());
        assertEquals(10, coupon.getAmount());
        assertEquals(99.5, coupon.getPrice());
        assertEquals(Category.FOOD, coupon.getCategory());
        assertEquals("Test Coupon", coupon.getTitle());
        assertFalse(coupon.isExpired());
    }

    @Test
    void doesNotExposePasswordsInJsonResponses() {
        Company company = Company.builder()
                .id(1)
                .name("Example")
                .email("company@example.com")
                .password("private-company-password")
                .build();

        Customer customer = Customer.builder()
                .id(2)
                .first_name("Example")
                .last_name("Customer")
                .email("customer@example.com")
                .password("private-customer-password")
                .build();

        String companyJson =
                jsonMapper.writeValueAsString(company);

        String customerJson =
                jsonMapper.writeValueAsString(customer);

        assertFalse(companyJson.contains("password"));
        assertFalse(companyJson.contains("private-company-password"));

        assertFalse(customerJson.contains("password"));
        assertFalse(customerJson.contains("private-customer-password"));
    }
}
