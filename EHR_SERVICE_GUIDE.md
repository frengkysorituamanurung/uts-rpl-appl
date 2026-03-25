# EHR Service - Testing Guide

## 🚀 Start EHR Service

### Prerequisites
1. Service Registry running (port 8761)
2. User Service running (port 8081)
3. Appointment Service running (port 8082)
4. PostgreSQL running (via docker-compose)

### Start the Service

```bash
# Terminal 4 (after other services)
cd ehr-service
mvn spring-boot:run
```

Wait for: `Started EhrServiceApplication in X seconds`

### Verify Service

1. **Check Eureka Dashboard**: http://localhost:8761
   - Should see `EHR-SERVICE` registered

2. **Health Check**:
```bash
curl http://localhost:8083/actuator/health
```

---

## 📝 Complete Test Scenario

### Scenario: Complete Patient Visit Flow

#### Step 1: Create Medical Record

```bash
curl -X POST http://localhost:8083/api/ehr/records \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "appointmentId": 1,
    "visitDate": "2026-03-25T10:30:00",
    "chiefComplaint": "Headache and fever",
    "diagnosis": "Viral infection",
    "treatmentPlan": "Rest and medication",
    "notes": "Patient reports symptoms for 2 days",
    "temperature": 38.5,
    "bloodPressureSystolic": 120,
    "bloodPressureDiastolic": 80,
    "heartRate": 75,
    "respiratoryRate": 16,
    "weight": 70.5,
    "height": 175.0,
    "oxygenSaturation": 98
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Medical record created successfully",
  "data": {
    "id": 1,
    "patientId": 2,
    "doctorId": 1,
    "diagnosis": "Viral infection",
    "status": "DRAFT"
  }
}
```

---

#### Step 2: Create Prescription

```bash
curl -X POST http://localhost:8083/api/ehr/prescriptions \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "medicalRecordId": 1,
    "issueDate": "2026-03-25",
    "validityDays": 30,
    "notes": "Take with food",
    "items": [
      {
        "medicineName": "Paracetamol",
        "dosage": "500mg",
        "frequency": "3 times daily",
        "durationDays": 5,
        "instructions": "Take after meals",
        "quantity": 15
      },
      {
        "medicineName": "Vitamin C",
        "dosage": "1000mg",
        "frequency": "Once daily",
        "durationDays": 7,
        "instructions": "Take in the morning",
        "quantity": 7
      }
    ]
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Prescription created successfully",
  "data": {
    "id": 1,
    "prescriptionNumber": "RX-A1B2C3D4",
    "patientId": 2,
    "doctorId": 1,
    "status": "ACTIVE",
    "items": [
      {
        "medicineName": "Paracetamol",
        "dosage": "500mg",
        "frequency": "3 times daily"
      }
    ]
  }
}
```

---

#### Step 3: Add Lab Result

```bash
curl -X POST http://localhost:8083/api/ehr/lab-results \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "medicalRecordId": 1,
    "testName": "Complete Blood Count",
    "testType": "Hematology",
    "testDate": "2026-03-25",
    "resultDate": "2026-03-26",
    "result": "WBC: 8.5, RBC: 4.8, Hemoglobin: 14.2",
    "normalRange": "WBC: 4-11, RBC: 4.5-5.5, Hb: 13-17",
    "unit": "10^9/L",
    "labName": "MediLab",
    "technician": "John Lab Tech",
    "notes": "All values within normal range"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Lab result created successfully",
  "data": {
    "id": 1,
    "resultNumber": "LAB-X1Y2Z3W4",
    "testName": "Complete Blood Count",
    "status": "PENDING"
  }
}
```

---

### Step 4: Get Patient Medical History

```bash
curl http://localhost:8083/api/ehr/records/patient/2
```

Expected: Array of all medical records for patient

---

### Step 5: Get Patient Prescriptions

```bash
curl http://localhost:8083/api/ehr/prescriptions/patient/2
```

Expected: Array of all prescriptions for patient

---

### Step 6: Get Patient Lab Results

```bash
curl http://localhost:8083/api/ehr/lab-results/patient/2
```

Expected: Array of all lab results for patient

---

### Step 7: Finalize Medical Record

```bash
curl -X PUT http://localhost:8083/api/ehr/records/1/finalize
```

Expected response:
```json
{
  "success": true,
  "message": "Medical record finalized successfully",
  "data": {
    "id": 1,
    "status": "FINALIZED"
  }
}
```

---

### Step 8: Update Lab Result Status

```bash
curl -X PUT http://localhost:8083/api/ehr/lab-results/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "VERIFIED"}'
```

---

### Step 9: Get Prescription by Number

```bash
# Use the prescription number from Step 2
curl http://localhost:8083/api/ehr/prescriptions/number/RX-A1B2C3D4
```

---

## 🧪 Additional Test Scenarios

### Test 1: Multiple Prescriptions for Same Patient

```bash
# Create second prescription
curl -X POST http://localhost:8083/api/ehr/prescriptions \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "issueDate": "2026-03-26",
    "items": [
      {
        "medicineName": "Amoxicillin",
        "dosage": "250mg",
        "frequency": "3 times daily",
        "durationDays": 7,
        "quantity": 21
      }
    ]
  }'

# List all prescriptions
curl http://localhost:8083/api/ehr/prescriptions/patient/2
```

---

### Test 2: Update Medical Record

```bash
curl -X PUT http://localhost:8083/api/ehr/records/1 \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "chiefComplaint": "Headache and fever - Updated",
    "diagnosis": "Viral infection with mild dehydration",
    "treatmentPlan": "Rest, medication, and increase fluid intake",
    "notes": "Patient condition improving"
  }'
```

---

### Test 3: Mark Prescription as Dispensed

```bash
curl -X PUT http://localhost:8083/api/ehr/prescriptions/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "DISPENSED"}'
```

---

### Test 4: Get Doctor's Medical Records

```bash
curl http://localhost:8083/api/ehr/records/doctor/1
```

---

## 📊 API Endpoints Summary

### Medical Records (6 endpoints)
```
POST   /api/ehr/records                    - Create medical record
GET    /api/ehr/records/{id}               - Get by ID
GET    /api/ehr/records/patient/{id}       - List by patient
GET    /api/ehr/records/doctor/{id}        - List by doctor
PUT    /api/ehr/records/{id}               - Update record
PUT    /api/ehr/records/{id}/finalize      - Finalize record
```

### Prescriptions (5 endpoints)
```
POST   /api/ehr/prescriptions                      - Create prescription
GET    /api/ehr/prescriptions/{id}                 - Get by ID
GET    /api/ehr/prescriptions/number/{number}      - Get by number
GET    /api/ehr/prescriptions/patient/{id}         - List by patient
PUT    /api/ehr/prescriptions/{id}/status          - Update status
```

### Lab Results (4 endpoints)
```
POST   /api/ehr/lab-results                - Create lab result
GET    /api/ehr/lab-results/{id}           - Get by ID
GET    /api/ehr/lab-results/patient/{id}   - List by patient
PUT    /api/ehr/lab-results/{id}/status    - Update status
```

**Total**: 15 endpoints

---

## 📋 Database Verification

```bash
# Connect to database
docker exec -it meditrack-ehr-db psql -U meditrack -d ehr_db

# View medical records
SELECT id, patient_id, doctor_id, diagnosis, status FROM medical_records;

# View prescriptions
SELECT id, prescription_number, patient_id, status FROM prescriptions;

# View prescription items
SELECT pi.id, p.prescription_number, pi.medicine_name, pi.dosage 
FROM prescription_items pi 
JOIN prescriptions p ON pi.prescription_id = p.id;

# View lab results
SELECT id, result_number, patient_id, test_name, status FROM lab_results;

# Exit
\q
```

---

## 🎯 Success Criteria

EHR Service is working correctly if:
- ✅ Service registers with Eureka
- ✅ Can create medical records with vital signs
- ✅ Can create prescriptions with multiple items
- ✅ Can add lab results
- ✅ Can retrieve patient medical history
- ✅ Can finalize medical records
- ✅ Can update prescription and lab result status
- ✅ Generates unique prescription and lab result numbers

---

## 🐛 Troubleshooting

### Service won't start
```bash
# Check if port 8083 is in use
lsof -i :8083

# Check database connection
docker ps | grep ehr-db
```

### Prescription items not saving
- Check if items array is properly formatted in JSON
- Verify prescription object is saved before items

### Cannot update finalized record
- This is expected behavior
- Finalized records are read-only

---

**EHR Service is now ready! 🎉**

Next: Test integration between Appointment → EHR → Pharmacy services
