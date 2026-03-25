#!/bin/bash

# MediTrack - View Service Logs Script
# This script helps view logs from running services

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PROJECT_ROOT=$(pwd)
LOGS_DIR="$PROJECT_ROOT/logs"

echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   MediTrack - Service Logs Viewer         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

if [ ! -d "$LOGS_DIR" ]; then
    echo -e "${YELLOW}Logs directory not found.${NC}"
    echo "Services may not have been started yet."
    exit 1
fi

# List available log files
echo "Available service logs:"
echo ""
i=1
declare -a log_files
for log_file in "$LOGS_DIR"/*.log; do
    if [ -f "$log_file" ]; then
        service_name=$(basename "$log_file" .log)
        echo "  $i) $service_name"
        log_files[$i]=$log_file
        i=$((i + 1))
    fi
done

echo "  a) View all logs"
echo "  q) Quit"
echo ""

# Read user choice
read -p "Select a service to view logs (1-$((i-1)), a, or q): " choice

if [ "$choice" = "q" ]; then
    echo "Exiting..."
    exit 0
elif [ "$choice" = "a" ]; then
    echo ""
    echo -e "${GREEN}Showing all logs (press Ctrl+C to stop):${NC}"
    echo ""
    tail -f "$LOGS_DIR"/*.log
elif [ "$choice" -ge 1 ] && [ "$choice" -lt $i ]; then
    selected_log="${log_files[$choice]}"
    service_name=$(basename "$selected_log" .log)
    echo ""
    echo -e "${GREEN}Showing logs for $service_name (press Ctrl+C to stop):${NC}"
    echo ""
    tail -f "$selected_log"
else
    echo "Invalid choice"
    exit 1
fi
