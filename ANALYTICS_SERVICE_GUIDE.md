# Analytics Service - Testing Guide

## 🚀 Start Analytics Service

### Prerequisites
1. Service Registry running (port 8761)
2. User Service running (port 8081)
3. Appointment Service running (port 8082)
4. EHR Service running (port 8083)
5. Pharmacy Service running (port 8084)
6. Payment Service running (port 8086)

### Start the Service

```bash
# Terminal 7 (after all other services)
cd analytics-service
mvn spring-boot:run
```

Wait for: `Started AnalyticsServiceApplication in X seconds`

### Verify Service

1. **Check Eureka Dashboard**: http://localhost:8761
   - Should see `ANALYTICS-SERVICE` registered

2. **Health Check**:
```bash
curl http://localhost:8085/actuator/health
```

---

## 📝 Complete Test Scenario

### Scenario: Analytics and Reporting

#### Step 1: Get Complete Dashboard Summary

```bash
curl http://localhost:8085/api/analytics/dashboard
```

Expected response:
```json
{
  "success": true,
  "message": "Dashboard summary retrieved successfully",
  "data": {
    "userStatistics": {
      "totalDoctors": 5,
      "totalPatients": 20,
      "totalPharmacists": 3,
      "totalAdmins": 2,
      "totalUsers": 30
    },
    "appointmentStatistics": {
      "totalScheduled": 15,
      "totalCompleted": 45,
      "totalCancelled": 5,
      "totalAppointments": 65
    },
    "paymentStatistics": {
      "totalPending": 3,
      "totalCompleted": 50,
      "totalFailed": 2,
      "totalRevenue": 15750000.0
    },
    "pharmacyStatistics": {
      "totalMedicines": 150,
      "lowStockItems": 8
    }
  }
}
```

---

#### Step 2: Get User Statistics Only

```bash
curl http://localhost:8085/api/analytics/users
```

Expected response:
```json
{
  "success": true,
  "message": "User statistics retrieved successfully",
  "data": {
    "totalDoctors": 5,
    "totalPatients": 20,
    "totalPharmacists": 3,
    "totalAdmins": 2,
    "totalUsers": 30
  }
}
```

---

#### Step 3: Get Appointment Statistics

```bash
curl http://localhost:8085/api/analytics/appointments
```

Expected response:
```json
{
  "success": true,
  "message": "Appointment statistics retrieved successfully",
  "data": {
    "totalScheduled": 15,
    "totalCompleted": 45,
    "totalCancelled": 5,
    "totalAppointments": 65
  }
}
```

---

#### Step 4: Get Payment Statistics

```bash
curl http://localhost:8085/api/analytics/payments
```

Expected response:
```json
{
  "success": true,
  "message": "Payment statistics retrieved successfully",
  "data": {
    "totalPending": 3,
    "totalCompleted": 50,
    "totalFailed": 2,
    "totalRevenue": 15750000.0
  }
}
```

---

#### Step 5: Get Pharmacy Statistics

```bash
curl http://localhost:8085/api/analytics/pharmacy
```

Expected response:
```json
{
  "success": true,
  "message": "Pharmacy statistics retrieved successfully",
  "data": {
    "totalMedicines": 150,
    "lowStockItems": 8
  }
}
```

---

## 🧪 Test with Real Data

### Scenario: Create Data and See Analytics Update

#### 1. Create Some Users

```bash
# Create 2 doctors
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"doctor1@test.com","password":"pass","firstName":"Dr. John","lastName":"Doe","phoneNumber":"081111111111","role":"DOCTOR"}'

curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"doctor2@test.com","password":"pass","firstName":"Dr. Jane","lastName":"Smith","phoneNumber":"081111111112","role":"DOCTOR"}'

# Create 3 patients
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"patient1@test.com","password":"pass","firstName":"Alice","lastName":"Johnson","phoneNumber":"081222222221","role":"PATIENT"}'

curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"patient2@test.com","password":"pass","firstName":"Bob","lastName":"Williams","phoneNumber":"081222222222","role":"PATIENT"}'

curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"patient3@test.com","password":"pass","firstName":"Charlie","lastName":"Brown","phoneNumber":"081222222223","role":"PATIENT"}'
```

#### 2. Check User Statistics

```bash
curl http://localhost:8085/api/analytics/users
```

Should show updated counts!

---

#### 3. Create Some Appointments

```bash
# Create 3 appointments
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"doctorId":1,"appointmentDateTime":"2026-04-01T10:00:00","durationMinutes":30,"type":"CONSULTATION","reasonForVisit":"Checkup"}'

curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":3,"doctorId":1,"appointmentDateTime":"2026-04-01T11:00:00","durationMinutes":30,"type":"CONSULTATION","reasonForVisit":"Follow-up"}'

curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":4,"doctorId":2,"appointmentDateTime":"2026-04-01T14:00:00","durationMinutes":45,"type":"FOLLOW_UP","reasonForVisit":"Treatment review"}'
```

#### 4. Check Appointment Statistics

```bash
curl http://localhost:8085/api/analytics/appointments
```

Should show 3 scheduled appointments!

---

#### 5. Create Payments

```bash
# Create 2 payments
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"paymentType":"APPOINTMENT","referenceId":1,"amount":150000,"paymentMethod":"CASH","description":"Consultation fee"}'

curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{"patientId":3,"paymentType":"APPOINTMENT","referenceId":2,"amount":150000,"paymentMethod":"CREDIT_CARD","description":"Consultation fee"}'

# Process one payment
curl -X POST http://localhost:8086/api/payments/1/process
```

#### 6. Check Payment Statistics

```bash
curl http://localhost:8085/api/analytics/payments
```

Should show updated payment counts and revenue!

---

#### 7. Add Medicines

```bash
# Add 3 medicines
curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{"name":"Paracetamol","manufacturer":"PharmaCorp","category":"ANALGESIC","dosageForm":"Tablet","strength":"500mg","price":5000,"stockQuantity":100,"reorderLevel":20,"requiresPrescription":false}'

curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{"name":"Amoxicillin","manufacturer":"MediPharm","category":"ANTIBIOTIC","dosageForm":"Capsule","strength":"250mg","price":15000,"stockQuantity":15,"reorderLevel":20,"requiresPrescription":true}'

curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{"name":"Vitamin C","manufacturer":"HealthPlus","category":"VITAMIN_SUPPLEMENT","dosageForm":"Tablet","strength":"1000mg","price":8000,"stockQuantity":200,"reorderLevel":30,"requiresPrescription":false}'
```

#### 8. Check Pharmacy Statistics

```bash
curl http://localhost:8085/api/analytics/pharmacy
```

Should show 3 medicines and 1 low stock item (Amoxicillin)!

---

#### 9. Get Complete Dashboard

```bash
curl http://localhost:8085/api/analytics/dashboard
```

Should show all updated statistics!

---

## 📊 API Endpoints Summary

### Analytics Endpoints (5 endpoints)
```
GET    /api/analytics/dashboard           - Complete dashboard summary
GET    /api/analytics/users                - User statistics
GET    /api/analytics/appointments         - Appointment statistics
GET    /api/analytics/payments             - Payment statistics
GET    /api/analytics/pharmacy             - Pharmacy statistics
```

**Total**: 5 endpoints

---

## 🎯 Success Criteria

Analytics Service is working correctly if:
- ✅ Service registers with Eureka
- ✅ Can communicate with all other services via Feign
- ✅ Aggregates user statistics from User Service
- ✅ Aggregates appointment statistics from Appointment Service
- ✅ Aggregates payment statistics and calculates revenue
- ✅ Aggregates pharmacy statistics
- ✅ Provides unified dashboard summary
- ✅ Handles service failures gracefully

---

## 💡 Service-to-Service Communication

Analytics Service uses **OpenFeign** to communicate with other services:

```
Analytics Service
    ├── → User Service (via Feign)
    ├── → Appointment Service (via Feign)
    ├── → Payment Service (via Feign)
    └── → Pharmacy Service (via Feign)
```

All communication goes through Eureka service discovery!

---

## 🐛 Troubleshooting

### Service won't start
```bash
# Check if port 8085 is in use
lsof -i :8085

# Check Eureka registration
curl http://localhost:8761
```

### Analytics returns zero values
- Verify all other services are running
- Check Eureka dashboard - all services should be UP
- Verify there is data in other services
- Check console logs for Feign errors

### Feign client errors
```bash
# Check if target service is registered
curl http://localhost:8761

# Verify target service is accessible
curl http://localhost:8081/api/users/role/DOCTOR
curl http://localhost:8082/api/appointments/status/SCHEDULED
```

---

## 📈 Dashboard Use Cases

### 1. Admin Dashboard
```bash
# Get complete overview
curl http://localhost:8085/api/analytics/dashboard
```

Use this for:
- System health monitoring
- Business metrics overview
- Quick insights into platform usage

### 2. Financial Reports
```bash
# Get payment statistics
curl http://localhost:8085/api/analytics/payments
```

Use this for:
- Revenue tracking
- Payment success rate
- Pending payments monitoring

### 3. Inventory Alerts
```bash
# Get pharmacy statistics
curl http://localhost:8085/api/analytics/pharmacy
```

Use this for:
- Low stock alerts
- Inventory management
- Reorder planning

### 4. Appointment Management
```bash
# Get appointment statistics
curl http://localhost:8085/api/analytics/appointments
```

Use this for:
- Capacity planning
- Doctor workload analysis
- Cancellation rate tracking

---

## 🔄 Real-time Updates

Analytics data is fetched in real-time from other services:

1. **Create new data** in any service
2. **Immediately query** analytics endpoint
3. **See updated statistics**

No caching or delays - always fresh data!

---

## 🎨 Frontend Integration Example

```javascript
// Fetch dashboard data
fetch('http://localhost:8085/api/analytics/dashboard')
  .then(response => response.json())
  .then(data => {
    const stats = data.data;
    
    // Display user stats
    console.log(`Total Users: ${stats.userStatistics.totalUsers}`);
    console.log(`Doctors: ${stats.userStatistics.totalDoctors}`);
    console.log(`Patients: ${stats.userStatistics.totalPatients}`);
    
    // Display revenue
    console.log(`Total Revenue: Rp ${stats.paymentStatistics.totalRevenue}`);
    
    // Display low stock alert
    if (stats.pharmacyStatistics.lowStockItems > 0) {
      console.log(`⚠️ ${stats.pharmacyStatistics.lowStockItems} items low on stock!`);
    }
  });
```

---

**Analytics Service is now ready! 🎉**

**🎊 ALL SERVICES COMPLETE! 🎊**

You now have a fully functional microservices healthcare platform:
- ✅ User Management
- ✅ Appointment Scheduling
- ✅ Electronic Health Records
- ✅ Pharmacy & Inventory
- ✅ Payment Processing
- ✅ Analytics & Reporting

