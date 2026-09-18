const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const APK_PATH = path.resolve(__dirname, 'Localiiiy-debug.apk');

const server = http.createServer((req, res) => {
    const url = req.url.split('?')[0];

    // Serve APK directly
    if (url === '/Localiiiy-debug.apk' || url === '/app-debug.apk' || url.endsWith('.apk') || url === '/download') {
        if (!fs.existsSync(APK_PATH)) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('APK file not found.');
            return;
        }

        const stat = fs.statSync(APK_PATH);
        const fileSize = stat.size;
        const range = req.headers.range;

        if (range) {
            const parts = range.replace(/bytes=/, "").split("-");
            const start = parseInt(parts[0], 10);
            const end = parts[1] ? parseInt(parts[1], 10) : fileSize - 1;
            const chunksize = (end - start) + 1;
            const file = fs.createReadStream(APK_PATH, { start, end });
            res.writeHead(206, {
                'Content-Range': `bytes ${start}-${end}/${fileSize}`,
                'Accept-Ranges': 'bytes',
                'Content-Length': chunksize,
                'Content-Type': 'application/vnd.android.package-archive',
                'Content-Disposition': 'attachment; filename="Localiiiy-debug.apk"'
            });
            file.pipe(res);
        } else {
            res.writeHead(200, {
                'Content-Length': fileSize,
                'Content-Type': 'application/vnd.android.package-archive',
                'Content-Disposition': 'attachment; filename="Localiiiy-debug.apk"',
                'Accept-Ranges': 'bytes',
                'Cache-Control': 'no-cache'
            });
            fs.createReadStream(APK_PATH).pipe(res);
        }
        return;
    }

    // Serve landing page with download button
    const html = `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Download Localiiiy APK</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
        body {
            background: linear-gradient(135deg, #051408 0%, #020b04 100%);
            color: #ffffff;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 24px;
            text-align: center;
        }
        .card {
            background: rgba(18, 38, 22, 0.85);
            border: 1px solid #00FF41;
            box-shadow: 0 0 30px rgba(0, 255, 65, 0.2);
            border-radius: 24px;
            padding: 36px 24px;
            max-width: 440px;
            width: 100%;
        }
        .icon {
            width: 80px;
            height: 80px;
            border-radius: 20px;
            background: #00FF41;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 42px;
            margin: 0 auto 20px auto;
            box-shadow: 0 0 20px rgba(0, 255, 65, 0.4);
        }
        h1 { font-size: 26px; font-weight: 800; margin-bottom: 8px; color: #ffffff; }
        p.subtitle { color: #A0C5A8; font-size: 15px; margin-bottom: 24px; }
        .meta {
            background: rgba(0, 255, 65, 0.08);
            border: 1px dashed rgba(0, 255, 65, 0.4);
            border-radius: 12px;
            padding: 12px 16px;
            margin-bottom: 24px;
            font-size: 13px;
            color: #C8E6C9;
            display: flex;
            justify-content: space-around;
        }
        .btn-download {
            display: block;
            width: 100%;
            background: #00FF41;
            color: #020E04;
            font-size: 18px;
            font-weight: 800;
            padding: 18px 24px;
            border-radius: 16px;
            text-decoration: none;
            transition: transform 0.15s ease, background 0.15s ease;
            box-shadow: 0 4px 20px rgba(0, 255, 65, 0.4);
        }
        .btn-download:active { transform: scale(0.97); background: #00DD38; }
        .instructions {
            margin-top: 32px;
            text-align: left;
            background: rgba(255, 255, 255, 0.04);
            border-radius: 16px;
            padding: 18px;
            font-size: 13px;
            color: #D1D5DB;
        }
        .instructions h3 { font-size: 14px; margin-bottom: 12px; color: #00FF41; }
        .instructions ol { padding-left: 20px; }
        .instructions li { margin-bottom: 8px; line-height: 1.4; }
    </style>
</head>
<body>
    <div class="card">
        <div class="icon">📡</div>
        <h1>Localiiiy</h1>
        <p class="subtitle">Hyperlocal Live Radar & Discovery</p>
        
        <div class="meta">
            <div>📦 <strong>38.4 MB</strong></div>
            <div>🤖 <strong>Android APK</strong></div>
            <div>⚡ <strong>v1.0.0</strong></div>
        </div>

        <a href="/Localiiiy-debug.apk" class="btn-download" download="Localiiiy-debug.apk">
            ⬇️ Download APK (38.4 MB)
        </a>

        <div class="instructions">
            <h3>📱 How to Install on Your Phone</h3>
            <ol>
                <li>Tap the green <strong>Download APK</strong> button above.</li>
                <li>When the download finishes, tap the notification or open <strong>Downloads</strong> in your file manager.</li>
                <li>Tap <strong>Localiiiy-debug.apk</strong> and choose <strong>Install</strong>.</li>
                <li>If prompted, allow <em>"Install unknown apps"</em> for your browser.</li>
            </ol>
        </div>
    </div>
</body>
</html>`;

    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(html);
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`APK download server running at http://0.0.0.0:${PORT}`);
});
