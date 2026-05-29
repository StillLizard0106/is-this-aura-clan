# 🍽️ CanteenQ
> A digital pre-ordering system for school canteens and food stalls

![Status](https://img.shields.io/badge/Status-In%20Development-yellow)
![Frontend](https://img.shields.io/badge/Frontend-React%20%2B%20Vite-61DAFB?logo=react)
![Backend](https://img.shields.io/badge/Backend-Java%20Spring%20Boot-6DB33F?logo=springboot)
![Database](https://img.shields.io/badge/Database-PostgreSQL-4169E1?logo=postgresql)
![Auth](https://img.shields.io/badge/Auth-Firebase-FFCA28?logo=firebase)

---

## 👥 Group Members

| Name |
|------|
| *Vince Justine Navarro* |
| *Sean Ethan Go* |
| *John Kyle Obedoza* |
| *Guillermo II Trespeces* |

---

## 📋 Project Overview

| Field | Details |
|-------|---------|
| **Application Name** | CanteenQ *(Working Title)* |
| **Target Audience** | Students, Canteen Staff / Food Stall Vendors |

### 🎯 Goal
The goal of the system is to reduce long lines and food waste in the school canteen by allowing students to pre-order meals digitally before their break time. The system will also help canteen staff and vendors manage incoming orders efficiently from a centralized dashboard.

---

## ❗ Problem Statement

The current canteen process requires students to:
- Physically fall in line during break time
- Wait for their orders to be prepared on the spot
- Risk running out of time or food selling out before they are served

Additionally, canteen vendors have **no advance knowledge** of expected orders, making it difficult to prepare the right amount of food — often resulting in either **shortage or waste**.

---

## 💡 Proposed Solution

**CanteenQ** will provide:
- ✅ A digital pre-ordering system for canteen meals and food stall items
- ✅ Real-time order status tracking for students
- ✅ Advance order visibility for canteen staff and vendors
- ✅ Automated order queuing and numbering
- ✅ Separate dashboards for students and canteen staff
- ✅ User authentication using Firebase Authentication with school email validation

---

## 🏆 Competitive Advantage

Most school canteens still rely entirely on **walk-in, cash-based transactions** with no digital ordering infrastructure. CanteenQ bridges this gap specifically for institutional school environments.

---

## ⚙️ System Features

### 1. User Registration and Authentication

#### Student Registration
The system will allow students to register using their school email address through **Firebase Authentication**. The backend will validate whether the email belongs to a legitimate school email domain after Firebase confirms the account.

**Validation Logic:**

```
IF email domain is valid:
    → Firebase creates the user account
    → User is added to the PostgreSQL database
    → Account is registered as a student account

IF email domain is invalid:
    → Registration is denied
    → User receives an error message
```

#### Staff / Vendor Accounts
Canteen staff and vendor accounts will **not** be publicly registered. Instead, accounts will be manually created in Firebase and added to the database by administrators.

---

### 2. Data to be Stored

#### User Information
- Student Name
- Student ID
- Email Address
- Firebase UID
- User Role

#### Menu Information
- Stall Name
- Item Name
- Item Description
- Price
- Availability Status
- Category

#### Order Information
- Student Who Ordered
- Items Ordered
- Total Price
- Order Time
- Pickup Time Slot
- Order Status
- Queue Number

---

### 3. Pre-Order System Features

Students will be able to:
- Browse available food stalls and menus
- Add items to a cart
- Select a pickup time slot
- Place and confirm orders before break time
- Track their order status in real time
- Cancel orders within an allowed cancellation window

#### ⏱️ Order Expiration
If a student does not pick up their order within **10 minutes** after their selected pickup slot, the order is marked as **unclaimed** and flagged on the staff dashboard.

#### 🚫 Order Restrictions
To prevent abuse:
- Each student can only have **one active order at a time** per stall
- Orders must be placed at least **15 minutes before** the selected pickup slot

---

### 4. Dashboards

#### 🎓 Student Dashboard
| Feature | Description |
|---------|-------------|
| Menu Browser | View available food stalls and items |
| Cart | Add and manage items before checkout |
| Order Status | Track order in real time |
| Queue Number | See assigned queue number |
| Pickup Slot | View selected pickup time |
| Order History | View past orders |

#### 🧑‍🍳 Staff / Vendor Dashboard
| Feature | Description |
|---------|-------------|
| Incoming Orders | View new orders in real time |
| Queue List | Manage orders per time slot |
| Order Status | Update orders to preparing / ready / completed |
| Menu Toggle | Enable or disable sold-out items instantly |
| Daily Summary | View total orders for the day |

---

### 5. User Roles

#### 🎓 Student
- Browse menus
- Place and cancel orders
- Track order status
- View order history

#### 🧑‍🍳 Canteen Staff / Vendor
- Manage incoming orders
- Update menu availability
- Monitor the order queue
- Access the staff dashboard

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | React JS + Vite |
| **Backend** | Java Spring Boot |
| **Database** | PostgreSQL |
| **Authentication** | Firebase Authentication |

---

## 🔐 How Firebase Authentication Works in CanteenQ

```
1. Student registers using their school email on the React frontend
2. Firebase Authentication creates and manages the user account
3. Firebase returns a token to the frontend upon successful login
4. The frontend sends the Firebase token to the Spring Boot backend with every request
5. The Spring Boot backend verifies the token using the Firebase Admin SDK
6. If the token is valid → backend processes the request
7. If the token is invalid or expired → backend rejects the request
```

> Firebase handles password hashing, token generation, and session management — removing the need to build a custom authentication system from scratch.

---

## 🗄️ Proposed Database Tables

### `users`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| name | VARCHAR | Student full name |
| student_id | VARCHAR | School student ID |
| email | VARCHAR | School email address |
| firebase_uid | VARCHAR | Firebase UID |
| role | ENUM | `student` or `staff` |

### `stalls`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| stall_name | VARCHAR | Name of the food stall |
| vendor_name | VARCHAR | Name of the vendor |
| operating_hours | VARCHAR | Operating hours |

### `menu_items`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| item_name | VARCHAR | Name of the food item |
| description | TEXT | Item description |
| price | DECIMAL | Item price |
| category | VARCHAR | Food category |
| is_available | BOOLEAN | Availability status |
| stall_id | UUID | Foreign key → stalls |

### `orders`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| student_id | UUID | Foreign key → users |
| total_price | DECIMAL | Total order amount |
| pickup_slot | TIMESTAMP | Selected pickup time |
| queue_number | INT | Assigned queue number |
| status | ENUM | `pending`, `preparing`, `ready`, `completed`, `unclaimed` |

### `order_items`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key |
| order_id | UUID | Foreign key → orders |
| menu_item_id | UUID | Foreign key → menu_items |
| quantity | INT | Quantity ordered |
| subtotal | DECIMAL | Item subtotal |

---

## 🔄 System Workflow

### 🎓 Student Workflow
```
1. Student registers or logs in via Firebase Authentication
2. Student browses food stalls and menu items
3. Student adds items to cart and selects a pickup time slot
4. Student places the order
5. Queue number is generated
6. Student tracks order status in real time
7. Student picks up order at the canteen
```

### 🧑‍🍳 Staff / Vendor Workflow
```
1. Staff logs in via Firebase Authentication
2. Staff views incoming orders in real time
3. Staff prepares orders by queue
4. Staff marks orders as ready for pickup
5. Staff marks orders as completed upon pickup
6. Staff manages menu availability throughout the day
```

---

## 🚀 Future Improvements

- [ ] Online payment integration
- [ ] QR code order pickup verification
- [ ] Student order ratings and reviews
- [ ] **End of Day Summary & Analytics Dashboard** — Vendors and canteen staff will be able to view a summary of the day's orders at the end of each day, including total orders received, total revenue per stall, most ordered items, number of completed vs unclaimed orders, and busiest pickup time slots displayed through graphs and charts
- [ ] Push or email notifications for order status updates
- [ ] Mobile application support
- [ ] Loyalty or points reward system

---

## 📌 Conclusion

**CanteenQ** aims to modernize the school canteen experience by replacing the traditional walk-in ordering process with a digital pre-ordering system. The project improves convenience for students by reducing wait times, while helping canteen vendors manage orders more efficiently through a centralized platform for ordering, queue management, and menu availability. By using **Firebase Authentication**, the system ensures secure and reliable user management without the overhead of building a custom authentication system from scratch.

---
