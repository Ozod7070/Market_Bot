package uz.pdp.enums;

public enum OrderStatus {

    PENDING("⏳ Kutilmoqda"),
    SHIPPED("🚚 Yo'lda"),
    DELIVERED("✅ Yetkazildi");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
