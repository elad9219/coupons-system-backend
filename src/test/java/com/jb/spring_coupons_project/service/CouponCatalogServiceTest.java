
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
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
class CouponCatalogServiceTest {

    @Autowired
    private CouponCatalogService catalogService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pagesContainDistinctCouponsInStableOrder() {
        var first = catalogService.getCatalog(
                0, 12, null, null, "newest"
        );

        var second = catalogService.getCatalog(
                1, 12, null, null, "newest"
        );

        assertEquals(50, first.totalElements());
        assertEquals(5, first.totalPages());
        assertEquals(12, first.content().size());
        assertEquals(12, second.content().size());

        Set<Integer> firstIds = new HashSet<>(
                first.content().stream()
                        .map(Coupon::getId)
                        .toList()
        );

        for (Coupon coupon : second.content()) {
            assertFalse(firstIds.contains(coupon.getId()));
        }

        assertTrue(
                first.content().getLast().getId() >
                        second.content().getFirst().getId()
        );
    }

    @Test
    void filtersCategoryAndPriceTogether() {
        Coupon reference = couponRepository.findAll()
                .getFirst();

        Category category = reference.getCategory();
        double maxPrice = reference.getPrice();

        var result = catalogService.getCatalog(
                0, 48, category, maxPrice, "price_asc"
        );

        assertFalse(result.content().isEmpty());

        for (Coupon coupon : result.content()) {
            assertEquals(category, coupon.getCategory());
            assertTrue(coupon.getPrice() <= maxPrice);
        }
    }

    @Test
    void sortsPricesAscendingAndDescending() {
        List<Coupon> ascending = catalogService.getCatalog(
                0, 48, null, null, "price_asc"
        ).content();

        List<Coupon> descending = catalogService.getCatalog(
                0, 48, null, null, "price_desc"
        ).content();

        assertFalse(ascending.isEmpty());
        assertFalse(descending.isEmpty());

        for (int i = 1; i < ascending.size(); i++) {
            assertTrue(
                    ascending.get(i - 1).getPrice() <=
                            ascending.get(i).getPrice()
            );
        }

        for (int i = 1; i < descending.size(); i++) {
            assertTrue(
                    descending.get(i - 1).getPrice() >=
                            descending.get(i).getPrice()
            );
        }
    }

    @Test
    void outOfRangePageReturnsEmptyContent() {
        var result = catalogService.getCatalog(
                999, 12, null, null, "newest"
        );

        assertTrue(result.content().isEmpty());
        assertEquals(50, result.totalElements());
    }

    @Test
    void rejectsInvalidPaginationSortingAndPrice() {
        assertBadRequest(-1, 12, null, "newest");
        assertBadRequest(0, 0, null, "newest");
        assertBadRequest(0, 49, null, "newest");
        assertBadRequest(0, 12, -1.0, "newest");
        assertBadRequest(
                0, 12, Double.NaN, "newest"
        );
        assertBadRequest(0, 12, null, "unknown");
    }

    @Test
    void endpointReturnsPageMetadata() throws Exception {
        mockMvc.perform(
                        get("/api/guest/catalog")
                                .param("page", "0")
                                .param("size", "12")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(12))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(12))
                .andExpect(jsonPath("$.totalElements").value(50))
                .andExpect(jsonPath("$.totalPages").value(5));
    }

    @Test
    void endpointRejectsInvalidPageSize() throws Exception {
        mockMvc.perform(
                        get("/api/guest/catalog")
                                .param("size", "10000")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void legacyGuestEndpointRemainsAvailable() throws Exception {
        mockMvc.perform(
                        get("/api/guest/allSystemCoupons")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(50));
    }

    private void assertBadRequest(
            int page,
            int size,
            Double maxPrice,
            String sort
    ) {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> catalogService.getCatalog(
                        page, size, null, maxPrice, sort
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST.value(),
                error.getStatusCode().value()
        );
    }
}
