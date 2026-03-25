# MediTrack - Implementation Guide (Local Development)

## Overview

Implementasi sederhana MediTrack untuk development local dengan fokus pada functionality dasar tanpa integrasi yang kompleks.

## Prerequisites

- Java 21
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL (via Docker)
- IDE (IntelliJ IDEA / VS Code)

## Quick Start

### 1. Start Infrastructure

```bash
# Start databases, RabbitMQ, Redis
docker-compose up -d
```

### 2. Build All Services

```bash
# Build parent and all modules
mvn clean install
```

### 3. Start Services (in order)

```bash
# Terminal 1: Service Registry
cd service-registry
mvn spring-boot:run

# Terminal 2: User Service
cd user-service
mvn spring-boot:run

# Terminal 3: Appointment Service
cd appointment-service
mvn spring-boot:run

# Terminal 4: EHR Service
cd ehr-service
mvn spring-boot:run

# Terminal 5: Pharmacy Service
cd pharmacy-service
mvn spring-boot:run

# Terminal 6: Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 7: Analytics Service
cd analytics-service
mvn spring-boot:run
```

## Service Ports

| Service | Port | URL |
|---------|------|-----|
| Service Registry | 8761 | http://localhost:8761 |
| User Service | 8081 | http://localhost:8081 |
| Appointment Service | 8082 | http://localhost:8082 |
| EHR Service | 8083 | http://localhost:8083 |
| Pharmacy Service | 8084 | http://localhost:8084 |
| Payment Service | 8086 | http://localhost:8086 |
| Analytics Service | 8085 | http://localhost:8085 |

## Implemented Features (Simple Version)

### a) User Management
- ✅ Register user (Patient, Doctor, Pharmacist, Admin)
- ✅ Login (simple, no JWT for now)
- ✅ Get user by ID
- ✅ List users by role
- ✅ Update user profile

**Endpoints:**
```
POST   /api/users/register
POST   /api/users/login
GET    /api/users/{id}
GET    /api/users/role/{role}
PUT    /api/users/{id}
```

### b) Appointment Scheduling
- ✅ Book appointment
- ✅ Reschedule appointment
- ✅ Cancel appointment
- ✅ List appointments by patient
- ✅ List appointments by doctor

**Endpoints:**
```
POST   /api/appointments
PUT    /api/appointments/{id}/reschedule
DELETE /api/appointments/{id}
GET    /api/appointments/patient/{patientId}
GET    /api/appointments/doctor/{doctorId}
```

### c) Electronic Health Records (EHR)
- ✅ Create medical record
- ✅ Get patient medical history
- ✅ Create prescription
- ✅ Get prescription by ID
- ✅ Add lab result
- ✅ Get lab results by patient

**Endpoints:**
```
POST   /api/ehr/records
GET    /api/ehr/records/patient/{patientId}
POST   /api/ehr/prescriptions
GET    /api/ehr/prescriptions/{id}
POST   /api/ehr/lab-results
GET    /api/ehr/lab-results/patient/{patientId}
```

### d) Pharmacy Integration
- ✅ Add medicine to inventory
- ✅ Create prescription order
- ✅ Update order status
- ✅ Check stock availability
- ✅ List medicines

**Endpoints:**
```
POST   /api/pharmacy/medicines
POST   /api/pharmacy/orders
PUT    /api/pharmacy/orders/{id}/status
GET    /api/pharmacy/medicines
GET    /api/pharmacy/inventory/{medicineId}
```

### e) Analytics (Simple)
- ✅ Count appointments by status
- ✅ Count users by role
- ✅ Top prescribed medicines
- ✅ Revenue summary

**Endpoints:**
```
GET    /api/analytics/appointments/summary
GET    /api/analytics/users/summary
GET    /api/analytics/medicines/top
GET    /api/analytics/revenue/summary
```

### f) Payment System
- ✅ Create payment
- ✅ Get payment by ID
- ✅ List payments by user
- ✅ Simple payment status update (no real gateway integration)

**Endpoints:**
```
POST   /api/payments
GET    /api/payments/{id}
GET    /api/payments/user/{userId}
PUT    /api/payments/{id}/status
```

## Database Schema (Auto-created by JPA)

Each service has its own database:
- `user_db` - User Service
- `appointment_db` - Appointment Service
- `ehr_db` - EHR Service
- `pharmacy_db` - Pharmacy Service
- `payment_db` - Payment Service
- `analytics_db` - Analytics Service

## Testing with cURL

### Register a Doctor
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@meditrack.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "081234567890",
    "role": "DOCTOR"
  }'
```

### Register a Patient
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@meditrack.com",
    "password": "password123",
    "firstName": "Jane",
    "lastName": "Smith",
    "phoneNumber": "081234567891",
    "role": "PATIENT"
  }'
```

### Book an Appointment
```bash
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "appointmentDateTime": "2026-04-01T10:00:00",
    "reasonForVisit": "Regular checkup"
  }'
```

### Create a Prescription
```bash
curl -X POST http://localhost:8083/api/ehr/prescriptions \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "medicines": [
      {
        "medicineName": "Paracetamol",
        "dosage": "500mg",
        "frequency": "3x daily",
        "durationDays": 7
      }
    ]
  }'
```

## Simplified Features (No External Integration)

### What's NOT Implemented (for simplicity):
- ❌ JWT Authentication (using simple session)
- ❌ Real payment gateway integration (Stripe/PayPal)
- ❌ Real email/SMS notifications
- ❌ File upload for medical documents
- ❌ Insurance claims API integration
- ❌ Laboratory system integration
- ❌ Complex analytics with ML
- ❌ API Gateway (direct service calls)
- ❌ Config Server (using local configs)

### What's Simplified:
- ✅ In-memory session instead of JWT
- ✅ Simple password storage (no BCrypt for now)
- ✅ Mock payment processing
- ✅ Console logging instead of email/SMS
- ✅ Basic CRUD operations
- ✅ Simple analytics queries
- ✅ Direct database access (no caching)

## Development Tips

### Hot Reload
Use Spring Boot DevTools for hot reload:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Database GUI
Access PostgreSQL using:
- pgAdmin: http://localhost:5050
- DBeaver
- IntelliJ Database Tools

### View Logs
```bash
# Follow logs for a service
tail -f logs/user-service.log
```

### Reset Database
```bash
# Stop and remove containers
docker-compose down -v

# Start fresh
docker-compose up -d
```

## Project Structure

```
meditrack/
├── common-lib/              # Shared utilities
├── service-registry/        # Eureka Server
├── user-service/           # User management
├── appointment-service/    # Appointments
├── ehr-service/           # Medical records
├── pharmacy-service/      # Pharmacy & inventory
├── payment-service/       # Payments
├── analytics-service/     # Simple analytics
├── docker-compose.yml     # Infrastructure
└── pom.xml               # Parent POM
```

## Next Steps for Production

When ready for production, add:
1. JWT Authentication & Authorization
2. API Gateway (Spring Cloud Gateway)
3. Config Server
4. Real payment gateway integration
5. Email/SMS service integration
6. File storage (S3/MinIO)
7. Caching (Redis)
8. Message Queue (RabbitMQ)
9. Monitoring (Prometheus, Grafana)
10. Logging (ELK Stack)

## Troubleshooting

### Service won't start
- Check if port is already in use
- Verify database is running: `docker ps`
- Check logs in console

### Database connection error
- Verify docker-compose is running
- Check database credentials in application.yml
- Wait a few seconds for database to be ready

### Service not registering with Eureka
- Ensure Service Registry is running first
- Check eureka.client.service-url in application.yml
- Wait 30 seconds for registration

## Support

For issues or questions:
1. Check logs in console
2. Verify all services are running
3. Check database connections
4. Review application.yml configurations

---

**Happy Coding! 🚀**
