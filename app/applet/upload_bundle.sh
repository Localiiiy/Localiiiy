#!/bin/bash
BUNDLE="Localiiiy.aab"
cp ./app/build/outputs/bundle/debug/app-debug.aab "$BUNDLE"
echo "=== App Bundle (AAB) Size ==="
ls -lh "$BUNDLE"

echo "=== 1. Litterbox / Catbox ==="
curl -s -F "reqtype=fileupload" -F "time=72h" -F "fileToUpload=@$BUNDLE" https://litterbox.catbox.moe/resources/internals/api.php
echo ""

echo "=== 2. Catbox ==="
curl -s -F "reqtype=fileupload" -F "fileToUpload=@$BUNDLE" https://catbox.moe/user/api.php
echo ""

echo "=== 3. 0x0.st ==="
curl -s -F "file=@$BUNDLE" https://0x0.st
echo ""

echo "=== 4. Pixeldrain ==="
curl -s -T "$BUNDLE" https://pixeldrain.com/api/file/Localiiiy.aab
echo ""

echo "=== 5. Oshi.at ==="
curl -s -F "f=@$BUNDLE" https://oshi.at
echo ""

echo "=== 6. Uguu.se ==="
curl -s -F "files[]=@$BUNDLE" https://uguu.se/upload.php
echo ""

echo "=== 7. Bashupload ==="
curl -s https://bashupload.com/Localiiiy.aab --data-binary "@$BUNDLE"
echo ""

echo "=== 8. Gofile ==="
SERVER=$(curl -s https://api.gofile.io/servers | grep -o '"name":"[^"]*"' | head -n1 | cut -d'"' -f4)
if [ -n "$SERVER" ]; then
    echo "Gofile Server: $SERVER"
    curl -s -F "file=@$BUNDLE" "https://${SERVER}.gofile.io/contents/uploadfile"
    echo ""
fi

echo "=== 9. File.io ==="
curl -s -F "file=@$BUNDLE" https://file.io
echo ""

echo "=== 10. Transfer.sh ==="
curl -s --upload-file "$BUNDLE" https://transfer.sh/Localiiiy.aab
echo ""
