#!/bin/bash

# MediTrack - Automated Service Startup Script
# This script starts all microservices automatically

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project root directory
PROJECT_ROOT=$(pwd)
LOGS_DIR="$PROJECT_ROOT/logs"

# Create logs directory
mkdir -p "$LOGS_DIR"

echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   MediTrack - Automated Service Startup   ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

# Function to print step
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print warning
print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Step 1: Check prerequisites
print_step "Step 1: Checking prerequisites..."
if [ -f "./check-prerequisites.sh" ]; then
    if ./check-prerequisites.sh > /dev/null 2>&1; then
        print_success "Prerequisites check passed"
    else
        print_error "Prerequisites check failed"
        echo "Please run: ./check-prerequisites.sh"
        exit 1
    fi
else
    print_warning "check-prerequisites.sh not found, skipping..."
fi
echo ""

# Step 2: Start Docker infrastructure
print_step "Step 2: Starting Docker infrastructure..."
if docker ps > /dev/null 2>&1; then
    docker-compose up -d
    print_success "Docker containers started"
    echo "   Waiting 30 seconds for databases to initialize..."
    sleep 30
else
    print_error "Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi
echo ""

# Step 3: Build project
print_step "Step 3: Building project..."
echo "   This may take a few minutes on first run..."
if mvn clean install -DskipTests > "$LOGS_DIR/build.log" 2>&1; then
    print_success "Project built successfully"
else
    print_error "Build failed"
    echo "Check logs: $LOGS_DIR/build.log"
    exit 1
fi
echo ""

# Step 4: Start services
print_step "Step 4: Starting microservices..."
echo ""

# Array of services with their directories and wait times
declare -a SERVICES=(
    "service-registry:Service Registry:30"
    "user-service:User Service:20"
    "appointment-service:Appointment Service:20"
    "ehr-service:EHR Service:20"
    "pharmacy-service:Pharmacy Service:20"
    "payment-service:Payment Service:20"
    "analytics-service:Analytics Service:20"
)

# Start each service
for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service_dir service_name wait_time <<< "$service_info"
    
    echo -e "${YELLOW}Starting $service_name...${NC}"
    
    # Start service in background
    cd "$PROJECT_ROOT/$service_dir"
    nohup mvn spring-boot:run > "$LOGS_DIR/$service_dir.log" 2>&1 &
    SERVICE_PID=$!
    
    # Save PID to file
    echo $SERVICE_PID > "$LOGS_DIR/$service_dir.pid"
    
    print_success "$service_name started (PID: $SERVICE_PID)"
    echo "   Log file: $LOGS_DIR/$service_dir.log"
    
    # Wait before starting next service
    if [ "$service_dir" != "analytics-service" ]; then
        echo "   Waiting ${wait_time}s before starting next service..."
        sleep $wait_time
    fi
    
    cd "$PROJECT_ROOT"
    echo ""
done

print_success "All services started!"
echo ""

# Step 5: Wait for services to be ready
print_step "Step 5: Waiting for services to be ready..."
echo "   This may take 1-2 minutes..."
sleep 60
echo ""

# Step 6: Verify services
print_step "Step 6: Verifying services..."
echo ""

if [ -f "./verify-services.sh" ]; then
    ./verify-services.sh
else
    print_warning "verify-services.sh not found, skipping verification"
    echo ""
    echo "Please manually check:"
    echo "  • Eureka Dashboard: http://localhost:8761"
    echo "  • All 7 services should be registered"
fi

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║          Setup Complete! 🎉                ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
echo ""
echo "Quick Links:"
echo "  • Eureka Dashboard: http://localhost:8761"
echo "  • User Service: http://localhost:8081"
echo "  • Appointment Service: http://localhost:8082"
echo "  • EHR Service: http://localhost:8083"
echo "  • Pharmacy Service: http://localhost:8084"
echo "  • Analytics Service: http://localhost:8085"
echo "  • Payment Service: http://localhost:8086"
echo ""
echo "Logs location: $LOGS_DIR/"
echo ""
echo "To stop all services, run: ./stop-all-services.sh"
echo ""
echo "Next steps:"
echo "  1. Import postman-collection.json to Postman"
echo "  2. Start testing APIs"
echo "  3. See POSTMAN_COLLECTION_GUIDE.md for examples"
echo ""
