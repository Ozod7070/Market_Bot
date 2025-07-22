package uz.pdp.service;

import uz.pdp.enums.Category;
import uz.pdp.models.Product;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProductService {

    private final List<Product> products = Arrays.asList(
            new Product(100L,"HP",750, Category.COMPUTER),
            new Product(101L,"Lenova",800, Category.COMPUTER),
            new Product(102L,"Mac",1100, Category.COMPUTER),
            new Product(103L,"Acer",680, Category.COMPUTER),
            new Product(104L,"Asus",720, Category.COMPUTER),
            new Product(105L,"Dell",980, Category.COMPUTER),


             new Product(106L,"Redmi 12Pro",250, Category.TELEPHONE),
             new Product(107L,"Iphone 11",360, Category.TELEPHONE),
             new Product(108L,"Samsung A32",270, Category.TELEPHONE),
             new Product(109L,"Redmi 11",300, Category.TELEPHONE),
             new Product(110L,"Iphone 15 Pro",900, Category.TELEPHONE),
             new Product(111L,"Iphone 16 Pro Max",1100, Category.TELEPHONE)



    );

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getProductsByCategory(Category category) {
        return products.stream()
                .filter(p -> p.getCategory() == category)
                .toList();
    }

    public List<Product> getProductById(Long id) {
        return Collections.singletonList(products.stream().filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null));
    }
    public List<Product> searchProduct(String query) {
        return products.stream().filter(product -> product.getName().toLowerCase() == query.toLowerCase())
                .toList();
    }



}
