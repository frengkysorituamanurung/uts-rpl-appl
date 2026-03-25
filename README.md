# MediTrack - Digital Healthcare Platform

## Overview
MediTrack adalah platform healthcare digital berbasis microservices yang menghubungkan pasien, dokter, dan apotek.

## 🚀 Quick Start

**🤖 Automated Setup (Recommended)**: See [AUTOMATION_SCRIPTS.md](AUTOMATION_SCRIPTS.md)

### One-Command Setup

```bash
# Start everything automatically
./start-all-services.sh
```

This will automatically:
1. Check prerequisites
2. Start Docker containers
3. Build the project
4. Start all 7 microservices
5. Verify everything is running

**Total time**: ~5-10 minutes (first run)

### Manual Setup

**For detailed manual setup, see [LOCAL_SETUP_GUIDE.md](LOCAL_SETUP_GUIDE.md)**

```bash
# 1. Check prerequisites
./check-prerequisites.sh

# 2. Start infrastructure
docker-compose up -d

# 3. Build project
mvn clean install -DskipTests

# 4. Start Service Registry (Terminal 1)
cd service-registry && mvn spring-boot:run

# 5. Start other services (in separate terminals)
cd user-service && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
cd ehr-service && mvn spring-boot:run
cd pharmacy-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd analytics-service && mvn spring-boot:run

# 6. Verify all services
./verify-services.sh
```

### Verify Services

Visit Eureka Dashboard: http://localhost:8761

You should see all 7 services registered!

### Stop Services

```bash
# Stop all services
./stop-all-services.sh
```

📖 **Automation Guide**: [AUTOMATION_SCRIPTS.md](AUTOMATION_SCRIPTS.md)  
📖 **Complete Setup Guide**: [LOCAL_SETUP_GUIDE.md](LOCAL_SETUP_GUIDE.md)  
📖 **Quick Reference**: [QUICK_START.md](QUICK_START.md)

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

### Main Documentation
- 📖 [README.md](README.md) - Project overview
- 🤖 [AUTOMATION_SCRIPTS.md](AUTOMATION_SCRIPTS.md) - **Automation scripts guide** ⭐
- 🚀 [LOCAL_SETUP_GUIDE.md](LOCAL_SETUP_GUIDE.md) - Complete manual setup guide
- 📖 [QUICK_START.md](QUICK_START.md) - Quick start guide
- 📖 [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture details
- 📖 [IMPLEMENTATION.md](IMPLEMENTATION.md) - Implementation guide
- 📖 [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) - Current status
- 📖 [PROJECT_COMPLETE.md](PROJECT_COMPLETE.md) - Project completion summary

### Setup Scripts
- 🤖 [start-all-services.sh](start-all-services.sh) - **Start all services automatically** ⭐
- 🛑 [stop-all-services.sh](stop-all-services.sh) - Stop all services
- 🔄 [restart-all-services.sh](restart-all-services.sh) - Restart all services
- 📋 [view-logs.sh](view-logs.sh) - View service logs interactively
- 🔧 [check-prerequisites.sh](check-prerequisites.sh) - Check system requirements
- ✅ [verify-services.sh](verify-services.sh) - Verify all services are running
- 🧪 [test-integration.sh](test-integration.sh) - Integration test script

### Service Testing Guides
- 📖 [APPOINTMENT_SERVICE_GUIDE.md](APPOINTMENT_SERVICE_GUIDE.md) - Appointment testing
- 📖 [EHR_SERVICE_GUIDE.md](EHR_SERVICE_GUIDE.md) - EHR testing
- 📖 [PHARMACY_SERVICE_GUIDE.md](PHARMACY_SERVICE_GUIDE.md) - Pharmacy testing
- 📖 [PAYMENT_SERVICE_GUIDE.md](PAYMENT_SERVICE_GUIDE.md) - Payment testing
- 📖 [ANALYTICS_SERVICE_GUIDE.md](ANALYTICS_SERVICE_GUIDE.md) - Analytics testing

### API Testing
- 📖 [POSTMAN_COLLECTION_GUIDE.md](POSTMAN_COLLECTION_GUIDE.md) - Postman collection guide
- 📦 [postman-collection.json](postman-collection.json) - Complete API collection (61 endpoints)

### Reports (Indonesian)
- 📁 [laporan-01/](laporan-01/) - Design Thinking & Architecture Selection
- 📁 [laporan-02/](laporan-02/) - System Decomposition & Modeling
- 📁 [laporan-03/](laporan-03/) - Architecture Visualization

## API Testing

### Postman Collection

Import `postman-collection.json` untuk testing lengkap semua API endpoints.

**Collection includes**:
- 61 API endpoints
- 7 microservices
- Complete request examples
- Sample data for testing

**Quick Import**:
1. Open Postman
2. Click Import
3. Select `postman-collection.json`
4. Start testing!

📖 **Detailed Guide**: See [POSTMAN_COLLECTION_GUIDE.md](POSTMAN_COLLECTION_GUIDE.md)

### Sample cURL Commands

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

# Book appointment
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "appointmentDateTime": "2026-04-01T10:00:00",
    "durationMinutes": 30,
    "type": "CONSULTATION",
    "reasonForVisit": "Regular checkup"
  }'

# Get analytics dashboard
curl http://localhost:8085/api/analytics/dashboard

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
