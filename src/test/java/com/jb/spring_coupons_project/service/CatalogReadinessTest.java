
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class CatalogReadinessTest {

    @Test
    void catalogReturnsServiceUnavailableBeforeStartupCompletes() {
        CatalogReadiness readiness = new CatalogReadiness();

        CouponRepository repository =
                mock(CouponRepository.class);

        CouponCatalogService service =
                new CouponCatalogService(repository, readiness);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getCatalog(
                        0, 12, null, null, "newest"
                )
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );

        verifyNoInteractions(repository);
    }

    @Test
    void catalogBecomesAvailableAfterApplicationReadyEvent() {
        CatalogReadiness readiness = new CatalogReadiness();

        assertThrows(
                ResponseStatusException.class,
                readiness::ensureReady
        );

        readiness.markReady();

        assertDoesNotThrow(readiness::ensureReady);
    }
}
