package com.jb.spring_coupons_project.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller ensures that when a user refreshes the page on a React route,
 * Spring Boot forwards the request back to index.html instead of returning a 404 error.
 */
@Controller
public class ReactRoutingController {

    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public String redirect() {
        // Forward to index.html so React Router can handle the routing
        return "forward:/index.html";
    }
}