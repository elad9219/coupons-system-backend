package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.repository.CompanyRepository;
import com.jb.spring_coupons_project.repository.CouponRepository;
import com.jb.spring_coupons_project.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeding implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(DataSeeding.class);

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (companyRepository.count() > 0) {
            log.info("Demo data already exists. Skipping seeding.");
            return;
        }

        log.info("Generating demo data...");

        Random random = new Random();

        List<Company> companies = new ArrayList<>();

        String[] companyNames = {
                "Sony", "CocaCola", "KSP", "Ivory",
                "Castro", "Renuar", "Fox", "Nike",
                "Adidas", "ElAl", "Isrotel", "Fattal",
                "SuperPharm", "Be", "Shufersal"
        };

        for (String name : companyNames) {
            companies.add(Company.builder()
                    .name(name)
                    .email(name.toLowerCase() + "@contact.com")
                    .password("1234")
                    .build());
        }

        List<Company> savedCompanies =
                companyRepository.saveAll(companies);

        List<Customer> customers = new ArrayList<>();

        String[] firstNames = {
                "Kobi", "Dana", "Ron", "Gal", "Tal",
                "Ben", "Shir", "May", "Noy", "Dan",
                "Omer", "Adi", "Yossi", "Sarah", "David"
        };

        String[] lastNames = {
                "Shasha", "Levi", "Cohen", "Mizrachi",
                "Peretz", "Biton", "Dahan", "Katz",
                "Azulay", "Gabay"
        };

        customers.add(Customer.builder()
                .first_name("Kobi")
                .last_name("Shasha")
                .email("kobi@gmail.com")
                .password("1234")
                .build());

        for (int i = 0; i < 20; i++) {
            String firstName =
                    firstNames[random.nextInt(firstNames.length)];

            String lastName =
                    lastNames[random.nextInt(lastNames.length)];

            customers.add(Customer.builder()
                    .first_name(firstName)
                    .last_name(lastName)
                    .email(
                            firstName.toLowerCase() + "." +
                                    lastName.toLowerCase() + i +
                                    "@gmail.com"
                    )
                    .password("1234")
                    .build());
        }

        List<Customer> savedCustomers =
                customerRepository.saveAll(customers);

        Customer demoCustomer = savedCustomers.get(0);

        String[][] foodCoupons = {
                {
                        "ארוחת המבורגר זוגית",
                        "2 המבורגרים + צ'יפס + שתייה. תקף בכל הסניפים.",
                        "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=800&q=80"
                },
                {
                        "פיצה משפחתית + תוספת",
                        "פיצה ענקית XL עם תוספת לבחירה ושתייה גדולה.",
                        "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80"
                },
                {
                        "סושי ללילה",
                        "קומבינציית סושי 24 יחידות כולל רולים מיוחדים.",
                        "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=800&q=80"
                }
        };

        String[][] electricCoupons = {
                {
                        "PlayStation 5 Slim",
                        "הקונסולה החדשה והדקה, כולל שלט נוסף ומשחק.",
                        "https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?auto=format&fit=crop&w=800&q=80"
                },
                {
                        "MacBook Pro M3",
                        "מחשב נייד חזק במיוחד לעבודה וגרפיקה.",
                        "https://images.unsplash.com/photo-1625242504068-6bcff3a603da?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                },
                {
                        "אייפון 15 פרו",
                        "הסמארטפון המתקדם בעולם עם מצלמת 48MP.",
                        "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=800&q=80"
                },
                {
                        "אוזניות Sony XM5",
                        "ביטול רעשים אקטיבי וסאונד איכותי.",
                        "https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=800&q=80"
                }
        };

        String[][] vacationCoupons = {
                {
                        "חופשה באילת",
                        "סופ\"ש במלון 5 כוכבים, הכל כלול.",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80"
                },
                {
                        "טיסה לניו יורק",
                        "טיסה ישירה במחיר מיוחד למזמינים מראש.",
                        "https://images.unsplash.com/photo-1587019158091-1a103c5dd17f?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                },
                {
                        "צימר בצפון",
                        "לילה בבקתת עץ רומנטית עם ג'קוזי וארוחת בוקר.",
                        "https://images.unsplash.com/photo-1587061949409-02df41d5e562?auto=format&fit=crop&w=800&q=80"
                }
        };

        List<Coupon> coupons = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Company company = savedCompanies.get(
                    random.nextInt(savedCompanies.size())
            );

            Category category = Category.values()[
                    random.nextInt(Category.values().length)
                    ];

            String title;
            String description;
            String image;
            double price;

            if (category == Category.FOOD ||
                    category == Category.RESTAURANT) {

                String[] data = foodCoupons[
                        random.nextInt(foodCoupons.length)
                        ];

                title = data[0];
                description = data[1];
                image = data[2];
                price = 50 + random.nextInt(150);

            } else if (category == Category.ELECTRICITY) {

                String[] data = electricCoupons[
                        random.nextInt(electricCoupons.length)
                        ];

                title = data[0];
                description = data[1];
                image = data[2];
                price = 1000 + random.nextInt(5000);

            } else if (category == Category.VACATION ||
                    category == Category.HOTELS) {

                String[] data = vacationCoupons[
                        random.nextInt(vacationCoupons.length)
                        ];

                title = data[0];
                description = data[1];
                image = data[2];
                price = 1500 + random.nextInt(3000);

            } else {

                title = "שובר מתנה " + company.getName();
                description =
                        "שובר לרכישה ברשת " +
                                company.getName() + " בשווי המלא.";

                image = "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=800&q=80";
                price = 100 + random.nextInt(400);
            }

            coupons.add(Coupon.builder()
                    .companyId(company.getId())
                    .category(category)
                    .title(title)
                    .description(description)
                    .start_date(
                            LocalDate.now().minusDays(
                                    random.nextInt(20)
                            )
                    )
                    .end_date(
                            LocalDate.now().plusDays(
                                    10 + random.nextInt(90)
                            )
                    )
                    .amount(5 + random.nextInt(50))
                    .price(price)
                    .image(image)
                    .expired(false)
                    .build());
        }

        List<Coupon> savedCoupons =
                couponRepository.saveAll(coupons);

        Coupon firstCoupon = savedCoupons.get(0);
        Coupon secondCoupon = savedCoupons.get(1);

        // Update stock before inserting purchase relationships.
        // A subsequent entity merge could otherwise overwrite
        // relationships inserted directly into the join table.
        firstCoupon.setAmount(firstCoupon.getAmount() - 1);
        secondCoupon.setAmount(secondCoupon.getAmount() - 1);

        couponRepository.saveAll(
                Arrays.asList(firstCoupon, secondCoupon)
        );

        // Insert the purchase relationships as the final writes.
        couponRepository.addPurchasedCoupon(
                demoCustomer.getId(),
                firstCoupon.getId()
        );

        couponRepository.addPurchasedCoupon(
                demoCustomer.getId(),
                secondCoupon.getId()
        );

        log.info(
                "Demo data seeded: {} companies, {} customers, {} coupons, 2 purchases",
                savedCompanies.size(),
                savedCustomers.size(),
                savedCoupons.size()
        );
    }
}