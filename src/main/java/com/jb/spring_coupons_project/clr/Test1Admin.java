package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.util.TablePrinter; // Make sure this import exists
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@Order(1)
@RequiredArgsConstructor
public class Test1Admin implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        if (companyRepository.count() > 0) {
            System.out.println(">> Data already exists. Skipping Initialization.");
            printData(); // Print existing data anyway
            return;
        }

        System.out.println(">> Initializing System Data (Hebrew)...");

        // 1. Create Companies
        Company sony = Company.builder().name("Sony").email("sony@sony.com").password("1234").build();
        Company cocaCola = Company.builder().name("Coca Cola").email("coke@cola.com").password("1234").build();
        Company elAl = Company.builder().name("El-Al").email("elal@fly.com").password("1234").build();
        Company superPharm = Company.builder().name("Super Pharm").email("super@pharm.com").password("1234").build();

        companyRepository.saveAll(Arrays.asList(sony, cocaCola, elAl, superPharm));
        System.out.println(">> Companies created.");

        // 2. Create Customers
        Customer kobi = Customer.builder().first_name("Kobi").last_name("Shasha").email("kobi@gmail.com").password("1234").build();
        Customer dana = Customer.builder().first_name("Dana").last_name("Cohen").email("dana@gmail.com").password("1234").build();

        customerRepository.saveAll(Arrays.asList(kobi, dana));
        System.out.println(">> Customers created.");

        // 3. Create Coupons
        Coupon ps5 = Coupon.builder().companyId(sony.getId()).category(Category.ELECTRICITY).title("פלייסטיישן 5").description("קונסולה + שלט נוסף מתנה").start_date(LocalDate.now().minusDays(1)).end_date(LocalDate.now().plusMonths(3)).amount(50).price(2200).image("https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?q=80&w=2070&auto=format&fit=crop").build();
        Coupon tv = Coupon.builder().companyId(sony.getId()).category(Category.ELECTRICITY).title("טלוויזיה 65 אינץ").description("מסך OLED חכם ומתקדם").start_date(LocalDate.now()).end_date(LocalDate.now().plusMonths(6)).amount(20).price(4500).image("https://images.unsplash.com/photo-1593784991095-a205069470b6?q=80&w=2070&auto=format&fit=crop").build();
        Coupon familyMeal = Coupon.builder().companyId(cocaCola.getId()).category(Category.FOOD).title("ארוחה משפחתית").description("2 פיצות + בקבוק קולה").start_date(LocalDate.now().minusDays(5)).end_date(LocalDate.now().plusWeeks(2)).amount(100).price(89.90).image("https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=2070&auto=format&fit=crop").build();
        Coupon flight = Coupon.builder().companyId(elAl.getId()).category(Category.VACATION).title("טיסה לניו יורק").description("טיסה ישירה כולל מזוודה").start_date(LocalDate.now()).end_date(LocalDate.now().plusMonths(1)).amount(10).price(2800).image("https://images.unsplash.com/photo-1500835556837-99ac94a94552?q=80&w=1974&auto=format&fit=crop").build();
        Coupon perfume = Coupon.builder().companyId(superPharm.getId()).category(Category.OTHER).title("בושם שאנל").description("בושם לאישה 100 מ\"ל").start_date(LocalDate.now()).end_date(LocalDate.now().plusWeeks(4)).amount(15).price(450).image("https://images.unsplash.com/photo-1541643600914-78b084683601?q=80&w=1904&auto=format&fit=crop").build();

        // Expired Coupons
        Coupon expiredHotel = Coupon.builder().companyId(elAl.getId()).category(Category.HOTELS).title("סופ\"ש באילת").description("חופשה במלון יוקרה").start_date(LocalDate.now().minusMonths(2)).end_date(LocalDate.now().minusDays(1)).amount(0).price(1200).image("https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?q=80&w=2049&auto=format&fit=crop").build();
        Coupon expiredBowling = Coupon.builder().companyId(cocaCola.getId()).category(Category.BOWLING).title("כרטיס לבאולינג").description("משחק ליחיד + נעליים").start_date(LocalDate.now().minusMonths(1)).end_date(LocalDate.now().minusDays(5)).amount(5).price(20).image("https://images.unsplash.com/photo-1538334468846-993c44db67f1?q=80&w=2070&auto=format&fit=crop").build();

        couponRepository.saveAll(Arrays.asList(ps5, tv, familyMeal, flight, perfume, expiredHotel, expiredBowling));

        try { couponRepository.markExpiredCoupons(); } catch (Exception e) {}

        // Seeding Purchase
        couponRepository.addPurchasedCoupon(kobi.getId(), ps5.getId());
        ps5.setAmount(ps5.getAmount() - 1);
        couponRepository.save(ps5);

        System.out.println(">> DATA INITIALIZATION COMPLETE");

        printData();
    }

    private void printData() {
        System.out.println("\n\n-----------------------------------------------------------------------------------------------------------------");
        System.out.println("   CURRENT DATABASE STATE");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        System.out.println("\n>>> COMPANIES LIST:");
        TablePrinter.print(companyRepository.findAll());

        System.out.println("\n>>> CUSTOMERS LIST:");
        TablePrinter.print(customerRepository.findAll());

        System.out.println("\n>>> COUPONS LIST:");
        TablePrinter.print(couponRepository.findAll());
        System.out.println("-----------------------------------------------------------------------------------------------------------------\n\n");
    }
}