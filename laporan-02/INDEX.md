# Laporan 02 - System Decomposition & Modeling

## 📋 Daftar Dokumen

### 1. README.md - Laporan Utama
**Isi**: Penjelasan lengkap tentang system decomposition MediTrack

**Bagian A - Daftar Modules/Services dan Responsibilities**:
- Overview decomposition
- Detail 10 microservices:
  - API Gateway
  - Service Registry (Eureka)
  - Config Server
  - User Service
  - Appointment Service
  - EHR Service
  - Pharmacy Service
  - Payment Service
  - Analytics Service
  - Notification Service
- Service dependencies matrix
- Communication patterns

**Kesimpulan**:
- Ringkasan system decomposition
- Key design principles
- Benefits of decomposition
- Next steps

---

### 2. entities-and-relationships.md
**Isi**: Detail entities dan relationships untuk setiap service

**Bagian B - Key Classes/Entities dan Relationships**:

1. **User Service Domain Model**
   - User (abstract base class)
   - Patient, Doctor, Pharmacist, Admin (inheritance)
   - Role, Permission
   - Address (value object)
   - Enums: Gender, UserStatus, BloodType, AdminLevel

2. **Appointment Service Domain Model**
   - Appointment
   - DoctorSchedule
   - AppointmentSlot
   - AppointmentHistory
   - Waitlist
   - Enums: AppointmentType, AppointmentStatus, SlotStatus

3. **EHR Service Domain Model**
   - MedicalRecord
   - VitalSigns (value object)
   - Prescription, PrescriptionItem
   - LabResult
   - MedicalDocument
   - Allergy
   - Enums: RecordStatus, PrescriptionStatus, DocumentType

4. **Pharmacy Service Domain Model**
   - Medicine
   - PharmacyInventory
   - PrescriptionOrder, OrderItem
   - StockMovement
   - Supplier
   - Enums: MedicineCategory, OrderStatus, MovementType

5. **Payment Service Domain Model**
   - Payment
   - Invoice, InvoiceItem
   - InsuranceClaim, ClaimDocument
   - PaymentMethod
   - Refund
   - Enums: PaymentStatus, InvoiceStatus, ClaimStatus

6. **Analytics Service Domain Model**
   - AppointmentMetrics
   - DoctorPerformance
   - DrugUsageTrend
   - RevenueMetrics
   - PatientOutcome

7. **Notification Service Domain Model**
   - Notification
   - NotificationTemplate
   - NotificationPreference
   - Enums: NotificationType, NotificationChannel

**Cross-Service Relationships**:
- Logical relationships via IDs
- Event-driven relationships

---

### 3. uml-diagrams.md
**Isi**: UML diagrams menggunakan Mermaid syntax

**Bagian C - UML Diagrams**:

1. **Component Diagram**
   - System overview
   - All services and their connections

2. **Class Diagrams** (7 diagrams):
   - User Service (dengan inheritance hierarchy)
   - Appointment Service
   - EHR Service
   - Pharmacy Service
   - Payment Service
   - Analytics Service
   - Notification Service

3. **Sequence Diagrams** (3 diagrams):
   - Appointment booking flow
   - Prescription order flow
   - User authentication flow

4. **State Diagrams** (3 diagrams):
   - Appointment lifecycle
   - Prescription order lifecycle
   - Payment lifecycle

5. **Activity Diagram**:
   - Patient registration flow

6. **Deployment Diagram**:
   - Kubernetes deployment architecture

**Total**: 16 UML diagrams

---

### 4. service-summary-table.md
**Isi**: Quick reference tables untuk semua services

**Sections**:
1. **Service Summary Table**
   - Service name, port, database, responsibilities, entities

2. **Service Dependencies Matrix**
   - Dependencies, consumers, events

3. **API Endpoints Summary**
   - Complete API list untuk setiap service

4. **Database Schema Summary**
   - Tables per service database

5. **Event Catalog**
   - Published events dengan payload dan consumers

6. **Technology Stack per Service**
   - Framework, database, cache, message queue

7. **Scalability Configuration**
   - Instance count recommendations
   - Resource allocation

8. **Security Configuration**
   - Authentication, authorization, roles

9. **Monitoring & Health Checks**
   - Health check endpoints
   - Key metrics

10. **Quick Start Commands**
    - Docker, Maven commands

---

## 🎯 Cara Menggunakan Dokumentasi Ini

### Untuk Memahami Arsitektur:
1. Mulai dari **README.md** - Bagian A untuk overview services
2. Lihat **uml-diagrams.md** - Component Diagram untuk visualisasi
3. Baca **service-summary-table.md** untuk quick reference

### Untuk Memahami Domain Model:
1. Baca **entities-and-relationships.md** untuk detail entities
2. Lihat **uml-diagrams.md** - Class Diagrams untuk visualisasi
3. Pahami relationships antar entities

### Untuk Memahami Flow:
1. Lihat **uml-diagrams.md** - Sequence Diagrams
2. Pahami **uml-diagrams.md** - State Diagrams
3. Lihat **service-summary-table.md** - Event Catalog

### Untuk Implementation:
1. Gunakan **entities-and-relationships.md** sebagai blueprint
2. Refer ke **service-summary-table.md** untuk API endpoints
3. Follow **uml-diagrams.md** untuk business logic flow

---

## 📊 Statistik Dokumentasi

### Services Documented: 10
- Infrastructure: 3 (Registry, Config, Gateway)
- Core Business: 6 (User, Appointment, EHR, Pharmacy, Payment, Analytics)
- Supporting: 1 (Notification)

### Entities Documented: 50+
- User Service: 7 entities
- Appointment Service: 5 entities
- EHR Service: 7 entities
- Pharmacy Service: 6 entities
- Payment Service: 7 entities
- Analytics Service: 5 entities
- Notification Service: 3 entities

### UML Diagrams: 16
- Component: 1
- Class: 7
- Sequence: 3
- State: 3
- Activity: 1
- Deployment: 1

### API Endpoints: 60+
- User Service: 7 endpoints
- Appointment Service: 8 endpoints
- EHR Service: 9 endpoints
- Pharmacy Service: 8 endpoints
- Payment Service: 8 endpoints
- Analytics Service: 7 endpoints
- Notification Service: 5 endpoints

### Events: 11
- Appointment Service: 3 events
- EHR Service: 2 events
- Pharmacy Service: 3 events
- Payment Service: 3 events

---

## 🔗 Navigasi Cepat

| Topik | File | Section |
|-------|------|---------|
| Service Overview | README.md | Bagian A |
| Service Responsibilities | README.md | Bagian A |
| Entity Details | entities-and-relationships.md | Bagian B |
| Class Diagrams | uml-diagrams.md | Sections 2-8 |
| Sequence Diagrams | uml-diagrams.md | Sections 9-11 |
| State Diagrams | uml-diagrams.md | Sections 13-15 |
| API Reference | service-summary-table.md | API Endpoints Summary |
| Database Schema | service-summary-table.md | Database Schema Summary |
| Event Catalog | service-summary-table.md | Event Catalog |
| Quick Reference | service-summary-table.md | All sections |

---

## ✅ Checklist Kelengkapan

### Bagian A - Modules/Services dan Responsibilities
- [x] List semua services (10 services)
- [x] Detail responsibilities per service
- [x] Service dependencies
- [x] Communication patterns
- [x] Business rules per service
- [x] Event publishing/consuming

### Bagian B - Key Classes/Entities dan Relationships
- [x] Entities untuk User Service
- [x] Entities untuk Appointment Service
- [x] Entities untuk EHR Service
- [x] Entities untuk Pharmacy Service
- [x] Entities untuk Payment Service
- [x] Entities untuk Analytics Service
- [x] Entities untuk Notification Service
- [x] Relationships within services
- [x] Relationships across services
- [x] Enums dan value objects

### Bagian C - UML Diagrams
- [x] Component diagram
- [x] Class diagrams (7 services)
- [x] Sequence diagrams (3 flows)
- [x] State diagrams (3 lifecycles)
- [x] Activity diagram
- [x] Deployment diagram
- [x] Menggunakan Mermaid syntax
- [x] Dapat di-render di Markdown viewers

---

## 📝 Catatan Penting

1. **Mermaid Diagrams**: Semua UML diagrams menggunakan Mermaid syntax yang dapat di-render di:
   - GitHub
   - GitLab
   - VS Code (dengan Mermaid extension)
   - Markdown viewers yang support Mermaid

2. **Entity Relationships**: 
   - Within service: Strong consistency (JPA relationships)
   - Across services: Eventual consistency (via IDs dan events)

3. **API Versioning**: Semua APIs menggunakan `/api/v1/` prefix untuk versioning

4. **Event-Driven**: Asynchronous communication menggunakan RabbitMQ untuk loose coupling

5. **Database per Service**: Setiap service memiliki database sendiri untuk data isolation

---

## 🚀 Next Steps

Setelah memahami dokumentasi ini:

1. **Review dengan Team**: Diskusikan design decisions
2. **Validate dengan Stakeholders**: Pastikan business requirements terpenuhi
3. **Start Implementation**: Gunakan dokumentasi sebagai blueprint
4. **Iterate**: Update dokumentasi seiring development

---

**Dokumentasi dibuat untuk**: MediTrack Digital Healthcare Platform  
**Arsitektur**: Microservices  
**Tanggal**: 2026  
**Versi**: 1.0
