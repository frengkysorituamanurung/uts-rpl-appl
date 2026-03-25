# Payment Service - Testing Guide

## 🚀 Start Payment Service

### Prerequisites
1. Service Registry running (port 8761)
2. User Service running (port 8081)
3. Appointment Service running (port 8082)
4. EHR Service running (port 8083)
5. Pharmacy Service running (port 8084)
6. PostgreSQL running (via docker-compose)

### Start the Service

```bash
# Terminal 6 (after other services)
cd payment-service
mvn spring-boot:run
```

Wait for: `Started PaymentServiceApplication in X seconds`

### Verify Service

1. **Check Eureka Dashboard**: http://localhost:8761
   - Should see `PAYMENT-SERVICE` registered

2. **Health Check**:
```bash
curl http://localhost:8086/actuator/health
```

---

## 📝 Complete Test Scenario

### Scenario: Complete Payment Workflow

#### Step 1: Create Payment for Appointment

```bash
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "APPOINTMENT",
    "referenceId": 1,
    "referenceNumber": "APT-12345",
    "amount": 150000,
    "paymentMethod": "CREDIT_CARD",
    "description": "Consultation fee with Dr. Ahmad",
    "notes": "Regular checkup appointment"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Payment created successfully",
  "data": {
    "id": 1,
    "paymentNumber": "PAY-A1B2C3D4",
    "patientId": 2,
    "paymentType": "APPOINTMENT",
    "amount": 150000,
    "status": "PENDING"
  }
}
```

---

#### Step 2: Create Payment for Pharmacy Order

```bash
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "PHARMACY_ORDER",
    "referenceId": 1,
    "referenceNumber": "ORD-X1Y2Z3W4",
    "amount": 131000,
    "paymentMethod": "E_WALLET",
    "description": "Medication payment",
    "notes": "Prescription order payment"
  }'
```

---

#### Step 3: Get Payment by ID

```bash
curl http://localhost:8086/api/payments/1
```

---

#### Step 4: Get Payment by Number

```bash
curl http://localhost:8086/api/payments/number/PAY-A1B2C3D4
```

---

#### Step 5: Process Payment (Simulated)

```bash
curl -X POST http://localhost:8086/api/payments/1/process
```

Expected response (90% success rate):
```json
{
  "success": true,
  "message": "Payment processed",
  "data": {
    "id": 1,
    "paymentNumber": "PAY-A1B2C3D4",
    "status": "COMPLETED",
    "transactionId": "TXN-ABC123DEF456",
    "paidAt": "2026-03-25T14:30:00"
  }
}
```

Or if failed (10% chance):
```json
{
  "success": true,
  "message": "Payment processed",
  "data": {
    "id": 1,
    "status": "FAILED",
    "failureReason": "Payment gateway declined the transaction"
  }
}
```

---

#### Step 6: Get Patient Payments

```bash
curl http://localhost:8086/api/payments/patient/2
```

---

#### Step 7: Get Payments by Status

```bash
# Get completed payments
curl http://localhost:8086/api/payments/status/COMPLETED

# Get pending payments
curl http://localhost:8086/api/payments/status/PENDING

# Get failed payments
curl http://localhost:8086/api/payments/status/FAILED
```

---

#### Step 8: Get Payments by Type

```bash
# Get appointment payments
curl http://localhost:8086/api/payments/type/APPOINTMENT

# Get pharmacy order payments
curl http://localhost:8086/api/payments/type/PHARMACY_ORDER
```

---

#### Step 9: Manual Status Update

```bash
curl -X PUT http://localhost:8086/api/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "notes": "Payment verified manually"
  }'
```

---

#### Step 10: Refund Payment

```bash
curl -X POST http://localhost:8086/api/payments/1/refund \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Appointment cancelled by doctor"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Payment refunded successfully",
  "data": {
    "id": 1,
    "status": "REFUNDED",
    "notes": "Refunded: Appointment cancelled by doctor"
  }
}
```

---

## 🧪 Additional Test Scenarios

### Test 1: Multiple Payment Methods

```bash
# Cash payment
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "APPOINTMENT",
    "referenceId": 2,
    "amount": 200000,
    "paymentMethod": "CASH",
    "description": "Specialist consultation"
  }'

# Bank transfer
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "LAB_TEST",
    "referenceId": 1,
    "amount": 350000,
    "paymentMethod": "BANK_TRANSFER",
    "description": "Complete blood count test"
  }'

# Insurance
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "APPOINTMENT",
    "referenceId": 3,
    "amount": 500000,
    "paymentMethod": "INSURANCE",
    "description": "Surgery consultation",
    "notes": "Insurance claim number: INS-2024-001"
  }'
```

---

### Test 2: Duplicate Payment Prevention

```bash
# Try to create payment with same reference
curl -X POST http://localhost:8086/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 2,
    "paymentType": "APPOINTMENT",
    "referenceId": 1,
    "amount": 150000,
    "paymentMethod": "CASH"
  }'
```

Expected: Error message about duplicate payment

---

### Test 3: Invalid Status Transition

```bash
# Try to update refunded payment
curl -X PUT http://localhost:8086/api/payments/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED"
  }'
```

Expected: Error message about invalid status transition

---

### Test 4: Refund Non-Completed Payment

```bash
# Try to refund pending payment
curl -X POST http://localhost:8086/api/payments/2/refund \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Test refund"
  }'
```

Expected: Error message - only completed payments can be refunded

---

## 📊 API Endpoints Summary

### Payment Management (10 endpoints)
```
POST   /api/payments                      - Create payment
GET    /api/payments/{id}                 - Get by ID
GET    /api/payments/number/{number}      - Get by payment number
GET    /api/payments/patient/{id}         - List by patient
GET    /api/payments/status/{status}      - List by status
GET    /api/payments/type/{type}          - List by type
POST   /api/payments/{id}/process         - Process payment
PUT    /api/payments/{id}/status          - Update status
POST   /api/payments/{id}/refund          - Refund payment
```

**Total**: 9 endpoints

---

## 📋 Database Verification

```bash
# Connect to database
docker exec -it meditrack-payment-db psql -U meditrack -d payment_db

# View payments
SELECT id, payment_number, patient_id, payment_type, amount, status FROM payments;

# View payments by status
SELECT payment_number, payment_type, amount, status, paid_at 
FROM payments 
WHERE status = 'COMPLETED';

# View payments by patient
SELECT payment_number, payment_type, amount, status 
FROM payments 
WHERE patient_id = 2;

# Exit
\q
```

---

## 🎯 Success Criteria

Payment Service is working correctly if:
- ✅ Service registers with Eureka
- ✅ Can create payments for appointments and orders
- ✅ Prevents duplicate payments
- ✅ Simulates payment processing
- ✅ Generates unique payment numbers
- ✅ Tracks payment status transitions
- ✅ Supports multiple payment methods
- ✅ Can refund completed payments
- ✅ Validates status transitions

---

## 💡 Payment Flow Integration

### Complete Patient Journey:

1. **Book Appointment** (Appointment Service)
   ```bash
   POST /api/appointments
   ```

2. **Create Payment for Appointment** (Payment Service)
   ```bash
   POST /api/payments
   {
     "paymentType": "APPOINTMENT",
     "referenceId": <appointment_id>
   }
   ```

3. **Process Payment** (Payment Service)
   ```bash
   POST /api/payments/{id}/process
   ```

4. **Complete Appointment** (Appointment Service)
   ```bash
   PUT /api/appointments/{id}/status
   {"status": "COMPLETED"}
   ```

5. **Create Medical Record** (EHR Service)
   ```bash
   POST /api/ehr/records
   ```

6. **Create Prescription** (EHR Service)
   ```bash
   POST /api/ehr/prescriptions
   ```

7. **Create Pharmacy Order** (Pharmacy Service)
   ```bash
   POST /api/pharmacy/orders
   ```

8. **Create Payment for Order** (Payment Service)
   ```bash
   POST /api/payments
   {
     "paymentType": "PHARMACY_ORDER",
     "referenceId": <order_id>
   }
   ```

9. **Process Order Payment** (Payment Service)
   ```bash
   POST /api/payments/{id}/process
   ```

10. **Dispense Medication** (Pharmacy Service)
    ```bash
    PUT /api/pharmacy/orders/{id}/status
    {"status": "DISPENSED"}
    ```

---

## 🐛 Troubleshooting

### Service won't start
```bash
# Check if port 8086 is in use
lsof -i :8086

# Check database connection
docker ps | grep payment-db
```

### Payment processing always fails
- This is simulated with 10% failure rate
- Check console logs for processing messages
- Retry the process endpoint

### Cannot refund payment
- Verify payment status is COMPLETED
- Check if payment was already refunded

---

## 📈 Payment Statistics

### Get payment summary by status:
```bash
# In PostgreSQL
SELECT status, COUNT(*), SUM(amount) 
FROM payments 
GROUP BY status;
```

### Get payment summary by type:
```bash
SELECT payment_type, COUNT(*), SUM(amount) 
FROM payments 
GROUP BY payment_type;
```

### Get payment summary by method:
```bash
SELECT payment_method, COUNT(*), SUM(amount) 
FROM payments 
GROUP BY payment_method;
```

---

**Payment Service is now ready! 🎉**

Next: Implement Analytics Service for reporting and insights

