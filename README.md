E-Commerce Application
A simple e-commerce application built with Spring Boot and Thymeleaf.
Prerequisites

Java 17
Maven
MySQL

Setup Instructions

Clone the Repository
git clone https://github.com/your-username/ecommerce.git


Configure Database

Create a MySQL database named ecommerce_db.
Update src/main/resources/application.properties with your MySQL credentials:spring.datasource.username=your_username
spring.datasource.password=your_password

Build and Run
mvn clean install
mvn spring-boot:run

Access the Application

Open http://localhost:8080 in your browser.


Features

Browse and view product details.
Add products to cart and checkout.
User registration and login.
Admin panel to manage products and orders.

Technologies

Backend: Spring Boot, Spring Data JPA, Spring Security
Frontend: Thymeleaf, Bootstrap
Database: MySQL

GitHub Repository
The source code is available at: https://github.com/kennedy193/ecormerce-website
