package com.example.MiniProject1;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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






    //

    @Test
    public void updateProduct_withValidId_shouldUpdateSuccessfully() {
        // Arrange
        Product product = new Product(UUID.randomUUID(), "OldName", 30.0);
        productRepository.addProduct(product);

        // Act
        Product updatedProduct = productService.updateProduct(product.getId(), "NewName", 50.0);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("NewName", updatedProduct.getName());
        assertEquals(50.0, updatedProduct.getPrice());
    }

    @Test
    public void updateProduct_withInvalidId_shouldThrowException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> productService.updateProduct(invalidId, "UpdatedName", 40.0),
                "Should throw ResponseStatusException when product ID is invalid");
    }

    @Test
    public void updateProduct_withNullId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(null,
                        "UpdatedName", 40.0),
                "Should throw IllegalArgumentException when product ID is null");
    }




    //



    @Test
    public void applyDiscount_withValidProducts_shouldApplyDiscount() {
        // Arrange
        Product product1 = new Product(UUID.randomUUID(), "Product1", 100.0);
        Product product2 = new Product(UUID.randomUUID(), "Product2", 200.0);
        productRepository.addProduct(product1);
        productRepository.addProduct(product2);

        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product1.getId());
        productIds.add(product2.getId());

        // Act
        productService.applyDiscount(10.0, productIds);

        // Assert
        assertEquals(90.0, productService.getProductById(product1.getId()).getPrice());
        assertEquals(180.0, productService.getProductById(product2.getId()).getPrice());
    }

    @Test
    public void applyDiscount_withInvalidProductIds_shouldIgnoreThem() {
        // Arrange
        Product product = new Product(UUID.randomUUID(), "Product", 100.0);
        productRepository.addProduct(product);

        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());
        productIds.add(UUID.randomUUID()); // Invalid product ID

        // Act
        productService.applyDiscount(10.0, productIds);

        // Assert
        assertEquals(90.0, productService.getProductById(product.getId()).getPrice());
    }

    @Test
    public void applyDiscount_withHighDiscount_shouldNotSetPriceBelowZero() {
        // Arrange
        Product product = new Product(UUID.randomUUID(), "Expensive Product", 50.0);
        productRepository.addProduct(product);

        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());

        // Act
        productService.applyDiscount(110.0, productIds); // Trying to apply 110% discount, should cap at 100%

        // Assert
        Product updatedProduct = productRepository.getProductById(product.getId());
        assertNotNull(updatedProduct, "Product should exist in the repository");
        assertEquals(0.0, updatedProduct.getPrice(), "Price should be 0 when discount is too high");
    }





    @Test
    public void deleteProductById_withValidId_shouldDeleteSuccessfully() {
        // Arrange
        Product product = new Product(UUID.randomUUID(), "TestProduct", 50.0);
        productRepository.addProduct(product);

        // Act
        productService.deleteProductById(product.getId());

        // Assert
        assertNull(productService.getProductById(product.getId()));
    }

    @Test
    public void deleteProductById_withInvalidId_shouldThrowException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> productService.deleteProductById(invalidId),
                "Should throw ResponseStatusException when product ID is invalid");
    }

    @Test
    public void deleteProductById_withNullId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProductById(null),
                "Should throw IllegalArgumentException when product ID is null");
    }

    @Test
    public void deleteProductById_whenProductListIsEmpty_shouldThrowException() {
        // Arrange
        UUID invalidProductId = UUID.randomUUID(); // Generate a random ID

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.deleteProductById(invalidProductId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode(),
                "Deleting a product from an empty list should return 404 NOT FOUND");
        assertEquals("Product not found", exception.getReason(),
                "Exception message should indicate that the product was not found");
    }

}

