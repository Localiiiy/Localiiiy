#!/bin/bash
echo "=========================================="
echo "Uploading AAB Bundle (Localiiiy.aab)..."
echo "=========================================="

echo "--- Pixeldrain AAB ---"
curl -s -F "file=@Localiiiy.aab" https://pixeldrain.com/api/file
echo ""

echo "--- Transfer.sh AAB ---"
curl -s --upload-file ./Localiiiy.aab https://transfer.sh/Localiiiy.aab
echo ""

echo "--- Litterbox AAB ---"
curl -s -F "reqtype=fileupload" -F "time=72h" -F "fileToUpload=@Localiiiy.aab" https://litterbox.catbox.moe/resources/internals/api.php
echo ""

echo "--- Bashupload AAB ---"
curl -s https://bashupload.com/Localiiiy.aab --data-binary "@Localiiiy.aab"
echo ""

echo "--- Oshi.at AAB ---"
curl -s -F "f=@Localiiiy.aab" https://oshi.at
echo ""

echo "=========================================="
echo "Uploading APK File (Localiiiy.apk)..."
echo "=========================================="

# Check if APK is present in workspace root, otherwise copy it
if [ ! -f "Localiiiy.apk" ]; then
    cp ./app/build/outputs/apk/debug/app-debug.apk Localiiiy.apk
fi

echo "--- Pixeldrain APK ---"
curl -s -F "file=@Localiiiy.apk" https://pixeldrain.com/api/file
echo ""

echo "--- Transfer.sh APK ---"
curl -s --upload-file ./Localiiiy.apk https://transfer.sh/Localiiiy.apk
echo ""

echo "--- Litterbox APK ---"
curl -s -F "reqtype=fileupload" -F "time=72h" -F "fileToUpload=@Localiiiy.apk" https://litterbox.catbox.moe/resources/internals/api.php
echo ""

echo "--- Bashupload APK ---"
curl -s https://bashupload.com/Localiiiy.apk --data-binary "@Localiiiy.apk"
echo ""

echo "--- Oshi.at APK ---"
curl -s -F "f=@Localiiiy.apk" https://oshi.at
echo ""
