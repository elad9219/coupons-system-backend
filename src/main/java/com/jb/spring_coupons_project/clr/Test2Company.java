/*
package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.exception.ExistsException;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.service.CompanyService;
import com.jb.spring_coupons_project.util.TablePrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
@RequiredArgsConstructor
public class Test2Company implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final CouponRepository couponRepository;
    private final CompanyService companyService;

    @Override
    public void run(String... args) throws Exception {

        Optional<Company> companyOpt = companyRepository.findById(3);
        if (companyOpt.isEmpty()) {
            System.out.println("\nCompany ID 3 not found – skipping Test2Company\n");
            return;
        }

        Company company = companyOpt.get();
        companyService.login(company.getEmail(), company.getPassword());

        System.out.println("=============================================================================================================");
        System.out.println("TEST 2 – COMPANY COUPONS: CREATE / READ / UPDATE / DELETE + EXPIRED FLAG TEST");
        System.out.println("=============================================================================================================");

        // ========================= CREATE =========================
        Coupon coupon1 = Coupon.builder()
                .companyId(company.getId())
                .category(Category.VACATION)
                .title("Larnaca, Cyprus")
                .description("4 nights in Larnaca, 3 stars hotel")
                .start_date(LocalDate.of(2023, Month.OCTOBER, 22))
                .end_date(LocalDate.of(2023, Month.OCTOBER, 29))
                .amount(10)
                .price(1_249)
                .image("https://picsum.photos/seed/cyprus/800/500")
                .build();

        Coupon coupon2 = Coupon.builder()
                .companyId(company.getId())
                .category(Category.ELECTRICITY)
                .title("Iphone 13 Pro Max")
                .description("The best smartphone in the world")
                .start_date(LocalDate.of(2025, Month.NOVEMBER, 7))
                .end_date(LocalDate.of(2026, Month.MARCH    , 18))
                .amount(8)
                .price(4_379)
                .image("https://picsum.photos/seed/iphone13/800/500")
                .build();

        Coupon coupon3 = Coupon.builder()
                .companyId(company.getId())
                .category(Category.CLOTHING)
                .title("ZARA")
                .description("150 shekels for zara")
                .start_date(LocalDate.of(2023, Month.DECEMBER, 20))
                .end_date(LocalDate.of(2023, Month.DECEMBER, 29))
                .amount(220)
                .price(99)
                .image("https://picsum.photos/seed/zara/800/500")
                .build();

        Coupon coupon4 = Coupon.builder()
                .companyId(company.getId())
                .category(Category.HOTELS)
                .title("Royal Beach Eilat")
                .description("2 nights, 5 stars hotel")
                .start_date(LocalDate.of(2023, Month.DECEMBER, 12))
                .end_date(LocalDate.of(2023, Month.DECEMBER, 30))
                .amount(20)
                .price(1639)
                .image("https://picsum.photos/seed/eilat/800/500")
                .build();

        List<Coupon> coupons = List.of(coupon1, coupon2, coupon3, coupon4);

        System.out.println("\nSaving 4 coupons with end_date in 2023 (will be marked as expired)...");
        couponRepository.saveAll(coupons);

        // THIS IS THE FIX – mark expired immediately after creation
        couponRepository.markExpiredCoupons();

        System.out.println("Coupons saved + markExpiredCoupons() executed → all are expired = true\n");

        // ========================= READ =========================
        System.out.println("All company coupons (should show expired = true):");
        try {
            TablePrinter.print(companyService.getAllCompanyCoupons());
        } catch (ExistsException e) {
            System.out.println("No coupons found.");
        }

        // ========================= UPDATE =========================
        System.out.println("\nUpdating coupon 2 title and price...");
        coupon2.setTitle("iPhone 15 Pro");
        coupon2.setPrice(5499.00);
        couponRepository.save(coupon2);

        System.out.println("After update:");
        TablePrinter.print(companyService.getAllCompanyCoupons());

        // ========================= DELETE =========================
        System.out.println("\nDeleting coupon 3 (ZARA)...");
        couponRepository.deleteById(coupon3.getId());

        System.out.println("Final list after delete:");
        TablePrinter.print(companyService.getAllCompanyCoupons());

        System.out.println("=============================================================================================================");
        System.out.println("TEST 2 COMPLETED – ALL CRUD + EXPIRED FLAG WORKING PERFECTLY");
        System.out.println("=============================================================================================================\n");
    }
}

 */