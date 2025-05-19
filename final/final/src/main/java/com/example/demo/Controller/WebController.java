package com.example.demo.Controller;

import com.example.demo.Model.Order;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Service.OrderService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class WebController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final Map<String, List<CartItem>> carts = new HashMap<>(); // In-memory cart storage

    public WebController(ProductService productService, UserService userService, OrderService orderService) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.carts.clear(); // Clear the in-memory cart on startup
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        return "home";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        productService.findById(id).ifPresent(product -> model.addAttribute("product", product));
        return "product-details";
    }

  @GetMapping("/orders")
public String viewOrders(Model model) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    try {
        List<Order> orders = orderService.findByUsername(username);
        model.addAttribute("orders", orders != null ? orders : new ArrayList<>());
    } catch (Exception e) {
        model.addAttribute("error", "Failed to load orders: " + e.getMessage());
        return "error";
    }
    return "orders";
}
// @GetMapping("/orders")
// public String viewOrders(Model model) {
//     String username = SecurityContextHolder.getContext().getAuthentication().getName();
//     userService.ensureUserExists(username); // Ensure user exists in database
//     try {
//         List<Order> orders = orderService.findByUsername(username);
//         model.addAttribute("orders", orders != null ? orders : new ArrayList<>());
//     } catch (Exception e) {
//         model.addAttribute("error", "Failed to load orders: " + e.getMessage());
//         return "error";
//     }
//     return "orders";
// }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "signup";
        }
        userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), Set.of("USER"));
        return "redirect:/login";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam int quantity, Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<CartItem> userCart = carts.computeIfAbsent(username, k -> new ArrayList<>());

            Product product = productService.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            CartItem existingItem = userCart.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                userCart.add(new CartItem(product, quantity));
            }
            return "redirect:/cart";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add product to cart: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CartItem> cartItems = carts.getOrDefault(username, new ArrayList<>());
        // Remove any items with null Product
        cartItems.removeIf(item -> item.getProduct() == null);
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        carts.remove(username); // Clear the user's cart after checkout
        return "redirect:/orders";
    }

    @PostMapping("/admin/products")
    public String addProduct(@Valid @ModelAttribute Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "admin-products";
        }
        productService.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders";
    }

    // Helper class to represent a cart item
    private static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

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
}