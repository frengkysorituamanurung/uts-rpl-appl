#!/bin/bash

echo "🧪 Testing Appointment Service..."
echo ""

BASE_URL="http://localhost:8082/api/appointments"

echo "1️⃣ Booking an appointment..."
RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "appointmentDateTime": "2026-04-01T10:00:00",
    "durationMinutes": 30,
    "type": "CONSULTATION",
    "reasonForVisit": "Regular checkup",
    "symptoms": "Mild headache"
  }')

echo "$RESPONSE" | jq '.'
APPOINTMENT_ID=$(echo "$RESPONSE" | jq -r '.data.id')
echo ""

if [ "$APPOINTMENT_ID" != "null" ]; then
    echo "✅ Appointment booked successfully! ID: $APPOINTMENT_ID"
    echo ""

    echo "2️⃣ Getting appointment details..."
    curl -s "$BASE_URL/$APPOINTMENT_ID" | jq '.'
    echo ""

    echo "3️⃣ Rescheduling appointment..."
    curl -s -X PUT "$BASE_URL/$APPOINTMENT_ID/reschedule" \
      -H "Content-Type: application/json" \
      -d '{
        "newAppointmentDateTime": "2026-04-01T14:00:00",
        "reason": "Patient requested different time"
      }' | jq '.'
    echo ""

    echo "4️⃣ Updating status to CONFIRMED..."
    curl -s -X PUT "$BASE_URL/$APPOINTMENT_ID/status" \
      -H "Content-Type: application/json" \
      -d '{"status": "CONFIRMED"}' | jq '.'
    echo ""

    echo "5️⃣ Getting appointments by patient..."
    curl -s "$BASE_URL/patient/2" | jq '.'
    echo ""

    echo "6️⃣ Getting appointments by doctor..."
    curl -s "$BASE_URL/doctor/1" | jq '.'
    echo ""

    echo "✅ All tests completed!"
else
    echo "❌ Failed to book appointment"
fi
