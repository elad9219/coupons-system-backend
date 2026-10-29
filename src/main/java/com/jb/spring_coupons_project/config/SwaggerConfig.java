
package com.jb.spring_coupons_project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI couponsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupons Management API")
                        .version("1.0.0")
                        .description(
                                "REST API for administrators, companies, " +
                                        "customers, coupon management and purchases."
                        ));
    }
}
