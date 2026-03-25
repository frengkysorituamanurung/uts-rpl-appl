#!/bin/bash

# MediTrack Services Verification Script
# This script checks if all services are running correctly

echo "🔍 MediTrack Services Verification"
echo "===================================="
echo ""

ERRORS=0

# Function to check service health
check_service() {
    SERVICE_NAME=$1
    PORT=$2
    HEALTH_URL="http://localhost:$PORT/actuator/health"
    
    echo -n "Checking $SERVICE_NAME (port $PORT)... "
    
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL 2>/dev/null)
    
    if [ "$RESPONSE" = "200" ]; then
        echo "✅ UP"
        return 0
    else
        echo "❌ DOWN (HTTP $RESPONSE)"
        ERRORS=$((ERRORS + 1))
        return 1
    fi
}

# Function to check Eureka registration
check_eureka_registration() {
    SERVICE_NAME=$1
    
    echo -n "Checking $SERVICE_NAME registration in Eureka... "
    
    EUREKA_RESPONSE=$(curl -s http://localhost:8761/eureka/apps 2>/dev/null)
    
    if echo "$EUREKA_RESPONSE" | grep -q "$SERVICE_NAME"; then
        echo "✅ Registered"
        return 0
    else
        echo "❌ Not registered"
        ERRORS=$((ERRORS + 1))
        return 1
    fi
}

echo "1. Checking Docker Containers"
echo "------------------------------"

# Check Docker containers
if docker ps &> /dev/null; then
    RUNNING_CONTAINERS=$(docker ps --format "{{.Names}}" | grep meditrack | wc -l)
    echo "   Running containers: $RUNNING_CONTAINERS"
    
    if [ $RUNNING_CONTAINERS -ge 7 ]; then
        echo "   ✅ Docker infrastructure is running"
    else
        echo "   ⚠️  Expected at least 7 containers"
        echo "   Run: docker-compose up -d"
    fi
else
    echo "   ❌ Cannot connect to Docker"
    ERRORS=$((ERRORS + 1))
fi

echo ""
echo "2. Checking Service Registry (Eureka)"
echo "--------------------------------------"

EUREKA_URL="http://localhost:8761"
echo -n "Checking Eureka dashboard... "

EUREKA_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $EUREKA_URL 2>/dev/null)

if [ "$EUREKA_STATUS" = "200" ]; then
    echo "✅ Accessible"
    echo "   URL: $EUREKA_URL"
else
    echo "❌ Not accessible"
    echo "   Please start service-registry first"
    ERRORS=$((ERRORS + 1))
fi

echo ""
echo "3. Checking Microservices Health"
echo "---------------------------------"

check_service "User Service" 8081
check_service "Appointment Service" 8082
check_service "EHR Service" 8083
check_service "Pharmacy Service" 8084
check_service "Analytics Service" 8085
check_service "Payment Service" 8086

echo ""
echo "4. Checking Eureka Registration"
echo "--------------------------------"

if [ "$EUREKA_STATUS" = "200" ]; then
    check_eureka_registration "USER-SERVICE"
    check_eureka_registration "APPOINTMENT-SERVICE"
    check_eureka_registration "EHR-SERVICE"
    check_eureka_registration "PHARMACY-SERVICE"
    check_eureka_registration "ANALYTICS-SERVICE"
    check_eureka_registration "PAYMENT-SERVICE"
else
    echo "   ⚠️  Skipping (Eureka not accessible)"
fi

echo ""
echo "5. Testing API Endpoints"
echo "------------------------"

# Test User Service
echo -n "Testing User Service API... "
USER_TEST=$(curl -s http://localhost:8081/api/users/role/DOCTOR 2>/dev/null)
if echo "$USER_TEST" | grep -q "success"; then
    echo "✅ Working"
else
    echo "❌ Failed"
    ERRORS=$((ERRORS + 1))
fi

# Test Analytics Service
echo -n "Testing Analytics Service API... "
ANALYTICS_TEST=$(curl -s http://localhost:8085/api/analytics/dashboard 2>/dev/null)
if echo "$ANALYTICS_TEST" | grep -q "success"; then
    echo "✅ Working"
else
    echo "❌ Failed"
    ERRORS=$((ERRORS + 1))
fi

echo ""
echo "6. Database Connectivity"
echo "------------------------"

# Test PostgreSQL connections
echo -n "Testing User Database... "
if docker exec meditrack-user-db psql -U meditrack -d user_db -c "SELECT 1;" &> /dev/null; then
    echo "✅ Connected"
else
    echo "❌ Connection failed"
    ERRORS=$((ERRORS + 1))
fi

echo -n "Testing Appointment Database... "
if docker exec meditrack-appointment-db psql -U meditrack -d appointment_db -c "SELECT 1;" &> /dev/null; then
    echo "✅ Connected"
else
    echo "❌ Connection failed"
    ERRORS=$((ERRORS + 1))
fi

echo -n "Testing EHR Database... "
if docker exec meditrack-ehr-db psql -U meditrack -d ehr_db -c "SELECT 1;" &> /dev/null; then
    echo "✅ Connected"
else
    echo "❌ Connection failed"
    ERRORS=$((ERRORS + 1))
fi

echo -n "Testing Pharmacy Database... "
if docker exec meditrack-pharmacy-db psql -U meditrack -d pharmacy_db -c "SELECT 1;" &> /dev/null; then
    echo "✅ Connected"
else
    echo "❌ Connection failed"
    ERRORS=$((ERRORS + 1))
fi

echo -n "Testing Payment Database... "
if docker exec meditrack-payment-db psql -U meditrack -d payment_db -c "SELECT 1;" &> /dev/null; then
    echo "✅ Connected"
else
    echo "❌ Connection failed"
    ERRORS=$((ERRORS + 1))
fi

echo ""
echo "===================================="

if [ $ERRORS -eq 0 ]; then
    echo "✅ All services are running correctly!"
    echo ""
    echo "🎉 MediTrack platform is ready to use!"
    echo ""
    echo "Quick Links:"
    echo "  • Eureka Dashboard: http://localhost:8761"
    echo "  • User Service: http://localhost:8081"
    echo "  • Analytics Dashboard: http://localhost:8085/api/analytics/dashboard"
    echo ""
    echo "Next steps:"
    echo "  • Import postman-collection.json to Postman"
    echo "  • Start testing APIs"
    echo "  • See POSTMAN_COLLECTION_GUIDE.md for examples"
    exit 0
else
    echo "❌ Found $ERRORS error(s)"
    echo ""
    echo "Troubleshooting:"
    echo "  1. Check if all services are started"
    echo "  2. Check service logs for errors"
    echo "  3. Verify Docker containers are running"
    echo "  4. See LOCAL_SETUP_GUIDE.md for help"
    exit 1
fi
