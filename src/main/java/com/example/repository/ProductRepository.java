package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/products.json";

    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    public ProductRepository() {

    }

    public Product addProduct(Product product) {
        ArrayList<Product> products = getProducts();

        if(products.contains(product)){
            throw new IllegalArgumentException("Product already exists");
        }

        save(product);
        return product;
    }
//nada
    public ArrayList<Product> getProducts() {
        ArrayList<Product> products = findAll();
        return products;
    }
//nada
    public Product getProductById(UUID productId) {
        Product product = getProducts()
                .stream()
                .filter(prod -> prod.getId().equals(productId))
                .findFirst().orElse(null);

        return product;
    }

//ganna
    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> products = getProducts();
        boolean result = products.stream().anyMatch(product -> product.getId().equals(productId));

        if (!result) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        products.forEach(product -> {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
            }
        });
        saveAll(products);
        return getProductById(productId);
    }

    //ganna
    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        if (discount < 0 || discount > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid discount value. Must be between 0 and 100.");
        }

        ArrayList<Product> products = getProducts();
        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount / 100); // Apply the discount
                product.setPrice(newPrice);
            }
        }
        saveAll(products);
    }

    //ganna
    public void deleteProductById(UUID productId) {
        ArrayList<Product> products = getProducts();
        boolean result = products.removeIf(product -> product.getId().equals(productId));

        if (!result) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        saveAll(products);
    }

}