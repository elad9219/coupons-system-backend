package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import com.jb.spring_coupons_project.util.TablePrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeding implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        if (companyRepository.count() > 0) {
            System.out.println(">> Data exists. Printing...");
            printData();
            return;
        }

        System.out.println(">> Generating HIGH QUALITY Data...");

        // --- 1. Companies (Real Names) ---
        List<Company> companies = new ArrayList<>();
        String[] companyNames = {"Sony", "CocaCola", "KSP", "Ivory", "Castro", "Renuar", "Fox", "Nike", "Adidas", "ElAl", "Isrotel", "Fattal", "SuperPharm", "Be", "Shufersal"};

        for (String name : companyNames) {
            companies.add(Company.builder()
                    .name(name)
                    .email(name.toLowerCase() + "@contact.com")
                    .password("1234")
                    .build());
        }
        companyRepository.saveAll(companies);
        System.out.println(">> Companies created: " + companies.size());

        // --- 2. Customers (Real Names) ---
        List<Customer> customers = new ArrayList<>();
        String[] firstNames = {"Kobi", "Dana", "Ron", "Gal", "Tal", "Ben", "Shir", "May", "Noy", "Dan", "Omer", "Adi", "Yossi", "Sarah", "David"};
        String[] lastNames = {"Shasha", "Levi", "Cohen", "Mizrachi", "Peretz", "Biton", "Dahan", "Katz", "Azulay", "Gabay"};
        Random random = new Random();

        // Main Demo User
        Customer demoKobi = Customer.builder().first_name("Kobi").last_name("Shasha").email("kobi@gmail.com").password("1234").build();
        customers.add(demoKobi);

        for (int i = 0; i < 20; i++) {
            String fName = firstNames[random.nextInt(firstNames.length)];
            String lName = lastNames[random.nextInt(lastNames.length)];
            customers.add(Customer.builder()
                    .first_name(fName)
                    .last_name(lName)
                    .email(fName.toLowerCase() + "." + lName.toLowerCase() + i + "@gmail.com")
                    .password("1234")
                    .build());
        }
        customerRepository.saveAll(customers);
        System.out.println(">> Customers created: " + customers.size());

        // --- 3. Coupons (Realistic Content) ---
        List<Coupon> coupons = new ArrayList<>();

        // Data Pools for realistic generation
        String[][] foodCoupons = {
                {"ארוחת המבורגר זוגית", "2 המבורגרים + צ'יפס + שתייה. תקף בכל הסניפים.", "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=800&q=80"},
                {"פיצה משפחתית + תוספת", "פיצה ענקית XL עם תוספת לבחירה ושתייה גדולה.", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80"},
                {"סושי ללילה", "קומבינציית סושי 24 יחידות כולל רולים מיוחדים.", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=800&q=80"}
        };

        String[][] electCoupons = {
                {"PlayStation 5 Slim", "הקונסולה החדשה והדקה, כולל שלט נוסף ומשחק.", "https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?auto=format&fit=crop&w=800&q=80"},
                {"MacBook Pro M3", "מחשב נייד חזק במיוחד לעבודה וגרפיקה.", "https://images.unsplash.com/photo-1517336714731-489689fd1ca4?auto=format&fit=crop&w=800&q=80"},
                {"אייפון 15 פרו", "הסמארטפון המתקדם בעולם עם מצלמת 48MP.", "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=800&q=80"}, // Fixed link
                {"אוזניות Sony XM5", "ביטול רעשים אקטיבי וסאונד איכותי.", "https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=800&q=80"}
        };

        String[][] vacationCoupons = {
                {"חופשה באילת", "סופ\"ש במלון 5 כוכבים, הכל כלול.", "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80"},
                {"טיסה לניו יורק", "טיסה ישירה במחיר מיוחד למזמינים מראש.", "https://images.unsplash.com/photo-1496442226666-8d4a0e62e6e9?auto=format&fit=crop&w=800&q=80"}, // NYC Image
                {"צימר בצפון", "לילה בבקתת עץ רומנטית עם ג'קוזי וארוחת בוקר.", "https://images.unsplash.com/photo-1587061949409-02df41d5e562?auto=format&fit=crop&w=800&q=80"}
        };

        // Create 50 coupons mixing these pools
        for (int i = 0; i < 50; i++) {
            Company comp = companies.get(random.nextInt(companies.size()));
            Category cat = Category.values()[random.nextInt(Category.values().length - 1)]; // Skip ALL

            String title, desc, img;
            double price;

            if (cat == Category.FOOD || cat == Category.RESTAURANT) {
                String[] data = foodCoupons[random.nextInt(foodCoupons.length)];
                title = data[0]; desc = data[1]; img = data[2];
                price = 50 + random.nextInt(150);
            } else if (cat == Category.ELECTRICITY) {
                String[] data = electCoupons[random.nextInt(electCoupons.length)];
                title = data[0]; desc = data[1]; img = data[2];
                price = 1000 + random.nextInt(5000);
            } else if (cat == Category.VACATION || cat == Category.HOTELS) {
                String[] data = vacationCoupons[random.nextInt(vacationCoupons.length)];
                title = data[0]; desc = data[1]; img = data[2];
                price = 1500 + random.nextInt(3000);
            } else {
                title = "שובר מתנה " + comp.getName();
                desc = "שובר לרכישה ברשת " + comp.getName() + " בשווי המלא.";
                img = "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800&q=80"; // Gift card
                price = 100 + random.nextInt(400);
            }

            coupons.add(Coupon.builder()
                    .companyId(comp.getId())
                    .category(cat)
                    .title(title)
                    .description(desc)
                    .start_date(LocalDate.now().minusDays(random.nextInt(20)))
                    .end_date(LocalDate.now().plusDays(10 + random.nextInt(90)))
                    .amount(5 + random.nextInt(50))
                    .price(price)
                    .image(img)
                    .expired(false)
                    .build());
        }

        couponRepository.saveAll(coupons);

        // Seeding Purchase for Kobi (to test disabled button)
        Coupon coupon1 = coupons.get(0);
        Coupon coupon2 = coupons.get(1);

        couponRepository.addPurchasedCoupon(demoKobi.getId(), coupon1.getId());
        couponRepository.addPurchasedCoupon(demoKobi.getId(), coupon2.getId());

        // Decrement amounts
        coupon1.setAmount(coupon1.getAmount() - 1);
        coupon2.setAmount(coupon2.getAmount() - 1);
        couponRepository.saveAll(Arrays.asList(coupon1, coupon2));

        System.out.println(">> DATA SEEDED SUCCESSFULLY");
        printData();
    }

    private void printData() {
        System.out.println("\n\n---------------- DATABASE STATE ----------------");
        System.out.println("Companies:");
        TablePrinter.print(companyRepository.findAll());
        System.out.println("\nCustomers:");
        TablePrinter.print(customerRepository.findAll());
        System.out.println("\nCoupons (First 10):");
        List<Coupon> allCoupons = couponRepository.findAll();
        TablePrinter.print(allCoupons.subList(0, Math.min(10, allCoupons.size())));
        System.out.println("------------------------------------------------\n\n");
    }
}