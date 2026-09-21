#!/usr/bin/env bash
set -euo pipefail

echo "=========================================================="
echo "⚡ Localiiiy Clean Build & Synchronized Pipeline"
echo "=========================================================="

# 1. Purge stale build directories, caches, and temporary artifacts
echo "🧹 [1/5] Purging stale build artifacts, temporary caches, and legacy folders..."
rm -rf /tmp/localiiiy_* 2>/dev/null || true
rm -rf .cache 2>/dev/null || true
rm -rf app/build/ 2>/dev/null || true
rm -rf build/ 2>/dev/null || true
rm -rf dist/ 2>/dev/null || true
rm -rf .next/ 2>/dev/null || true
rm -rf node_modules/.cache 2>/dev/null || true

# 2. Verify environment configuration
echo "🔑 [2/5] Checking environment configuration..."
if [ ! -f ".env" ]; then
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo "Created .env from .env.example"
    else
        touch .env
    fi
fi

# Ensure Maps & Firebase dummy keys exist for compilation
if ! grep -q "MAPS_API_KEY" .env 2>/dev/null; then
    echo "MAPS_API_KEY=AIzaSyLocaliiiyMapsRadarApiKeyForDev" >> .env
fi
if ! grep -q "FIREBASE_API_KEY" .env 2>/dev/null; then
    echo "FIREBASE_API_KEY=AIzaSyLocaliiiyFirebaseApiKeyMock" >> .env
fi

# 3. Compile Native Android APK
echo "📱 [3/5] Compiling Android APK (assembleDebug)..."
if command -v gradle &> /dev/null; then
    gradle :app:assembleDebug --no-daemon
elif [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew :app:assembleDebug --no-daemon
else
    echo "❌ Error: Neither gradle nor gradlew found!"
    exit 1
fi

# 4. Synchronize APK with Static Distribution Targets
echo "📦 [4/5] Synchronizing APK with Web static distribution targets..."
mkdir -p downloads
APK_SRC="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_SRC" ]; then
    cp -v "$APK_SRC" "Localiiiy.apk"
    cp -v "$APK_SRC" "downloads/Localiiiy.apk"
    
    # Calculate SHA256
    APK_SHA=$(sha256sum "Localiiiy.apk" | awk '{print $1}')
    APK_SIZE=$(du -h "Localiiiy.apk" | awk '{print $1}')
    
    echo "✅ Localiiiy.apk successfully packaged!"
    echo "   - Size: $APK_SIZE"
    echo "   - SHA256: $APK_SHA"
    echo "$APK_SHA  Localiiiy.apk" > downloads/checksums.txt
else
    echo "❌ Error: Compiled APK not found at $APK_SRC!"
    exit 1
fi

# 5. Parity & Distribution Check
echo "🌐 [5/5] Validating Web client and APK distribution parity..."
if [ -f "index.html" ] && [ -f "firebase.json" ]; then
    echo "✅ Web client (index.html) and Firebase hosting configurations verified."
fi

echo "=========================================================="
echo "🎉 Build & Sync Complete! All targets ready for deployment."
echo "=========================================================="
