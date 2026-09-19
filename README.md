# 🌍 Localiiiy — Hyperlocal Social Radar & Community Commerce

[![Build Android APK and Create GitHub Release](https://github.com/Localiiiy/Localiiiy/actions/workflows/build-apk.yml/badge.svg)](https://github.com/Localiiiy/Localiiiy/actions/workflows/build-apk.yml)
[![Live Web Application](https://img.shields.io/badge/Web_App-Live_Preview-00B4D8?style=flat&logo=googlechrome)](https://localiiiy.vercel.app)

Localiiiy connects verified neighbors, empowers hyperlocal creators with 100% fair ad-share monetization, and enables peer-to-peer commerce protected by Safe-Haven Handshake Escrow.

---

## 📱 Download Localiiiy for Android

### Method 1: GitHub Releases (Direct One-Tap APK Download)
1. Go to the [**Releases**](https://github.com/Localiiiy/Localiiiy/releases) tab on the right sidebar.
2. Under **Assets**, tap **`app-debug.apk`** to download.
3. Open the downloaded file on your Android phone to install and run immediately.

### Method 2: GitHub Actions Artifacts
1. Go to the [**Actions**](https://github.com/Localiiiy/Localiiiy/actions) tab.
2. Click the latest workflow run: **Build Android APK and Create GitHub Release**.
3. Scroll to the bottom under **Artifacts** and download **`Localiiiy-Android-APK`**.

---

## 🌐 Live Web Experience
- **Live Hosted Web App**: [https://localiiiy.vercel.app](https://localiiiy.vercel.app)
- **GitHub Pages**: Enabled via Settings → Pages.

---

## 🛡️ Security & Privacy Architecture
- **Zero-Knowledge Identity Vault**: Pseudonymous identity generation and one-tap identity scrubbing.
- **Server-Authoritative Wallets**: Client code cannot alter wallet balances (`firestore.rules` enforces zero-trust).
- **Anti-Fraud Payouts**: Strict minimum threshold enforcement ($1,000 USD) and atomic idempotency keys.
- **Safe-Haven Handshake Escrow**: Tamper-proof physical QR-verification for marketplace transactions.
