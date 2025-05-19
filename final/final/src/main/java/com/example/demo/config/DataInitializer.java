package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            roles.add("USER");
            admin.setRoles(roles);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            Set<String> roles = new HashSet<>();
            roles.add("USER");
            user.setRoles(roles);
            userRepository.save(user);
        }

        if (productRepository.count() == 0) {
            Product product1 = new Product();
            product1.setName("Sample Product 1");
            product1.setPrice(29.99);
            product1.setDescription("A sample product.");
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Sample Product 2");
            product2.setPrice(39.99);
            product2.setDescription("Another sample product.");
            productRepository.save(product2);
        }
    }
}