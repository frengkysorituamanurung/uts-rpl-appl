# 🚀 MediTrack - Complete Local Setup Guide

Panduan lengkap untuk menjalankan MediTrack platform di local environment.

---

## 📋 Prerequisites

### 1. Software Requirements

| Software | Version | Download Link |
|----------|---------|---------------|
| Java JDK | 21 | https://adoptium.net/ |
| Maven | 3.8+ | https://maven.apache.org/download.cgi |
| Docker | Latest | https://www.docker.com/products/docker-desktop |
| Docker Compose | Latest | Included with Docker Desktop |
| Git | Latest | https://git-scm.com/downloads |

### 2. Verify Installation

```bash
# Check Java version
java -version
# Expected: openjdk version "21.x.x"

# Check Maven version
mvn -version
# Expected: Apache Maven 3.8.x or higher

# Check Docker version
docker --version
# Expected: Docker version 20.x.x or higher

# Check Docker Compose version
docker-compose --version
# Expected: Docker Compose version 2.x.x or higher
```

### 3. System Requirements

- **RAM**: Minimum 8GB (Recommended 16GB)
- **Disk Space**: Minimum 5GB free space
- **OS**: Windows 10/11, macOS, or Linux
- **Ports Required**: 5432-5437, 5672, 6379, 8761, 8081-8086

---

## 📁 Project Structure

```
meditrack/
├── common-lib/              # Shared library
├── service-registry/        # Eureka Server (Port 8761)
├── user-service/           # User Management (Port 8081)
├── appointment-service/    # Appointments (Port 8082)
├── ehr-service/           # Medical Records (Port 8083)
├── pharmacy-service/      # Pharmacy (Port 8084)
├── analytics-service/     # Analytics (Port 8085)
├── payment-service/       # Payments (Port 8086)
├── docker-compose.yml     # Infrastructure
└── pom.xml               # Parent POM
```

---

## 🔧 Step-by-Step Setup

### Step 1: Clone Repository

```bash
# Clone the repository
git clone <repository-url>
cd meditrack

# Verify project structure
ls -la
```

### Step 2: Start Infrastructure (Docker)

```bash
# Start all infrastructure services
docker-compose up -d

# Verify containers are running
docker ps

# Expected output: 9 containers running
# - meditrack-user-db (PostgreSQL)
# - meditrack-appointment-db (PostgreSQL)
# - meditrack-ehr-db (PostgreSQL)
# - meditrack-pharmacy-db (PostgreSQL)
# - meditrack-payment-db (PostgreSQL)
# - meditrack-rabbitmq (RabbitMQ)
# - meditrack-redis (Redis)
```

**Wait 30 seconds** for databases to initialize.

### Step 3: Verify Database Connections

```bash
# Test PostgreSQL connection
docker exec -it meditrack-user-db psql -U meditrack -d user_db -c "SELECT 1;"

# If successful, you'll see:
#  ?column? 
# ----------
#         1
```

### Step 4: Build All Services

```bash
# Build parent project and all modules
mvn clean install -DskipTests

# This will:
# 1. Build common-lib
# 2. Build all 7 microservices
# 3. Create JAR files

# Expected output: BUILD SUCCESS
```

**Note**: First build may take 5-10 minutes to download dependencies.

### Step 5: Start Services (One by One)

Open **7 separate terminal windows** and run each service:

#### Terminal 1: Service Registry (MUST START FIRST!)

```bash
cd service-registry
mvn spring-boot:run

# Wait for: "Started ServiceRegistryApplication"
# Then open: http://localhost:8761
```

**⚠️ IMPORTANT**: Wait until Eureka dashboard is accessible before starting other services!

#### Terminal 2: User Service

```bash
cd user-service
mvn spring-boot:run

# Wait for: "Started UserServiceApplication"
# Check Eureka: http://localhost:8761 (should show USER-SERVICE)
```

#### Terminal 3: Appointment Service

```bash
cd appointment-service
mvn spring-boot:run

# Wait for: "Started AppointmentServiceApplication"
```

#### Terminal 4: EHR Service

```bash
cd ehr-service
mvn spring-boot:run

# Wait for: "Started EhrServiceApplication"
```

#### Terminal 5: Pharmacy Service

```bash
cd pharmacy-service
mvn spring-boot:run

# Wait for: "Started PharmacyServiceApplication"
```

#### Terminal 6: Payment Service

```bash
cd payment-service
mvn spring-boot:run

# Wait for: "Started PaymentServiceApplication"
```

#### Terminal 7: Analytics Service

```bash
cd analytics-service
mvn spring-boot:run

# Wait for: "Started AnalyticsServiceApplication"
```

---

## ✅ Verification Steps

### 1. Check Eureka Dashboard

Open: http://localhost:8761

**Expected**: All 7 services should be registered:
- USER-SERVICE
- APPOINTMENT-SERVICE
- EHR-SERVICE
- PHARMACY-SERVICE
- ANALYTICS-SERVICE
- PAYMENT-SERVICE

### 2. Test Each Service Health

```bash
# User Service
curl http://localhost:8081/actuator/health

# Appointment Service
curl http://localhost:8082/actuator/health

# EHR Service
curl http://localhost:8083/actuator/health

# Pharmacy Service
curl http://localhost:8084/actuator/health

# Analytics Service
curl http://localhost:8085/actuator/health

# Payment Service
curl http://localhost:8086/actuator/health
```

**Expected Response** for each:
```json
{"status":"UP"}
```

### 3. Quick Functional Test

```bash
# Test 1: Register a user
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@meditrack.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "phoneNumber": "081234567890",
    "role": "PATIENT"
  }'

# Expected: {"success":true,"message":"User registered successfully",...}

# Test 2: Get analytics dashboard
curl http://localhost:8085/api/analytics/dashboard

# Expected: {"success":true,"message":"Dashboard summary retrieved successfully",...}
```

---

## 🎯 Complete Testing Flow

### 1. Create Test Data

```bash
# Register Doctor
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@test.com",
    "password": "pass123",
    "firstName": "Dr. Ahmad",
    "lastName": "Wijaya",
    "phoneNumber": "081111111111",
    "role": "DOCTOR"
  }'

# Register Patient
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@test.com",
    "password": "pass123",
    "firstName": "Budi",
    "lastName": "Santoso",
    "phoneNumber": "082222222222",
    "role": "PATIENT"
  }'

# Register Pharmacist
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pharmacist@test.com",
    "password": "pass123",
    "firstName": "Siti",
    "lastName": "Rahayu",
    "phoneNumber": "083333333333",
    "role": "PHARMACIST"
  }'
```

### 2. Book Appointment

```bash
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "appointmentDateTime": "2026-04-01T10:00:00",
    "durationMinutes": 30,
    "type": "CONSULTATION",
    "reasonForVisit": "Regular checkup",
    "symptoms": "Feeling tired"
  }'
```

### 3. Create Medical Record

```bash
curl -X POST http://localhost:8083/api/ehr/records \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "appointmentId": 1,
    "visitDate": "2026-03-25T10:30:00",
    "chiefComplaint": "Fatigue",
    "diagnosis": "Vitamin deficiency",
    "treatmentPlan": "Vitamin supplements",
    "notes": "Patient needs rest",
    "temperature": 36.5,
    "bloodPressureSystolic": 120,
    "bloodPressureDiastolic": 80,
    "heartRate": 72,
    "respiratoryRate": 16,
    "weight": 70.0,
    "height": 175.0,
    "oxygenSaturation": 98
  }'
```

### 4. Add Medicine

```bash
curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vitamin B Complex",
    "description": "Multivitamin supplement",
    "manufacturer": "HealthCorp",
    "category": "VITAMIN_SUPPLEMENT",
    "dosageForm": "Tablet",
    "strength": "100mg",
    "price": 50000,
    "stockQuantity": 100,
    "reorderLevel": 20,
    "expiryDate": "2027-12-31",
    "requiresPrescription": false,
    "storageConditions": "Room temperature",
    "sideEffects": "None"
  }'
```

### 5. Create Payment

```bash
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "APPOINTMENT",
    "referenceId": 1,
    "amount": 150000,
    "paymentMethod": "CASH",
    "description": "Consultation fee"
  }'
```

### 6. View Analytics

```bash
curl http://localhost:8085/api/analytics/dashboard | json_pp
```

---

## 🐛 Troubleshooting

### Problem 1: Port Already in Use

**Error**: `Port 8081 is already in use`

**Solution**:
```bash
# Find process using the port
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or use different port in application.yml
```

### Problem 2: Docker Containers Not Starting

**Error**: `Cannot connect to Docker daemon`

**Solution**:
```bash
# Start Docker Desktop
# Wait for Docker to fully start

# Restart containers
docker-compose down
docker-compose up -d
```

### Problem 3: Database Connection Failed

**Error**: `Connection refused to localhost:5432`

**Solution**:
```bash
# Check if PostgreSQL container is running
docker ps | grep postgres

# Restart database
docker-compose restart meditrack-user-db

# Check logs
docker logs meditrack-user-db
```

### Problem 4: Service Not Registering with Eureka

**Error**: Service not showing in Eureka dashboard

**Solution**:
```bash
# 1. Ensure Eureka is running first
curl http://localhost:8761

# 2. Wait 30 seconds for registration

# 3. Check service logs for errors

# 4. Restart the service
```

### Problem 5: Maven Build Failed

**Error**: `BUILD FAILURE`

**Solution**:
```bash
# Clean Maven cache
mvn clean

# Delete .m2 repository (if needed)
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -DskipTests
```

### Problem 6: Out of Memory

**Error**: `Java heap space`

**Solution**:
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m"

# Or add to each service startup
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m"
```

---

## 🔄 Stopping Services

### Stop All Services

```bash
# Stop all Spring Boot services (Ctrl+C in each terminal)

# Stop Docker containers
docker-compose down

# Or stop without removing volumes
docker-compose stop
```

### Restart Services

```bash
# Restart infrastructure
docker-compose restart

# Restart individual service
cd user-service
mvn spring-boot:run
```

---

## 📊 Service Ports Reference

| Service | Port | URL |
|---------|------|-----|
| Eureka Dashboard | 8761 | http://localhost:8761 |
| User Service | 8081 | http://localhost:8081 |
| Appointment Service | 8082 | http://localhost:8082 |
| EHR Service | 8083 | http://localhost:8083 |
| Pharmacy Service | 8084 | http://localhost:8084 |
| Analytics Service | 8085 | http://localhost:8085 |
| Payment Service | 8086 | http://localhost:8086 |
| PostgreSQL (User) | 5432 | localhost:5432 |
| PostgreSQL (Appointment) | 5433 | localhost:5433 |
| PostgreSQL (EHR) | 5434 | localhost:5434 |
| PostgreSQL (Pharmacy) | 5436 | localhost:5436 |
| PostgreSQL (Payment) | 5437 | localhost:5437 |
| RabbitMQ Management | 15672 | http://localhost:15672 |
| Redis | 6379 | localhost:6379 |

---

## 🎨 Using Postman

### 1. Import Collection

1. Open Postman
2. Click **Import**
3. Select `postman-collection.json`
4. Collection appears with 61 endpoints

### 2. Test Complete Flow

Follow the order in Postman:
1. User Service → Register users
2. Appointment Service → Book appointment
3. EHR Service → Create medical record
4. Pharmacy Service → Add medicines
5. Payment Service → Process payment
6. Analytics Service → View dashboard

📖 **Detailed Guide**: See [POSTMAN_COLLECTION_GUIDE.md](POSTMAN_COLLECTION_GUIDE.md)

---

## 📝 Database Access

### Connect to PostgreSQL

```bash
# User Database
docker exec -it meditrack-user-db psql -U meditrack -d user_db

# Appointment Database
docker exec -it meditrack-appointment-db psql -U meditrack -d appointment_db

# EHR Database
docker exec -it meditrack-ehr-db psql -U meditrack -d ehr_db

# Pharmacy Database
docker exec -it meditrack-pharmacy-db psql -U meditrack -d pharmacy_db

# Payment Database
docker exec -it meditrack-payment-db psql -U meditrack -d payment_db
```

### Useful SQL Commands

```sql
-- List all tables
\dt

-- View table structure
\d users

-- Query data
SELECT * FROM users;

-- Exit
\q
```

---

## 🚀 Quick Start Script

Create a file `start-all.sh`:

```bash
#!/bin/bash

echo "🚀 Starting MediTrack Platform..."

# Start infrastructure
echo "📦 Starting Docker containers..."
docker-compose up -d
sleep 30

# Build project
echo "🔨 Building project..."
mvn clean install -DskipTests

# Start services in background
echo "🎯 Starting services..."

cd service-registry && mvn spring-boot:run > ../logs/registry.log 2>&1 &
sleep 30

cd ../user-service && mvn spring-boot:run > ../logs/user.log 2>&1 &
sleep 20

cd ../appointment-service && mvn spring-boot:run > ../logs/appointment.log 2>&1 &
sleep 20

cd ../ehr-service && mvn spring-boot:run > ../logs/ehr.log 2>&1 &
sleep 20

cd ../pharmacy-service && mvn spring-boot:run > ../logs/pharmacy.log 2>&1 &
sleep 20

cd ../payment-service && mvn spring-boot:run > ../logs/payment.log 2>&1 &
sleep 20

cd ../analytics-service && mvn spring-boot:run > ../logs/analytics.log 2>&1 &

echo "✅ All services started!"
echo "📊 Check Eureka: http://localhost:8761"
echo "📝 Check logs in ./logs/ directory"
```

Make it executable:
```bash
chmod +x start-all.sh
./start-all.sh
```

---

## 📚 Additional Resources

- [QUICK_START.md](QUICK_START.md) - 5-minute quick start
- [PROJECT_COMPLETE.md](PROJECT_COMPLETE.md) - Project overview
- [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) - Implementation details
- Service-specific guides in root directory

---

## ✅ Success Checklist

- [ ] Java 21 installed
- [ ] Maven 3.8+ installed
- [ ] Docker running
- [ ] All ports available (8761, 8081-8086)
- [ ] Docker containers started (9 containers)
- [ ] Project built successfully
- [ ] Service Registry running (http://localhost:8761)
- [ ] All 7 services registered in Eureka
- [ ] Health checks passing
- [ ] Test API calls successful
- [ ] Postman collection imported

---

## 🎉 You're Ready!

Jika semua checklist di atas sudah ✅, maka MediTrack platform sudah siap digunakan!

**Next Steps**:
1. Import Postman collection
2. Test complete patient journey
3. Explore analytics dashboard
4. Check service logs for any issues

**Happy Testing! 🚀**

