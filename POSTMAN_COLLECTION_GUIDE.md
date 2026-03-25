# MediTrack Postman Collection Guide

## 📦 Collection Overview

File `postman-collection.json` berisi **59+ API endpoints** dari 7 microservices MediTrack platform.

---

## 📋 Services Included

### 1. User Service (6 endpoints)
- Register Doctor
- Register Patient
- Login
- Get User by ID
- List All Doctors
- List All Patients

### 2. Appointment Service (8 endpoints)
- Book Appointment
- Get Appointment by ID
- Get Appointments by Patient
- Get Appointments by Doctor
- Reschedule Appointment
- Cancel Appointment
- Update Appointment Status
- Get Appointments by Status

### 3. EHR Service (15 endpoints)

#### Medical Records (6 endpoints)
- Create Medical Record
- Get Medical Record by ID
- Get Patient Medical History
- Get Doctor's Medical Records
- Update Medical Record
- Finalize Medical Record

#### Prescriptions (5 endpoints)
- Create Prescription
- Get Prescription by ID
- Get Prescription by Number
- Get Patient Prescriptions
- Update Prescription Status

#### Lab Results (4 endpoints)
- Create Lab Result
- Get Lab Result by ID
- Get Patient Lab Results
- Update Lab Result Status

### 4. Pharmacy Service (17 endpoints)

#### Medicines (9 endpoints)
- Create Medicine
- Get Medicine by ID
- Get Medicine by Code
- Get All Medicines
- Get Medicines by Category
- Search Medicines
- Get Low Stock Medicines
- Update Medicine
- Update Stock

#### Orders (7 endpoints)
- Create Order
- Get Order by ID
- Get Order by Number
- Get Patient Orders
- Get Pharmacist Orders
- Get Orders by Status
- Update Order Status

### 5. Payment Service (10 endpoints)
- Create Payment for Appointment
- Create Payment for Pharmacy Order
- Get Payment by ID
- Get Payment by Number
- Get Patient Payments
- Get Payments by Status
- Get Payments by Type
- Process Payment
- Update Payment Status
- Refund Payment

### 6. Analytics Service (5 endpoints)
- Get Dashboard Summary
- Get User Statistics
- Get Appointment Statistics
- Get Payment Statistics
- Get Pharmacy Statistics

---

## 🚀 How to Use

### 1. Import to Postman

1. Open Postman
2. Click **Import** button
3. Select `postman-collection.json`
4. Collection will appear in your workspace

### 2. Set Up Environment (Optional)

Create a Postman environment with these variables:

```
BASE_URL_USER = http://localhost:8081
BASE_URL_APPOINTMENT = http://localhost:8082
BASE_URL_EHR = http://localhost:8083
BASE_URL_PHARMACY = http://localhost:8084
BASE_URL_ANALYTICS = http://localhost:8085
BASE_URL_PAYMENT = http://localhost:8086
```

### 3. Test Complete Flow

#### Step 1: Create Users
1. Run "Register Doctor"
2. Run "Register Patient"
3. Run "Login" to verify

#### Step 2: Book Appointment
1. Run "Book Appointment"
2. Run "Get Appointment by ID" to verify

#### Step 3: Create Medical Record
1. Run "Create Medical Record"
2. Run "Create Prescription"
3. Run "Create Lab Result"

#### Step 4: Process Pharmacy Order
1. Run "Create Medicine" (add some medicines first)
2. Run "Create Order" (using prescription number)
3. Run "Update Order Status" to DISPENSED

#### Step 5: Process Payment
1. Run "Create Payment for Appointment"
2. Run "Process Payment"
3. Run "Create Payment for Pharmacy Order"
4. Run "Process Payment"

#### Step 6: View Analytics
1. Run "Get Dashboard Summary"
2. See all statistics updated!

---

## 📊 Collection Statistics

| Service | Endpoints | Methods |
|---------|-----------|---------|
| User Service | 6 | GET, POST |
| Appointment Service | 8 | GET, POST, PUT, DELETE |
| EHR Service | 15 | GET, POST, PUT |
| Pharmacy Service | 17 | GET, POST, PUT |
| Payment Service | 10 | GET, POST, PUT |
| Analytics Service | 5 | GET |
| **TOTAL** | **61** | **5 methods** |

---

## 🔧 Request Examples

### POST Request with Body
```json
{
  "email": "doctor@test.com",
  "password": "password123",
  "firstName": "Dr. Ahmad",
  "lastName": "Wijaya",
  "phoneNumber": "081234567890",
  "role": "DOCTOR"
}
```

### GET Request with Path Parameter
```
http://localhost:8081/api/users/1
```

### GET Request with Query Parameter
```
http://localhost:8084/api/pharmacy/medicines/search?name=para
```

### PUT Request with Body
```json
{
  "status": "COMPLETED"
}
```

---

## 🎯 Testing Scenarios

### Scenario 1: Complete Patient Journey
1. Register Patient → User Service
2. Register Doctor → User Service
3. Book Appointment → Appointment Service
4. Create Payment → Payment Service
5. Process Payment → Payment Service
6. Complete Appointment → Appointment Service
7. Create Medical Record → EHR Service
8. Create Prescription → EHR Service
9. Add Medicines → Pharmacy Service
10. Create Order → Pharmacy Service
11. Create Payment → Payment Service
12. Dispense Order → Pharmacy Service
13. View Dashboard → Analytics Service

### Scenario 2: Inventory Management
1. Create Multiple Medicines → Pharmacy Service
2. Check Low Stock → Pharmacy Service
3. Update Stock → Pharmacy Service
4. View Pharmacy Stats → Analytics Service

### Scenario 3: Financial Reporting
1. Create Multiple Payments → Payment Service
2. Process Payments → Payment Service
3. View Payment Statistics → Analytics Service
4. Check Revenue → Analytics Service

---

## 🐛 Common Issues

### Issue 1: Connection Refused
**Solution**: Ensure all services are running
```bash
# Check Eureka dashboard
curl http://localhost:8761
```

### Issue 2: 404 Not Found
**Solution**: Verify the endpoint URL and service port

### Issue 3: 500 Internal Server Error
**Solution**: Check service logs for detailed error messages

### Issue 4: Foreign Key Constraint
**Solution**: Ensure referenced entities exist (e.g., create user before appointment)

---

## 📝 Response Format

All responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    // Response data here
  }
}
```

Error response:
```json
{
  "success": false,
  "message": "Error message here",
  "data": null
}
```

---

## 🔄 Service Dependencies

```
Analytics Service
    ├── depends on → User Service
    ├── depends on → Appointment Service
    ├── depends on → Payment Service
    └── depends on → Pharmacy Service

Pharmacy Service
    └── depends on → EHR Service (prescription number)

Payment Service
    ├── depends on → Appointment Service (appointment ID)
    └── depends on → Pharmacy Service (order ID)

EHR Service
    └── depends on → Appointment Service (appointment ID)

Appointment Service
    └── depends on → User Service (patient ID, doctor ID)
```

---

## 💡 Tips

1. **Run services in order**: Start with User Service, then others
2. **Use variables**: Store IDs in Postman variables for reuse
3. **Check Eureka**: Always verify services are registered
4. **Test incrementally**: Test each service before moving to next
5. **Save responses**: Use Postman's test scripts to save response data

---

## 📚 Additional Resources

- `QUICK_START.md` - Quick setup guide
- `APPOINTMENT_SERVICE_GUIDE.md` - Appointment testing
- `EHR_SERVICE_GUIDE.md` - EHR testing
- `PHARMACY_SERVICE_GUIDE.md` - Pharmacy testing
- `PAYMENT_SERVICE_GUIDE.md` - Payment testing
- `ANALYTICS_SERVICE_GUIDE.md` - Analytics testing

---

**Happy Testing! 🎉**

