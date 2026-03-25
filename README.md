# MediTrack - Digital Healthcare Platform

## Overview
MediTrack adalah platform healthcare digital berbasis microservices yang menghubungkan pasien, dokter, dan apotek.

## 🚀 Quick Start

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Build project
mvn clean install -DskipTests

# 3. Start Service Registry
cd service-registry && mvn spring-boot:run

# 4. Start User Service (new terminal)
cd user-service && mvn spring-boot:run

# 5. Test API
curl http://localhost:8081/api/users/role/DOCTOR
```

📖 **Detailed Guide**: See [QUICK_START.md](QUICK_START.md)

## Technology Stack
- Java 21
- Spring Boot 3.2
- Spring Cloud 2023 (Eureka)
- PostgreSQL 16
- Docker & Docker Compose
- Maven 3.8+

## Implemented Services (Local Development)

### ✅ Running Services (ALL COMPLETE!)
1. **service-registry** (Port: 8761) - Eureka Server
2. **user-service** (Port: 8081) - User Management
3. **appointment-service** (Port: 8082) - Appointments
4. **ehr-service** (Port: 8083) - Medical Records
5. **pharmacy-service** (Port: 8084) - Pharmacy & Inventory
6. **payment-service** (Port: 8086) - Payments
7. **analytics-service** (Port: 8085) - Analytics & Reporting ✅ NEW!

## Features (Simplified for Local Dev)

### a) User Management ✅
- Register users (Patient, Doctor, Pharmacist, Admin)
- Simple login (no JWT yet)
- Get user by ID
- List users by role
- Update user profile

### b) Appointment Scheduling ✅
- Book appointment
- Reschedule appointment
- Cancel appointment
- List appointments by patient/doctor/status
- Update appointment status
- Doctor availability checking

### c) Electronic Health Records (EHR) ✅
- Store patient medical records with vital signs
- Create prescriptions with multiple items
- Add lab results
- Finalize medical records
- Update prescription and lab result status
- Generate unique prescription and lab result numbers

### d) Pharmacy Integration ✅
- Medicine inventory management
- Prescription order processing
- Stock management with automatic updates
- Low stock tracking
- Search and filter medicines
- Order status tracking

### e) Analytics ✅
- Dashboard summary with all statistics
- User statistics by role
- Appointment statistics by status
- Payment statistics with revenue calculation
- Pharmacy statistics with low stock alerts
- Real-time data aggregation via Feign
- Service-to-service communication

### f) Payment System ✅
- Payment recording for appointments and orders
- Multiple payment methods support
- Payment processing simulation
- Payment status tracking
- Refund management
- Duplicate payment prevention

## Project Structure (Current Implementation)
```
meditrack/
├── common-lib/              # ✅ Shared utilities & DTOs
├── service-registry/        # ✅ Eureka Server
├── user-service/           # ✅ User management (IMPLEMENTED)
├── appointment-service/    # ✅ Appointments (IMPLEMENTED)
├── ehr-service/           # ✅ Medical Records (IMPLEMENTED)
├── pharmacy-service/      # ✅ Pharmacy & Inventory (IMPLEMENTED)
├── payment-service/       # ✅ Payments (IMPLEMENTED)
├── analytics-service/     # ✅ Analytics & Reporting (IMPLEMENTED)
├── docker-compose.yml     # ✅ Infrastructure setup
├── pom.xml               # ✅ Parent POM
├── QUICK_START.md        # ✅ Quick start guide
├── IMPLEMENTATION.md     # ✅ Implementation details
├── APPOINTMENT_SERVICE_GUIDE.md # ✅ Appointment testing
├── EHR_SERVICE_GUIDE.md   # ✅ EHR testing
├── PHARMACY_SERVICE_GUIDE.md # ✅ Pharmacy testing
├── PAYMENT_SERVICE_GUIDE.md # ✅ Payment testing
├── ANALYTICS_SERVICE_GUIDE.md # ✅ Analytics testing
└── postman-collection.json # ✅ API testing
```

## Documentation

- 📖 [Quick Start Guide](QUICK_START.md) - Get started in 5 minutes
- 📖 [Implementation Guide](IMPLEMENTATION.md) - Detailed implementation
- 📖 [Architecture Documentation](ARCHITECTURE.md) - System architecture
- 📁 [Laporan 01](laporan-01/) - Design Thinking & Architecture Selection
- 📁 [Laporan 02](laporan-02/) - System Decomposition & Modeling
- 📁 [Laporan 03](laporan-03/) - Architecture Visualization

## API Testing

Import `postman-collection.json` to Postman or use cURL:

```bash
# Register a doctor
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@test.com",
    "password": "password",
    "firstName": "Dr. Ahmad",
    "lastName": "Wijaya",
    "phoneNumber": "081234567890",
    "role": "DOCTOR"
  }'

# Login
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"doctor@test.com","password":"password"}'

# List all doctors
curl http://localhost:8081/api/users/role/DOCTOR
```

## Development Status

| Service | Status | Features |
|---------|--------|----------|
| Service Registry | ✅ Complete | Service discovery |
| User Service | ✅ Complete | Register, Login, CRUD |
| Appointment Service | ✅ Complete | Book, Reschedule, Cancel, List |
| EHR Service | ✅ Complete | Medical Records, Prescriptions, Lab Results |
| Pharmacy Service | ✅ Complete | Inventory, Orders, Stock Management |
| Payment Service | ✅ Complete | Payments, Processing, Refunds |
| Analytics Service | ✅ Complete | Dashboard, Statistics, Reporting |

## Next Steps

1. ✅ Setup infrastructure (Docker Compose)
2. ✅ Implement Service Registry
3. ✅ Implement User Service
4. ✅ Implement Appointment Service
5. ✅ Implement EHR Service
6. ✅ Implement Pharmacy Service
7. ✅ Implement Payment Service
8. ✅ Implement Analytics Service

## 🎉 ALL CORE SERVICES COMPLETE!

The MediTrack platform is now fully functional with all 7 microservices implemented and integrated!

## Simplified for Local Development

This implementation focuses on core functionality without:
- ❌ JWT Authentication (using simple session)
- ❌ API Gateway (direct service calls)
- ❌ Config Server (local configs)
- ❌ Message Queue (direct calls)
- ❌ External integrations (payment gateways, email, SMS)
- ❌ File storage (S3/MinIO)
- ❌ Advanced caching (Redis)

These will be added in production version.

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL (via Docker)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd meditrack
```

2. **Start infrastructure**
```bash
docker-compose up -d
```

3. **Build the project**
```bash
mvn clean install -DskipTests
```

4. **Start Service Registry**
```bash
cd service-registry
mvn spring-boot:run
```

5. **Start User Service** (in new terminal)
```bash
cd user-service
mvn spring-boot:run
```

6. **Start Appointment Service** (in new terminal)
```bash
cd appointment-service
mvn spring-boot:run
```

7. **Start EHR Service** (in new terminal)
```bash
cd ehr-service
mvn spring-boot:run
```

9. **Start Pharmacy Service** (in new terminal)
```bash
cd pharmacy-service
mvn spring-boot:run
```

11. **Start Analytics Service** (in new terminal)
```bash
cd analytics-service
mvn spring-boot:run
```

12. **Verify all services**
- Service Registry: http://localhost:8761 (should show 7 services)
- User Service: http://localhost:8081/api/users/role/DOCTOR
- Appointment Service: http://localhost:8082/api/appointments/status/SCHEDULED
- EHR Service: http://localhost:8083/actuator/health
- Pharmacy Service: http://localhost:8084/api/pharmacy/medicines
- Payment Service: http://localhost:8086/api/payments/status/PENDING
- Analytics Service: http://localhost:8085/api/analytics/dashboard

🎉 **All 7 microservices are now running!**

### Quick Test

```bash
# Register a user
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@meditrack.com",
    "password": "password",
    "firstName": "Test",
    "lastName": "User",
    "phoneNumber": "08123456789",
    "role": "PATIENT"
  }'
```

## Troubleshooting

### Port already in use
```bash
# Find process using port
lsof -i :8081

# Kill process
kill -9 <PID>
```

### Database connection error
```bash
# Restart Docker containers
docker-compose restart

# Check if containers are running
docker ps
```

### Service not registering with Eureka
- Ensure Service Registry is running first
- Wait 30 seconds for registration
- Check http://localhost:8761

## Contributing

This is an educational project for learning microservices architecture.

## License

This project is for educational purposes.
