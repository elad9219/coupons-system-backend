
package com.jb.spring_coupons_project.service;

import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.CouponException;

public final class CouponInputValidator {

    private static final int MAX_TITLE_LENGTH = 25;
    private static final int MAX_DESCRIPTION_LENGTH = 1024;
    private static final int MAX_IMAGE_LENGTH = 1024;
    private static final int MAX_COUPON_AMOUNT = 100_000;
    private static final double MAX_PRICE = 9999.0;

    private CouponInputValidator() {
    }

    public static void validate(
            Coupon coupon,
            boolean creating
    ) throws CouponException {

        if (coupon == null) {
            throw new CouponException("Coupon details are required");
        }

        if (coupon.getCategory() == null) {
            throw new CouponException("Coupon category is required");
        }

        String title = normalize(coupon.getTitle());

        if (title == null || title.isEmpty() ||
                title.length() > MAX_TITLE_LENGTH) {
            throw new CouponException(
                    "Coupon title must contain between 1 and " +
                            MAX_TITLE_LENGTH + " characters"
            );
        }

        String description = normalize(
                coupon.getDescription()
        );

        if (description == null || description.isEmpty() ||
                description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new CouponException(
                    "Coupon description must contain between 1 and " +
                            MAX_DESCRIPTION_LENGTH + " characters"
            );
        }

        if (coupon.getStart_date() == null ||
                coupon.getEnd_date() == null) {
            throw new CouponException(
                    "Coupon start and end dates are required"
            );
        }

        if (coupon.getEnd_date().isBefore(
                coupon.getStart_date()
        )) {
            throw new CouponException(
                    "Coupon end date cannot be before start date"
            );
        }

        int amount = coupon.getAmount();

        if (amount < (creating ? 1 : 0)) {
            throw new CouponException(
                    creating
                            ? "New coupon stock must be greater than zero"
                            : "Coupon stock cannot be negative"
            );
        }

        if (amount > MAX_COUPON_AMOUNT) {
            throw new CouponException(
                    "Coupon amount must not exceed " +
                            MAX_COUPON_AMOUNT
            );
        }

        double price = coupon.getPrice();

        if (!Double.isFinite(price) ||
                price <= 0 ||
                price > MAX_PRICE) {
            throw new CouponException(
                    "Coupon price must be greater than zero " +
                            "and must not exceed 9999"
            );
        }

        String image = normalize(coupon.getImage());

        if (image != null && image.length() > MAX_IMAGE_LENGTH) {
            throw new CouponException(
                    "Coupon image URL is too long"
            );
        }

        coupon.setTitle(title);
        coupon.setDescription(description);
        coupon.setImage(image == null || image.isEmpty()
                ? null
                : image);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
