
package com.jb.spring_coupons_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CORS {

    private final List<String> allowedOrigins;

    public CORS(
            @Value("${COUPONS_ALLOWED_ORIGINS:http://localhost:3000,http://127.0.0.1:3000,https://coupons-gamma.vercel.app}")
            String configuredOrigins
    ) {
        this.allowedOrigins = Arrays.stream(
                        configuredOrigins.split(",")
                )
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();

        if (allowedOrigins.isEmpty() ||
                allowedOrigins.stream().anyMatch(
                        origin -> origin.contains("*")
                )) {
            throw new IllegalStateException(
                    "CORS requires explicit allowed origins"
            );
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowCredentials(true);

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Accept")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return new CorsFilter(source);
    }
}
