# Pharmacy Service - Testing Guide

## 🚀 Start Pharmacy Service

### Prerequisites
1. Service Registry running (port 8761)
2. User Service running (port 8081)
3. Appointment Service running (port 8082)
4. EHR Service running (port 8083)
5. PostgreSQL running (via docker-compose)

### Start the Service

```bash
# Terminal 5 (after other services)
cd pharmacy-service
mvn spring-boot:run
```

Wait for: `Started PharmacyServiceApplication in X seconds`

### Verify Service

1. **Check Eureka Dashboard**: http://localhost:8761
   - Should see `PHARMACY-SERVICE` registered

2. **Health Check**:
```bash
curl http://localhost:8084/actuator/health
```

---

## 📝 Complete Test Scenario

### Scenario: Complete Pharmacy Workflow

#### Step 1: Add Medicines to Inventory

```bash
# Add Paracetamol
curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Paracetamol",
    "description": "Pain reliever and fever reducer",
    "manufacturer": "PharmaCorp",
    "category": "ANALGESIC",
    "dosageForm": "Tablet",
    "strength": "500mg",
    "price": 5000,
    "stockQuantity": 100,
    "reorderLevel": 20,
    "expiryDate": "2027-12-31",
    "requiresPrescription": false,
    "storageConditions": "Store at room temperature",
    "sideEffects": "Nausea, allergic reactions"
  }'

# Add Amoxicillin
curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Amoxicillin",
    "description": "Antibiotic for bacterial infections",
    "manufacturer": "MediPharm",
    "category": "ANTIBIOTIC",
    "dosageForm": "Capsule",
    "strength": "250mg",
    "price": 15000,
    "stockQuantity": 50,
    "reorderLevel": 10,
    "expiryDate": "2027-06-30",
    "requiresPrescription": true,
    "storageConditions": "Store in cool, dry place",
    "sideEffects": "Diarrhea, nausea, skin rash"
  }'

# Add Vitamin C
curl -X POST http://localhost:8084/api/pharmacy/medicines \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vitamin C",
    "description": "Immune system support",
    "manufacturer": "HealthPlus",
    "category": "VITAMIN_SUPPLEMENT",
    "dosageForm": "Tablet",
    "strength": "1000mg",
    "price": 8000,
    "stockQuantity": 200,
    "reorderLevel": 30,
    "expiryDate": "2028-03-31",
    "requiresPrescription": false,
    "storageConditions": "Store at room temperature",
    "sideEffects": "Mild stomach upset"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Medicine created successfully",
  "data": {
    "id": 1,
    "medicineCode": "MED-A1B2C3D4",
    "name": "Paracetamol",
    "status": "AVAILABLE"
  }
}
```

---

#### Step 2: List All Medicines

```bash
curl http://localhost:8084/api/pharmacy/medicines
```

---

#### Step 3: Search Medicine by Name

```bash
curl "http://localhost:8084/api/pharmacy/medicines/search?name=para"
```

---

#### Step 4: Get Medicines by Category

```bash
curl http://localhost:8084/api/pharmacy/medicines/category/ANTIBIOTIC
```

---

#### Step 5: Create Prescription Order

First, create a prescription in EHR Service, then create order:

```bash
curl -X POST http://localhost:8084/api/pharmacy/orders \
  -H "Content-Type: application/json" \
  -d '{
    "prescriptionNumber": "RX-A1B2C3D4",
    "patientId": 2,
    "pharmacistId": 3,
    "notes": "Patient needs medication urgently",
    "items": [
      {
        "medicineId": 1,
        "quantity": 15,
        "instructions": "Take 1 tablet 3 times daily after meals"
      },
      {
        "medicineId": 3,
        "quantity": 7,
        "instructions": "Take 1 tablet once daily in the morning"
      }
    ]
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 1,
    "orderNumber": "ORD-X1Y2Z3W4",
    "prescriptionNumber": "RX-A1B2C3D4",
    "status": "PENDING",
    "totalAmount": 131000,
    "items": [
      {
        "medicineName": "Paracetamol",
        "quantity": 15,
        "subtotal": 75000
      },
      {
        "medicineName": "Vitamin C",
        "quantity": 7,
        "subtotal": 56000
      }
    ]
  }
}
```

---

#### Step 6: Get Order by ID

```bash
curl http://localhost:8084/api/pharmacy/orders/1
```

---

#### Step 7: Get Patient Orders

```bash
curl http://localhost:8084/api/pharmacy/orders/patient/2
```

---

#### Step 8: Update Order Status to PROCESSING

```bash
curl -X PUT http://localhost:8084/api/pharmacy/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PROCESSING",
    "pharmacistNotes": "Preparing medication"
  }'
```

---

#### Step 9: Update Order Status to READY

```bash
curl -X PUT http://localhost:8084/api/pharmacy/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "READY",
    "pharmacistNotes": "Medication ready for pickup"
  }'
```

---

#### Step 10: Dispense Order (Reduces Stock)

```bash
curl -X PUT http://localhost:8084/api/pharmacy/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "DISPENSED",
    "pharmacistNotes": "Medication dispensed to patient"
  }'
```

This will automatically reduce stock quantities.

---

#### Step 11: Check Updated Stock

```bash
# Check Paracetamol stock (should be 85 now, was 100)
curl http://localhost:8084/api/pharmacy/medicines/1

# Check Vitamin C stock (should be 193 now, was 200)
curl http://localhost:8084/api/pharmacy/medicines/3
```

---

#### Step 12: Manual Stock Update

```bash
# Add stock
curl -X PUT http://localhost:8084/api/pharmacy/medicines/1/stock \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 50,
    "operation": "ADD"
  }'

# Subtract stock
curl -X PUT http://localhost:8084/api/pharmacy/medicines/1/stock \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 10,
    "operation": "SUBTRACT"
  }'
```

---

#### Step 13: Check Low Stock Medicines

```bash
curl http://localhost:8084/api/pharmacy/medicines/low-stock
```

---

#### Step 14: Get Orders by Status

```bash
curl http://localhost:8084/api/pharmacy/orders/status/DISPENSED
```

---

## 🧪 Additional Test Scenarios

### Test 1: Insufficient Stock Error

```bash
# Try to create order with quantity > stock
curl -X POST http://localhost:8084/api/pharmacy/orders \
  -H "Content-Type: application/json" \
  -d '{
    "prescriptionNumber": "RX-TEST123",
    "patientId": 2,
    "pharmacistId": 3,
    "items": [
      {
        "medicineId": 2,
        "quantity": 1000
      }
    ]
  }'
```

Expected: Error message about insufficient stock

---

### Test 2: Duplicate Order Prevention

```bash
# Try to create order with same prescription number
curl -X POST http://localhost:8084/api/pharmacy/orders \
  -H "Content-Type: application/json" \
  -d '{
    "prescriptionNumber": "RX-A1B2C3D4",
    "patientId": 2,
    "pharmacistId": 3,
    "items": [
      {
        "medicineId": 1,
        "quantity": 5
      }
    ]
  }'
```

Expected: Error message about duplicate order

---

### Test 3: Update Medicine Details

```bash
curl -X PUT http://localhost:8084/api/pharmacy/medicines/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Paracetamol",
    "description": "Updated description",
    "manufacturer": "PharmaCorp",
    "category": "ANALGESIC",
    "dosageForm": "Tablet",
    "strength": "500mg",
    "price": 5500,
    "stockQuantity": 125,
    "reorderLevel": 20,
    "expiryDate": "2027-12-31",
    "requiresPrescription": false,
    "storageConditions": "Store at room temperature",
    "sideEffects": "Nausea, allergic reactions"
  }'
```

---

## 📊 API Endpoints Summary

### Medicine Management (10 endpoints)
```
POST   /api/pharmacy/medicines                    - Create medicine
GET    /api/pharmacy/medicines/{id}               - Get by ID
GET    /api/pharmacy/medicines/code/{code}        - Get by code
GET    /api/pharmacy/medicines                    - List all
GET    /api/pharmacy/medicines/category/{cat}     - List by category
GET    /api/pharmacy/medicines/search?name=       - Search by name
GET    /api/pharmacy/medicines/low-stock          - Get low stock items
PUT    /api/pharmacy/medicines/{id}               - Update medicine
PUT    /api/pharmacy/medicines/{id}/stock         - Update stock
```

### Prescription Orders (7 endpoints)
```
POST   /api/pharmacy/orders                       - Create order
GET    /api/pharmacy/orders/{id}                  - Get by ID
GET    /api/pharmacy/orders/number/{number}       - Get by order number
GET    /api/pharmacy/orders/patient/{id}          - List by patient
GET    /api/pharmacy/orders/pharmacist/{id}       - List by pharmacist
GET    /api/pharmacy/orders/status/{status}       - List by status
PUT    /api/pharmacy/orders/{id}/status           - Update status
```

**Total**: 17 endpoints

---

## 📋 Database Verification

```bash
# Connect to database
docker exec -it meditrack-pharmacy-db psql -U meditrack -d pharmacy_db

# View medicines
SELECT id, medicine_code, name, stock_quantity, status FROM medicines;

# View orders
SELECT id, order_number, prescription_number, status, total_amount FROM prescription_orders;

# View order items
SELECT oi.id, po.order_number, m.name, oi.quantity, oi.subtotal 
FROM order_items oi 
JOIN prescription_orders po ON oi.prescription_order_id = po.id
JOIN medicines m ON oi.medicine_id = m.id;

# Exit
\q
```

---

## 🎯 Success Criteria

Pharmacy Service is working correctly if:
- ✅ Service registers with Eureka
- ✅ Can add medicines to inventory
- ✅ Can search and filter medicines
- ✅ Can create prescription orders
- ✅ Validates stock availability
- ✅ Prevents duplicate orders
- ✅ Automatically reduces stock on dispensing
- ✅ Tracks low stock items
- ✅ Generates unique medicine and order codes

---

## 🐛 Troubleshooting

### Service won't start
```bash
# Check if port 8084 is in use
lsof -i :8084

# Check database connection
docker ps | grep pharmacy-db
```

### Stock not reducing
- Verify order status is set to DISPENSED
- Check if stock update operation is correct

### Cannot create order
- Verify prescription number exists in EHR service
- Check medicine IDs are valid
- Ensure sufficient stock available

---

**Pharmacy Service is now ready! 🎉**

Next: Integrate with Payment Service for billing

