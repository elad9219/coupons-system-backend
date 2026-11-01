
package com.jb.spring_coupons_project.advice;

import com.jb.spring_coupons_project.exception.CompanyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:api_error_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
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
class ApiErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiErrorAdvice apiErrorAdvice;

    @Test
    void loginWithoutUserTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "admin@admin.com",
                                          "password": "admin"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad login"));
    }

    @Test
    void loginWithoutPasswordReturnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "admin@admin.com",
                                          "userType": "ADMIN"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad login"));
    }

    @Test
    void malformedJsonReturnsStructuredBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{invalid")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value("Invalid request")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Malformed request body")
                );
    }

    @Test
    void duplicateCustomerReturnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/guest/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "first_name": "Test",
                                          "last_name": "Customer",
                                          "email": "kobi@gmail.com",
                                          "password": "test1234"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value("Invalid request")
                )
                .andExpect(
                        jsonPath("$.description").value(
                                "Email already exists"
                        )
                );
    }

    @Test
    void invalidCategoryReturnsStructuredBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/api/guest/allCouponsByCategory/NOT_A_CATEGORY")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value("Invalid request")
                );
    }

    @Test
    void companyValidationUsesExistingErrorStructure() {
        ErrorDetail error = apiErrorAdvice.handleBusinessValidation(
                new CompanyException("Company name is required")
        );

        assertNotNull(error);
        assertEquals("Invalid request", error.getError());
        assertEquals(
                "Company name is required",
                error.getDescription()
        );
    }
}
