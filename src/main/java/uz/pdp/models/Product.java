package uz.pdp.models;


import uz.pdp.enums.Category;



public class Product {
    private final Long id;
    private final String name;
    private final double price;
    private final Category category;

    public Product(Long id, String name, double price, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }
}
