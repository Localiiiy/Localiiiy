#!/bin/bash
APK="Localiiiy.apk"
cp ./app/build/outputs/apk/debug/app-debug.apk "$APK"
echo "=== APK Size ==="
ls -lh "$APK"

echo "=== 1. Litterbox / Catbox ==="
curl -s -F "reqtype=fileupload" -F "time=72h" -F "fileToUpload=@$APK" https://litterbox.catbox.moe/resources/internals/api.php
echo ""

echo "=== 2. Catbox ==="
curl -s -F "reqtype=fileupload" -F "fileToUpload=@$APK" https://catbox.moe/user/api.php
echo ""

echo "=== 3. 0x0.st ==="
curl -s -F "file=@$APK" https://0x0.st
echo ""

echo "=== 4. Pixeldrain ==="
curl -s -T "$APK" https://pixeldrain.com/api/file/Localiiiy.apk
echo ""

echo "=== 5. Oshi.at ==="
curl -s -F "f=@$APK" https://oshi.at
echo ""

echo "=== 6. Uguu.se ==="
curl -s -F "files[]=@$APK" https://uguu.se/upload.php
echo ""

echo "=== 7. Bashupload ==="
curl -s https://bashupload.com/Localiiiy.apk --data-binary "@$APK"
echo ""

echo "=== 8. Gofile ==="
SERVER=$(curl -s https://api.gofile.io/servers | grep -o '"name":"[^"]*"' | head -n1 | cut -d'"' -f4)
if [ -n "$SERVER" ]; then
    echo "Gofile Server: $SERVER"
    curl -s -F "file=@$APK" "https://${SERVER}.gofile.io/contents/uploadfile"
    echo ""
fi

echo "=== 9. File.io ==="
curl -s -F "file=@$APK" https://file.io
echo ""

echo "=== 10. Transfer.sh ==="
curl -s --upload-file "$APK" https://transfer.sh/Localiiiy.apk
echo ""

