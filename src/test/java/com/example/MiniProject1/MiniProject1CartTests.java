package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    private Cart cart;
    private UUID cartId;
    private Product product;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cart = new Cart(userId, new ArrayList<>());
        cartId = cart.getId();
        product = new Product(UUID.randomUUID(), "Test Product", 100.0);
    }

    // ------------------ TESTS FOR addCart() ------------------
    @Test
    void addCart_ShouldReturnAddedCart_WhenValidCartProvided() {
        when(cartRepository.addCart(cart)).thenReturn(cart);

        Cart result = cartService.addCart(cart);

        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).addCart(cart);
    }

    @Test
    void addCart_ShouldReturnEmptyCart_WhenCartHasNoProducts() {
        Cart emptyCart = new Cart(UUID.randomUUID(), new ArrayList<>());
        when(cartRepository.addCart(emptyCart)).thenReturn(emptyCart);

        Cart result = cartService.addCart(emptyCart);

        assertNotNull(result);
        assertEquals(emptyCart, result);
        assertTrue(result.getProducts().isEmpty()); // Ensure it's still empty
        verify(cartRepository, times(1)).addCart(emptyCart);
    }

    @Test
    void addCart_ShouldThrowException_WhenCartIsNull() {
        assertThrows(NullPointerException.class, () -> cartService.addCart(null));
    }

    // ------------------ TESTS FOR getCarts() ------------------
    @Test
    void getCarts_ShouldReturnListOfCarts_WhenOneCartExists() {
        ArrayList<Cart> cartList = new ArrayList<>();
        cartList.add(cart);
        when(cartRepository.getCarts()).thenReturn(cartList);

        var result = cartService.getCarts();

        assertEquals(1, result.size());
        assertEquals(cart, result.get(0));
        verify(cartRepository, times(1)).getCarts();
    }

    @Test
    void getCarts_ShouldReturnMultipleCarts_WhenMultipleCartsExist() {
        Cart cart2 = new Cart(UUID.randomUUID(), new ArrayList<>());
        ArrayList<Cart> cartList = new ArrayList<>();
        cartList.add(cart);
        cartList.add(cart2);

        when(cartRepository.getCarts()).thenReturn(cartList);

        var result = cartService.getCarts();

        assertEquals(2, result.size());
        assertTrue(result.contains(cart));
        assertTrue(result.contains(cart2));
        verify(cartRepository, times(1)).getCarts();
    }

    @Test
    void getCarts_ShouldReturnEmptyList_WhenNoCartsExist() {
        when(cartRepository.getCarts()).thenReturn(new ArrayList<>());

        var result = cartService.getCarts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartRepository, times(1)).getCarts();
    }

    // ------------------ TESTS FOR getCartById() ------------------
    @Test
    void getCartById_ShouldReturnCart_WhenValidIdProvided() {
        when(cartRepository.getCartById(cartId)).thenReturn(cart);

        Cart result = cartService.getCartById(cartId);

        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).getCartById(cartId);
    }

    @Test
    void getCartById_ShouldThrowException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartById(null));
    }

    @Test
    void getCartById_ShouldThrowException_WhenCartNotFound() {
        when(cartRepository.getCartById(cartId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> cartService.getCartById(cartId));
    }

    // ------------------ TESTS FOR getCartByUserId() ------------------
    @Test
    void getCartByUserId_ShouldReturnCart_WhenValidUserIdProvided() {
        when(cartRepository.getCartByUserId(userId)).thenReturn(cart);

        Cart result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).getCartByUserId(userId);
    }

    @Test
    void getCartByUserId_ShouldThrowException_WhenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.getCartByUserId(null));
    }

    @Test
    void getCartByUserId_ShouldThrowException_WhenCartNotFound() {
        when(cartRepository.getCartByUserId(userId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> cartService.getCartByUserId(userId));
    }

    // ------------------ TESTS FOR addProductToCart() ------------------
    @Test
    void addProductToCart_ShouldCallRepository_WhenValidInputsProvided() {
        cartService.addProductToCart(cartId, product);

        verify(cartRepository, times(1)).addProductToCart(cartId, product);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenCartIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(null, product));
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(cartId, null));
    }

    // ------------------ TESTS FOR deleteProductFromCart() ------------------
    @Test
    void deleteProductFromCart_ShouldCallRepository_WhenValidInputsProvided() {
        cartService.deleteProductFromCart(cartId, product);

        verify(cartRepository, times(1)).deleteProductFromCart(cartId, product);
    }

    @Test
    void deleteProductFromCart_ShouldThrowException_WhenCartIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteProductFromCart(null, product));
    }

    @Test
    void deleteProductFromCart_ShouldThrowException_WhenProductIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteProductFromCart(cartId, null));
    }

    // ------------------ TESTS FOR deleteCartById() ------------------
    @Test
    void deleteCartById_ShouldCallRepository_WhenValidCartIdProvided() {
        cartService.deleteCartById(cartId);

        verify(cartRepository, times(1)).deleteCartById(cartId);
    }

    @Test
    void deleteCartById_ShouldThrowException_WhenCartDoesNotExist() {
        doThrow(new RuntimeException("Cart not found")).when(cartRepository).deleteCartById(cartId);

        assertThrows(RuntimeException.class, () -> cartService.deleteCartById(cartId));

        verify(cartRepository, times(1)).deleteCartById(cartId);
    }

    @Test
    void deleteCartById_ShouldThrowException_WhenCartIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteCartById(null));
    }
}
