# Laporan 02: System Decomposition & Modeling

## MediTrack - Digital Healthcare Platform

---

## Daftar Isi
1. [Daftar Modules/Services dan Responsibilities](#a-daftar-modulesservices-dan-responsibilities)
2. [Key Classes/Entities dan Relationships](#b-key-classesentities-dan-relationships)
3. [UML Diagrams](#c-uml-diagrams)

---

## A. Daftar Modules/Services dan Responsibilities

### Overview Decomposition

MediTrack dipecah menjadi 10 microservices berdasarkan bounded contexts dan business capabilities:

```
MediTrack Platform
├── Infrastructure Services
│   ├── Service Registry (Eureka)
│   ├── Config Server
│   └── API Gateway
├── Core Business Services
│   ├── User Service
│   ├── Appointment Service
│   ├── EHR Service
│   ├── Pharmacy Service
│   ├── Payment Service
│   └── Analytics Service
└── Supporting Services
    └── Notification Service
```

---

### 1. API Gateway

**Port**: 8080  
**Technology**: Spring Cloud Gateway

**Responsibilities**:
- Routing requests ke microservices yang sesuai
- Authentication & authorization (JWT validation)
- Rate limiting dan throttling
- Request/response transformation
- Load balancing
- Circuit breaker implementation
- API versioning
- CORS handling

**Key Features**:
- Single entry point untuk semua clients
- Centralized security enforcement
- Request logging dan monitoring
- API composition untuk complex queries

**Endpoints Routing**:
```
/api/v1/users/**        → User Service
/api/v1/appointments/** → Appointment Service
/api/v1/ehr/**          → EHR Service
/api/v1/pharmacy/**     → Pharmacy Service
/api/v1/payments/**     → Payment Service
/api/v1/analytics/**    → Analytics Service
```

---

### 2. Service Registry (Eureka Server)

**Port**: 8761  
**Technology**: Spring Cloud Netflix Eureka

**Responsibilities**:
- Service discovery dan registration
- Health monitoring semua services
- Load balancing information
- Service instance management
- Heartbeat monitoring

**Key Features**:
- Dynamic service discovery
- Automatic failover
- Service health dashboard
- No single point of failure (dapat di-cluster)

---

### 3. Config Server

**Port**: 8888  
**Technology**: Spring Cloud Config

**Responsibilities**:
- Centralized configuration management
- Environment-specific configurations (dev, staging, prod)
- Dynamic configuration updates tanpa restart
- Configuration versioning
- Encryption untuk sensitive data

**Key Features**:
- Git-backed configuration
- Configuration refresh tanpa downtime
- Profile-based configuration
- Secure property encryption

---

### 4. User Service

**Port**: 8081  
**Database**: PostgreSQL (user_db)

**Responsibilities**:
- User registration dan authentication
- User profile management
- Role dan permission management (RBAC)
- Password management (reset, change)
- User session management
- Multi-factor authentication (MFA)
- User verification (email, phone)

**User Types**:
1. **Patient**: End users yang mencari layanan kesehatan
2. **Doctor**: Medical professionals yang memberikan layanan
3. **Pharmacist**: Pharmacy staff yang mengelola obat
4. **Admin**: System administrators

**Key Features**:
- JWT token generation dan validation
- OAuth2 / OpenID Connect support
- Password encryption (BCrypt)
- Account lockout mechanism
- Audit logging untuk security events

**Business Rules**:
- Email harus unique
- Password minimal 8 karakter dengan complexity requirements
- Doctor harus memiliki license number yang valid
- Pharmacist harus terdaftar di pharmacy tertentu

---

### 5. Appointment Service

**Port**: 8082  
**Database**: PostgreSQL (appointment_db)

**Responsibilities**:
- Appointment booking dan management
- Doctor schedule management
- Appointment slot generation
- Appointment rescheduling
- Appointment cancellation
- Appointment reminders
- Waitlist management
- Appointment history

**Key Features**:
- Real-time availability checking
- Conflict detection
- Automatic reminder scheduling
- Cancellation policy enforcement
- Recurring appointment support

**Business Rules**:
- Appointment minimal 1 jam sebelum waktu yang dipilih
- Cancellation gratis jika 24 jam sebelumnya
- Doctor tidak bisa double-booked
- Maximum 3 active appointments per patient
- Appointment duration: 15, 30, 45, atau 60 menit

**Event Publishing**:
- AppointmentBooked
- AppointmentCancelled
- AppointmentRescheduled
- AppointmentCompleted
- AppointmentNoShow

---

### 6. EHR Service (Electronic Health Records)

**Port**: 8083  
**Database**: PostgreSQL (ehr_db)  
**Storage**: S3/MinIO untuk medical documents

**Responsibilities**:
- Medical record management
- Prescription management
- Lab result storage dan retrieval
- Medical document management (X-rays, scans, reports)
- Patient medical history
- Diagnosis recording
- Treatment plan management
- Allergy dan medication tracking

**Key Features**:
- HIPAA compliant data storage
- Encryption at rest dan in transit
- Audit trail untuk semua access
- Version control untuk medical records
- Document scanning dan OCR
- HL7/FHIR standard support

**Business Rules**:
- Hanya doctor yang bisa create/update medical records
- Patient dapat view tapi tidak edit records
- Prescription harus memiliki doctor signature
- Lab results harus verified sebelum release ke patient
- Medical records retention: minimum 7 tahun

**Event Publishing**:
- MedicalRecordCreated
- PrescriptionIssued
- LabResultAvailable
- DiagnosisRecorded

**Security**:
- Role-based access control
- Data masking untuk sensitive information
- Comprehensive audit logging
- Compliance reporting

---

### 7. Pharmacy Service

**Port**: 8084  
**Database**: PostgreSQL (pharmacy_db)

**Responsibilities**:
- Medicine inventory management
- Prescription order processing
- Stock level monitoring
- Medicine catalog management
- Order fulfillment tracking
- Supplier management
- Expiry date tracking
- Batch number tracking

**Key Features**:
- Real-time stock updates
- Low stock alerts
- Automatic reorder suggestions
- Barcode scanning support
- Medicine interaction checking
- Generic medicine suggestions

**Business Rules**:
- Prescription required untuk controlled substances
- Stock tidak boleh negative
- Alert jika stock < minimum level
- Medicine expired tidak bisa dijual
- Maximum order quantity per prescription
- Pharmacist approval required untuk order fulfillment

**Event Publishing**:
- OrderCreated
- OrderFulfilled
- OrderCancelled
- StockLowAlert
- MedicineExpiringSoon

**Integration**:
- EHR Service untuk prescription verification
- Payment Service untuk order payment
- Notification Service untuk order updates

---

### 8. Payment Service

**Port**: 8086  
**Database**: PostgreSQL (payment_db)

**Responsibilities**:
- Payment processing
- Invoice generation
- Insurance claim management
- Payment history tracking
- Refund processing
- Payment method management
- Billing cycle management
- Payment reconciliation

**Key Features**:
- Multiple payment gateway support (Stripe, PayPal)
- Insurance provider integration
- Automatic invoice generation
- Payment reminder system
- Installment payment support
- Payment receipt generation

**Business Rules**:
- Payment harus completed sebelum service delivery
- Refund maksimal 30 hari setelah payment
- Insurance claim verification required
- Maximum 3 payment methods per user
- Failed payment retry mechanism (max 3 attempts)

**Event Publishing**:
- PaymentCompleted
- PaymentFailed
- PaymentRefunded
- InvoiceGenerated
- InsuranceClaimSubmitted
- InsuranceClaimApproved
- InsuranceClaimRejected

**Security**:
- PCI DSS compliance
- Payment data encryption
- Tokenization untuk card information
- Fraud detection mechanism

---

### 9. Analytics Service

**Port**: 8085  
**Database**: PostgreSQL (analytics_db) + Data Warehouse

**Responsibilities**:
- Patient outcome analytics
- Doctor performance metrics
- Drug usage trend analysis
- Revenue analytics
- Appointment statistics
- Custom report generation
- Dashboard data aggregation
- Predictive analytics (future)

**Key Features**:
- Real-time dashboard updates
- Scheduled report generation
- Data visualization support
- Export to PDF/Excel
- Custom query builder
- Trend analysis
- Comparative analytics

**Metrics Tracked**:
- Appointment booking rate
- Appointment cancellation rate
- Average wait time
- Patient satisfaction scores
- Doctor utilization rate
- Revenue per service
- Medicine sales trends
- Insurance claim success rate

**Business Rules**:
- Data aggregation runs daily at midnight
- Reports older than 2 years archived
- Real-time metrics updated every 5 minutes
- Admin-only access untuk sensitive metrics

**Event Consumption**:
- Consumes events dari semua services
- Builds aggregated views
- Updates metrics in real-time

**Data Sources**:
- Read replicas dari operational databases
- Event stream dari message queue
- Batch imports untuk historical data

---

### 10. Notification Service

**Port**: 8087  
**Database**: PostgreSQL (notification_db)

**Responsibilities**:
- Email notification delivery
- SMS notification delivery
- Push notification delivery (future)
- Notification template management
- Notification scheduling
- Notification history tracking
- Delivery status tracking
- Notification preferences management

**Key Features**:
- Multiple channel support (email, SMS, push)
- Template engine dengan variable substitution
- Scheduled notification delivery
- Retry mechanism untuk failed deliveries
- Delivery rate limiting
- Unsubscribe management

**Notification Types**:
- Appointment confirmations
- Appointment reminders (24h, 1h before)
- Prescription ready notifications
- Payment receipts
- Lab result available
- Password reset
- Account verification
- System announcements

**Business Rules**:
- Respect user notification preferences
- No notifications between 10 PM - 7 AM (unless urgent)
- Maximum 5 notifications per day per user
- Retry failed deliveries 3 times dengan exponential backoff
- Unsubscribe link required di email

**Event Consumption**:
- AppointmentBooked → Send confirmation
- AppointmentReminder → Send reminder
- PrescriptionReady → Notify patient
- PaymentCompleted → Send receipt
- LabResultAvailable → Notify patient

**Integration**:
- Email: SendGrid / AWS SES
- SMS: Twilio / AWS SNS
- Push: Firebase Cloud Messaging (future)

---

## Service Dependencies Matrix

| Service | Depends On | Used By |
|---------|-----------|---------|
| API Gateway | All services | All clients |
| Service Registry | None | All services |
| Config Server | None | All services |
| User Service | Config, Registry | API Gateway, All services (auth) |
| Appointment Service | Config, Registry, User | API Gateway, Analytics |
| EHR Service | Config, Registry, User | API Gateway, Pharmacy, Analytics |
| Pharmacy Service | Config, Registry, User, EHR | API Gateway, Payment, Analytics |
| Payment Service | Config, Registry, User | API Gateway, Pharmacy, Analytics |
| Analytics Service | Config, Registry | API Gateway |
| Notification Service | Config, Registry | All services (via events) |

---

## Communication Patterns

### Synchronous (REST API)
- Client → API Gateway → Services
- Service-to-service calls untuk real-time data
- Used for: CRUD operations, queries

### Asynchronous (Message Queue)
- Event publishing via RabbitMQ
- Event consumption by interested services
- Used for: Notifications, analytics updates, cross-service updates

### Hybrid
- Appointment booking: Synchronous (immediate response) + Asynchronous (notifications)
- Payment processing: Synchronous (payment) + Asynchronous (receipt, analytics)

---

## Kesimpulan

### Ringkasan System Decomposition

MediTrack telah berhasil didekomposisi menjadi 10 microservices yang well-defined dengan responsibilities yang jelas:

#### Infrastructure Services (3)
1. **Service Registry**: Service discovery dan health monitoring
2. **Config Server**: Centralized configuration management
3. **API Gateway**: Single entry point dan security enforcement

#### Core Business Services (6)
4. **User Service**: User management dan authentication
5. **Appointment Service**: Appointment scheduling dan management
6. **EHR Service**: Electronic health records dan medical data
7. **Pharmacy Service**: Medicine inventory dan prescription orders
8. **Payment Service**: Payment processing dan insurance claims
9. **Analytics Service**: Reporting dan business intelligence

#### Supporting Services (1)
10. **Notification Service**: Multi-channel notifications

---

### Key Design Principles Applied

#### 1. Bounded Context (DDD)
Setiap service memiliki bounded context yang jelas:
- **Identity Context**: User Service
- **Clinical Context**: Appointment + EHR Services
- **Pharmacy Context**: Pharmacy Service
- **Financial Context**: Payment Service
- **Analytics Context**: Analytics Service
- **Communication Context**: Notification Service

#### 2. Single Responsibility
Setiap service fokus pada satu domain area:
- User Service → Identity & Access
- Appointment Service → Scheduling
- EHR Service → Medical Records
- Pharmacy Service → Medicine Management
- Payment Service → Financial Transactions
- Analytics Service → Data Analysis
- Notification Service → Communication

#### 3. Loose Coupling
Services berkomunikasi via:
- **REST APIs**: Synchronous communication
- **Message Queue**: Asynchronous events
- **No Direct DB Access**: Database per service

#### 4. High Cohesion
Related functionality grouped together:
- Prescription creation di EHR Service
- Prescription ordering di Pharmacy Service
- Prescription payment di Payment Service
- Clear separation of concerns

---

### Entity Relationship Summary

#### Within Services (Strong Consistency)
- JPA relationships (One-to-Many, Many-to-One, etc.)
- Foreign keys dalam database
- ACID transactions

#### Across Services (Eventual Consistency)
- Reference via IDs (patientId, doctorId, etc.)
- Event-driven synchronization
- No foreign keys across databases
- Saga pattern untuk distributed transactions

---

### Communication Patterns

#### Synchronous (REST)
```
Client → API Gateway → Service
- Real-time responses
- CRUD operations
- Query operations
```

#### Asynchronous (Events)
```
Service → RabbitMQ → Interested Services
- Notifications
- Analytics updates
- Cross-service updates
- Eventual consistency
```

---

### Benefits of This Decomposition

#### 1. Scalability
- Scale services independently based on load
- Horizontal scaling per service
- Efficient resource utilization

#### 2. Maintainability
- Clear service boundaries
- Smaller, focused codebases
- Independent deployment
- Easier testing

#### 3. Extensibility
- Easy to add new services
- Technology flexibility per service
- No impact on existing services

#### 4. Resilience
- Fault isolation
- Circuit breaker pattern
- Graceful degradation
- No single point of failure

#### 5. Team Organization
- Teams can work independently
- Clear ownership
- Parallel development
- Reduced coordination overhead

---

### UML Diagrams Coverage

Dokumentasi UML yang telah dibuat mencakup:

1. **Component Diagram**: System architecture overview
2. **Class Diagrams**: 
   - User Service (inheritance hierarchy)
   - Appointment Service
   - EHR Service
   - Pharmacy Service
   - Payment Service
   - Analytics Service
   - Notification Service
3. **Sequence Diagrams**:
   - Appointment booking flow
   - Prescription order flow
   - User authentication flow
4. **State Diagrams**:
   - Appointment lifecycle
   - Prescription order lifecycle
   - Payment lifecycle
5. **Activity Diagram**: Patient registration flow
6. **Deployment Diagram**: Kubernetes deployment

---

### Next Steps

Berdasarkan system decomposition ini, langkah selanjutnya adalah:

1. **Implementation Phase**:
   - Setup project structure
   - Implement domain models
   - Create REST APIs
   - Setup databases
   - Implement event publishing/consuming

2. **Integration Phase**:
   - Service-to-service communication
   - API Gateway configuration
   - Message queue setup
   - Cache implementation

3. **Testing Phase**:
   - Unit tests per service
   - Integration tests
   - Contract tests
   - End-to-end tests

4. **Deployment Phase**:
   - Containerization (Docker)
   - Kubernetes deployment
   - CI/CD pipeline
   - Monitoring setup

---

### Conclusion

System decomposition MediTrack menghasilkan arsitektur microservices yang:
- ✅ Well-structured dengan clear boundaries
- ✅ Scalable dan maintainable
- ✅ Resilient dengan fault isolation
- ✅ Extensible untuk future requirements
- ✅ Compliant dengan healthcare regulations
- ✅ Ready untuk implementation

Dokumentasi lengkap dengan UML diagrams memberikan blueprint yang jelas untuk development team dalam mengimplementasikan platform MediTrack.
