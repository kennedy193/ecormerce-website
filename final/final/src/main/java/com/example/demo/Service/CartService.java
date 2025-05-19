package com.example.demo.Service;


import com.example.demo.Model.Product;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final ProductService productService;
    private final Map<String, List<CartItem>> carts = new HashMap<>(); // In-memory cart storage: username -> List<CartItem>

   // @Autowired
    public CartService(ProductService productService) {
        this.productService = productService;
    }

    // Add a product to the user's cart
    public void addProduct(Long productId, int quantity) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CartItem> userCart = carts.computeIfAbsent(username, k -> new ArrayList<>());

        // Check if the product is already in the cart
        Product product = productService.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        CartItem existingItem = userCart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity if product already exists in cart
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item to cart
            userCart.add(new CartItem(product, quantity));
        }
    }

    // Get the cart items for the current user
    public List<CartItem> getCartItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return carts.getOrDefault(username, new ArrayList<>());
    }

    // Calculate total price of the cart
    public double getTotal() {
        return getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    // Clear the cart after checkout (optional, depending on your requirements)
    public void clearCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        carts.remove(username);
    }
}

// Helper class to represent a cart item
class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}