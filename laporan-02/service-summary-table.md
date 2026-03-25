# Service Summary Table

## Quick Reference - MediTrack Services

| # | Service Name | Port | Database | Key Responsibilities | Main Entities |
|---|-------------|------|----------|---------------------|---------------|
| 1 | **Service Registry** | 8761 | - | Service discovery, health monitoring | - |
| 2 | **Config Server** | 8888 | - | Centralized configuration | - |
| 3 | **API Gateway** | 8080 | - | Routing, authentication, rate limiting | - |
| 4 | **User Service** | 8081 | user_db | User management, authentication, RBAC | User, Patient, Doctor, Pharmacist, Admin, Role, Permission |
| 5 | **Appointment Service** | 8082 | appointment_db | Appointment booking, scheduling, doctor availability | Appointment, DoctorSchedule, AppointmentSlot, Waitlist |
| 6 | **EHR Service** | 8083 | ehr_db | Medical records, prescriptions, lab results | MedicalRecord, Prescription, LabResult, MedicalDocument, Allergy |
| 7 | **Pharmacy Service** | 8084 | pharmacy_db | Medicine inventory, prescription orders | Medicine, PharmacyInventory, PrescriptionOrder, StockMovement |
| 8 | **Payment Service** | 8086 | payment_db | Payment processing, insurance claims | Payment, Invoice, InsuranceClaim, Refund |
| 9 | **Analytics Service** | 8085 | analytics_db | Reporting, metrics, trends analysis | AppointmentMetrics, DoctorPerformance, DrugUsageTrend, RevenueMetrics |
| 10 | **Notification Service** | 8087 | notification_db | Email, SMS, push notifications | Notification, NotificationTemplate, NotificationPreference |

---

## Service Dependencies Matrix

| Service | Depends On | Consumed By | Events Published | Events Consumed |
|---------|-----------|-------------|------------------|-----------------|
| **Service Registry** | None | All services | - | - |
| **Config Server** | None | All services | - | - |
| **API Gateway** | Registry, Config, All services | All clients | - | - |
| **User Service** | Registry, Config | API Gateway, All services | UserRegistered, UserUpdated | - |
| **Appointment Service** | Registry, Config, User | API Gateway | AppointmentBooked, AppointmentCancelled, AppointmentCompleted | - |
| **EHR Service** | Registry, Config, User | API Gateway, Pharmacy | PrescriptionIssued, LabResultAvailable | AppointmentCompleted |
| **Pharmacy Service** | Registry, Config, User, EHR | API Gateway, Payment | OrderCreated, OrderFulfilled, StockLowAlert | PrescriptionIssued, PaymentCompleted |
| **Payment Service** | Registry, Config, User | API Gateway, Pharmacy | PaymentCompleted, PaymentFailed, ClaimSubmitted | OrderCreated |
| **Analytics Service** | Registry, Config | API Gateway | - | All events from other services |
| **Notification Service** | Registry, Config | - | - | All events from other services |

---

## API Endpoints Summary

### User Service (Port 8081)
```
POST   /api/v1/users/register          - Register new user
POST   /api/v1/users/login             - User login
GET    /api/v1/users/{id}              - Get user profile
PUT    /api/v1/users/{id}              - Update user profile
GET    /api/v1/users/doctors           - List doctors
GET    /api/v1/users/doctors/{id}      - Get doctor details
POST   /api/v1/users/password/reset    - Reset password
```

### Appointment Service (Port 8082)
```
POST   /api/v1/appointments                        - Book appointment
GET    /api/v1/appointments/{id}                   - Get appointment details
PUT    /api/v1/appointments/{id}                   - Reschedule appointment
DELETE /api/v1/appointments/{id}                   - Cancel appointment
GET    /api/v1/appointments/patient/{patientId}    - Patient appointments
GET    /api/v1/appointments/doctor/{doctorId}      - Doctor appointments
GET    /api/v1/appointments/doctor/{id}/slots      - Available slots
POST   /api/v1/appointments/doctor/schedule        - Set doctor schedule
```

### EHR Service (Port 8083)
```
POST   /api/v1/ehr/records                         - Create medical record
GET    /api/v1/ehr/records/patient/{patientId}     - Get patient records
GET    /api/v1/ehr/records/{id}                    - Get record details
POST   /api/v1/ehr/prescriptions                   - Create prescription
GET    /api/v1/ehr/prescriptions/{id}              - Get prescription
POST   /api/v1/ehr/lab-results                     - Upload lab result
GET    /api/v1/ehr/lab-results/patient/{id}        - Get lab results
POST   /api/v1/ehr/documents                       - Upload document
GET    /api/v1/ehr/allergies/patient/{id}          - Get allergies
```

### Pharmacy Service (Port 8084)
```
POST   /api/v1/pharmacy/orders                     - Create order
GET    /api/v1/pharmacy/orders/{id}                - Get order details
PUT    /api/v1/pharmacy/orders/{id}/status         - Update order status
GET    /api/v1/pharmacy/orders/patient/{id}        - Patient orders
GET    /api/v1/pharmacy/medicines                  - List medicines
GET    /api/v1/pharmacy/medicines/search           - Search medicines
GET    /api/v1/pharmacy/inventory                  - Get inventory
PUT    /api/v1/pharmacy/inventory/{id}             - Update stock
```

### Payment Service (Port 8086)
```
POST   /api/v1/payments                            - Process payment
GET    /api/v1/payments/{id}                       - Get payment details
POST   /api/v1/payments/refund                     - Request refund
GET    /api/v1/payments/invoices/patient/{id}      - Get invoices
POST   /api/v1/payments/insurance-claims           - Submit claim
GET    /api/v1/payments/insurance-claims/{id}      - Get claim status
POST   /api/v1/payments/methods                    - Add payment method
GET    /api/v1/payments/methods/user/{id}          - Get payment methods
```

### Analytics Service (Port 8085)
```
GET    /api/v1/analytics/appointments              - Appointment metrics
GET    /api/v1/analytics/doctors/performance       - Doctor performance
GET    /api/v1/analytics/drugs/usage               - Drug usage trends
GET    /api/v1/analytics/revenue                   - Revenue metrics
GET    /api/v1/analytics/patients/outcomes         - Patient outcomes
GET    /api/v1/analytics/dashboard                 - Dashboard data
POST   /api/v1/analytics/reports                   - Generate report
```

### Notification Service (Port 8087)
```
POST   /api/v1/notifications/send                  - Send notification
GET    /api/v1/notifications/user/{userId}         - Get notifications
PUT    /api/v1/notifications/{id}/read             - Mark as read
GET    /api/v1/notifications/preferences/{userId}  - Get preferences
PUT    /api/v1/notifications/preferences/{userId}  - Update preferences
```

---

## Database Schema Summary

### User Service Database (user_db)
```
Tables:
- users (base table)
- patients (extends users)
- doctors (extends users)
- pharmacists (extends users)
- admins (extends users)
- roles
- permissions
- user_roles (junction table)
- role_permissions (junction table)
```

### Appointment Service Database (appointment_db)
```
Tables:
- appointments
- doctor_schedules
- appointment_slots
- appointment_history
- waitlist
```

### EHR Service Database (ehr_db)
```
Tables:
- medical_records
- prescriptions
- prescription_items
- lab_results
- medical_documents
- allergies
```

### Pharmacy Service Database (pharmacy_db)
```
Tables:
- medicines
- pharmacy_inventory
- prescription_orders
- order_items
- stock_movements
- suppliers
```

### Payment Service Database (payment_db)
```
Tables:
- payments
- invoices
- invoice_items
- insurance_claims
- claim_documents
- payment_methods
- refunds
```

### Analytics Service Database (analytics_db)
```
Tables:
- appointment_metrics
- doctor_performance
- drug_usage_trends
- revenue_metrics
- patient_outcomes
```

### Notification Service Database (notification_db)
```
Tables:
- notifications
- notification_templates
- notification_preferences
```

---

## Event Catalog

### Published Events

| Service | Event Name | Payload | Consumers |
|---------|-----------|---------|-----------|
| **Appointment Service** | AppointmentBooked | appointmentId, patientId, doctorId, dateTime | Notification, Analytics, EHR |
| **Appointment Service** | AppointmentCancelled | appointmentId, reason | Notification, Analytics |
| **Appointment Service** | AppointmentCompleted | appointmentId, patientId, doctorId | EHR, Analytics |
| **EHR Service** | PrescriptionIssued | prescriptionId, patientId, doctorId | Pharmacy, Notification, Analytics |
| **EHR Service** | LabResultAvailable | labResultId, patientId | Notification, Analytics |
| **Pharmacy Service** | OrderCreated | orderId, patientId, totalAmount | Payment, Analytics |
| **Pharmacy Service** | OrderFulfilled | orderId, patientId | Notification, Analytics |
| **Pharmacy Service** | StockLowAlert | medicineId, currentStock, minimumStock | Notification |
| **Payment Service** | PaymentCompleted | paymentId, userId, amount, referenceId | Pharmacy, Notification, Analytics |
| **Payment Service** | PaymentFailed | paymentId, userId, reason | Notification, Analytics |
| **Payment Service** | ClaimSubmitted | claimId, patientId, amount | Notification, Analytics |

---

## Technology Stack per Service

| Service | Framework | Database | Cache | Message Queue | Other |
|---------|-----------|----------|-------|---------------|-------|
| **Service Registry** | Spring Cloud Eureka | - | - | - | - |
| **Config Server** | Spring Cloud Config | - | - | - | Git backend |
| **API Gateway** | Spring Cloud Gateway | - | Redis | - | JWT, Rate Limiter |
| **User Service** | Spring Boot 3.2 | PostgreSQL | Redis | - | Spring Security, BCrypt |
| **Appointment Service** | Spring Boot 3.2 | PostgreSQL | Redis | RabbitMQ | Spring Data JPA |
| **EHR Service** | Spring Boot 3.2 | PostgreSQL | - | RabbitMQ | S3/MinIO, Spring Data JPA |
| **Pharmacy Service** | Spring Boot 3.2 | PostgreSQL | Redis | RabbitMQ | Spring Data JPA |
| **Payment Service** | Spring Boot 3.2 | PostgreSQL | - | RabbitMQ | Stripe/PayPal SDK |
| **Analytics Service** | Spring Boot 3.2 | PostgreSQL | - | RabbitMQ | Spring Batch |
| **Notification Service** | Spring Boot 3.2 | PostgreSQL | - | RabbitMQ | SendGrid, Twilio |

---

## Scalability Configuration

### Recommended Instance Count per Service

| Service | Dev | Staging | Production | Auto-scale |
|---------|-----|---------|------------|------------|
| **Service Registry** | 1 | 2 | 3 | No |
| **Config Server** | 1 | 2 | 2 | No |
| **API Gateway** | 1 | 2 | 3-5 | Yes |
| **User Service** | 1 | 2 | 3-5 | Yes |
| **Appointment Service** | 1 | 2 | 3-7 | Yes |
| **EHR Service** | 1 | 2 | 3-5 | Yes |
| **Pharmacy Service** | 1 | 2 | 2-4 | Yes |
| **Payment Service** | 1 | 2 | 3-5 | Yes |
| **Analytics Service** | 1 | 1 | 2-3 | Yes |
| **Notification Service** | 1 | 2 | 3-5 | Yes |

### Resource Allocation (Production)

| Service | CPU | Memory | Storage |
|---------|-----|--------|---------|
| **Service Registry** | 0.5 core | 512 MB | 1 GB |
| **Config Server** | 0.5 core | 512 MB | 2 GB |
| **API Gateway** | 1 core | 1 GB | 1 GB |
| **User Service** | 1 core | 1 GB | 5 GB |
| **Appointment Service** | 1 core | 1 GB | 10 GB |
| **EHR Service** | 2 cores | 2 GB | 50 GB |
| **Pharmacy Service** | 1 core | 1 GB | 10 GB |
| **Payment Service** | 1 core | 1 GB | 10 GB |
| **Analytics Service** | 2 cores | 2 GB | 20 GB |
| **Notification Service** | 1 core | 1 GB | 5 GB |

---

## Security Configuration

### Authentication & Authorization

| Service | Auth Type | Roles Required | Special Permissions |
|---------|-----------|----------------|---------------------|
| **User Service** | JWT | Public (register/login) | - |
| **Appointment Service** | JWT | PATIENT, DOCTOR, ADMIN | BOOK_APPOINTMENT, VIEW_SCHEDULE |
| **EHR Service** | JWT | DOCTOR, PATIENT, ADMIN | READ_EHR, WRITE_EHR, WRITE_PRESCRIPTION |
| **Pharmacy Service** | JWT | PHARMACIST, PATIENT, ADMIN | MANAGE_INVENTORY, DISPENSE_MEDICINE |
| **Payment Service** | JWT | PATIENT, ADMIN | PROCESS_PAYMENT, VIEW_INVOICES |
| **Analytics Service** | JWT | ADMIN, DOCTOR | VIEW_ANALYTICS, GENERATE_REPORTS |
| **Notification Service** | Internal | System | - |

---

## Monitoring & Health Checks

### Health Check Endpoints

All services expose:
```
GET /actuator/health        - Health status
GET /actuator/info          - Service info
GET /actuator/metrics       - Metrics
GET /actuator/prometheus    - Prometheus metrics
```

### Key Metrics to Monitor

| Metric Type | Examples |
|-------------|----------|
| **Application** | Request rate, Response time, Error rate |
| **Business** | Appointments/hour, Orders/day, Revenue/day |
| **Infrastructure** | CPU usage, Memory usage, Disk I/O |
| **Database** | Connection pool, Query time, Deadlocks |
| **Message Queue** | Queue depth, Message rate, Consumer lag |

---

## Quick Start Commands

### Start Infrastructure
```bash
docker-compose up -d
```

### Start Services (Development)
```bash
# Service Registry
cd service-registry && mvn spring-boot:run

# Config Server
cd config-server && mvn spring-boot:run

# API Gateway
cd api-gateway && mvn spring-boot:run

# Business Services
cd user-service && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
cd ehr-service && mvn spring-boot:run
cd pharmacy-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd analytics-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### Build All Services
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

---

This summary table provides a quick reference for all services, their configurations, and key information for the MediTrack platform.
