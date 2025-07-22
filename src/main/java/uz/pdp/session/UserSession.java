package uz.pdp.session;



import uz.pdp.enums.UserState;
import uz.pdp.models.Product;

import java.util.ArrayList;
import java.util.List;



public class UserSession {
    private final Long chatId;
    private UserState state;
    private final List<Product> cart;
    private List<Product> lastCategoryProducts = new ArrayList<>();
    private List<Product> lastSearchResults = new ArrayList<>();

    public UserSession(Long chatId) {
        this.chatId = chatId;
        this.state = UserState.MAIN_MENU;
        this.cart = new ArrayList<>();
    }

    public void addToCart(Product product) {
        this.cart.add(product);
    }

    public void clearCart() {
        this.cart.clear();
    }

    public Long getChatId() {
        return chatId;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public List<Product> getCart() {
        return cart;
    }

    public List<Product> getLastCategoryProducts() {
        return lastCategoryProducts;
    }

    public void setLastCategoryProducts(List<Product> lastCategoryProducts) {
        this.lastCategoryProducts = lastCategoryProducts;
    }

    public List<Product> getLastSearchResults() {
        return lastSearchResults;
    }

    public void setLastSearchResults(List<Product> lastSearchResults) {
        this.lastSearchResults = lastSearchResults;
    }
}




