# 🤖 MediTrack - Automation Scripts Guide

Panduan lengkap untuk menggunakan automation scripts untuk setup dan menjalankan MediTrack platform.

---

## 📋 Available Scripts

| Script | Description | Usage |
|--------|-------------|-------|
| `check-prerequisites.sh` | Check system requirements | `./check-prerequisites.sh` |
| `start-all-services.sh` | Start all services automatically | `./start-all-services.sh` |
| `stop-all-services.sh` | Stop all running services | `./stop-all-services.sh` |
| `restart-all-services.sh` | Restart all services | `./restart-all-services.sh` |
| `verify-services.sh` | Verify all services are running | `./verify-services.sh` |
| `view-logs.sh` | View service logs interactively | `./view-logs.sh` |

---

## 🚀 Quick Start (Automated)

### One-Command Setup

```bash
# Start everything automatically
./start-all-services.sh
```

This script will:
1. ✅ Check prerequisites
2. ✅ Start Docker containers
3. ✅ Build the project
4. ✅ Start all 7 microservices
5. ✅ Verify services are running

**Total time**: ~5-10 minutes (first run)

---

## 📖 Detailed Usage

### 1. Check Prerequisites

Before starting, verify your system has all required software:

```bash
./check-prerequisites.sh
```

**What it checks**:
- Java 21
- Maven 3.8+
- Docker & Docker Compose
- Available ports (8761, 8081-8086, 5432-5437)
- System resources (RAM, disk space)

**Expected output**:
```
✅ All prerequisites are met!
```

---

### 2. Start All Services

Start the entire MediTrack platform:

```bash
./start-all-services.sh
```

**What it does**:

**Step 1**: Check prerequisites
```
▶ Step 1: Checking prerequisites...
✓ Prerequisites check passed
```

**Step 2**: Start Docker infrastructure
```
▶ Step 2: Starting Docker infrastructure...
✓ Docker containers started
   Waiting 30 seconds for databases to initialize...
```

**Step 3**: Build project
```
▶ Step 3: Building project...
   This may take a few minutes on first run...
✓ Project built successfully
```

**Step 4**: Start microservices
```
▶ Step 4: Starting microservices...

Starting Service Registry...
✓ Service Registry started (PID: 12345)
   Log file: logs/service-registry.log
   Waiting 30s before starting next service...

Starting User Service...
✓ User Service started (PID: 12346)
   Log file: logs/user-service.log
   Waiting 20s before starting next service...

[... continues for all 7 services ...]
```

**Step 5**: Wait for services to be ready
```
▶ Step 5: Waiting for services to be ready...
   This may take 1-2 minutes...
```

**Step 6**: Verify services
```
▶ Step 6: Verifying services...
[Runs verification checks]
```

**Final output**:
```
╔════════════════════════════════════════════╗
║          Setup Complete! 🎉                ║
╚════════════════════════════════════════════╝

Quick Links:
  • Eureka Dashboard: http://localhost:8761
  • User Service: http://localhost:8081
  [... all service URLs ...]

Logs location: logs/

To stop all services, run: ./stop-all-services.sh
```

---

### 3. Verify Services

Check if all services are running correctly:

```bash
./verify-services.sh
```

**What it checks**:
- Docker containers status
- Eureka dashboard accessibility
- All microservices health endpoints
- Eureka registration status
- API endpoint functionality
- Database connectivity

**Expected output**:
```
✅ All services are running correctly!

🎉 MediTrack platform is ready to use!
```

---

### 4. View Logs

View logs from running services:

```bash
./view-logs.sh
```

**Interactive menu**:
```
Available service logs:

  1) service-registry
  2) user-service
  3) appointment-service
  4) ehr-service
  5) pharmacy-service
  6) payment-service
  7) analytics-service
  8) build
  a) View all logs
  q) Quit

Select a service to view logs (1-8, a, or q):
```

**Options**:
- Select `1-8` to view specific service log
- Select `a` to view all logs simultaneously
- Select `q` to quit
- Press `Ctrl+C` to stop viewing logs

**Example - View User Service logs**:
```bash
./view-logs.sh
# Select: 2

Showing logs for user-service (press Ctrl+C to stop):

2024-03-25 10:30:15.123  INFO 12346 --- [main] c.m.u.UserServiceApplication
: Starting UserServiceApplication...
[... live log output ...]
```

---

### 5. Stop All Services

Stop all running services:

```bash
./stop-all-services.sh
```

**What it does**:
- Stops all microservices gracefully
- Stops Docker containers
- Cleans up PID files

**Output**:
```
▶ Stopping microservices...

Stopping service-registry (PID: 12345)... ✓ Stopped
Stopping user-service (PID: 12346)... ✓ Stopped
[... continues for all services ...]

▶ Stopping Docker containers...
✓ Docker containers stopped

╔════════════════════════════════════════════╗
║     All Services Stopped Successfully     ║
╚════════════════════════════════════════════╝
```

---

### 6. Restart All Services

Restart all services (stop + start):

```bash
./restart-all-services.sh
```

This is equivalent to:
```bash
./stop-all-services.sh
./start-all-services.sh
```

---

## 📁 Logs Directory

All service logs are stored in `logs/` directory:

```
logs/
├── service-registry.log
├── user-service.log
├── appointment-service.log
├── ehr-service.log
├── pharmacy-service.log
├── payment-service.log
├── analytics-service.log
├── build.log
├── service-registry.pid
├── user-service.pid
└── [... other PID files ...]
```

**Log files** (`.log`): Service output and errors  
**PID files** (`.pid`): Process IDs for running services

---

## 🔧 Troubleshooting

### Problem 1: Script Permission Denied

**Error**: `Permission denied: ./start-all-services.sh`

**Solution**:
```bash
chmod +x *.sh
```

---

### Problem 2: Services Not Starting

**Check logs**:
```bash
./view-logs.sh
# Select the service that failed
```

**Common issues**:
- Port already in use
- Database not ready
- Build failed

**Solution**:
```bash
# Stop everything
./stop-all-services.sh

# Check ports
lsof -i :8081

# Restart
./start-all-services.sh
```

---

### Problem 3: Docker Not Running

**Error**: `Docker is not running`

**Solution**:
1. Start Docker Desktop
2. Wait for Docker to fully start
3. Run script again:
```bash
./start-all-services.sh
```

---

### Problem 4: Build Failed

**Check build log**:
```bash
cat logs/build.log
```

**Solution**:
```bash
# Clean Maven cache
mvn clean

# Try manual build
mvn clean install -DskipTests

# If successful, run script again
./start-all-services.sh
```

---

### Problem 5: Service Stuck/Not Responding

**Solution**:
```bash
# Force stop all services
./stop-all-services.sh

# Kill any remaining Java processes
pkill -9 java

# Restart
./start-all-services.sh
```

---

## 🎯 Best Practices

### 1. First Time Setup

```bash
# 1. Check prerequisites
./check-prerequisites.sh

# 2. If all OK, start services
./start-all-services.sh

# 3. Wait for completion (5-10 minutes)

# 4. Verify everything is running
./verify-services.sh

# 5. Import Postman collection and test
```

### 2. Daily Development

```bash
# Morning: Start services
./start-all-services.sh

# During development: View logs if needed
./view-logs.sh

# Evening: Stop services
./stop-all-services.sh
```

### 3. After Code Changes

```bash
# Restart specific service manually
cd user-service
mvn spring-boot:run

# Or restart all services
./restart-all-services.sh
```

### 4. Clean Restart

```bash
# Stop everything
./stop-all-services.sh

# Remove Docker volumes (clean database)
docker-compose down -v

# Start fresh
./start-all-services.sh
```

---

## 📊 Service Startup Times

| Service | Startup Time | Wait Time |
|---------|--------------|-----------|
| Service Registry | ~30s | 30s |
| User Service | ~20s | 20s |
| Appointment Service | ~20s | 20s |
| EHR Service | ~20s | 20s |
| Pharmacy Service | ~20s | 20s |
| Payment Service | ~20s | 20s |
| Analytics Service | ~20s | 0s |

**Total startup time**: ~3-4 minutes (after build)

---

## 🔄 Script Workflow

```
start-all-services.sh
    │
    ├─> check-prerequisites.sh
    │   └─> Verify Java, Maven, Docker, Ports
    │
    ├─> docker-compose up -d
    │   └─> Start PostgreSQL, RabbitMQ, Redis
    │
    ├─> mvn clean install -DskipTests
    │   └─> Build all services
    │
    ├─> Start services sequentially
    │   ├─> service-registry (wait 30s)
    │   ├─> user-service (wait 20s)
    │   ├─> appointment-service (wait 20s)
    │   ├─> ehr-service (wait 20s)
    │   ├─> pharmacy-service (wait 20s)
    │   ├─> payment-service (wait 20s)
    │   └─> analytics-service
    │
    └─> verify-services.sh
        └─> Check all services are UP
```

---

## 💡 Tips

1. **First run takes longer**: Maven downloads dependencies (~5-10 minutes)
2. **Subsequent runs are faster**: Dependencies are cached (~3-4 minutes)
3. **Monitor logs**: Use `./view-logs.sh` to debug issues
4. **Check Eureka**: Always verify at http://localhost:8761
5. **Be patient**: Wait for each service to fully start before testing

---

## 📚 Related Documentation

- [LOCAL_SETUP_GUIDE.md](LOCAL_SETUP_GUIDE.md) - Complete manual setup guide
- [POSTMAN_COLLECTION_GUIDE.md](POSTMAN_COLLECTION_GUIDE.md) - API testing guide
- [PROJECT_COMPLETE.md](PROJECT_COMPLETE.md) - Project overview

---

## ✅ Success Checklist

After running `./start-all-services.sh`:

- [ ] Script completed without errors
- [ ] All 7 services started (check logs/)
- [ ] Eureka shows 7 registered services (http://localhost:8761)
- [ ] `./verify-services.sh` shows all ✅
- [ ] Can access all service URLs
- [ ] Postman collection imported
- [ ] Test APIs working

---

**Happy Coding! 🚀**

