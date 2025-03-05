package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        ArrayList<Product> products = getProducts();
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        Product productToUpdate = products.stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        productToUpdate.setName(newName);
        productToUpdate.setPrice(newPrice);

        productRepository.saveAll(products);
        return productToUpdate;
    }

    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        if (discount < 0 || productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid discount or product list");
        }

        // Cap the discount at 100%
        if (discount > 100) {
            discount = 100;
        }

        ArrayList<Product> products = productRepository.getProducts();
        boolean updated = false;

        for (Product product : products) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount / 100);
                newPrice = Math.max(newPrice, 0);
                product.setPrice(newPrice);
                updated = true;
            }
        }

        if (!updated) {
            throw new IllegalArgumentException("No matching products found for discount");
        }

        productRepository.overrideData(products);
    }


    public void deleteProductById(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        productRepository.deleteProductById(productId);
    }
}