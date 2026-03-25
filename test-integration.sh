#!/bin/bash

# MediTrack Integration Test Script
# Tests complete flow: User → Appointment → EHR

echo "🧪 MediTrack Integration Test"
echo "=============================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URLs
USER_SERVICE="http://localhost:8081"
APPOINTMENT_SERVICE="http://localhost:8082"
EHR_SERVICE="http://localhost:8083"

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to test endpoint
test_endpoint() {
    local name=$1
    local url=$2
    local method=$3
    local data=$4
    
    echo -n "Testing: $name... "
    
    if [ "$method" == "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    elif [ "$method" == "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X PUT "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" "$url")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓ PASSED${NC} (HTTP $http_code)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        echo -e "${RED}✗ FAILED${NC} (HTTP $http_code)"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        echo "$body"
    fi
    echo ""
}

echo "📋 Phase 1: User Service Tests"
echo "--------------------------------"

# Test 1: Register Doctor
test_endpoint "Register Doctor" \
    "$USER_SERVICE/api/users/register" \
    "POST" \
    '{
        "email": "integration.doctor@test.com",
        "password": "password123",
        "firstName": "Dr. Integration",
        "lastName": "Test",
        "phoneNumber": "081234567890",
        "role": "DOCTOR"
    }'

# Test 2: Register Patient
test_endpoint "Register Patient" \
    "$USER_SERVICE/api/users/register" \
    "POST" \
    '{
        "email": "integration.patient@test.com",
        "password": "password123",
        "firstName": "Patient",
        "lastName": "Test",
        "phoneNumber": "081234567891",
        "role": "PATIENT"
    }'

# Test 3: Login Doctor
test_endpoint "Login Doctor" \
    "$USER_SERVICE/api/users/login" \
    "POST" \
    '{
        "email": "integration.doctor@test.com",
        "password": "password123"
    }'

# Test 4: List Doctors
test_endpoint "List All Doctors" \
    "$USER_SERVICE/api/users/role/DOCTOR" \
    "GET"

echo ""
echo "📅 Phase 2: Appointment Service Tests"
echo "--------------------------------------"

# Test 5: Book Appointment
test_endpoint "Book Appointment" \
    "$APPOINTMENT_SERVICE/api/appointments" \
    "POST" \
    '{
        "patientId": 2,
        "doctorId": 1,
        "appointmentDateTime": "2026-04-01T10:00:00",
        "durationMinutes": 30,
        "type": "CONSULTATION",
        "reasonForVisit": "Integration test checkup",
        "symptoms": "Testing symptoms"
    }'

# Test 6: Get Appointment by ID
test_endpoint "Get Appointment by ID" \
    "$APPOINTMENT_SERVICE/api/appointments/1" \
    "GET"

# Test 7: List Patient Appointments
test_endpoint "List Patient Appointments" \
    "$APPOINTMENT_SERVICE/api/appointments/patient/2" \
    "GET"

# Test 8: Update Appointment Status
test_endpoint "Update Appointment Status to COMPLETED" \
    "$APPOINTMENT_SERVICE/api/appointments/1/status" \
    "PUT" \
    '{"status": "COMPLETED"}'

echo ""
echo "🏥 Phase 3: EHR Service Tests"
echo "------------------------------"

# Test 9: Create Medical Record
test_endpoint "Create Medical Record" \
    "$EHR_SERVICE/api/ehr/records" \
    "POST" \
    '{
        "patientId": 2,
        "doctorId": 1,
        "appointmentId": 1,
        "visitDate": "2026-03-25T10:30:00",
        "chiefComplaint": "Integration test complaint",
        "diagnosis": "Test diagnosis",
        "treatmentPlan": "Test treatment",
        "notes": "Integration test notes",
        "temperature": 37.0,
        "bloodPressureSystolic": 120,
        "bloodPressureDiastolic": 80,
        "heartRate": 72,
        "respiratoryRate": 16,
        "weight": 70.0,
        "height": 175.0,
        "oxygenSaturation": 98
    }'

# Test 10: Create Prescription
test_endpoint "Create Prescription" \
    "$EHR_SERVICE/api/ehr/prescriptions" \
    "POST" \
    '{
        "patientId": 2,
        "doctorId": 1,
        "medicalRecordId": 1,
        "issueDate": "2026-03-25",
        "validityDays": 30,
        "notes": "Integration test prescription",
        "items": [
            {
                "medicineName": "Test Medicine",
                "dosage": "500mg",
                "frequency": "2 times daily",
                "durationDays": 7,
                "instructions": "Take with food",
                "quantity": 14
            }
        ]
    }'

# Test 11: Create Lab Result
test_endpoint "Create Lab Result" \
    "$EHR_SERVICE/api/ehr/lab-results" \
    "POST" \
    '{
        "patientId": 2,
        "doctorId": 1,
        "medicalRecordId": 1,
        "testName": "Integration Test",
        "testType": "Blood Test",
        "testDate": "2026-03-25",
        "resultDate": "2026-03-26",
        "result": "Normal",
        "normalRange": "Normal range",
        "unit": "mg/dL",
        "labName": "Test Lab",
        "technician": "Test Tech",
        "notes": "Integration test result"
    }'

# Test 12: Get Patient Medical History
test_endpoint "Get Patient Medical History" \
    "$EHR_SERVICE/api/ehr/records/patient/2" \
    "GET"

# Test 13: Get Patient Prescriptions
test_endpoint "Get Patient Prescriptions" \
    "$EHR_SERVICE/api/ehr/prescriptions/patient/2" \
    "GET"

# Test 14: Get Patient Lab Results
test_endpoint "Get Patient Lab Results" \
    "$EHR_SERVICE/api/ehr/lab-results/patient/2" \
    "GET"

# Test 15: Finalize Medical Record
test_endpoint "Finalize Medical Record" \
    "$EHR_SERVICE/api/ehr/records/1/finalize" \
    "PUT"

echo ""
echo "📊 Test Summary"
echo "==============="
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo -e "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}❌ Some tests failed!${NC}"
    exit 1
fi
