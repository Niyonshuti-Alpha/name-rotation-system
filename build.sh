#!/bin/bash
echo "🚀 Custom build script for Render..."

# Make mvnw executable and build
chmod +x mvnw
./mvnw clean package -DskipTests -Dmaven.test.skip=true

# If above fails, try system maven
if [ $? -ne 0 ]; then
    echo "Maven wrapper failed, trying system maven..."
    mvn clean package -DskipTests -Dmaven.test.skip=true
fi

echo "✅ Build attempt complete!"
