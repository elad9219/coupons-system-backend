# **Coupons Management System**

A comprehensive full-stack web application for managing, selling, and purchasing coupons. The system facilitates a marketplace connecting companies and customers, governed by an administrator. Built with **Spring Boot** (Java) for the backend and **React** (TypeScript) with **Material UI** for the frontend.

## **Quick Links**

* **Live Demo**: [https://coupons.runmydocker-app.com/](https://www.google.com/search?q=https://coupons.runmydocker-app.com/)  
* **Swagger API**: [https://coupons.runmydocker-app.com/swagger-ui.html](https://www.google.com/search?q=https://coupons.runmydocker-app.com/swagger-ui.html)  
* **Backend Repository**: [https://github.com/elad9219/coupons-project](https://www.google.com/search?q=https://github.com/elad9219/coupons-project)  
* **Frontend Repository**: [https://github.com/elad9219/coupons-project-frontend](https://www.google.com/search?q=https://github.com/elad9219/coupons-project-frontend)

## **Table of Contents**

* [Overview](https://www.google.com/search?q=%23overview)  
* [Features](https://www.google.com/search?q=%23features)  
* [Technologies](https://www.google.com/search?q=%23technologies)  
* [Screenshots](https://www.google.com/search?q=%23screenshots)  
* [Installation](https://www.google.com/search?q=%23installation)  
* [Usage & Demo Credentials](https://www.google.com/search?q=%23usage)  
* [Project Structure](https://www.google.com/search?q=%23project-structure)  
* [Contact](https://www.google.com/search?q=%23contact)

## **Overview**

The Coupons System is a Single Page Application (SPA) designed to handle high-load coupon trading. It features a robust **RESTful API**, JWT-based authentication, and a scheduled daily job that automatically invalidates expired coupons. The frontend provides a responsive, professional dashboard for all user types.

## **Features**

### **For Administrator**

* **Company Management**: Add, update, delete, and view all companies in the system.  
* **Customer Management**: Add, update, delete, and view all customers.  
* **System Oversight**: Full access to view system data.

### **For Companies**

* **Coupon Management**: Create new coupons with categories, prices, images, and expiration dates.  
* **Inventory Control**: Update existing coupons and track stock (Amount).  
* **Dashboard**: View all active coupons belonging to the specific company.  
* **Filtering**: Filter coupons by category and maximum price.

### **For Customers**

* **Marketplace**: Browse all available coupons in the system.  
* **Purchase System**: Buy coupons (stock and expiration validated in real-time).  
* **Personal Portfolio**: View purchased coupons history.  
* **Smart Filtering**: Filter available or owned coupons by category and price.

### **General / System**

* **Security**: Secure Login with JWT (JSON Web Tokens) and Role-Based Access Control (RBAC).  
* **Daily Job**: Background thread that runs once a day to detect and mark expired coupons.  
* **Guest Mode**: View available coupons and register as a new customer.

## **Technologies**

### **Backend**

* **Language**: Java 11  
* **Framework**: Spring Boot 2.7.11  
* **Database**: PostgreSQL  
* **ORM**: Hibernate / Spring Data JPA  
* **Security**: Spring Security, JWT (jjwt)  
* **Documentation**: Swagger 2  
* **Tools**: Lombok, Maven

### **Frontend**

* **Framework**: React 18  
* **Language**: TypeScript  
* **State Management**: Redux Toolkit  
* **Routing**: React Router DOM v6  
* **UI Library**: Material UI (MUI) v5  
* **HTTP Client**: Axios (with Interceptors)

### **DevOps**

* **Containerization**: Docker  
* **Deployment**: Docker Cloud

## **Screenshots**

### **Login Page**

\<img width="800" alt="Login Page" src="https://www.google.com/search?q=https://via.placeholder.com/800x450%3Ftext%3DLogin%2BPage%2BScreenshot" /\>

### **Admin Dashboard**

\<img width="800" alt="Admin Dashboard" src="https://www.google.com/search?q=https://via.placeholder.com/800x450%3Ftext%3DAdmin%2BDashboard%2BScreenshot" /\>

### **Coupons Marketplace**

\<img width="800" alt="Marketplace" src="https://www.google.com/search?q=https://via.placeholder.com/800x450%3Ftext%3DMarketplace%2BScreenshot" /\>

*Note: Replace the placeholder links above with actual screenshots of your application.*

## **Installation**

### **Prerequisites**

* Java 11 JDK  
* Node.js & npm  
* PostgreSQL Database  
* Docker (Optional)

### **1\. Database Setup**

Ensure you have a PostgreSQL instance running. Update the application.properties file:

spring.datasource.url=jdbc:postgresql://\[node128.codingbc.com:7878/niv\_test\](https://node128.codingbc.com:7878/niv\_test)  
spring.datasource.username=postgres  
spring.datasource.password=Nov2017890\#

### **2\. Backend Setup**

Navigate to the backend directory and run:

mvn clean install  
mvn spring-boot:run

### **3\. Frontend Setup**

Navigate to the frontend directory and run:

npm install  
npm start

## **Usage**

To test the system immediately, use these demo credentials:

| Role | Email | Password |
| ----: | ----: | ----: |
| **Administrator** | admin@admin.com | admin |
| **Company** | sony@contact.com | 1234 |
| **Customer** | kobi@gmail.com | 1234 |

## **Project Structure**

### **Backend Structure**

src/main/java/com/jb/spring\_coupons\_project/  
├── advice/           \# Global Exception Handlers  
├── beans/            \# Entities (Company, Customer, Coupon)  
├── clr/              \# Data Seeding (CommandLineRunner)  
├── config/           \# CORS, Swagger, RestTemplate Config  
├── controller/       \# REST Controllers (API Endpoints)  
├── dailyJob/         \# Scheduled Tasks (Expiration Logic)  
├── repository/       \# JPA Repositories (DAO)  
├── security/         \# JWT Utilities & Logic  
└── service/          \# Business Logic (Admin, Company, Customer)

### **Frontend Structure**

src/  
├── Components/  
│   ├── admin/        \# Admin specific components  
│   ├── company/      \# Company specific components  
│   ├── customer/     \# Customer specific components  
│   ├── user/         \# Login, Register, Public views  
│   ├── mainLayout/   \# Layout wrapper  
│   └── routing/      \# App Routes  
├── redux/            \# Store and Reducers  
└── util/             \# Interceptors and Globals

## **Contact**

* **Author**: Niv  
* **GitHub**: [https://github.com/elad9219](https://www.google.com/search?q=https://github.com/elad9219)  
* **Email**: \[Insert Your Email\]  
* **LinkedIn**: \[Insert Your LinkedIn\]