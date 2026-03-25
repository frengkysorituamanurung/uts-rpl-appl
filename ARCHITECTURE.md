# MediTrack - Architecture Design Document

## 1. System Overview

MediTrack menggunakan arsitektur microservices untuk memastikan scalability, maintainability, dan fault tolerance.

## 2. Service Definitions

### 2.1 API Gateway
**Responsibility**: 
- Routing requests ke microservices yang sesuai
- Authentication & authorization
- Rate limiting
- Request/response transformation

**Technology**: Spring Cloud Gateway

**Endpoints**:
- `/api/users/**` → User Service
- `/api/appointments/**` → Appointment Service
- `/api/ehr/**` → EHR Service
- `/api/pharmacy/**` → Pharmacy Service
- `/api/analytics/**` → Analytics Service
- `/api/payments/**` → Payment Service

---

### 2.2 User Service
**Responsibility**:
- User registration & authentication
- User profile management
- Role management (Patient, Doctor, Pharmacist, Admin)
- Authorization & permissions

**Database**: PostgreSQL
- Tables: users, roles, permissions, user_roles

**APIs**:
- POST `/api/users/register` - Register user baru
- POST `/api/users/login` - User authentication
- GET `/api/users/{id}` - Get user profile
- PUT `/api/users/{id}` - Update user profile
- GET `/api/users/doctors` - List all doctors
- GET `/api/users/pharmacists` - List all pharmacists

---

### 2.3 Appointment Service
**Responsibility**:
- Appointment booking
- Appointment rescheduling & cancellation
- Doctor availability management
- Appointment reminders

**Database**: PostgreSQL
- Tables: appointments, doctor_schedules, appointment_slots

**APIs**:
- POST `/api/appointments` - Book appointment
- GET `/api/appointments/{id}` - Get appointment details
- PUT `/api/appointments/{id}` - Reschedule appointment
- DELETE `/api/appointments/{id}` - Cancel appointment
- GET `/api/appointments/patient/{patientId}` - Patient appointments
- GET `/api/appointments/doctor/{doctorId}` - Doctor appointments
- GET `/api/appointments/doctor/{doctorId}/availability` - Doctor availability

**Events Published**:
- AppointmentBooked
- AppointmentCancelled
- AppointmentRescheduled

---

### 2.4 EHR Service (Electronic Health Records)
**Responsibility**:
- Store patient medical history
- Manage prescriptions
- Store lab results
- Medical document management

**Database**: PostgreSQL + File Storage (S3/MinIO)
- Tables: medical_records, prescriptions, lab_results, medical_documents

**APIs**:
- POST `/api/ehr/records` - Create medical record
- GET `/api/ehr/records/patient/{patientId}` - Get patient records
- POST `/api/ehr/prescriptions` - Create prescription
- GET `/api/ehr/prescriptions/{id}` - Get prescription
- POST `/api/ehr/lab-results` - Upload lab results
- GET `/api/ehr/lab-results/patient/{patientId}` - Get lab results

**Security**: 
- Encryption at rest
- HIPAA compliance
- Audit logging

---

### 2.5 Pharmacy Service
**Responsibility**:
- Prescription order management
- Medicine inventory management
- Stock tracking
- Order fulfillment

**Database**: PostgreSQL
- Tables: medicines, pharmacy_inventory, prescription_orders, order_items

**APIs**:
- POST `/api/pharmacy/orders` - Create prescription order
- GET `/api/pharmacy/orders/{id}` - Get order details
- PUT `/api/pharmacy/orders/{id}/status` - Update order status
- GET `/api/pharmacy/inventory` - Get inventory
- PUT `/api/pharmacy/inventory/{medicineId}` - Update stock
- GET `/api/pharmacy/medicines/search` - Search medicines

**Events Published**:
- OrderCreated
- OrderFulfilled
- StockLow

---

### 2.6 Analytics Service
**Responsibility**:
- Patient outcome analytics
- Doctor performance metrics
- Drug usage trends
- Dashboard & reporting

**Database**: PostgreSQL (Read replicas) + Data Warehouse
- Tables: aggregated_metrics, reports

**APIs**:
- GET `/api/analytics/patients/outcomes` - Patient outcomes
- GET `/api/analytics/doctors/performance` - Doctor performance
- GET `/api/analytics/drugs/usage` - Drug usage trends
- GET `/api/analytics/dashboard` - Dashboard data
- POST `/api/analytics/reports/generate` - Generate custom report

**Data Sources**:
- Consumes events from other services
- Periodic batch jobs for aggregation

---

### 2.7 Payment Service
**Responsibility**:
- Payment processing
- Insurance claims management
- Billing & invoicing
- Payment history

**Database**: PostgreSQL
- Tables: payments, invoices, insurance_claims, transactions

**APIs**:
- POST `/api/payments` - Process payment
- GET `/api/payments/{id}` - Get payment details
- POST `/api/payments/insurance-claims` - Submit insurance claim
- GET `/api/payments/insurance-claims/{id}` - Get claim status
- GET `/api/payments/invoices/patient/{patientId}` - Get patient invoices
- POST `/api/payments/refund` - Process refund

**Integration**:
- Payment gateways (Stripe, PayPal)
- Insurance providers APIs

**Events Published**:
- PaymentCompleted
- PaymentFailed
- ClaimSubmitted

---

### 2.8 Notification Service
**Responsibility**:
- Email notifications
- SMS notifications
- Push notifications
- Notification templates

**Database**: PostgreSQL
- Tables: notifications, notification_templates, notification_logs

**APIs**:
- POST `/api/notifications/send` - Send notification
- GET `/api/notifications/user/{userId}` - Get user notifications
- PUT `/api/notifications/{id}/read` - Mark as read

**Events Consumed**:
- AppointmentBooked → Send confirmation
- AppointmentReminder → Send reminder
- PrescriptionReady → Notify patient
- PaymentCompleted → Send receipt

---

### 2.9 Service Registry (Eureka)
**Responsibility**:
- Service discovery
- Health monitoring
- Load balancing

**Technology**: Spring Cloud Netflix Eureka

---

### 2.10 Config Server
**Responsibility**:
- Centralized configuration management
- Environment-specific configs
- Dynamic configuration updates

**Technology**: Spring Cloud Config Server

---

## 3. Communication Patterns

### 3.1 Synchronous Communication
- REST APIs via API Gateway
- Service-to-service calls menggunakan Feign Client
- Circuit breaker pattern (Resilience4j)

### 3.2 Asynchronous Communication
- Message Queue: RabbitMQ / Apache Kafka
- Event-driven architecture
- Eventual consistency

### 3.3 Event Flow Examples

**Appointment Booking Flow**:
1. Patient books appointment via API Gateway
2. Appointment Service creates appointment
3. Publishes `AppointmentBooked` event
4. Notification Service consumes event → sends confirmation email
5. Analytics Service consumes event → updates metrics

**Prescription Order Flow**:
1. Doctor creates prescription in EHR Service
2. Patient orders via Pharmacy Service
3. Publishes `OrderCreated` event
4. Payment Service processes payment
5. Publishes `PaymentCompleted` event
6. Pharmacy Service fulfills order
7. Notification Service sends notification

---

## 4. Data Management

### 4.1 Database per Service
- Setiap service memiliki database sendiri
- No direct database access antar services
- Data consistency via events

### 4.2 Data Consistency
- Saga pattern untuk distributed transactions
- Event sourcing untuk audit trail
- CQRS untuk read-heavy operations (Analytics)

---

## 5. Security

### 5.1 Authentication & Authorization
- JWT tokens
- OAuth2 / OpenID Connect
- Role-based access control (RBAC)

### 5.2 Data Security
- Encryption at rest (database)
- Encryption in transit (TLS/SSL)
- HIPAA compliance untuk medical data
- Audit logging

### 5.3 API Security
- Rate limiting
- API key management
- CORS configuration

---

## 6. Scalability & Performance

### 6.1 Horizontal Scaling
- Stateless services
- Load balancing
- Auto-scaling dengan Kubernetes

### 6.2 Caching
- Redis untuk session management
- Cache frequently accessed data
- Cache invalidation strategy

### 6.3 Database Optimization
- Read replicas
- Connection pooling
- Query optimization
- Indexing strategy

---

## 7. Monitoring & Observability

### 7.1 Logging
- Centralized logging (ELK Stack)
- Structured logging
- Correlation IDs untuk tracing

### 7.2 Metrics
- Prometheus + Grafana
- Service health metrics
- Business metrics

### 7.3 Tracing
- Distributed tracing (Zipkin/Jaeger)
- Request flow visualization

---

## 8. Deployment

### 8.1 Containerization
- Docker containers
- Docker Compose untuk local development

### 8.2 Orchestration
- Kubernetes untuk production
- Helm charts
- CI/CD pipeline

---

## 9. Future Enhancements
- AI/ML untuk diagnosis assistance
- Telemedicine video consultation
- Mobile apps (iOS/Android)
- Blockchain untuk medical records
- IoT device integration
