package com.example.demo.Controller; // Ensure package is lowercase for consistency

import com.example.demo.Service.OrderService;
import com.example.demo.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControlller {

    private final OrderService orderService;
    private final ProductService productService;

    public AdminControlller(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "admin-orders";
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("product", new com.example.demo.Model.Product()); // Adjust package as needed
        return "admin-products";
    }
}