#!/bin/bash

# MediTrack - Stop All Services Script
# This script stops all running microservices

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PROJECT_ROOT=$(pwd)
LOGS_DIR="$PROJECT_ROOT/logs"

echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   MediTrack - Stop All Services           ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print info
print_info() {
    echo -e "${YELLOW}▶ $1${NC}"
}

# Stop services by PID files
print_info "Stopping microservices..."
echo ""

if [ -d "$LOGS_DIR" ]; then
    for pid_file in "$LOGS_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            service_name=$(basename "$pid_file" .pid)
            pid=$(cat "$pid_file")
            
            if ps -p $pid > /dev/null 2>&1; then
                echo -n "Stopping $service_name (PID: $pid)... "
                kill $pid 2>/dev/null
                sleep 2
                
                # Force kill if still running
                if ps -p $pid > /dev/null 2>&1; then
                    kill -9 $pid 2>/dev/null
                fi
                
                print_success "Stopped"
            else
                echo "$service_name (PID: $pid) - Already stopped"
            fi
            
            # Remove PID file
            rm "$pid_file"
        fi
    done
else
    print_error "Logs directory not found"
    echo "Services may not have been started with start-all-services.sh"
fi

echo ""

# Stop Docker containers
print_info "Stopping Docker containers..."
docker-compose stop
print_success "Docker containers stopped"

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║     All Services Stopped Successfully     ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════╝${NC}"
echo ""
echo "To start services again, run: ./start-all-services.sh"
echo ""
echo "To completely remove Docker containers and volumes:"
echo "  docker-compose down -v"
echo ""
