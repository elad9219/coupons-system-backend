
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.repository.CouponRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponCatalogService {

    private static final int MAX_PAGE_SIZE = 48;

    private final CouponRepository couponRepository;

    public record CatalogPage(
            List<Coupon> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean last
    ) {
    }

    @Transactional(readOnly = true)
    public CatalogPage getCatalog(
            int page,
            int size,
            Category category,
            Double maxPrice,
            String sort
    ) {
        validateRequest(page, size, maxPrice);

        Sort sorting = resolveSort(sort);

        Specification<Coupon> filters =
                (root, query, builder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (category != null) {
                        predicates.add(
                                builder.equal(
                                        root.get("category"),
                                        category
                                )
                        );
                    }

                    if (maxPrice != null) {
                        predicates.add(
                                builder.lessThanOrEqualTo(
                                        root.get("price"),
                                        maxPrice
                                )
                        );
                    }

                    return builder.and(
                            predicates.toArray(Predicate[]::new)
                    );
                };

        Page<Coupon> result = couponRepository.findAll(
                filters,
                PageRequest.of(page, size, sorting)
        );

        return new CatalogPage(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    private void validateRequest(
            int page,
            int size,
            Double maxPrice
    ) {
        if (page < 0) {
            throw badRequest("Page must be zero or greater");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw badRequest(
                    "Page size must be between 1 and " +
                            MAX_PAGE_SIZE
            );
        }

        if (maxPrice != null &&
                (!Double.isFinite(maxPrice) || maxPrice < 0)) {
            throw badRequest(
                    "Maximum price must be a finite, non-negative number"
            );
        }
    }

    private Sort resolveSort(String sort) {
        if (sort == null) {
            throw badRequest("Sort is required");
        }

        return switch (sort) {
            case "newest" -> Sort.by(
                    Sort.Order.desc("id")
            );

            case "price_asc" -> Sort.by(
                    Sort.Order.asc("price"),
                    Sort.Order.desc("id")
            );

            case "price_desc" -> Sort.by(
                    Sort.Order.desc("price"),
                    Sort.Order.desc("id")
            );

            case "title" -> Sort.by(
                    Sort.Order.asc("title"),
                    Sort.Order.desc("id")
            );

            default -> throw badRequest(
                    "Unsupported catalog sort"
            );
        };
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}
