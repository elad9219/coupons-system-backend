
package com.jb.spring_coupons_project.advice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:coupon_amount_api_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "COUPONS_JWT_SECRET_BASE64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
@AutoConfigureMockMvc
class CouponAmountApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void oversizedAmountReturnsSpecificBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/company/addCoupon")
                                .header("Authorization", "Bearer invalid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "category": "FOOD",
                                          "title": "Test coupon",
                                          "description": "Test description",
                                          "start_date": "2030-01-01",
                                          "end_date": "2030-01-31",
                                          "amount": 9999999999999,
                                          "price": 100
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value("Invalid request")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(org.hamcrest.Matchers.containsString(
                                        "coupon amount"
                                ))
                );
    }
}
