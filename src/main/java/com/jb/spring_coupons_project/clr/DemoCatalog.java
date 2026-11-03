package com.jb.spring_coupons_project.clr;

import com.jb.spring_coupons_project.beans.Category;
import com.jb.spring_coupons_project.beans.Company;
import com.jb.spring_coupons_project.beans.Coupon;
import com.jb.spring_coupons_project.beans.Customer;
import com.jb.spring_coupons_project.security.PasswordCodec;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.jb.spring_coupons_project.beans.Category.*;

final class DemoCatalog {

    private DemoCatalog() {
    }

    private static final List<String> COMPANY_NAMES = List.of(
            "Sony", "TechPoint", "StyleHouse",
            "UrbanWear", "FreshMarket", "BellaKitchen",
            "TokyoTable", "BlueHorizon", "NorthCabins",
            "CitySpa", "AdventurePark", "StrikeZone",
            "SkylineTravel", "BookCorner", "CreativeStudio"
    );

    private static final List<String> CUSTOMER_NAMES = List.of(
            "Kobi Shasha", "Dana Levi", "Ron Cohen",
            "Gal Mizrachi", "Tal Peretz", "Ben Biton",
            "Shir Dahan", "May Katz", "Noy Azulay",
            "Dan Gabay", "Omer Levi", "Adi Cohen",
            "Yossi Mizrachi", "Sarah Peretz", "David Katz",
            "Noa Shalev", "Yuval Barak", "Maya Rosen",
            "Eitan Sharon", "Lior Aviv", "Tamar Mor"
    );

    private record Offer(
            int companyIndex, Category category, String title,
            String description, double price, int stock,
            int startOffset, String image) {
    }

    private static String photo(String id) {
        return "https://images.unsplash.com/photo-" + id +
                "?auto=format&fit=crop&w=800&q=80";
    }

    private static final List<Offer> OFFERS = List.of(
            new Offer(0, ELECTRICITY, "אוזניות אלחוטיות",
                    "אוזניות קשת עם ביטול רעשים, נרתיק נשיאה ואחריות לשנה.",
                    849, 40, -14, photo("1618366712010-f4ae9c647dcb")),
            new Offer(0, ELECTRICITY, "קונסולת משחק ביתית",
                    "קונסולת משחק עם שלט אלחוטי. איסוף מהסניף בתיאום מראש.",
                    2190, 24, -10, photo("1606144042614-b2417e99c4e3")),
            new Offer(0, ELECTRICITY, "רמקול נייד",
                    "רמקול אלחוטי קומפקטי עם חיבור Bluetooth וכבל טעינה.",
                    349, 60, -7, photo("1608043152269-423dbba4e7e1")),
            new Offer(0, ELECTRICITY, "מצלמה ליוצרים",
                    "ערכת צילום הכוללת מצלמה, עדשה ותיק. ההטבה תיפתח בשבוע הבא.",
                    2690, 12, 7, photo("1516035069371-29a1b244cc32")),
            new Offer(1, ELECTRICITY, "מחשב נייד לעבודה",
                    "מחשב נייד לעבודה וללימודים, עם מטען ואחריות לשנה.",
                    3290, 18, -14, photo("1496181133206-80ce9b88a853")),
            new Offer(1, ELECTRICITY, "מקלדת אלחוטית",
                    "מקלדת קומפקטית לשולחן העבודה, עם חיבור אלחוטי למחשב.",
                    249, 75, -8, photo("1587829741301-dc798b83add3")),
            new Offer(1, ELECTRICITY, "עכבר לעבודה ולמשחק",
                    "עכבר אופטי ארגונומי עם כפתורים הניתנים להתאמה.",
                    159, 90, -6, photo("1527814050087-3793815479db")),
            new Offer(1, ELECTRICITY, "שעון חכם יומיומי",
                    "שעון חכם עם מעקב פעילות והתראות. המלאי להטבה זו אזל.",
                    499, 0, -12, photo("1523275335684-37898b6baf30")),
            new Offer(2, CLOTHING, "חולצת כותנה קלאסית",
                    "חולצת כותנה בגזרה ישרה. בחירת צבע ומידה לפי מלאי הסניף.",
                    89, 150, -14, photo("1521572163474-6864f9cf17ab")),
            new Offer(2, CLOTHING, "ג׳ינס ליום יום",
                    "מכנסי ג׳ינס בגזרה נוחה. מדידה והחלפת מידה בסניף.",
                    229, 85, -9, photo("1542272604-787c3835535d")),
            new Offer(2, CLOTHING, "מעיל לעונת המעבר",
                    "מעיל קל עם כיסים וסגירת כפתורים, במבחר מידות.",
                    319, 35, -5, photo("1544022613-e87ca75a784a")),
            new Offer(2, CLOTHING, "נעלי ספורט אדומות",
                    "נעלי ספורט ליום יום. בחירת מידה ומדידה בסניף.",
                    349, 45, -11, photo("1542291026-7eec264c27ff")),
            new Offer(3, CLOTHING, "תיק גב עירוני",
                    "תיק גב עם תא מרופד למחשב וכיסים לציוד אישי.",
                    179, 55, -14, photo("1553062407-98eeb64c6a62")),
            new Offer(3, CLOTHING, "מעיל עירוני קל",
                    "מעיל בגזרה יומיומית. בחירת מידה לפי מלאי הסניף.",
                    279, 28, -8, photo("1544022613-e87ca75a784a")),
            new Offer(3, CLOTHING, "מארז חולצות בסיס",
                    "שלוש חולצות כותנה בצבעים לבחירה. המלאי להטבה זו אזל.",
                    149, 0, -7, photo("1521572163474-6864f9cf17ab")),
            new Offer(4, FOOD, "מארז קפה קלוי",
                    "שתי שקיות פולי קפה, 250 גרם כל אחת. בחירת דרגת טחינה באיסוף.",
                    89, 120, -14, photo("1447933601403-0c6688de566e")),
            new Offer(4, FOOD, "סל לחמי מחמצת",
                    "שני לחמי מחמצת טריים ולחמניות. הזמנה יום מראש.",
                    49, 60, -4, photo("1509440159596-0249088772ff")),
            new Offer(4, FOOD, "סל פירות העונה",
                    "סל פירות מגוון במשקל ארבעה קילוגרמים. ההרכב משתנה לפי העונה.",
                    119, 80, -9, photo("1619566636858-adf3ef46400b")),
            new Offer(4, FOOD, "מארז סלטים טריים",
                    "שלושה סלטים אישיים, עם רטבים באריזות נפרדות. איסוף באותו יום.",
                    69, 70, -3, photo("1540420773420-3366772f4999")),
            new Offer(5, RESTAURANT, "ארוחת המבורגר זוגית",
                    "שני המבורגרים, שתי תוספות ושתי שתיות. ישיבה במקום או איסוף.",
                    129, 100, -14, photo("1594212699903-ec8a3eca50f5")),
            new Offer(5, RESTAURANT, "פיצה משפחתית",
                    "פיצה משפחתית עם שתי תוספות ושתייה גדולה. הזמנה מראש.",
                    89, 120, -8, photo("1513104890138-7c749659a591")),
            new Offer(5, RESTAURANT, "ערב פסטה לשניים",
                    "שתי מנות פסטה לבחירה וסלט משותף. בהזמנת מקום מראש.",
                    139, 65, -6, photo("1551183053-bf91a1d81141")),
            new Offer(5, RESTAURANT, "ארוחת בוקר זוגית",
                    "שתי ארוחות בוקר עם לחם, ממרחים ושתייה חמה. עד השעה 12:00.",
                    119, 80, -5, photo("1533089860892-a7c6f0a88666")),
            new Offer(6, RESTAURANT, "קומבינציית סושי",
                    "מגש סושי של 24 יחידות, עם רטבים וג׳ינג׳ר. לאיסוף עצמי.",
                    149, 90, -14, photo("1579871494447-9811cf80d66c")),
            new Offer(6, RESTAURANT, "ראמן לשניים",
                    "שתי קערות ראמן עם ירקות ותוספת לבחירה. לישיבה במקום.",
                    119, 55, -7, photo("1569718212165-3a8278d5f624")),
            new Offer(6, RESTAURANT, "מגש סושי לאירוח",
                    "מגש של 48 יחידות סושי. יש להזמין לפחות שעתיים מראש.",
                    239, 30, -4, photo("1579871494447-9811cf80d66c")),
            new Offer(7, HOTELS, "חופשה זוגית ליד הים",
                    "שני לילות בחדר זוגי וארוחת בוקר. הזמנה מראש לפי זמינות.",
                    1890, 20, -14, photo("1582719508461-905c673771fd")),
            new Offer(7, HOTELS, "סוף שבוע במלון",
                    "שני לילות לזוג בסוף שבוע, עם ארוחת בוקר וגישה לבריכה.",
                    2490, 8, -9, photo("1582719508461-905c673771fd")),
            new Offer(7, HOTELS, "לילה עירוני באמצע שבוע",
                    "לילה בחדר זוגי באמצע השבוע, כולל ארוחת בוקר לזוג.",
                    590, 35, -5, photo("1611892440504-42a792e24d32")),
            new Offer(7, HOTELS, "חופשה משפחתית",
                    "שני לילות בחדר משפחתי לזוג ושני ילדים. בתיאום מראש.",
                    2990, 10, -8, photo("1611892440504-42a792e24d32")),
            new Offer(8, VACATION, "לילה בבקתת עץ",
                    "לילה לזוג בבקתת עץ עם פינת קפה. יש לתאם את מועד האירוח.",
                    990, 18, -14, photo("1587061949409-02df41d5e562")),
            new Offer(8, VACATION, "חופשה שקטה ביער",
                    "שני לילות לזוג בבקתה, כולל סל ארוחת בוקר בכל יום.",
                    1490, 3, -7, photo("1587061949409-02df41d5e562")),
            new Offer(8, VACATION, "קמפינג זוגי בטבע",
                    "לילה במתחם קמפינג מוסדר לשני מבוגרים. יש להביא אוהל וציוד אישי.",
                    249, 45, -3, photo("1504280390367-361c6d9f38f4")),
            new Offer(9, SPA, "עיסוי אישי",
                    "עיסוי של 50 דקות למבוגר אחד. בחירת טיפול בתיאום מראש.",
                    219, 55, -14, photo("1519823551278-64ac92734fb1")),
            new Offer(9, SPA, "יום ספא מרגיע",
                    "כניסה למתחם הספא ועיסוי של 30 דקות. למבוגר אחד.",
                    299, 35, -8, photo("1540555700478-4be289fbecef")),
            new Offer(9, SPA, "עיסוי זוגי",
                    "שני טיפולי עיסוי של 50 דקות בחדר זוגי. בתיאום מראש.",
                    449, 20, -6, photo("1519823551278-64ac92734fb1")),
            new Offer(9, SPA, "מארז טיפוח לבית",
                    "מארז טיפוח הכולל תחליב גוף, סבון ומגבת פנים. איסוף מהמתחם.",
                    129, 65, -4, photo("1540555700478-4be289fbecef")),
            new Offer(10, ATTRACTIONS, "סיור טבע מודרך",
                    "סיור הליכה קבוצתי של שעתיים. יש להגיע עם נעליים סגורות ומים.",
                    79, 50, -14, photo("1473448912268-2022ce9509d8")),
            new Offer(10, ATTRACTIONS, "טיול אופניים זוגי",
                    "השכרת שני זוגות אופניים וקסדות לשעתיים, במסלול מסומן.",
                    129, 25, -8, photo("1571068316344-75bc76f77890")),
            new Offer(10, ATTRACTIONS, "שיט קיאקים זוגי",
                    "שיט של כשעה בקיאק זוגי, כולל ציוד בטיחות ותדריך.",
                    189, 30, -7, photo("1620903669944-de50fbe78210")),
            new Offer(10, ATTRACTIONS, "בוקר משפחתי בטבע",
                    "סיור מודרך למשפחה של עד ארבע נפשות, עם עצירה לפיקניק עצמאי.",
                    119, 40, -5, photo("1473448912268-2022ce9509d8")),
            new Offer(11, BOWLING, "באולינג זוגי",
                    "משחק אחד לשני משתתפים, כולל השכרת נעליים. בהזמנת מסלול.",
                    89, 80, -14, photo("1538511059256-46e76f13f071")),
            new Offer(11, BOWLING, "באולינג למשפחה",
                    "משחק אחד לעד ארבעה משתתפים, כולל השכרת נעליים.",
                    159, 45, -8, photo("1723073250008-a8ca01b8fdcb")),
            new Offer(11, BOWLING, "ערב באולינג לחברים",
                    "שעת משחק במסלול לעד שישה משתתפים, כולל השכרת נעליים.",
                    299, 20, -4, photo("1538511059256-46e76f13f071")),
            new Offer(12, VACATION, "שובר לחופשה בחו״ל",
                    "שובר בסך 1,500 ש״ח להזמנת חופשה. ניתן למימוש בהזמנה אחת.",
                    1290, 25, -14, photo("1436491865332-7a61a109cc05")),
            new Offer(12, VACATION, "שובר לטיסה בארץ",
                    "שובר בסך 500 ש״ח להזמנת טיסה בארץ. המימוש ייפתח בעוד שבועיים.",
                    399, 40, 14, photo("1436491865332-7a61a109cc05")),
            new Offer(13, OTHER, "מארז ספרים לקריאה",
                    "שלושה ספרים ממבחר החנות. ניתן לבחור את הכותרים בזמן האיסוף.",
                    129, 90, -14, photo("1507842217343-583bb7270b66")),
            new Offer(13, OTHER, "מפגש מועדון קריאה",
                    "מפגש קבוצתי בן 90 דקות בהנחיית מנחה. ההרשמה למועד מראש.",
                    79, 5, -6, photo("1507842217343-583bb7270b66")),
            new Offer(14, OTHER, "סדנת קדרות אישית",
                    "סדנה בת שעתיים הכוללת חומרים, הדרכה ושריפת כלי אחד.",
                    189, 24, -14, photo("1493106641515-6b5631de4bb9")),
            new Offer(14, OTHER, "סדנת ציור בצבע",
                    "סדנת ציור בת שעתיים, עם קנבס וצבעים. אין צורך בניסיון קודם.",
                    149, 30, -5, photo("1460661419201-fd4cecdf8a8b"))
    );

    static List<Company> companies() {
        List<Company> companies = new ArrayList<>();
        for (String name : COMPANY_NAMES) {
            String email = name.equals("Sony")
                    ? "sony@contact.com"
                    : name.toLowerCase(Locale.ROOT) + "@example.com";
            companies.add(Company.builder()
                    .name(name)
                    .email(email)
                    .password(PasswordCodec.encode("1234"))
                    .build());
        }
        return companies;
    }

    static List<Customer> customers() {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < CUSTOMER_NAMES.size(); i++) {
            String[] name = CUSTOMER_NAMES.get(i).split(" ", 2);
            String email = i == 0 ? "kobi@gmail.com"
                    : (name[0] + "." + name[1]).toLowerCase(Locale.ROOT)
                    + "@example.com";
            customers.add(Customer.builder()
                    .first_name(name[0])
                    .last_name(name[1])
                    .email(email)
                    .password(PasswordCodec.encode("1234"))
                    .build());
        }
        return customers;
    }

    static List<Coupon> coupons(List<Company> companies, LocalDate today) {
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < OFFERS.size(); i++) {
            Offer offer = OFFERS.get(i);
            coupons.add(Coupon.builder()
                    .companyId(companies.get(offer.companyIndex()).getId())
                    .category(offer.category())
                    .title(offer.title())
                    .description(offer.description())
                    .price(offer.price())
                    .amount(offer.stock())
                    .start_date(today.plusDays(offer.startOffset()))
                    .end_date(today.plusDays(90 + (i % 5) * 15L))
                    .image(offer.image())
                    .expired(false)
                    .build());
        }
        return coupons;
    }
}