#!/bin/bash
APK="Localiiiy.apk"

echo "Testing Tmpfiles Direct DL..."
curl -sI "https://tmpfiles.org/dl/wFwdEmzzDb59/localiiiy.apk" | grep -iE 'content-length|HTTP'

echo "Testing Gofile page..."
curl -sI "https://gofile.io/d/GoUM8QE8" | grep -iE 'HTTP'

echo "Uploading to Buzzheavier..."
curl -s -T "$APK" https://buzzheavier.com/Localiiiy.apk || true
echo ""

echo "Uploading new Tmpfiles with direct DL..."
TMP_RESP=$(curl -s -F "file=@$APK" https://tmpfiles.org/api/v1/upload)
echo "$TMP_RESP"

