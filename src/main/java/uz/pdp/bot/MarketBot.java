package uz.pdp.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.controller.CallbackQueryHandler;
import uz.pdp.controller.CommandHandler;
import uz.pdp.service.OrderService;
import uz.pdp.service.ProductService;
import uz.pdp.session.UserSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketBot extends TelegramLongPollingBot {

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    private final CommandHandler commandHandler = new CommandHandler(this, sessions, productService,orderService);
    private final CallbackQueryHandler callbackHandler = new CallbackQueryHandler(this, sessions, productService, orderService);
    private static final String USER_NAME = "t.me/Market_77Bot";
    private static final String BOT_TOKEN = "7764923201:AAEJjeaH8-5Si3opp8jUAmm2g-UN8aKVjSg";
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if(text.startsWith("/")) {
                commandHandler.handleCommand(update.getMessage());
            }else {
                callbackHandler.handleText(chatId, text);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return USER_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}



