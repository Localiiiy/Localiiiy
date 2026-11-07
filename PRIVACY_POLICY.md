# 🛡️ Localiiiy — Global Privacy Policy, Data Safety & Community Guidelines (`v2.4.0`)

**Effective Date:** November 2026 • **Version:** `2.4.0 Production`  
**Google Play Store:** [https://play.google.com/store/apps/details?id=com.aistudio.localiiiy.live](https://play.google.com/store/apps/details?id=com.aistudio.localiiiy.live)  
**Apple App Store:** Available Soon  
**Official Web App:** [https://localiiiy.web.app](https://localiiiy.web.app)  
**Web Privacy Portal:** [https://localiiiy.web.app/privacy](https://localiiiy.web.app/privacy)  
**Delete Account Permanently:** [https://localiiiy.web.app/delete-account](https://localiiiy.web.app/delete-account)  
**GitHub Canonical Policy:** [https://github.com/Localiiiy/Localiiiy/blob/main/PRIVACY_POLICY.md](https://github.com/Localiiiy/Localiiiy/blob/main/PRIVACY_POLICY.md)

---

## Section 1.0 — Overview & Core Privacy Principles

**Localiiiy** ("we", "us", "our", or "the Platform") is a privacy-first hyperlocal social discovery, 4K creator studio, short-form clips, and neighborhood marketplace platform accessible via our Android Application (`com.aistudio.localiiiy.live`) and synchronized Progressive Web App (`https://localiiiy.web.app`).

- **Zero Data Brokerage:** We never sell, rent, or trade your personal data or location coordinates to data brokers or third-party behavioral advertising networks.
- **Connections-Only Social Graph:** All social relationships on Localiiiy operate strictly as mutual or verified **Connections** (`Connect` / `Connected` / `Connections`).
- **Non-Trader / Consumer Developer Status (EU DSA Article 30):** Localiiiy is operated by an independent developer for community social connection and peer-to-peer neighborhood discovery.

---

## Section 2.0 — Google Play Store Data Safety Disclosures

### A. Personal Information & Authentication
- **Display Name, `@username` Handle, Email & Phone Verification:** Processed securely via **Firebase Authentication** and **Cloud Firestore** strictly for account creation, 6-digit OTP verification, duplicate-handle prevention (`usernames` lock collection), and account recovery.
- **13+ Age Gate & Teen Protection (COPPA / DPDP / GDPR-K):** Users must be at least **13 years old**. Users aged 13–17 are automatically placed into **Ghost Mode (Stealth Cloak)** by default.

### B. Location Telemetry & ~110m Spatial Privacy Fuzzing (`roundCoord3`)
- **Approximate & Foreground Location Only:** Used exclusively when you actively use the **Live Radar**, **Nearby Pulse Feed**, or **Local Market** radius filters (`1 km – 50 km`).
- **Zero Background Tracking:** Location coordinates are **never** polled, tracked, or broadcast when the application is closed or in the background.
- **~110m Spatial Coordinate Fuzzing (`roundCoord3`):** Before any coordinate is transmitted to Cloud Firestore (`radar_beacons`), latitude and longitude are mathematically rounded to 3 decimal places (~110-meter neighborhood grid) so your exact home, room, or building address is never exposed.
- **Ghost Mode (Active by Default):** Switching on **Ghost Mode** immediately deletes your active beacon document from `radar_beacons`, allowing you to browse with zero GPS footprint.

### C. Photos, Videos & Media (`PickVisualMedia`)
- **Zero-Permission Android Photo Picker:** Localiiiy uses Android's native system Photo Picker (`ActivityResultContracts.PickVisualMedia`). The app never requests broad storage permissions (`READ_EXTERNAL_STORAGE` or `READ_MEDIA_IMAGES`).
- **Encrypted Cloud Transit:** Media uploaded for Pulse posts, Clips, Studio videos, or Market listings is transmitted over TLS 1.3 and protected with **Localiiiy Creator Copyright (LCC)** SHA-256 cryptographic fingerprints.

### D. What Localiiiy NEVER Collects
- We **NEVER** scrape or upload your device contact address book.
- We **NEVER** record background microphone audio.
- We **NEVER** read private SMS messages or device files outside the system Photo Picker.
- We **NEVER** store raw credit card numbers or bank PINs.

---

## Section 3.0 — Real User Account Deletion & Right to Permanent Erasure (GDPR Art. 17 / DPDP)

Every user has an unconditional **Right to Erasure**:
1. **Dedicated Web Permanent Deletion Portal:** Delete your account and all cloud records online at **[https://localiiiy.web.app/delete-account](https://localiiiy.web.app/delete-account)**.
2. **1-Tap In-App & Web Erasure:** Navigate to **Space (Profile) → Delete My Account Permanently** in either the Android app or Web app (`https://localiiiy.web.app`).
3. **Instant Irrevocable Purge:** Triggering account deletion immediately erases:
   - Your Firebase Authentication account record (`auth.currentUser.delete()`).
   - Your Cloud Firestore profile (`users/{uid}`), username reservation (`usernames/{handle}`), and active radar beacon (`radar_beacons/{uid}`).
   - All local SQLite Room database tables and browser `localStorage` caches.
4. **Direct Support Erasure Request:** You may also request deletion anytime via **Localiiiy.Helpdesk@gmail.com**.

---

## Section 4.0 — Dual-Reach Creator Distribution & Sovereignty

- **Hyperlocal Anchoring (`Neighbor` 1–5 km):** Content first surfaces to nearby neighbors, enabling authentic local discovery without requiring large connection counts.
- **Algorithmic & Chronological Modes (`Neighbor → City → Earth`):** Creators control whether broadcasts remain hyperlocal or scale globally to the **Earth** feed, while viewers can toggle **⚡ Pure Chronological (Zero-Algorithm)** sorting at any time.

---

## Section 5.0 — Community Safety, Anti-Stalking Shield & Zero-Tolerance UGC Policy

- **Prohibited Content:** Violence, hate speech, sexual exploitation, harassment, cyberstalking, illegal goods, and deceptive marketplace listings are strictly prohibited.
- **Cyberstalking Safety Shield & Panic Cloak:** Includes rapid-approach velocity alerts, 1-tap **Ghost Mode + Radar Beacon Purge**, Police Precinct Safe-Haven Green Zone routing, and a **Decoy Calculator (`Panic Cloak`)**.
- **24-Hour Moderation SLA:** Every post, clip, market item, and user profile includes 1-tap reporting and instant 2-way blocking.

---

## Section 6.0 — Official Links & Compliance Contact

- **Official Web App:** [https://localiiiy.web.app](https://localiiiy.web.app)
- **Google Play Store:** [https://play.google.com/store/apps/details?id=com.aistudio.localiiiy.live](https://play.google.com/store/apps/details?id=com.aistudio.localiiiy.live)
- **Terms of Service (GitHub):** [TERMS_OF_SERVICE.md](./TERMS_OF_SERVICE.md)
- **Terms of Service (Web):** [https://localiiiy.web.app/terms](https://localiiiy.web.app/terms)
- **Privacy & Safety Contact:** `support@localiiiy.web.app`
