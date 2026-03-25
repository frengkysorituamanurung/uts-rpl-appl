#!/bin/bash

# MediTrack Prerequisites Checker
# This script checks if all required software is installed

echo "🔍 MediTrack Prerequisites Checker"
echo "===================================="
echo ""

ERRORS=0

# Check Java
echo -n "Checking Java 21... "
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 21 ]; then
        echo "✅ Java $JAVA_VERSION found"
    else
        echo "❌ Java 21 required, found Java $JAVA_VERSION"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo "❌ Java not found"
    echo "   Install from: https://adoptium.net/"
    ERRORS=$((ERRORS + 1))
fi

# Check Maven
echo -n "Checking Maven... "
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
    echo "✅ Maven $MVN_VERSION found"
else
    echo "❌ Maven not found"
    echo "   Install from: https://maven.apache.org/download.cgi"
    ERRORS=$((ERRORS + 1))
fi

# Check Docker
echo -n "Checking Docker... "
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version | cut -d' ' -f3 | cut -d',' -f1)
    echo "✅ Docker $DOCKER_VERSION found"
    
    # Check if Docker is running
    if docker ps &> /dev/null; then
        echo "   ✅ Docker daemon is running"
    else
        echo "   ⚠️  Docker daemon is not running"
        echo "   Please start Docker Desktop"
        ERRORS=$((ERRORS + 1))
    fi
else
    echo "❌ Docker not found"
    echo "   Install from: https://www.docker.com/products/docker-desktop"
    ERRORS=$((ERRORS + 1))
fi

# Check Docker Compose
echo -n "Checking Docker Compose... "
if command -v docker-compose &> /dev/null; then
    COMPOSE_VERSION=$(docker-compose --version | cut -d' ' -f4 | cut -d',' -f1)
    echo "✅ Docker Compose $COMPOSE_VERSION found"
else
    echo "❌ Docker Compose not found"
    echo "   Usually included with Docker Desktop"
    ERRORS=$((ERRORS + 1))
fi

# Check Git
echo -n "Checking Git... "
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | cut -d' ' -f3)
    echo "✅ Git $GIT_VERSION found"
else
    echo "⚠️  Git not found (optional)"
    echo "   Install from: https://git-scm.com/downloads"
fi

echo ""
echo "Checking required ports..."

# Function to check if port is available
check_port() {
    PORT=$1
    SERVICE=$2
    if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "   ⚠️  Port $PORT ($SERVICE) is in use"
        return 1
    else
        echo "   ✅ Port $PORT ($SERVICE) is available"
        return 0
    fi
}

# Check all required ports
check_port 8761 "Service Registry"
check_port 8081 "User Service"
check_port 8082 "Appointment Service"
check_port 8083 "EHR Service"
check_port 8084 "Pharmacy Service"
check_port 8085 "Analytics Service"
check_port 8086 "Payment Service"
check_port 5432 "PostgreSQL User DB"
check_port 5433 "PostgreSQL Appointment DB"
check_port 5434 "PostgreSQL EHR DB"
check_port 5436 "PostgreSQL Pharmacy DB"
check_port 5437 "PostgreSQL Payment DB"

echo ""
echo "Checking system resources..."

# Check available RAM (macOS/Linux)
if [[ "$OSTYPE" == "darwin"* ]]; then
    TOTAL_RAM=$(sysctl -n hw.memsize | awk '{print int($1/1024/1024/1024)}')
    echo "   💾 Total RAM: ${TOTAL_RAM}GB"
    if [ "$TOTAL_RAM" -lt 8 ]; then
        echo "   ⚠️  Recommended: 8GB+ RAM"
    else
        echo "   ✅ RAM is sufficient"
    fi
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    TOTAL_RAM=$(free -g | awk '/^Mem:/{print $2}')
    echo "   💾 Total RAM: ${TOTAL_RAM}GB"
    if [ "$TOTAL_RAM" -lt 8 ]; then
        echo "   ⚠️  Recommended: 8GB+ RAM"
    else
        echo "   ✅ RAM is sufficient"
    fi
fi

# Check available disk space
AVAILABLE_SPACE=$(df -h . | awk 'NR==2 {print $4}')
echo "   💿 Available disk space: $AVAILABLE_SPACE"

echo ""
echo "===================================="

if [ $ERRORS -eq 0 ]; then
    echo "✅ All prerequisites are met!"
    echo ""
    echo "Next steps:"
    echo "1. Run: docker-compose up -d"
    echo "2. Run: mvn clean install -DskipTests"
    echo "3. Start services (see LOCAL_SETUP_GUIDE.md)"
    exit 0
else
    echo "❌ Found $ERRORS error(s)"
    echo ""
    echo "Please install missing software and try again."
    echo "See LOCAL_SETUP_GUIDE.md for detailed instructions."
    exit 1
fi
