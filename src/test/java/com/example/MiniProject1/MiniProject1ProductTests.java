package com.example.MiniProject1;

import com.example.model.Product;
import com.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MiniProject1ProductTests {
    @Autowired
    private ProductService productService;

    @Test
    public void createProduct_withValidInput_returnsProduct() {
        //Arrange
        Product product = new Product("TestProduct",20);

        //Act
        Product createdProduct = productService.addProduct(product);

        //Assert
    }
}

