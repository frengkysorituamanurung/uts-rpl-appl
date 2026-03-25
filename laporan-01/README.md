# Laporan 01: Design Thinking & Architecture Selection

## MediTrack - Digital Healthcare Platform

---

## Daftar Isi
1. [Pemilihan Arsitektur & Justifikasi](#a-pemilihan-arsitektur--justifikasi)
2. [Trade-offs dan Limitasi](#b-trade-offs-dan-limitasi)
3. [Dukungan terhadap Scalability, Maintainability, dan Extensibility](#c-dukungan-terhadap-scalability-maintainability-dan-extensibility)

---

## A. Pemilihan Arsitektur & Justifikasi

### Arsitektur Terpilih: **Microservices Architecture**

Untuk platform MediTrack, kami memilih **Microservices Architecture** sebagai fondasi sistem. Keputusan ini didasarkan pada analisis mendalam terhadap requirements platform yang kompleks dan ekspektasi pertumbuhan di masa depan.

### Justifikasi Berdasarkan Prinsip Arsitektur

#### 1. **Single Responsibility Principle (SRP)**

**Penerapan:**
Setiap microservice di MediTrack memiliki tanggung jawab yang jelas dan terbatas:
- **User Service**: Fokus hanya pada manajemen user dan autentikasi
- **Appointment Service**: Menangani scheduling dan availability
- **EHR Service**: Khusus untuk medical records dan data kesehatan
- **Pharmacy Service**: Dedicated untuk inventory dan prescription orders
- **Payment Service**: Isolasi untuk payment processing dan insurance claims
- **Analytics Service**: Terpisah untuk reporting dan data analysis
- **Notification Service**: Spesialisasi dalam pengiriman notifikasi

**Keuntungan:**
- Setiap service dapat dikembangkan, di-deploy, dan di-scale secara independen
- Tim dapat bekerja paralel tanpa konflik
- Perubahan pada satu service tidak mempengaruhi service lain
- Lebih mudah untuk memahami dan maintain codebase yang lebih kecil

**Contoh Kasus:**
Jika ada perubahan regulasi HIPAA yang mempengaruhi penyimpanan medical records, tim hanya perlu fokus pada EHR Service tanpa menyentuh service lain. Deployment dapat dilakukan tanpa downtime pada service lainnya.

---

#### 2. **Separation of Concerns (SoC)**

**Penerapan:**
Pemisahan concerns dilakukan di berbagai level:

**Level Business Domain:**
- Healthcare operations (Appointment, EHR) terpisah dari business operations (Payment, Analytics)
- Clinical data (EHR) terpisah dari operational data (Pharmacy inventory)
- User management terpisah dari business logic

**Level Technical:**
- **API Gateway**: Menangani routing, authentication, rate limiting
- **Service Registry**: Fokus pada service discovery
- **Config Server**: Centralized configuration management
- **Message Queue**: Asynchronous communication

**Level Data:**
- Database per service (Database-per-Service pattern)
- Setiap service memiliki data store sendiri
- No direct database access antar services

**Keuntungan:**
- Perubahan pada satu concern tidak mempengaruhi concern lainnya
- Memudahkan compliance dengan regulasi healthcare (HIPAA, GDPR)
- Flexibility dalam memilih technology stack per service
- Isolasi failure - jika satu service down, yang lain tetap berjalan

**Contoh Kasus:**
Ketika Payment Service mengalami high load saat akhir bulan (billing cycle), service lainnya seperti Appointment atau EHR tetap berjalan normal. Analytics Service dapat menggunakan read replicas tanpa mempengaruhi performa operational services.

---

#### 3. **Loose Coupling & High Cohesion**

**Penerapan:**

**Loose Coupling:**
- Services berkomunikasi via REST APIs dan message queues
- No direct dependencies antar services
- Event-driven architecture untuk asynchronous operations
- API Gateway sebagai single entry point
- Service discovery untuk dynamic service location

**High Cohesion:**
- Setiap service mengelompokkan functionality yang related
- Business logic yang berkaitan berada dalam satu service
- Shared utilities di common-lib module

**Communication Patterns:**
```
Synchronous (REST):
API Gateway → User Service (authentication)
API Gateway → Appointment Service (booking)

Asynchronous (Events):
Appointment Service → [AppointmentBooked Event] → Notification Service
Payment Service → [PaymentCompleted Event] → Analytics Service
Pharmacy Service → [StockLow Event] → Notification Service
```

**Keuntungan:**
- Services dapat di-deploy dan di-update independently
- Failure isolation - circuit breaker pattern mencegah cascade failures
- Technology diversity - setiap service bisa menggunakan tech stack yang paling sesuai
- Easier testing - unit test per service, integration test via contracts

**Contoh Kasus:**
Ketika Notification Service down untuk maintenance, appointment booking tetap berjalan. Events disimpan di message queue dan akan diproses ketika Notification Service kembali online. Tidak ada data loss dan user experience tidak terganggu.

---

### Prinsip Arsitektur Tambahan yang Mendukung

#### 4. **Scalability by Design**

- Horizontal scaling per service berdasarkan load
- Stateless services memudahkan scaling
- Load balancing otomatis via service registry
- Database read replicas untuk read-heavy services (Analytics)

#### 5. **Resilience & Fault Tolerance**

- Circuit breaker pattern (Resilience4j)
- Retry mechanisms dengan exponential backoff
- Fallback strategies
- Health checks dan monitoring

#### 6. **Security by Design**

- API Gateway untuk centralized authentication
- JWT tokens untuk stateless authentication
- Role-based access control (RBAC)
- Encryption at rest dan in transit
- Audit logging untuk compliance

---

## B. Trade-offs dan Limitasi

### Trade-offs

#### 1. **Kompleksitas vs Flexibility**

**Trade-off:**
- ✅ **Gain**: Flexibility tinggi, setiap service dapat dikembangkan dengan technology stack yang berbeda
- ❌ **Cost**: Kompleksitas operasional meningkat signifikan

**Detail:**
- Membutuhkan expertise dalam distributed systems
- Learning curve lebih tinggi untuk developer baru
- Debugging lebih challenging karena request flow melintasi multiple services
- Membutuhkan sophisticated monitoring dan tracing tools (Zipkin, Jaeger)

**Mitigasi:**
- Investasi dalam observability tools (ELK Stack, Prometheus, Grafana)
- Comprehensive documentation dan architecture diagrams
- Standardisasi development practices via common-lib
- Training program untuk team members

---

#### 2. **Data Consistency vs Autonomy**

**Trade-off:**
- ✅ **Gain**: Service autonomy, setiap service memiliki database sendiri
- ❌ **Cost**: Eventual consistency, tidak ada ACID transactions across services

**Detail:**
- Tidak bisa melakukan JOIN queries across services
- Distributed transactions memerlukan Saga pattern
- Data duplication untuk performance (denormalization)
- Potential data inconsistency window

**Contoh Skenario:**
```
Appointment Booking Flow:
1. Appointment Service creates appointment
2. Publishes AppointmentBooked event
3. Notification Service sends email (eventual)
4. Analytics Service updates metrics (eventual)

Jika Notification Service down, email tertunda tapi appointment tetap tercatat.
```

**Mitigasi:**
- Implement Saga pattern untuk distributed transactions
- Event sourcing untuk audit trail
- Compensating transactions untuk rollback
- Idempotent operations untuk retry safety
- Message queue dengan guaranteed delivery

---

#### 3. **Network Latency vs Service Independence**

**Trade-off:**
- ✅ **Gain**: Services dapat di-deploy dan di-scale independently
- ❌ **Cost**: Network calls menambah latency

**Detail:**
- Inter-service communication via network (REST/messaging)
- Latency accumulation pada chained service calls
- Network failures dapat menyebabkan service unavailability
- Bandwidth consumption lebih tinggi

**Contoh:**
```
Monolithic: 1 method call = ~1ms
Microservices: 1 REST call = ~50-100ms

Chained calls:
API Gateway → User Service → Appointment Service → EHR Service
Total latency: 150-300ms vs 3ms di monolithic
```

**Mitigasi:**
- Caching strategy (Redis) untuk frequently accessed data
- Asynchronous communication untuk non-critical operations
- API Gateway aggregation untuk mengurangi client-side calls
- Service mesh untuk optimized routing
- Circuit breaker untuk fast failure

---

#### 4. **Operational Overhead vs Scalability**

**Trade-off:**
- ✅ **Gain**: Granular scaling, hanya scale service yang membutuhkan
- ❌ **Cost**: Infrastructure dan operational complexity

**Detail:**
- Membutuhkan container orchestration (Kubernetes)
- Multiple databases untuk di-manage
- CI/CD pipeline lebih kompleks
- Monitoring multiple services
- Log aggregation dan distributed tracing

**Resource Requirements:**
```
Monolithic:
- 1 application server
- 1 database
- Simple deployment

Microservices:
- 10+ application instances
- 6+ databases
- Service registry, API Gateway, Config Server
- Message queue, Cache
- Monitoring stack
```

**Mitigasi:**
- Infrastructure as Code (Terraform, Helm)
- Automated deployment pipelines
- Centralized logging dan monitoring
- Container orchestration (Kubernetes)
- Cloud-managed services untuk reduce operational burden

---

### Limitasi

#### 1. **Testing Complexity**

**Limitasi:**
- Integration testing memerlukan multiple services running
- End-to-end testing lebih kompleks
- Test data management across services
- Contract testing untuk API compatibility

**Solusi:**
- Contract testing (Pact, Spring Cloud Contract)
- Service virtualization untuk testing
- Comprehensive unit tests per service
- Automated integration test suites

---

#### 2. **Data Management Challenges**

**Limitasi:**
- Tidak ada global transactions
- Data aggregation untuk reporting memerlukan effort ekstra
- Data migration lebih kompleks
- Referential integrity across services

**Solusi:**
- Event-driven data synchronization
- CQRS pattern untuk Analytics Service
- Data warehouse untuk reporting
- API composition untuk data aggregation

---

#### 3. **Deployment Complexity**

**Limitasi:**
- Coordinated deployment untuk breaking changes
- Version compatibility management
- Rollback strategy lebih kompleks
- Database migration coordination

**Solusi:**
- Blue-green deployment
- Canary releases
- API versioning strategy
- Backward compatibility requirements
- Feature flags

---

#### 4. **Initial Development Cost**

**Limitasi:**
- Lebih lama untuk initial setup
- Membutuhkan infrastructure setup di awal
- Learning curve untuk team
- Higher initial cost

**Justifikasi:**
- Long-term benefits outweigh initial cost
- Faster feature development setelah setup
- Better ROI untuk growing platform
- Reduced technical debt

---

## C. Dukungan terhadap Scalability, Maintainability, dan Extensibility

### 1. Scalability (Skalabilitas)

#### 1.1 Horizontal Scaling

**Implementasi:**
```
Load Distribution:
- User Service: 3 instances (high traffic)
- Appointment Service: 5 instances (peak hours)
- EHR Service: 4 instances (data-intensive)
- Payment Service: 3 instances (transaction processing)
- Analytics Service: 2 instances (batch processing)
- Pharmacy Service: 2 instances (moderate load)
- Notification Service: 3 instances (high volume)
```

**Keuntungan:**
- Scale hanya service yang membutuhkan
- Cost-effective - tidak perlu scale seluruh aplikasi
- Auto-scaling berdasarkan metrics (CPU, memory, request rate)
- Load balancing otomatis via service registry

**Contoh Skenario:**
```
Peak Hours (08:00 - 10:00):
- Appointment Service: scale dari 2 → 5 instances
- User Service: scale dari 2 → 3 instances
- Services lain: tetap di baseline

Billing Cycle (akhir bulan):
- Payment Service: scale dari 2 → 4 instances
- Analytics Service: scale dari 1 → 3 instances
```

---

#### 1.2 Database Scalability

**Strategy:**

**Read Replicas:**
```
Analytics Service:
- 1 Master (write)
- 3 Read Replicas (read-heavy queries)
- Reduces load on master database
```

**Sharding (Future):**
```
EHR Service:
- Shard by patient_id
- Distributes data across multiple databases
- Supports millions of patient records
```

**Caching:**
```
Redis Cache:
- User profiles (TTL: 1 hour)
- Doctor availability (TTL: 15 minutes)
- Medicine catalog (TTL: 24 hours)
- Reduces database load by 60-70%
```

---

#### 1.3 Asynchronous Processing

**Message Queue Benefits:**
- Decouples services
- Handles traffic spikes
- Guaranteed message delivery
- Retry mechanisms

**Use Cases:**
```
High Volume Operations:
- Notification sending (email, SMS)
- Analytics data processing
- Report generation
- Batch operations

Example:
1000 appointments booked → 1000 notification events
Notification Service processes queue at its own pace
No blocking, no data loss
```

---

#### 1.4 Geographic Distribution (Future)

**Multi-Region Deployment:**
```
Region 1 (Jakarta):
- Primary services
- Main databases

Region 2 (Surabaya):
- Read replicas
- Cache nodes
- Reduced latency for East Java users

Region 3 (Medan):
- Read replicas
- Cache nodes
- Reduced latency for Sumatra users
```

---

### 2. Maintainability (Kemudahan Pemeliharaan)

#### 2.1 Independent Development & Deployment

**Team Structure:**
```
Team 1: User & Authentication
- User Service
- API Gateway security

Team 2: Clinical Operations
- Appointment Service
- EHR Service

Team 3: Business Operations
- Pharmacy Service
- Payment Service

Team 4: Platform & Infrastructure
- Service Registry
- Config Server
- Monitoring
```

**Benefits:**
- Teams bekerja paralel tanpa konflik
- Faster development cycles
- Clear ownership dan responsibility
- Reduced coordination overhead

---

#### 2.2 Isolated Changes & Testing

**Deployment Independence:**
```
Scenario: Update Appointment Service
1. Develop & test in isolation
2. Deploy hanya Appointment Service
3. Other services tidak terpengaruh
4. Rollback mudah jika ada issue

vs Monolithic:
1. Change satu feature
2. Test entire application
3. Deploy entire application
4. Risk affects all features
```

**Testing Strategy:**
```
Unit Tests:
- Per service, isolated
- Fast execution
- High coverage

Integration Tests:
- Contract testing
- Service virtualization
- Automated test suites

E2E Tests:
- Critical user journeys
- Staging environment
- Automated regression tests
```

---

#### 2.3 Code Organization & Clarity

**Service Structure:**
```
appointment-service/
├── src/main/java/com/meditrack/appointment/
│   ├── controller/      # REST endpoints
│   ├── service/         # Business logic
│   ├── repository/      # Data access
│   ├── model/           # Domain models
│   ├── dto/             # Data transfer objects
│   ├── config/          # Configuration
│   └── exception/       # Exception handling
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/    # Flyway migrations
└── src/test/
```

**Benefits:**
- Smaller, focused codebases
- Easier to understand
- Faster onboarding untuk new developers
- Reduced cognitive load

---

#### 2.4 Monitoring & Debugging

**Observability Stack:**

**Logging (ELK Stack):**
```
- Centralized logging
- Structured logs dengan correlation IDs
- Log aggregation dari semua services
- Search & analysis capabilities

Example Log:
{
  "timestamp": "2026-03-25T10:30:00Z",
  "service": "appointment-service",
  "correlationId": "abc-123-def",
  "level": "INFO",
  "message": "Appointment created",
  "userId": "user-456",
  "appointmentId": "appt-789"
}
```

**Metrics (Prometheus + Grafana):**
```
Service Metrics:
- Request rate
- Response time
- Error rate
- CPU/Memory usage

Business Metrics:
- Appointments per hour
- Payment success rate
- Average wait time
- User registrations
```

**Distributed Tracing (Zipkin/Jaeger):**
```
Request Flow Visualization:
API Gateway → User Service (50ms)
           → Appointment Service (120ms)
           → EHR Service (80ms)
Total: 250ms

Identifies bottlenecks dan slow services
```

**Health Checks:**
```
Each Service Exposes:
- /actuator/health
- /actuator/metrics
- /actuator/info

Kubernetes liveness & readiness probes
Automatic restart jika unhealthy
```

---

#### 2.5 Documentation & Knowledge Management

**API Documentation:**
- OpenAPI/Swagger untuk setiap service
- Auto-generated dari code
- Interactive API testing
- Version history

**Architecture Documentation:**
- Architecture Decision Records (ADRs)
- Service dependency diagrams
- Data flow diagrams
- Deployment guides

---

### 3. Extensibility (Kemampuan Pengembangan)

#### 3.1 Adding New Services

**Ease of Extension:**
```
Scenario: Menambah Telemedicine Service

Steps:
1. Create new service module
2. Register dengan Service Registry
3. Add routes di API Gateway
4. Publish/consume events via message queue
5. Deploy independently

No changes needed di existing services!
```

**Example - Video Consultation Service:**
```
New Service:
- telemedicine-service (Port: 8089)

Capabilities:
- Video call scheduling
- Session management
- Recording storage

Integration:
- Consumes AppointmentBooked events
- Publishes ConsultationCompleted events
- Integrates dengan existing services via events
```

---

#### 3.2 Technology Flexibility

**Polyglot Architecture:**
```
Current (All Java 21):
- Consistency untuk initial development
- Shared expertise

Future Possibilities:
- Analytics Service → Python (ML/AI capabilities)
- Notification Service → Node.js (real-time)
- Pharmacy Service → Go (high performance)

Each service dapat menggunakan tech stack terbaik untuk use case-nya
```

---

#### 3.3 Feature Additions

**Non-Breaking Changes:**
```
Scenario: Add Lab Test Booking

Option 1: Extend Appointment Service
- Add new endpoints
- Backward compatible
- No impact on existing features

Option 2: New Lab Service
- Dedicated service untuk lab operations
- Integrates via events
- Complete isolation
```

**API Versioning:**
```
Support multiple API versions:
- /api/v1/appointments (legacy)
- /api/v2/appointments (new features)

Gradual migration, no breaking changes
```

---

#### 3.4 Third-Party Integrations

**Integration Points:**

**Payment Gateways:**
```
Payment Service:
- Current: Stripe integration
- Add: PayPal, GoPay, OVO
- Strategy pattern untuk multiple providers
- No changes di other services
```

**Insurance Providers:**
```
Payment Service:
- API integrations dengan insurance companies
- Claims submission automation
- Real-time verification
- Isolated dalam Payment Service
```

**External Labs:**
```
EHR Service:
- Lab result integration
- HL7/FHIR standards
- Automated result import
- Contained dalam EHR Service
```

**Notification Channels:**
```
Notification Service:
- Current: Email, SMS
- Add: WhatsApp, Telegram, Push notifications
- Plugin architecture
- No impact on event publishers
```

---

#### 3.5 Data & Analytics Extensions

**Analytics Capabilities:**

**Current:**
- Basic reporting
- Dashboard metrics
- Trend analysis

**Future Extensions:**
```
AI/ML Features:
- Predictive analytics (patient no-show prediction)
- Disease outbreak detection
- Treatment recommendation engine
- Resource optimization

Implementation:
- New ML Service
- Consumes data via events
- Provides predictions via API
- No changes to existing services
```

**Data Warehouse:**
```
Future Addition:
- Separate data warehouse
- ETL pipelines dari operational databases
- Complex analytics queries
- Business intelligence tools
- No impact on operational services
```

---

#### 3.6 Mobile & Web Extensions

**Multi-Platform Support:**

**Current:**
- REST APIs via API Gateway

**Future:**
```
Mobile Apps:
- iOS app
- Android app
- Same backend APIs
- Push notification support

Web Portal:
- Patient portal
- Doctor portal
- Admin dashboard
- Pharmacy portal

All consume same microservices via API Gateway
```

---

#### 3.7 Compliance & Regulatory Extensions

**Healthcare Regulations:**

**Current:**
- Basic HIPAA compliance
- Data encryption
- Audit logging

**Future Extensions:**
```
Enhanced Compliance:
- GDPR compliance module
- Data retention policies
- Right to be forgotten
- Consent management

Implementation:
- Compliance Service
- Integrates dengan all services
- Audit trail aggregation
- Policy enforcement
```

---

## Kesimpulan

### Ringkasan Keputusan Arsitektur

Pemilihan **Microservices Architecture** untuk MediTrack didasarkan pada:

1. **Prinsip Arsitektur yang Kuat:**
   - Single Responsibility Principle
   - Separation of Concerns
   - Loose Coupling & High Cohesion

2. **Trade-offs yang Dapat Diterima:**
   - Kompleksitas operasional vs flexibility
   - Eventual consistency vs service autonomy
   - Network latency vs independence
   - Operational overhead vs scalability

3. **Dukungan Komprehensif untuk:**
   - **Scalability**: Horizontal scaling, database optimization, caching
   - **Maintainability**: Independent deployment, clear ownership, observability
   - **Extensibility**: Easy service addition, technology flexibility, third-party integration

### Rekomendasi Implementasi

**Phase 1 (MVP - 3 bulan):**
- Core services: User, Appointment, EHR
- Basic infrastructure: Service Registry, API Gateway
- Single database per service
- Simple deployment

**Phase 2 (Growth - 6 bulan):**
- Add: Pharmacy, Payment, Notification services
- Implement message queue
- Add caching layer
- Enhanced monitoring

**Phase 3 (Scale - 12 bulan):**
- Add: Analytics service
- Implement CQRS
- Multi-region deployment
- Advanced features (AI/ML)

### Kesimpulan Akhir

Microservices architecture memberikan foundation yang solid untuk MediTrack dalam menghadapi kompleksitas healthcare domain dan ekspektasi pertumbuhan platform. Meskipun memiliki trade-offs dalam hal kompleksitas, benefits jangka panjang dalam scalability, maintainability, dan extensibility menjadikan pilihan ini optimal untuk platform healthcare yang modern dan future-proof.
