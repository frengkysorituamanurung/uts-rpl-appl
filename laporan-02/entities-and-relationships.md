# B. Key Classes/Entities dan Relationships

## 1. User Service - Domain Model

### Entities

#### User (Base Entity)
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Address address;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Role> roles;
}
```

#### Patient (extends User)
```java
@Entity
public class Patient extends User {
    private String patientId;
    private String emergencyContact;
    private String emergencyPhone;
    private BloodType bloodType;
    private List<String> allergies;
    private String insuranceProvider;
    private String insuranceNumber;
}
```

#### Doctor (extends User)
```java
@Entity
public class Doctor extends User {
    private String doctorId;
    private String licenseNumber;
    private String specialization;
    private Integer yearsOfExperience;
    private String qualification;
    private String hospitalAffiliation;
    private BigDecimal consultationFee;
    private DoctorStatus status;
    private Double rating;
    private Integer totalReviews;
}
```

#### Pharmacist (extends User)
```java
@Entity
public class Pharmacist extends User {
    private String pharmacistId;
    private String licenseNumber;
    private String pharmacyName;
    private String pharmacyAddress;
    private LocalDate licenseExpiryDate;
}
```

#### Admin (extends User)
```java
@Entity
public class Admin extends User {
    private String adminId;
    private String department;
    private AdminLevel level;
}
```

#### Role
```java
@Entity
public class Role {
    private Long id;
    private String name; // ROLE_PATIENT, ROLE_DOCTOR, etc.
    private String description;
    private Set<Permission> permissions;
}
```

#### Permission
```java
@Entity
public class Permission {
    private Long id;
    private String name; // READ_EHR, WRITE_PRESCRIPTION, etc.
    private String resource;
    private String action;
}
```

#### Address (Embeddable)
```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
```

### Enums
```java
enum Gender { MALE, FEMALE, OTHER }
enum UserStatus { ACTIVE, INACTIVE, SUSPENDED, DELETED }
enum DoctorStatus { AVAILABLE, BUSY, ON_LEAVE, RETIRED }
enum BloodType { A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, 
                 O_POSITIVE, O_NEGATIVE, AB_POSITIVE, AB_NEGATIVE }
enum AdminLevel { SUPER_ADMIN, ADMIN, MODERATOR }
```

### Relationships
- User 1:N Role (Many-to-Many via user_roles table)
- Role 1:N Permission (Many-to-Many via role_permissions table)
- User 1:1 Address (Embedded)

---

## 2. Appointment Service - Domain Model

### Entities

#### Appointment
```java
@Entity
public class Appointment {
    private Long id;
    private String appointmentNumber;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private Integer durationMinutes;
    private AppointmentType type;
    private AppointmentStatus status;
    private String reasonForVisit;
    private String symptoms;
    private String notes;
    private BigDecimal consultationFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}
```

#### DoctorSchedule
```java
@Entity
public class DoctorSchedule {
    private Long id;
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Boolean isAvailable;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}
```

#### AppointmentSlot
```java
@Entity
public class AppointmentSlot {
    private Long id;
    private Long doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;
    private Long appointmentId; // if booked
}
```

#### AppointmentHistory
```java
@Entity
public class AppointmentHistory {
    private Long id;
    private Long appointmentId;
    private AppointmentStatus previousStatus;
    private AppointmentStatus newStatus;
    private String changedBy;
    private String reason;
    private LocalDateTime changedAt;
}
```

#### Waitlist
```java
@Entity
public class Waitlist {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate preferredDate;
    private WaitlistStatus status;
    private LocalDateTime createdAt;
}
```

### Enums
```java
enum AppointmentType { CONSULTATION, FOLLOW_UP, EMERGENCY, ROUTINE_CHECKUP }
enum AppointmentStatus { SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, 
                         CANCELLED, NO_SHOW, RESCHEDULED }
enum SlotStatus { AVAILABLE, BOOKED, BLOCKED }
enum WaitlistStatus { ACTIVE, NOTIFIED, BOOKED, EXPIRED }
```

### Relationships
- Appointment N:1 Patient (via patientId)
- Appointment N:1 Doctor (via doctorId)
- DoctorSchedule N:1 Doctor (via doctorId)
- AppointmentSlot N:1 Doctor (via doctorId)
- AppointmentSlot 1:1 Appointment (optional)
- AppointmentHistory N:1 Appointment

---

## 3. EHR Service - Domain Model

### Entities

#### MedicalRecord
```java
@Entity
public class MedicalRecord {
    private Long id;
    private String recordNumber;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private LocalDateTime visitDate;
    private String chiefComplaint;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
    private VitalSigns vitalSigns;
    private RecordStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### VitalSigns (Embeddable)
```java
@Embeddable
public class VitalSigns {
    private Double temperature; // Celsius
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Double weight; // kg
    private Double height; // cm
    private Double bmi;
    private Integer oxygenSaturation;
}
```

#### Prescription
```java
@Entity
public class Prescription {
    private Long id;
    private String prescriptionNumber;
    private Long patientId;
    private Long doctorId;
    private Long medicalRecordId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private PrescriptionStatus status;
    private String notes;
    private List<PrescriptionItem> items;
    private LocalDateTime createdAt;
}
```

#### PrescriptionItem
```java
@Entity
public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private String instructions;
    private Integer quantity;
}
```

#### LabResult
```java
@Entity
public class LabResult {
    private Long id;
    private String resultNumber;
    private Long patientId;
    private Long doctorId;
    private Long medicalRecordId;
    private String testName;
    private String testType;
    private LocalDate testDate;
    private LocalDate resultDate;
    private String result;
    private String normalRange;
    private String unit;
    private ResultStatus status;
    private String labName;
    private String technician;
    private String notes;
    private String documentUrl;
}
```

#### MedicalDocument
```java
@Entity
public class MedicalDocument {
    private Long id;
    private String documentNumber;
    private Long patientId;
    private Long uploadedBy;
    private DocumentType type;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String mimeType;
    private String description;
    private LocalDateTime uploadedAt;
}
```

#### Allergy
```java
@Entity
public class Allergy {
    private Long id;
    private Long patientId;
    private String allergen;
    private AllergyType type;
    private AllergySeverity severity;
    private String reaction;
    private LocalDate diagnosedDate;
    private String notes;
}
```

### Enums
```java
enum RecordStatus { DRAFT, FINALIZED, AMENDED, ARCHIVED }
enum PrescriptionStatus { ACTIVE, DISPENSED, EXPIRED, CANCELLED }
enum ResultStatus { PENDING, COMPLETED, VERIFIED, ABNORMAL }
enum DocumentType { XRAY, CT_SCAN, MRI, ULTRASOUND, LAB_REPORT, 
                    DISCHARGE_SUMMARY, CONSENT_FORM, OTHER }
enum AllergyType { DRUG, FOOD, ENVIRONMENTAL, OTHER }
enum AllergySeverity { MILD, MODERATE, SEVERE, LIFE_THREATENING }
```

### Relationships
- MedicalRecord N:1 Patient (via patientId)
- MedicalRecord N:1 Doctor (via doctorId)
- MedicalRecord 1:1 Appointment (via appointmentId)
- Prescription N:1 Patient (via patientId)
- Prescription N:1 Doctor (via doctorId)
- Prescription N:1 MedicalRecord (optional)
- Prescription 1:N PrescriptionItem
- LabResult N:1 Patient (via patientId)
- LabResult N:1 MedicalRecord (optional)
- MedicalDocument N:1 Patient (via patientId)
- Allergy N:1 Patient (via patientId)

---

## 4. Pharmacy Service - Domain Model

### Entities

#### Medicine
```java
@Entity
public class Medicine {
    private Long id;
    private String medicineCode;
    private String name;
    private String genericName;
    private String manufacturer;
    private MedicineCategory category;
    private String description;
    private String dosageForm; // Tablet, Capsule, Syrup, etc.
    private String strength;
    private Boolean requiresPrescription;
    private BigDecimal unitPrice;
    private String storageConditions;
    private List<String> sideEffects;
    private List<String> contraindications;
    private MedicineStatus status;
}
```

#### PharmacyInventory
```java
@Entity
public class PharmacyInventory {
    private Long id;
    private Long medicineId;
    private String batchNumber;
    private Integer quantity;
    private Integer minimumStockLevel;
    private Integer reorderLevel;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String supplierName;
    private LocalDate receivedDate;
    private InventoryStatus status;
}
```

#### PrescriptionOrder
```java
@Entity
public class PrescriptionOrder {
    private Long id;
    private String orderNumber;
    private Long patientId;
    private Long prescriptionId;
    private Long pharmacistId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private PaymentStatus paymentStatus;
    private String notes;
    private List<OrderItem> items;
    private LocalDateTime fulfilledAt;
}
```

#### OrderItem
```java
@Entity
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long medicineId;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String batchNumber;
    private LocalDate expiryDate;
}
```

#### StockMovement
```java
@Entity
public class StockMovement {
    private Long id;
    private Long medicineId;
    private MovementType type;
    private Integer quantity;
    private String batchNumber;
    private String reference; // Order number, return number, etc.
    private String reason;
    private Long performedBy;
    private LocalDateTime performedAt;
}
```

#### Supplier
```java
@Entity
public class Supplier {
    private Long id;
    private String supplierCode;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private Address address;
    private SupplierStatus status;
    private Double rating;
}
```

### Enums
```java
enum MedicineCategory { ANTIBIOTIC, PAINKILLER, ANTIVIRAL, ANTIFUNGAL, 
                        CARDIOVASCULAR, DIABETES, RESPIRATORY, OTHER }
enum MedicineStatus { ACTIVE, DISCONTINUED, OUT_OF_STOCK }
enum InventoryStatus { AVAILABLE, LOW_STOCK, OUT_OF_STOCK, EXPIRED, RECALLED }
enum OrderStatus { PENDING, PROCESSING, READY, DISPENSED, CANCELLED }
enum PaymentStatus { PENDING, PAID, REFUNDED }
enum MovementType { PURCHASE, SALE, RETURN, ADJUSTMENT, EXPIRED, DAMAGED }
enum SupplierStatus { ACTIVE, INACTIVE, BLACKLISTED }
```

### Relationships
- PharmacyInventory N:1 Medicine
- PrescriptionOrder N:1 Patient (via patientId)
- PrescriptionOrder N:1 Pharmacist (via pharmacistId)
- PrescriptionOrder 1:N OrderItem
- OrderItem N:1 Medicine
- StockMovement N:1 Medicine
- Supplier 1:N PharmacyInventory (via supplierName)

---

## 5. Payment Service - Domain Model

### Entities

#### Payment
```java
@Entity
public class Payment {
    private Long id;
    private String paymentNumber;
    private Long userId;
    private String referenceType; // APPOINTMENT, PRESCRIPTION_ORDER
    private Long referenceId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String description;
    private String receiptUrl;
}
```

#### Invoice
```java
@Entity
public class Invoice {
    private Long id;
    private String invoiceNumber;
    private Long patientId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private InvoiceStatus status;
    private List<InvoiceItem> items;
}
```

#### InvoiceItem
```java
@Entity
public class InvoiceItem {
    private Long id;
    private Long invoiceId;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String itemType; // CONSULTATION, MEDICINE, LAB_TEST
    private Long itemId;
}
```

#### InsuranceClaim
```java
@Entity
public class InsuranceClaim {
    private Long id;
    private String claimNumber;
    private Long patientId;
    private String insuranceProvider;
    private String policyNumber;
    private Long invoiceId;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private ClaimStatus status;
    private LocalDate submittedDate;
    private LocalDate processedDate;
    private String rejectionReason;
    private List<ClaimDocument> documents;
}
```

#### ClaimDocument
```java
@Entity
public class ClaimDocument {
    private Long id;
    private Long claimId;
    private String documentType;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
}
```

#### PaymentMethod
```java
@Entity
public class PaymentMethod {
    private Long id;
    private Long userId;
    private MethodType type;
    private String cardNumber; // masked
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String token; // payment gateway token
    private Boolean isDefault;
    private Boolean isActive;
}
```

#### Refund
```java
@Entity
public class Refund {
    private Long id;
    private String refundNumber;
    private Long paymentId;
    private BigDecimal refundAmount;
    private String reason;
    private RefundStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String processedBy;
}
```

### Enums
```java
enum PaymentMethod { CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, 
                     CASH, INSURANCE, WALLET }
enum PaymentStatus { PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED }
enum InvoiceStatus { DRAFT, SENT, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED }
enum ClaimStatus { DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, 
                   REJECTED, PARTIALLY_APPROVED }
enum MethodType { CARD, BANK_ACCOUNT, WALLET }
enum RefundStatus { REQUESTED, APPROVED, PROCESSING, COMPLETED, REJECTED }
```

### Relationships
- Payment N:1 User (via userId)
- Invoice N:1 Patient (via patientId)
- Invoice 1:N InvoiceItem
- Invoice 1:N Payment
- InsuranceClaim N:1 Patient (via patientId)
- InsuranceClaim N:1 Invoice
- InsuranceClaim 1:N ClaimDocument
- PaymentMethod N:1 User (via userId)
- Refund N:1 Payment

---

## 6. Analytics Service - Domain Model

### Entities

#### AppointmentMetrics
```java
@Entity
public class AppointmentMetrics {
    private Long id;
    private LocalDate date;
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer cancelledAppointments;
    private Integer noShowAppointments;
    private Double averageWaitTime;
    private Double cancellationRate;
    private LocalDateTime calculatedAt;
}
```

#### DoctorPerformance
```java
@Entity
public class DoctorPerformance {
    private Long id;
    private Long doctorId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Double averageRating;
    private Integer totalReviews;
    private BigDecimal totalRevenue;
    private Double utilizationRate;
}
```

#### DrugUsageTrend
```java
@Entity
public class DrugUsageTrend {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private LocalDate date;
    private Integer quantitySold;
    private BigDecimal revenue;
    private Integer prescriptionCount;
}
```

#### RevenueMetrics
```java
@Entity
public class RevenueMetrics {
    private Long id;
    private LocalDate date;
    private BigDecimal consultationRevenue;
    private BigDecimal pharmacyRevenue;
    private BigDecimal labRevenue;
    private BigDecimal totalRevenue;
    private Integer totalTransactions;
}
```

#### PatientOutcome
```java
@Entity
public class PatientOutcome {
    private Long id;
    private Long patientId;
    private String diagnosis;
    private LocalDate treatmentStartDate;
    private LocalDate treatmentEndDate;
    private OutcomeStatus status;
    private String notes;
}
```

### Enums
```java
enum OutcomeStatus { IMPROVED, STABLE, DETERIORATED, RECOVERED, ONGOING }
```

---

## 7. Notification Service - Domain Model

### Entities

#### Notification
```java
@Entity
public class Notification {
    private Long id;
    private Long userId;
    private NotificationType type;
    private NotificationChannel channel;
    private String subject;
    private String message;
    private NotificationStatus status;
    private Integer retryCount;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String errorMessage;
}
```

#### NotificationTemplate
```java
@Entity
public class NotificationTemplate {
    private Long id;
    private String templateCode;
    private NotificationType type;
    private NotificationChannel channel;
    private String subject;
    private String body;
    private List<String> variables;
    private Boolean isActive;
}
```

#### NotificationPreference
```java
@Entity
public class NotificationPreference {
    private Long id;
    private Long userId;
    private NotificationType type;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
}
```

### Enums
```java
enum NotificationType { APPOINTMENT_CONFIRMATION, APPOINTMENT_REMINDER,
                        APPOINTMENT_CANCELLATION, PRESCRIPTION_READY,
                        LAB_RESULT_AVAILABLE, PAYMENT_RECEIPT,
                        PASSWORD_RESET, ACCOUNT_VERIFICATION }
enum NotificationChannel { EMAIL, SMS, PUSH }
enum NotificationStatus { PENDING, SENT, FAILED, CANCELLED }
```

### Relationships
- Notification N:1 User (via userId)
- NotificationPreference N:1 User (via userId)

---

## Cross-Service Relationships

### Logical Relationships (via IDs)

```
User Service
    └── User.id
         ├── Referenced by Appointment.patientId
         ├── Referenced by Appointment.doctorId
         ├── Referenced by MedicalRecord.patientId
         ├── Referenced by MedicalRecord.doctorId
         ├── Referenced by Prescription.patientId
         ├── Referenced by Prescription.doctorId
         ├── Referenced by PrescriptionOrder.patientId
         ├── Referenced by Payment.userId
         └── Referenced by Notification.userId

Appointment Service
    └── Appointment.id
         └── Referenced by MedicalRecord.appointmentId

EHR Service
    └── Prescription.id
         └── Referenced by PrescriptionOrder.prescriptionId

Payment Service
    └── Invoice.id
         └── Referenced by InsuranceClaim.invoiceId
```

### Event-Driven Relationships

```
Appointment Service
    └── Publishes: AppointmentBooked
         ├── Consumed by: Notification Service
         ├── Consumed by: Analytics Service
         └── Consumed by: EHR Service (prepare record)

EHR Service
    └── Publishes: PrescriptionIssued
         ├── Consumed by: Pharmacy Service
         ├── Consumed by: Notification Service
         └── Consumed by: Analytics Service

Pharmacy Service
    └── Publishes: OrderCreated
         ├── Consumed by: Payment Service
         ├── Consumed by: Notification Service
         └── Consumed by: Analytics Service

Payment Service
    └── Publishes: PaymentCompleted
         ├── Consumed by: Pharmacy Service (fulfill order)
         ├── Consumed by: Notification Service (send receipt)
         └── Consumed by: Analytics Service
```

---

## Summary

Setiap service memiliki domain model yang jelas dengan:
- **Entities**: Core business objects
- **Value Objects**: Embeddable objects (Address, VitalSigns)
- **Enums**: Type-safe constants
- **Relationships**: Within service (via JPA) dan across services (via IDs dan events)

Domain models dirancang dengan prinsip:
- **Bounded Context**: Setiap service memiliki context yang jelas
- **Aggregate Roots**: Entities utama yang menjadi entry point
- **Encapsulation**: Business logic dalam entities
- **Consistency**: Strong consistency dalam service, eventual consistency across services
