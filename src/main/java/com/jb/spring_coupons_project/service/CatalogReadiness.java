
package com.jb.spring_coupons_project.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CatalogReadiness {

    private volatile boolean ready = false;

    @EventListener(ApplicationReadyEvent.class)
    public void markReady() {
        ready = true;
    }

    public void ensureReady() {
        if (!ready) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The coupon catalog is still initializing"
            );
        }
    }
}
