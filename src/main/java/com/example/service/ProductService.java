package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        if(product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        return productRepository.addProduct(product);
    }

    public ArrayList<Product> getProducts() {
        return productRepository.getProducts();
    }

    public Product getProductById(UUID productId) {
        if(productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }

        return productRepository.getProductById(productId);
    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        return null;
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
    }

    public void deleteProductById(UUID productId) {
    }
}