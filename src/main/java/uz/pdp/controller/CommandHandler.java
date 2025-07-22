package uz.pdp.controller;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.MarketBot;
import uz.pdp.enums.Category;
import uz.pdp.enums.UserState;
import uz.pdp.models.Product;
import uz.pdp.service.OrderService;
import uz.pdp.service.ProductService;
import uz.pdp.session.UserSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    private final MarketBot bot;
    private final Map<Long, UserSession> sessions;
    private final ProductService productService;

    public CommandHandler(MarketBot bot, Map<Long, UserSession> sessions, ProductService productService, OrderService orderService) {
        this.bot = bot;
        this.sessions = sessions;
        this.productService = productService;
    }

    public void handleCommand(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();

        UserSession session = sessions.computeIfAbsent(chatId, UserSession::new);

        if (text.equals("/start") || text.equals("🏠 Bosh menu")) {
            session.setState(UserState.MAIN_MENU);
            bot.sendMessage(chatId, "Xush Kelibsiz", createMainMenuKeyboard());

        } else if (text.equals("Mahsulotlar")) {
            showCategories(chatId);
            session.setState(UserState.MAIN_MENU);

        } else if (text.equals("Telefon")) {
            showCategoryProducts(chatId, Category.TELEPHONE);

        } else if (text.equals("Kompyuter")) {
            showCategoryProducts(chatId, Category.COMPUTER);

        } else if (session.getState() == UserState.SEARCHING) {
            handleSearch(chatId, text);

        } else {
            bot.sendMessage(chatId, "Nomalum Buyruq", createMainMenuKeyboard());
        }
    }

    private void showCategories(Long chatId) {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Telefon"));
        row1.add(new KeyboardButton("Kompyuter"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("🏠 Bosh menu"));

        rows.add(row1);
        rows.add(row2);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();

        bot.sendMessage(chatId, "Kategoriyani tanlang:", keyboard);
    }

    private void showCategoryProducts(Long chatId, Category category) {
        List<Product> products = productService.getProductsByCategory(category);
        if (products.isEmpty()) {
            bot.sendMessage(chatId, "❌ Mahsulotlar topilmadi", createMainMenuKeyboard());
            return;
        }

        UserSession session = sessions.get(chatId);
        session.setLastCategoryProducts(products);

        List<KeyboardRow> rows = new ArrayList<>();
        for (Product product : products) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(product.getName()));
            rows.add(row);
        }

        KeyboardRow backRow = new KeyboardRow();
        backRow.add(new KeyboardButton("🏠 Bosh menu"));
        rows.add(backRow);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();

        bot.sendMessage(chatId, "Mahsulotni tanlang:", keyboard);
    }

    private void handleSearch(Long chatId, String text) {
        List<Product> results = productService.searchProduct(text);

        UserSession userSession = sessions.get(chatId);

        if (results.isEmpty()) {
            bot.sendMessage(chatId, "Mahsulot topilmadi", createMainMenuKeyboard());
            userSession.setState(UserState.MAIN_MENU);
        } else {
            userSession.setLastSearchResults(results);
            userSession.setState(UserState.SEARCHING_RESULTS);

            List<KeyboardRow> rows = new ArrayList<>();
            for (Product product : results) {
                KeyboardRow row = new KeyboardRow();
                row.add(new KeyboardButton(product.getName()));
                rows.add(row);
            }

            KeyboardRow backRow = new KeyboardRow();
            backRow.add(new KeyboardButton("Bosh menu"));
            rows.add(backRow);

            ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                    .keyboard(rows)
                    .resizeKeyboard(true)
                    .oneTimeKeyboard(false)
                    .build();

            bot.sendMessage(chatId, "Qidiruv natijalari:", replyKeyboardMarkup);
        }
    }

    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Mahsulotlar"));
        row1.add(new KeyboardButton("Savatim"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Buyurtmalarim"));
        row2.add(new KeyboardButton("Biz haqimizda"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Qidirish"));
        row3.add(new KeyboardButton("Bosh menu"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();
    }
}
