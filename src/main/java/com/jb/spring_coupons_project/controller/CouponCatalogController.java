
package com.jb.spring_coupons_project.controller;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.service.CouponCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class CouponCatalogController {

    private final CouponCatalogService catalogService;

    @GetMapping("/catalog")
    public ResponseEntity<CouponCatalogService.CatalogPage> getCatalog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        return ResponseEntity.ok(
                catalogService.getCatalog(
                        page,
                        size,
                        category,
                        maxPrice,
                        sort
                )
        );
    }
}
