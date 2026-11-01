
package com.jb.spring_coupons_project.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CorsSecurityTest {

    private final CorsFilter corsFilter = new CORS(
            "http://localhost:3000,https://coupons-gamma.vercel.app"
    ).corsFilter();

    @Test
    void allowsLocalFrontendPreflight()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "OPTIONS",
                        "/api/login"
                );

        request.addHeader(
                "Origin",
                "http://localhost:3000"
        );

        request.addHeader(
                "Access-Control-Request-Method",
                "POST"
        );

        request.addHeader(
                "Access-Control-Request-Headers",
                "authorization,content-type"
        );

        MockHttpServletResponse response =
                execute(request);

        assertEquals(200, response.getStatus());

        assertEquals(
                "http://localhost:3000",
                response.getHeader(
                        "Access-Control-Allow-Origin"
                )
        );

        assertEquals(
                "true",
                response.getHeader(
                        "Access-Control-Allow-Credentials"
                )
        );
    }

    @Test
    void allowsProductionFrontend()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/guest/catalog"
                );

        request.addHeader(
                "Origin",
                "https://coupons-gamma.vercel.app"
        );

        MockHttpServletResponse response =
                execute(request);

        assertEquals(200, response.getStatus());

        assertEquals(
                "https://coupons-gamma.vercel.app",
                response.getHeader(
                        "Access-Control-Allow-Origin"
                )
        );
    }

    @Test
    void rejectsUntrustedOrigin()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "GET",
                        "/api/guest/catalog"
                );

        request.addHeader(
                "Origin",
                "https://untrusted.example.com"
        );

        MockHttpServletResponse response =
                execute(request);

        assertEquals(403, response.getStatus());

        assertNull(
                response.getHeader(
                        "Access-Control-Allow-Origin"
                )
        );
    }

    @Test
    void rejectsWildcardConfiguration() {

        assertThrows(
                IllegalStateException.class,
                () -> new CORS("*")
        );
    }

    private MockHttpServletResponse execute(
            MockHttpServletRequest request
    ) throws ServletException, IOException {

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        corsFilter.doFilter(
                request,
                response,
                (servletRequest, servletResponse) -> {
                }
        );

        return response;
    }
}
