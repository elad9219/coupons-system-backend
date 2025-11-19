package com.jb.spring_coupons_project.dailyJob;

import com.jb.spring_coupons_project.repository.CouponRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@EnableAsync
@EnableScheduling
public class CouponDailyJob {

    private final CouponRepository couponRepository;

    public CouponDailyJob(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Jerusalem")
    public void markExpiredCoupons() {
        System.out.println("Daily job running - marking expired coupons...");
        couponRepository.markExpiredCoupons();
        System.out.println("Expired coupons marked successfully");
    }

    @PostConstruct
    public void init() {
        System.out.println("PostConstruct - marking expired coupons immediately on startup");
        couponRepository.markExpiredCoupons();
    }
}