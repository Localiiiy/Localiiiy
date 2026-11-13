const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 8080;
const PUBLIC_DIR = path.join(__dirname, 'public');

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.aab': 'application/octet-stream',
  '.apk': 'application/vnd.android.package-archive'
};

const server = http.createServer((req, res) => {
  const cleanUrl = (req.url || '/').split('?')[0];

  let targetFile = 'index.html';
  if (cleanUrl === '/delete-account' || cleanUrl.startsWith('/delete-account/')) {
    targetFile = 'delete-account.html';
  } else if (cleanUrl === '/privacy' || cleanUrl === '/privacy.html') {
    targetFile = 'privacy.html';
  } else if (cleanUrl === '/terms' || cleanUrl === '/terms.html') {
    targetFile = 'terms.html';
  } else if (cleanUrl === '/download-aab') {
    const aabFiles = fs.readdirSync(PUBLIC_DIR)
      .filter((f) => f.endsWith('.aab'))
      .sort()
      .reverse();
    targetFile = aabFiles[0] || 'Localiiiy-v3.5.0-build35-PLAYSTORE.aab';
  } else if (cleanUrl !== '/') {
    targetFile = cleanUrl.replace(/^\/+/, '');
  }

  let filePath = path.join(PUBLIC_DIR, targetFile);
  if (!fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
    const rootFallback = path.join(__dirname, targetFile);
    if (fs.existsSync(rootFallback) && !fs.statSync(rootFallback).isDirectory()) {
      filePath = rootFallback;
    } else {
      filePath = path.join(PUBLIC_DIR, 'index.html');
    }
  }

  const ext = path.extname(filePath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'text/html; charset=utf-8';
  const fileName = path.basename(filePath);

  fs.stat(filePath, (statErr, stats) => {
    if (statErr || !stats.isFile()) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('Not Found');
      return;
    }
    const headers = {
      'Content-Type': contentType,
      'Content-Length': stats.size,
      'Cache-Control': 'no-cache, no-store, must-revalidate'
    };
    if (ext === '.aab' || ext === '.apk') {
      headers['Content-Disposition'] = `attachment; filename="${fileName}"`;
      headers['X-Content-Type-Options'] = 'nosniff';
    }
    res.writeHead(200, headers);
    fs.createReadStream(filePath).pipe(res);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Localiiiy Web App server listening on port ${PORT}`);
});
