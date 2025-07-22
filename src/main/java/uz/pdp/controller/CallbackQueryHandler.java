package uz.pdp.controller;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.MarketBot;
import uz.pdp.enums.Category;
import uz.pdp.enums.UserState;
import uz.pdp.models.Order;
import uz.pdp.models.Product;
import uz.pdp.service.OrderService;
import uz.pdp.service.ProductService;
import uz.pdp.session.UserSession;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallbackQueryHandler {
    private final MarketBot bot;
    private final Map<Long, UserSession> sessions;
    private final ProductService productService;
    private final OrderService orderService;

    public CallbackQueryHandler(MarketBot bot, Map<Long, UserSession> sessions,
                                ProductService productService, OrderService orderService) {
        this.bot = bot;
        this.sessions = sessions;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void handleText(Long chatId, String text) {
        UserSession session = sessions.computeIfAbsent(chatId, k -> new UserSession(chatId));

        if (session.getState() == UserState.SEARCHING) {
            List<Product> results = productService.searchProduct(text);
            if (results.isEmpty()) {
                bot.sendMessage(chatId, "❌ Mahsulot topilmadi", createMainMenuKeyboard());
            } else {
                session.setLastSearchResults(results);
                session.setState(UserState.SEARCHING_RESULTS);
                showSearchResults(chatId, results);
            }
            return;
        }

        switch (text) {
            case "🏠 Bosh menu" -> showMainMenu(chatId);
            case "📦 Mahsulotlar" -> showCategories(chatId);
            case "📱 Telefon" -> showCategoryProducts(chatId, Category.TELEPHONE);
            case "Kompyuter" -> showCategoryProducts(chatId, Category.COMPUTER);
            case "🛒 Savatim" -> showCart(chatId);
            case "📋 Buyurtmalarim" -> showOrders(chatId);
            case "ℹ️ Biz haqimizda" -> showAbout(chatId);
            case "🔍 Qidirish" -> startSearch(chatId);
            default -> handleProductSelection(chatId, text);
        }
    }

    private void showMainMenu(Long chatId) {
        bot.sendMessage(chatId, "🏪 Bosh menu:", createMainMenuKeyboard());
    }

    private void showCategories(Long chatId) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("📱 Telefon"));
        row1.add(new KeyboardButton("Kompyuter"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("🏠 Bosh menu"));
        rows.add(row1);
        rows.add(row2);

        ReplyKeyboardMarkup kb = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
        bot.sendMessage(chatId, "📦 Kategoriyani tanlang:", kb);
    }

    private void showCategoryProducts(Long chatId, Category category) {
        List<Product> products = productService.getProductsByCategory(category);
        sessions.get(chatId).setLastCategoryProducts(products);

        List<KeyboardRow> rows = new ArrayList<>();
        if (products != null && !products.isEmpty()) {
            for (Product p : products) {
                KeyboardRow row = new KeyboardRow();
                row.add(new KeyboardButton(p.getName()));
                rows.add(row);
            }
        } else {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton("📭 Mahsulotlar mavjud emas"));
            rows.add(row);
        }
        KeyboardRow backRow = new KeyboardRow();
        backRow.add(new KeyboardButton("🏠 Bosh menu"));
        rows.add(backRow);

        ReplyKeyboardMarkup kb = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
        bot.sendMessage(chatId, "🛍 Mahsulotni tanlang:", kb);
    }

    private void handleProductSelection(Long chatId, String text) {
        UserSession session = sessions.get(chatId);
        List<Product> list = session.getState() == UserState.SEARCHING_RESULTS
                ? session.getLastSearchResults()
                : session.getLastCategoryProducts();

        if (list != null) {
            for (Product p : list) {
                if (p.getName().equalsIgnoreCase(text.trim())) {
                    session.addToCart(p);
                    bot.sendMessage(chatId, "✅ " + p.getName() + " savatga qo‘shildi!", createMainMenuKeyboard());
                    session.setState(UserState.MAIN_MENU);
                    return;
                }
            }
        }
        bot.sendMessage(chatId, "❌ Mahsulot topilmadi!", createMainMenuKeyboard());
    }

    private void showCart(Long chatId) {
        UserSession session = sessions.get(chatId);
        List<Product> cart = session.getCart();
        if (cart == null || cart.isEmpty()) {
            bot.sendMessage(chatId, "🛒 Savat bo‘sh!", createMainMenuKeyboard());
            return;
        }

        StringBuilder sb = new StringBuilder("🛒 Savatingiz:\n\n");
        cart.forEach(p -> sb.append("• ").append(p.getName()).append(" - ").append(p.getPrice()).append(" $\n"));
        sb.append("\n💰 Jami: ").append(cart.stream().mapToDouble(Product::getPrice).sum()).append(" $");
        bot.sendMessage(chatId, sb.toString(), createMainMenuKeyboard());
    }

    private void showOrders(Long chatId) {
        List<Order> orders = orderService.getUserOrders(chatId);
        if (orders == null || orders.isEmpty()) {
            bot.sendMessage(chatId, "📋 Buyurtmalar topilmadi!", createMainMenuKeyboard());
            return;
        }
        StringBuilder sb = new StringBuilder("📋 Buyurtmalaringiz:\n\n");
        orders.forEach(o -> sb.append("🆔 #").append(o.getId()).append("\n")
                .append("📦 ").append(o.getProductName()).append("\n")
                .append("📅 ").append(o.getOrderDate()).append("\n")
                .append("🚚 ").append(o.getStatus().getDescription()).append("\n\n"));
        bot.sendMessage(chatId, sb.toString(), createMainMenuKeyboard());
    }

    private void showAbout(Long chatId) {
        String about = "ℹ️ Biz haqimizda\n\n🏪 O‘zbekistondagi eng yaxshi texnologiya do‘koni!\n"
                + "📱 Telefon va Kompyuterlar\n\n🚚 Tez yetkazib berish\n💯 Sifat kafolati\n🎧 24/7 qo‘llab-quvvatlash\n\n☎️ Tel: 95‑636‑02‑10";
        bot.sendMessage(chatId, about, createMainMenuKeyboard());
    }

    private void startSearch(Long chatId) {
        UserSession session = sessions.computeIfAbsent(chatId, k -> new UserSession(chatId));
        session.setState(UserState.SEARCHING);

        System.out.println("startSearch chaqirildi. UserState: " + session.getState());

        bot.sendMessage(chatId, "🔍 Qidiruvni boshlang. Mahsulot nomini yozing:", createMainMenuKeyboard());
    }


    private void showSearchResults(Long chatId, List<Product> results) {
        sessions.get(chatId).setState(UserState.SEARCHING_RESULTS);
        List<KeyboardRow> rows = new ArrayList<>();
        results.forEach(p -> {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(p.getName()));
            rows.add(row);
        });
        KeyboardRow backRow = new KeyboardRow();
        backRow.add(new KeyboardButton("🏠 Bosh menu"));
        rows.add(backRow);

        ReplyKeyboardMarkup kb = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
        bot.sendMessage(chatId, "🔍 Qidiruv natijalari:", kb);
    }

    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow r1 = new KeyboardRow();
        r1.add(new KeyboardButton("📦 Mahsulotlar"));
        r1.add(new KeyboardButton("🛒 Savatim"));

        KeyboardRow r2 = new KeyboardRow();
        r2.add(new KeyboardButton("📋 Buyurtmalarim"));
        r2.add(new KeyboardButton("ℹ️ Biz haqimizda"));

        KeyboardRow r3 = new KeyboardRow();
        r3.add(new KeyboardButton("🔍 Qidirish"));
        r3.add(new KeyboardButton("🏠 Bosh menu"));

        rows.add(r1);
        rows.add(r2);
        rows.add(r3);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}
