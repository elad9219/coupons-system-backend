package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.*;
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

    /**
     * Exists & not exists tests (to see exceptions)
     */

    private final CompanyRepository companyRepository;
    private final CouponRepository couponRepository;

    private final CompanyService companyService;


    @Override
    public void run(String... args) throws Exception {

        Optional<Company> companyOpt = companyRepository.findById(3);
        if (!companyOpt.isPresent()) {
            System.out.println("\nCompany not found\n");
            return;
        }
        Company company = companyOpt.get();
        companyService.login(company.getEmail(), company.getPassword());
        System.out.println("-----------------------------------------------------------------------------------------------------------------");


        //CREATE
        Coupon coupon1 = Coupon.builder()
                .companyId(company.getId())
                .category(Category.VACATION)
                .title("Larnaca, Cyprus")
                .description("4 nights in Larnaca, 3 stars hotel")
                .start_date(LocalDate.of(2023, Month.OCTOBER, 22))
                .end_date(LocalDate.of(2023, Month.OCTOBER, 29))
                .amount(10)
                .price(1_249)
                .image("Image")
                .build();


        Coupon coupon2 = Coupon.builder()
                .companyId(company.getId())
                .category(Category.ELECTRICITY)
                .title("Iphone 13 Pro Max")
                .description("The best smartphone in the world")
                .start_date(LocalDate.of(2023, Month.NOVEMBER, 7))
                .end_date(LocalDate.of(2023, Month.NOVEMBER, 18))
                .amount(8)
                .price(4_379)
                .image("Image")
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
                .image("Image")
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
                .image("Image")
                .build();


        List<Coupon> couponList = List.of(coupon1, coupon2, coupon3, coupon4);
        System.out.println("\nsaving coupons\n");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");


        couponRepository.saveAll(couponList);


        //READ
        System.out.println();
        System.out.println("\nGet all company coupons: \n");
        try {
            TablePrinter.print(companyService.getAllCompanyCoupons());
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this company.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet one company coupon: \n");
        try {
            TablePrinter.print(companyService.getOneCompanyCoupon(4));
            TablePrinter.print(companyService.getOneCompanyCoupon(8));
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this ID.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet all company coupons by category: \n");
        try {
            TablePrinter.print(companyService.getAllCompanyCouponsByCategory(Category.ELECTRICITY));
            TablePrinter.print(companyService.getAllCompanyCouponsByCategory(Category.HOTELS));
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this category.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet all company coupons by maximum price: \n");
        try {
            TablePrinter.print(companyService.getAllCompanyCouponsByMaxPrice(1_700));
            TablePrinter.print(companyService.getAllCompanyCouponsByMaxPrice(15));
        } catch (ExistsException exception) {
            System.out.println("There are no coupons for this maximum price.");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("\nGet company details: \n");
        System.out.println(companyService.getCompanyDetails());
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println();



        //UPDATE
        Optional<Coupon> coup2Opt = couponRepository.findById(2);
        if (coup2Opt.isPresent()) {
            Coupon coup2 = coup2Opt.get();
            coup2.setPrice(4_279);
            couponRepository.save(coup2);
            System.out.println("\nUpdate coupon: \n");
            System.out.println("Coupon " + coup2.getTitle() + " updated");
        } else {
            System.out.println("\nCoupon not found for update\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");



        //DELETE
        Optional<Coupon> coupo1Opt = couponRepository.findById(1);
        System.out.println("\nDelete coupon: \n");
        if (coupo1Opt.isPresent()) {
            couponRepository.deleteById(1);
            System.out.println("\nCoupon deleted\n");
        } else {
            System.out.println("\nCoupon not found\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }
}