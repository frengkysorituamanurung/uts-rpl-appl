# Appointment Service - Testing Guide

## 🚀 Start Appointment Service

### Prerequisites
1. Service Registry running (port 8761)
2. User Service running (port 8081)
3. PostgreSQL running (via docker-compose)

### Start the Service

```bash
# Terminal 3 (after Service Registry and User Service)
cd appointment-service
mvn spring-boot:run
```

Wait for: `Started AppointmentServiceApplication in X seconds`

### Verify Service

1. **Check Eureka Dashboard**: http://localhost:8761
   - Should see `APPOINTMENT-SERVICE` registered

2. **Health Check**:
```bash
curl http://localhost:8082/actuator/health
```

---

## 📝 Complete Test Scenario

### Step 1: Register Users

**Register a Doctor:**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.ahmad@meditrack.com",
    "password": "password123",
    "firstName": "Dr. Ahmad",
    "lastName": "Wijaya",
    "phoneNumber": "081234567890",
    "role": "DOCTOR"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "dr.ahmad@meditrack.com",
    "firstName": "Dr. Ahmad",
    "lastName": "Wijaya",
    "role": "DOCTOR"
  }
}
```

**Register a Patient:**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "budi@meditrack.com",
    "password": "password123",
    "firstName": "Budi",
    "lastName": "Santoso",
    "phoneNumber": "081234567891",
    "role": "PATIENT"
  }'
```

Expected response:
```json
{
  "success": true,
  "data": {
    "id": 2,
    "email": "budi@meditrack.com",
    "firstName": "Budi",
    "lastName": "Santoso",
    "role": "PATIENT"
  }
}
```

---

### Step 2: Book an Appointment

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
    "symptoms": "Mild headache and fever"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Appointment booked successfully",
  "data": {
    "id": 1,
    "patientId": 2,
    "doctorId": 1,
    "appointmentDateTime": "2026-04-01T10:00:00",
    "durationMinutes": 30,
    "type": "CONSULTATION",
    "status": "SCHEDULED",
    "reasonForVisit": "Regular checkup",
    "symptoms": "Mild headache and fever"
  }
}
```

---

### Step 3: Get Appointment Details

```bash
curl http://localhost:8082/api/appointments/1
```

---

### Step 4: List Patient's Appointments

```bash
curl http://localhost:8082/api/appointments/patient/2
```

Expected: Array of appointments for patient ID 2

---

### Step 5: List Doctor's Appointments

```bash
curl http://localhost:8082/api/appointments/doctor/1
```

Expected: Array of appointments for doctor ID 1

---

### Step 6: Reschedule Appointment

```bash
curl -X PUT http://localhost:8082/api/appointments/1/reschedule \
  -H "Content-Type: application/json" \
  -d '{
    "newAppointmentDateTime": "2026-04-01T14:00:00",
    "reason": "Patient requested different time"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Appointment rescheduled successfully",
  "data": {
    "id": 1,
    "appointmentDateTime": "2026-04-01T14:00:00",
    "status": "RESCHEDULED"
  }
}
```

---

### Step 7: Update Appointment Status

**Mark as Confirmed:**
```bash
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

**Mark as In Progress:**
```bash
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PROGRESS"}'
```

**Mark as Completed:**
```bash
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}'
```

---

### Step 8: Get Appointments by Status

```bash
# Get all scheduled appointments
curl http://localhost:8082/api/appointments/status/SCHEDULED

# Get all completed appointments
curl http://localhost:8082/api/appointments/status/COMPLETED

# Get all cancelled appointments
curl http://localhost:8082/api/appointments/status/CANCELLED
```

---

### Step 9: Cancel Appointment

```bash
curl -X DELETE http://localhost:8082/api/appointments/1 \
  -H "Content-Type: application/json" \
  -d '{"reason": "Patient is not available"}'
```

Expected response:
```json
{
  "success": true,
  "message": "Appointment cancelled successfully",
  "data": null
}
```

---

## 🧪 Test Scenarios

### Scenario 1: Happy Path - Complete Appointment Flow

```bash
# 1. Book appointment
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"doctorId":1,"appointmentDateTime":"2026-04-05T09:00:00","reasonForVisit":"Consultation"}'

# 2. Confirm appointment
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"CONFIRMED"}'

# 3. Start consultation
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'

# 4. Complete consultation
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"COMPLETED"}'
```

---

### Scenario 2: Reschedule Flow

```bash
# 1. Book appointment
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"doctorId":1,"appointmentDateTime":"2026-04-10T10:00:00","reasonForVisit":"Follow-up"}'

# 2. Reschedule to different time
curl -X PUT http://localhost:8082/api/appointments/2/reschedule \
  -H "Content-Type: application/json" \
  -d '{"newAppointmentDateTime":"2026-04-10T15:00:00","reason":"Patient conflict"}'

# 3. Verify new time
curl http://localhost:8082/api/appointments/2
```

---

### Scenario 3: Cancellation Flow

```bash
# 1. Book appointment
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"doctorId":1,"appointmentDateTime":"2026-04-15T11:00:00","reasonForVisit":"Checkup"}'

# 2. Cancel appointment
curl -X DELETE http://localhost:8082/api/appointments/3 \
  -H "Content-Type: application/json" \
  -d '{"reason":"Patient emergency"}'

# 3. Verify cancellation
curl http://localhost:8082/api/appointments/3
```

---

### Scenario 4: Doctor Availability Check

```bash
# 1. Book first appointment
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"doctorId":1,"appointmentDateTime":"2026-04-20T10:00:00","reasonForVisit":"Consultation"}'

# 2. Try to book at same time (should fail)
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":3,"doctorId":1,"appointmentDateTime":"2026-04-20T10:00:00","reasonForVisit":"Consultation"}'
```

Expected error:
```json
{
  "success": false,
  "message": "Doctor is not available at the requested time"
}
```

---

## 🔍 Validation Tests

### Test 1: Past Date Validation

```bash
curl -X POST http://localhost:8082/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":2,"doctorId":1,"appointmentDateTime":"2020-01-01T10:00:00","reasonForVisit":"Test"}'
```

Expected error: "Appointment time must be in the future"

---

### Test 2: Cancel Already Cancelled

```bash
# Cancel once
curl -X DELETE http://localhost:8082/api/appointments/1 \
  -H "Content-Type: application/json" \
  -d '{"reason":"Test"}'

# Try to cancel again
curl -X DELETE http://localhost:8082/api/appointments/1 \
  -H "Content-Type: application/json" \
  -d '{"reason":"Test again"}'
```

Expected error: "Appointment is already cancelled"

---

### Test 3: Reschedule Completed Appointment

```bash
# Complete appointment
curl -X PUT http://localhost:8082/api/appointments/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"COMPLETED"}'

# Try to reschedule
curl -X PUT http://localhost:8082/api/appointments/1/reschedule \
  -H "Content-Type: application/json" \
  -d '{"newAppointmentDateTime":"2026-05-01T10:00:00","reason":"Test"}'
```

Expected error: "Cannot reschedule a completed appointment"

---

## 📊 Database Verification

```bash
# Connect to database
docker exec -it meditrack-appointment-db psql -U meditrack -d appointment_db

# View appointments
SELECT id, patient_id, doctor_id, appointment_date_time, status FROM appointments;

# Count by status
SELECT status, COUNT(*) FROM appointments GROUP BY status;

# Exit
\q
```

---

## 🎯 Success Criteria

Appointment Service is working correctly if:
- ✅ Service registers with Eureka
- ✅ Can book appointments
- ✅ Validates doctor availability
- ✅ Can reschedule appointments
- ✅ Can cancel appointments
- ✅ Can update appointment status
- ✅ Can list appointments by patient/doctor/status
- ✅ Validates business rules (past dates, double booking, etc.)

---

## 🐛 Troubleshooting

### Service won't start
```bash
# Check if port 8082 is in use
lsof -i :8082

# Check database connection
docker ps | grep appointment-db
```

### Database error
```bash
# Restart database
docker-compose restart

# Check logs
docker logs meditrack-appointment-db
```

### Not registering with Eureka
- Ensure Service Registry is running
- Wait 30 seconds
- Check http://localhost:8761

---

**Appointment Service is now ready! 🎉**
