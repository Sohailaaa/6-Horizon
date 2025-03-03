package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable UUID productId, @RequestBody Map<String, Object> body) {
        if (!body.containsKey("name") || !body.containsKey("price")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fields: name/price");
        }
        String newName = body.get("name").toString();
        double newPrice;
        try {
            newPrice = Double.parseDouble(body.get("price").toString());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid price value");
        }
        return productService.updateProduct(productId, newName, newPrice);
    }

    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam double discount, @RequestBody ArrayList<UUID> productIds) {
        if (discount < 0 || discount > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount must be between 0 and 100");
        }

        if (productIds == null || productIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product list cannot be empty");
        }

        productService.applyDiscount(discount, productIds);
        return "Discount applied successfully";
    }


    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId) {
        try{
            productService.deleteProductById(productId);
            return "Product deleted successfully";
        }catch (Exception e){
            return "Product not found";
        }

    }
}