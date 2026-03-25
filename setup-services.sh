#!/bin/bash

echo "🚀 Setting up MediTrack Services..."

# Create directories
echo "📁 Creating service directories..."

services=("appointment-service" "ehr-service" "pharmacy-service" "payment-service" "analytics-service")

for service in "${services[@]}"; do
    echo "Creating $service structure..."
    mkdir -p $service/src/main/java/com/meditrack/${service/-/}/model
    mkdir -p $service/src/main/java/com/meditrack/${service/-/}/repository
    mkdir -p $service/src/main/java/com/meditrack/${service/-/}/service
    mkdir -p $service/src/main/java/com/meditrack/${service/-/}/controller
    mkdir -p $service/src/main/java/com/meditrack/${service/-/}/dto
    mkdir -p $service/src/main/resources
done

echo "✅ Service directories created!"
echo ""
echo "📝 Next steps:"
echo "1. Run: docker-compose up -d"
echo "2. Build: mvn clean install"
echo "3. Start Service Registry: cd service-registry && mvn spring-boot:run"
echo "4. Start User Service: cd user-service && mvn spring-boot:run"
echo "5. Start other services similarly"
echo ""
echo "🎉 Setup complete!"
