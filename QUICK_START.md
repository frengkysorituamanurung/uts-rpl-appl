# MediTrack - Quick Start Guide

## 🚀 Cara Cepat Menjalankan Aplikasi (5 Menit)

### Step 1: Start Infrastructure (1 menit)

```bash
# Start PostgreSQL, RabbitMQ, Redis
docker-compose up -d

# Tunggu 10 detik untuk database siap
sleep 10
```

### Step 2: Build Project (2 menit)

```bash
# Build semua services
mvn clean install -DskipTests
```

### Step 3: Start Services (2 menit)

Buka 3 terminal dan jalankan:

**Terminal 1 - Service Registry:**
```bash
cd service-registry
mvn spring-boot:run
```

**Terminal 2 - User Service:**
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 3 - Test API:**
```bash
# Tunggu 30 detik untuk services start
sleep 30

# Register a doctor
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@test.com",
    "password": "password",
    "firstName": "Dr. John",
    "lastName": "Doe",
    "phoneNumber": "08123456789",
    "role": "DOCTOR"
  }'
```

## ✅ Verifikasi

1. **Service Registry**: http://localhost:8761
   - Harus melihat `USER-SERVICE` terdaftar

2. **User Service Health**: http://localhost:8081/actuator/health
   - Harus return `{"status":"UP"}`

3. **Test Register User**:
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@test.com",
    "password": "password",
    "firstName": "Jane",
    "lastName": "Smith",
    "phoneNumber": "08123456790",
    "role": "PATIENT"
  }'
```

Expected response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "patient@test.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "role": "PATIENT",
    "status": "ACTIVE"
  }
}
```

## 📝 Test Scenarios

### 1. User Management

**Register Doctor:**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"doctor1@test.com","password":"pass","firstName":"Dr. Ahmad","lastName":"Wijaya","phoneNumber":"081234567891","role":"DOCTOR"}'
```

**Register Patient:**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"patient1@test.com","password":"pass","firstName":"Budi","lastName":"Santoso","phoneNumber":"081234567892","role":"PATIENT"}'
```

**Login:**
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"doctor1@test.com","password":"pass"}'
```

**Get User by ID:**
```bash
curl http://localhost:8081/api/users/1
```

**List All Doctors:**
```bash
curl http://localhost:8081/api/users/role/DOCTOR
```

## 🛠️ Troubleshooting

### Port Already in Use
```bash
# Check what's using the port
lsof -i :8081

# Kill the process
kill -9 <PID>
```

### Database Connection Error
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart docker-compose
docker-compose restart
```

### Service Not Registering with Eureka
```bash
# Make sure Service Registry is running first
# Wait 30 seconds after starting a service
# Check Eureka dashboard: http://localhost:8761
```

## 📊 Database Access

**Using psql:**
```bash
docker exec -it meditrack-user-db psql -U meditrack -d user_db

# List tables
\dt

# Query users
SELECT * FROM users;

# Exit
\q
```

**Using pgAdmin:**
- URL: http://localhost:5050
- Email: admin@meditrack.com
- Password: admin

## 🔄 Reset Everything

```bash
# Stop all services (Ctrl+C in each terminal)

# Stop and remove containers
docker-compose down -v

# Clean build
mvn clean

# Start fresh
docker-compose up -d
mvn clean install -DskipTests
```

## 📚 Next Services to Implement

Setelah User Service berjalan, implement services lainnya dengan urutan:

1. ✅ User Service (DONE)
2. ⏳ Appointment Service
3. ⏳ EHR Service
4. ⏳ Pharmacy Service
5. ⏳ Payment Service
6. ⏳ Analytics Service

Setiap service mengikuti pattern yang sama:
- Model (Entity)
- Repository (JPA)
- Service (Business Logic)
- Controller (REST API)
- DTO (Data Transfer Object)

## 💡 Tips

1. **Start services in order**: Registry → User → Others
2. **Wait between starts**: Give each service 30 seconds
3. **Check logs**: Look for "Started Application in X seconds"
4. **Use Postman**: Import collection for easier testing
5. **Check Eureka**: Verify service registration

## 🎯 Success Criteria

Aplikasi berhasil jika:
- ✅ Service Registry running di port 8761
- ✅ User Service terdaftar di Eureka
- ✅ Bisa register user via API
- ✅ Bisa login user via API
- ✅ Database menyimpan data user

## 📞 Need Help?

1. Check logs di console
2. Verify docker containers: `docker ps`
3. Check database: `docker exec -it meditrack-user-db psql -U meditrack -d user_db`
4. Review application.yml configuration

---

**Selamat! Aplikasi MediTrack sudah running! 🎉**
