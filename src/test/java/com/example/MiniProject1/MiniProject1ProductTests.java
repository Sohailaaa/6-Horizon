package com.example.MiniProject1;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MiniProject1ProductTests {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.saveAll(new ArrayList<>()); // Ensure repository starts empty
    }

    @Test
    public void createProduct_withValidProduct_returnsProduct() {
        //Arrange
        Product product = new Product(UUID.randomUUID(),"TestProduct",20);

        //Act
        Product createdProduct = productService.addProduct(product);

        //Assert
        assertNotNull(createdProduct);
        assertEquals(product.getName(),createdProduct.getName());
        assertEquals(product.getPrice(),createdProduct.getPrice());
        assertTrue(productRepository.getProducts().contains(createdProduct));
    }

    @Test
    public void createProduct_withNullProduct_shouldThrowException() {
        //Act&Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.addProduct(null));

        assertEquals("Product cannot be null", exception.getMessage());
    }

    @Test
    public void createProduct_withExistingProduct_shouldThrowException() {
        //Arrange
        Product existingProduct = new Product(UUID.randomUUID(),"TestProduct",20);
        productRepository.addProduct(existingProduct);

        //Act&Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.addProduct(existingProduct));
        assertEquals("Product already exists", exception.getMessage());
    }

    @Test
    public void getAllProducts_shouldReturnSingleProduct() {
        //Arrange
        Product product = new Product(UUID.randomUUID(),"TestProduct",20);
        productRepository.addProduct(product);

        //Act
        ArrayList<Product> products = productService.getProducts();

        //Assert
        assertNotNull(products);
        assertEquals(1, products.size(), "List should contain exactly one product");
        assertEquals("TestProduct", products.get(0).getName());
    }

    @Test
    public void getAllProducts_shouldReturnListOfProducts() {
        //Arrange
        Product product1 = new Product(UUID.randomUUID(),"TestProduct1",20);
        productRepository.addProduct(product1);
        Product product2 = new Product(UUID.randomUUID(),"TestProduct2",20);
        productRepository.addProduct(product2);

        //Act
        ArrayList<Product> products = productService.getProducts();

        //Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("TestProduct1", products.get(0).getName());
        assertEquals("TestProduct2", products.get(1).getName());
    }

    @Test
    public void getAllProducts_shouldReturnEmptyList() {
        // Act
        ArrayList<Product> products = productService.getProducts();

        // Assert
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    public void getProductById_withValidId_shouldReturnProduct() {
        //Arrange
        Product product = new Product(UUID.randomUUID(),"TestProduct",20);
        productRepository.addProduct(product);

        //Act
        Product returnedProduct = productService.getProductById(product.getId());

        //Assert
        assertNotNull(returnedProduct);
        assertEquals(product.getName(),returnedProduct.getName());
        assertEquals(product.getPrice(),returnedProduct.getPrice());
    }

    @Test
    public void getProductById_WithInvalidId_shouldReturnNull() {
        //Arrange
        Product product = new Product(UUID.randomUUID(),"TestProduct",20);
        productRepository.addProduct(product);
        UUID invalidId = UUID.randomUUID();

        //Act
        Product returnedProduct = productService.getProductById(invalidId);

        //Assert
        assertNull(returnedProduct, "Product should be null when ID is invalid");
    }

    @Test
    void getProductById_withNullId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(null);
        }, "Should throw IllegalArgumentException when product ID is null");
    }
}

