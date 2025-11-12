# Medical Caretaker - Medicine Management System

A comprehensive Spring Boot application designed to help manage medication schedules, track medicine intake history, and coordinate between caregivers and care receivers. The system provides a secure, role-based platform for managing patient medications with real-time scheduling and audit trails.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [User Roles & Permissions](#user-roles--permissions)
- [Data Models](#data-models)
- [Configuration](#configuration)
- [Security](#security)

## 🎯 Overview

Medical Caretaker is a robust backend service that enables:

- **Care Receivers** to manage their medication schedules and track intake history
- **Caregivers** to monitor and manage medications for their assigned care receivers
- **Administrators** to oversee the entire system with full access

The system ensures medication compliance by providing scheduled reminders, recording medication intake, and maintaining detailed audit trails for healthcare management.

## ✨ Features

### Medicine Management

- Create and manage medicine profiles with descriptions and images
- Update medicine information and status (ACTIVE, INACTIVE, PAUSED)
- Track multiple medicines per patient
- Filter medicines by status and user

### Schedule Management

- Create detailed medicine schedules with dosage information
- Set multiple doses per day with specific timing
- Update schedules as needed
- Delete schedules when medications are no longer needed
- View day-wise medicine schedules

### Medicine History & Tracking

- Record medicine intake with timestamps
- Track medicine doses taken, missed, or skipped
- Generate medicine history reports for date ranges
- Monitor compliance with prescribed schedules

### User Management

- Support for multiple user roles (CARETAKER, CARE_RECEIVER, ADMIN)
- Secure user registration and authentication
- Caregivers can be linked to multiple care receivers
- Care receivers can have multiple caregivers

### Security Features

- Role-based access control (RBAC) with Spring Security
- Method-level authorization with `@PreAuthorize` annotations
- BCrypt password encryption (strength 12)
- HTTP Basic authentication
- CSRF protection disabled (for API usage)
- Stateless session management

## 🛠 Technology Stack

| Component      | Technology         | Version       |
| -------------- | ------------------ | ------------- |
| **Framework**  | Spring Boot        | 3.5.6         |
| **Java**       | OpenJDK            | 25            |
| **Database**   | MongoDB            | Cloud (Atlas) |
| **Security**   | Spring Security    | 3.5.6         |
| **Build Tool** | Maven              | Latest        |
| **Validation** | Jakarta Validation | 3.5.6         |
| **Lombok**     | Project Lombok     | Latest        |

### Dependencies

- `spring-boot-starter-data-mongodb` - MongoDB integration
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-security` - Authentication & Authorization
- `spring-boot-starter-validation` - Input validation
- `lombok` - Boilerplate code reduction

## 🏗 Project Architecture

### Directory Structure

```
medical-caretaker/
├── src/main/java/com/jatana/medicalcaretaker/
│   ├── controller/           # REST API endpoints
│   │   ├── UserController.java
│   │   ├── MedicineController.java
│   │   └── HelloController.java
│   ├── service/              # Business logic
│   │   ├── MedicineService.java
│   │   ├── MyUserDetailsService.java
│   │   └── UserPrincipal.java
│   ├── model/                # Data models & DTOs
│   │   ├── User.java
│   │   ├── Medicine.java
│   │   ├── MedicineSchedule.java
│   │   ├── MedicineHistory.java
│   │   └── dto/
│   │       ├── dayWiseSchedule/
│   │       ├── medicineHistory/
│   │       ├── medicineList/
│   │       ├── medicineWiseSchedule/
│   │       ├── request/
│   │       └── user/
│   ├── repo/                 # Data access layer
│   │   ├── UserRepo.java
│   │   ├── MedicineServiceRepo.java
│   │   ├── MedicineScheduleRepo.java
│   │   └── MedicineHistoryRepo.java
│   ├── config/               # Configuration classes
│   │   ├── SecurityConfig.java
│   │   └── MongoConfig.java
│   └── MedicalCaretakerApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

### Layered Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    REST API Layer                             │
│              (MedicineController, UserController)            │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│              Business Logic Layer (Services)                  │
│         (MedicineService, MyUserDetailsService)              │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│           Data Access Layer (Repositories)                    │
│      (UserRepo, MedicineRepo, ScheduleRepo, HistoryRepo)    │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│          Database Layer (MongoDB)                             │
│    Collections: users, medicines, schedules, history         │
└──────────────────────────────────────────────────────────────┘
```

## 🚀 Getting Started

### Prerequisites

- Java 25 or higher
- Maven 3.6 or higher
- MongoDB Account (Atlas or local instance)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/MANIKJATANA/medical-caretaker.git
   cd medical-caretaker
   ```

2. **Configure MongoDB Connection**

   Edit `src/main/resources/application.properties`:

   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/Medicine-Helper
   spring.data.mongodb.auto-index-creation=true
   ```

3. **Build the project**

   ```bash
   mvn clean install
   ```

4. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

### Testing

```bash
mvn test
```

## 📡 API Endpoints

### User Management

#### Create User

```http
POST /create
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "mobileNumber": "+1234567890",
  "role": "CARE_RECEIVER"
}
```

**Response:** 201 Created

```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "CARE_RECEIVER",
  "careTakerUserIds": [],
  "careReceiverUserIds": [],
  "mobileNumber": "+1234567890"
}
```

#### Add Caretaker

```http
POST /addCaretaker
Authorization: Basic <base64(email:password)>
Content-Type: application/json

{
  "caretakerEmail": "caregiver@example.com"
}
```

**Requires:** CARE_RECEIVER or ADMIN role  
**Response:** 200 OK

---

### Medicine Management

#### Get Medicines

```http
GET /api/medicines?filter=ACTIVE&userId=507f1f77bcf86cd799439011
Authorization: Basic <base64(email:password)>
```

**Requires:** CARETAKER, CARE_RECEIVER, or ADMIN role  
**Query Parameters:**

- `filter`: Medicine state filter (ACTIVE, INACTIVE, PAUSED)
- `userId`: Target user ID

**Response:** 200 OK

```json
[
  {
    "medicineId": "507f1f77bcf86cd799439012",
    "userId": "507f1f77bcf86cd799439011",
    "medicineName": "Aspirin",
    "medicineDescription": "Pain reliever"
  }
]
```

#### Get Medicine Schedule

```http
GET /api/medicineSchedule?medicineId=507f1f77bcf86cd799439012&userId=507f1f77bcf86cd799439011
Authorization: Basic <base64(email:password)>
```

**Response:** 200 OK

```json
{
  "medicineId": "507f1f77bcf86cd799439012",
  "userId": "507f1f77bcf86cd799439011",
  "medicineName": "Aspirin",
  "medicineDescription": "Pain reliever",
  "medicineImage": "https://example.com/aspirin.jpg",
  "schedule": {
    "MONDAY": ["08:00", "20:00"],
    "WEDNESDAY": ["08:00"]
  }
}
```

#### Get Day Schedule

```http
GET /api/daySchedule?date=2025-11-12&userId=507f1f77bcf86cd799439011
Authorization: Basic <base64(email:password)>
```

**Response:** 200 OK

```json
{
  "date": "2025-11-12",
  "userId": "507f1f77bcf86cd799439011",
  "dose": [
    {
      "time": "08:00",
      "medicines": [
        {
          "medicineId": "507f1f77bcf86cd799439012",
          "medicineName": "Aspirin",
          "medicineDescription": "Pain reliever",
          "status": "PENDING",
          "medicineImage": "https://example.com/aspirin.jpg"
        }
      ]
    }
  ]
}
```

#### Create Medicine Schedule

```http
POST /api/schedule
Authorization: Basic <base64(email:password)>
Content-Type: application/json

{
  "userId": "507f1f77bcf86cd799439011",
  "medicines": [
    {
      "medicineName": "Aspirin",
      "medicineDescription": "Pain reliever",
      "medicineImage": "https://example.com/aspirin.jpg",
      "schedule": {
        "MONDAY": {"08:00": "1 tablet", "20:00": "1 tablet"},
        "WEDNESDAY": {"08:00": "1 tablet"}
      }
    }
  ]
}
```

**Requires:** CARETAKER, CARE_RECEIVER, or ADMIN role  
**Response:** 201 Created

#### Update Medicine Schedule

```http
PUT /api/schedule
Authorization: Basic <base64(email:password)>
Content-Type: application/json

{
  "userId": "507f1f77bcf86cd799439011",
  "medicineId": "507f1f77bcf86cd799439012",
  "schedule": {
    "MONDAY": {"09:00": "2 tablets"},
    "FRIDAY": {"09:00": "1 tablet"}
  }
}
```

**Response:** 200 OK

#### Update Medicine Data

```http
PUT /api/medicine
Authorization: Basic <base64(email:password)>
Content-Type: application/json

{
  "medicineId": "507f1f77bcf86cd799439012",
  "userId": "507f1f77bcf86cd799439011",
  "name": "Aspirin 500mg",
  "description": "Updated description",
  "imageUrl": "https://example.com/aspirin-new.jpg"
}
```

**Response:** 200 OK

#### Delete Medicine Schedule

```http
DELETE /api/schedule?userId=507f1f77bcf86cd799439011
Authorization: Basic <base64(email:password)>
```

**Requires:** CARETAKER or ADMIN role  
**Response:** 200 OK

---

### Medicine History

#### Get Medicine History

```http
GET /api/medicineHistory?medicineId=507f1f77bcf86cd799439012&startDate=2025-11-01&endDate=2025-11-30&userId=507f1f77bcf86cd799439011
Authorization: Basic <base64(email:password)>
```

**Response:** 200 OK

```json
{
  "medicineId": "507f1f77bcf86cd799439012",
  "userId": "507f1f77bcf86cd799439011",
  "medicineName": "Aspirin",
  "medicineDescription": "Pain reliever",
  "medicineImage": "https://example.com/aspirin.jpg",
  "medicineDateSchedules": [
    { "actionTime": "2025-11-12T08:15:00", "action": "TAKEN" },
    { "actionTime": "2025-11-13T08:00:00", "action": "MISSED" }
  ]
}
```

#### Record User Action

```http
POST /api/userAction
Authorization: Basic <base64(email:password)>
Content-Type: application/json

{
  "userId": "507f1f77bcf86cd799439011",
  "medicineId": "507f1f77bcf86cd799439012",
  "action": "TAKEN",
  "actionDateTime": "2025-11-12T08:00:00",
  "actualDateTime": "2025-11-12T08:15:00",
  "note": "Took after breakfast"
}
```

**Requires:** CARE_RECEIVER or ADMIN role  
**Response:** 202 Accepted

---

## 👥 User Roles & Permissions

| Role              | Permission                             | Endpoints                                           |
| ----------------- | -------------------------------------- | --------------------------------------------------- |
| **ADMIN**         | Full system access                     | All endpoints                                       |
| **CARETAKER**     | Manage care receivers' medications     | GET medicines, schedule management, DELETE schedule |
| **CARE_RECEIVER** | Self-manage medications, report intake | GET medicines, schedule management, record actions  |

### Role-Based Endpoint Access

| Endpoint                    | ADMIN | CARETAKER | CARE_RECEIVER |
| --------------------------- | ----- | --------- | ------------- |
| `POST /create`              | ✓     | ✓         | ✓             |
| `POST /addCaretaker`        | ✓     | ✗         | ✓             |
| `GET /api/medicines`        | ✓     | ✓         | ✓             |
| `GET /api/medicineSchedule` | ✓     | ✓         | ✓             |
| `GET /api/daySchedule`      | ✓     | ✓         | ✓             |
| `GET /api/medicineHistory`  | ✓     | ✓         | ✓             |
| `POST /api/schedule`        | ✓     | ✓         | ✓             |
| `PUT /api/schedule`         | ✓     | ✓         | ✓             |
| `PUT /api/medicine`         | ✓     | ✓         | ✓             |
| `DELETE /api/schedule`      | ✓     | ✓         | ✗             |
| `POST /api/userAction`      | ✓     | ✗         | ✓             |

## 📊 Data Models

### User Entity

```java
{
  "id": "507f1f77bcf86cd799439011",
  "name": "John Doe",
  "email": "john@example.com",
  "password": "bcrypted_password",
  "role": "CARE_RECEIVER",
  "mobileNumber": "+1234567890",
  "careTakerUserIds": ["507f1f77bcf86cd799439012"],
  "careReceiverUserIds": []
}
```

### Medicine Entity

```java
{
  "id": "507f1f77bcf86cd799439012",
  "userId": "507f1f77bcf86cd799439011",
  "name": "Aspirin",
  "description": "Pain reliever and anti-inflammatory",
  "imageUrl": "https://example.com/aspirin.jpg",
  "state": "ACTIVE",
  "createdAt": "2025-11-01T10:30:00",
  "updatedAt": "2025-11-01T10:30:00",
  "createdBy": "507f1f77bcf86cd799439011",
  "updatedBy": "507f1f77bcf86cd799439011"
}
```

### MedicineSchedule Entity

```java
{
  "id": "507f1f77bcf86cd799439013",
  "userId": "507f1f77bcf86cd799439011",
  "medicineId": "507f1f77bcf86cd799439012",
  "scheduleItems": [
    {
      "date": "2025-11-12",
      "doses": [
        {
          "time": "08:00",
          "dose": "1 tablet"
        }
      ]
    }
  ]
}
```

### MedicineHistory Entity

```java
{
  "id": "507f1f77bcf86cd799439014",
  "userId": "507f1f77bcf86cd799439011",
  "medicineId": "507f1f77bcf86cd799439012",
  "dateSchedules": [
    {
      "date": "2025-11-12",
      "doseItems": [
        {
          "time": "08:00",
          "dose": "1 tablet",
          "status": "TAKEN"
        }
      ]
    }
  ]
}
```

## ⚙️ Configuration

### application.properties

```properties
# Application Name
spring.application.name=medical-caretaker

# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/Medicine-Helper

# MongoDB Features
spring.data.mongodb.auto-index-creation=true

# Logging (Optional)
logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
logging.level.org.mongodb.driver=DEBUG
```

### Key Configuration Features

- **Auto Index Creation:** Automatically creates MongoDB indexes from `@Indexed` annotations
- **Connection Pooling:** MongoDB Atlas connection pooling for scalability
- **Timeout Settings:** Configurable connection and socket timeouts
- **Retry Logic:** Automatic retry for failed write operations

## 🔒 Security

### Authentication

- **Method:** HTTP Basic Authentication
- **Password Encoding:** BCrypt with strength 12
- **Credentials Format:** Base64(email:password)

### Authorization

- **Method:** Role-Based Access Control (RBAC)
- **Implementation:** Spring Security `@PreAuthorize` annotations
- **Roles:** ADMIN, CARETAKER, CARE_RECEIVER

### Security Features

- ✅ CSRF protection disabled (stateless API)
- ✅ Stateless session management
- ✅ Password encryption with BCrypt
- ✅ Method-level authorization
- ✅ User detail service with role validation

### Best Practices

1. Always use HTTPS in production
2. Store credentials securely
3. Implement rate limiting for sensitive endpoints
4. Regular security audits and updates
5. Monitor access logs for suspicious activities

## 🔄 Example Workflow

### Setting up a Care Receiver with Medicine Schedule

1. **Create Care Receiver Account**

   ```bash
   POST /create
   {
     "name": "Jane Doe",
     "email": "jane@example.com",
     "password": "password123",
     "mobileNumber": "+1234567890",
     "role": "CARE_RECEIVER"
   }
   ```

2. **Create Caretaker Account**

   ```bash
   POST /create
   {
     "name": "Alice Smith",
     "email": "alice@example.com",
     "password": "password456",
     "mobileNumber": "+0987654321",
     "role": "CARETAKER"
   }
   ```

3. **Link Caretaker to Care Receiver**

   ```bash
   POST /addCaretaker
   Authorization: Basic jane@example.com:password123
   {
     "caretakerEmail": "alice@example.com"
   }
   ```

4. **Create Medicine Profile**

   ```bash
   POST /api/medicines
   {
     "userId": "jane_id",
     "name": "Aspirin",
     "description": "Pain reliever",
     "state": "ACTIVE"
   }
   ```

5. **Create Schedule**

   ```bash
   POST /api/schedule
   {
     "userId": "jane_id",
     "medicineId": "aspirin_id",
     "scheduleDates": [
       {
         "date": "2025-11-12",
         "doses": [
           {"time": "08:00", "dose": "1 tablet"},
           {"time": "20:00", "dose": "1 tablet"}
         ]
       }
     ]
   }
   ```

6. **Record Medicine Intake**

   ```bash
   POST /api/userAction
   Authorization: Basic jane@example.com:password123
   {
     "medicineId": "aspirin_id",
     "date": "2025-11-12",
     "time": "08:15",
     "status": "TAKEN"
   }
   ```

7. **View History**
   ```bash
   GET /api/medicineHistory?medicineId=aspirin_id&startDate=2025-11-01&endDate=2025-11-30&userId=jane_id
   ```

## 🐛 Troubleshooting

### MongoDB Connection Issues

- Verify connection string in `application.properties`
- Check network access in MongoDB Atlas
- Ensure IP whitelist includes your server
- Review logs for connection timeout errors

### Authentication Failures

- Verify email and password credentials
- Check Base64 encoding of credentials
- Ensure user has appropriate role
- Check if user account exists

### Permission Denied Errors

- Verify user role matches endpoint requirements
- Check `@PreAuthorize` annotations in controllers
- Ensure authenticated user is making request

## 📈 Future Enhancements

- [ ] JWT token-based authentication
- [ ] Email/SMS notifications for medicine reminders
- [ ] Mobile app integration
- [ ] Medicine interaction checker
- [ ] AI-powered compliance analytics
- [ ] Multi-language support
- [ ] Push notifications
- [ ] Medicine refill alerts

## 📄 License

This project is proprietary software. All rights reserved.

## 👨‍💼 Author

**Manik Jatana**  
GitHub: [@MANIKJATANA](https://github.com/MANIKJATANA)

## 🤝 Contributing

For bug reports and feature requests, please create an issue in the repository.

---

**Last Updated:** November 12, 2025
