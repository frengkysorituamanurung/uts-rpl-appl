#!/bin/bash

# MediTrack - Restart All Services Script
# This script restarts all microservices

# Colors for output
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   MediTrack - Restart All Services        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

# Stop all services
echo "Stopping all services..."
./stop-all-services.sh

echo ""
echo "Waiting 5 seconds before restart..."
sleep 5
echo ""

# Start all services
echo "Starting all services..."
./start-all-services.sh
