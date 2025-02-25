package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        save(product);
        return product;
    }
//nada
    public ArrayList<Product> getProducts() {
        return null;
    }
//nada
    public Product getProductById(UUID productId) {
        return null;
    }
//ganna
    public Product updateProduct(UUID productId, String newName, double newPrice) {
        return null;
    }
    //ganna
    public void applyDiscount(double discount, ArrayList<UUID> productIds) {

    }
    //ganna
    public void deleteProductById(UUID productId) {

    }

}