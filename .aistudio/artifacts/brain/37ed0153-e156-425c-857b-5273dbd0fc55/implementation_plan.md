# Localiiiy Web & Android 100% Parity, Decluttered Store Badges & Account Deletion Plan

## App Description
Synchronize the **Localiiiy (`v2.4.0`)** Web App (`https://localiiiy.web.app`) 100% with the Android Jetpack Compose APK/AAB experience across visual layout, Material 3 vector iconography, real-time Firestore collections, clean live/empty states, decluttered official store badges (**Google Play Store** + **Apple App Store: Available Soon** only), and a dedicated **Permanent Account Deletion** workflow (`/delete-account`) for Google Play Data Safety compliance.

---

## Confirmed User Decisions
- **Store Download Links Decluttering:** Remove all Google Drive APK mirror links, `"Direct APK & 10 Mirrors"` buttons/modals, and duplicate top sticky banners. Display **only** clean, official **Google Play Store** (`Download the app from Google Play Store`) and **Apple App Store (`Available Soon`)** badges on the Sign-In card and Space/Profile tab.
- **Permanent Account Deletion Flow:** Create a dedicated standalone `/delete-account` page (`https://localiiiy.web.app/delete-account`) AND place direct clickable **"Delete My Account Permanently"** links on the Sign-In screen, the Space (Profile) tab, and the Privacy Policy page.
- **100% Android Visual, Symbol & Clean-State Parity:** Replace all remaining emoji icons and static demo listings (`$120 Vintage 35mm Film Camera`, `Artisan Sourdough Loaves`, cyclist clip in Clips, `$142.50` Studio demo card, `Alex Rivera` instant login) with Material 3 SVG vector icons, the exact Android top header and bottom floating deck bar, and real-time Firestore listeners with clean Material 3 empty states.

---

## User Experience & Design

### Aesthetic Direction & Color Palette
- **Theme Polarity:** Intentional Dark Mode (`#070D17` Deep Obsidian canvas, `#0F172A` elevated card surface, `#152033` glass container surface) with 1-tap theme switching (`Obsidian Dark`, `Daylight Clean`, `Emerald Night`) matching the Android `Theme.kt` palette.
- **Brand Accents:**
  - **Primary (`#10B981` Emerald Mint):** Active navigation pills, verified proximity indicators, and primary CTAs.
  - **Secondary (`#38BDF8` Hyperlocal Cyan):** Radar sweeps, GPS telemetry badges, and Sovereign ID badges.
  - **Tertiary (`#F59E0B` Escrow Amber):** Safe-Haven Handshake Escrow badges and marketplace actions.
  - **Danger (`#EF4444` Crimson):** SOS dispatch and Permanent Account Deletion confirmation states.

### Typography & Hierarchy
- **Primary UI Font:** `Plus Jakarta Sans` / `Inter` for clean geometric legibility across headers, feed cards, and navigation labels.
- **Monospace Telemetry Font:** `JetBrains Mono` for SHA-256 Localiiiy Content Copyright (`LCC`) hashes, GPS distance badges, and 4-digit Safe-Haven OTP codes.

### Layout, Navigation & Key User Flows

```
+-------------------------------------------------------------------+
|  LOCALIIIY TOP BAR (Matches Android LocaliiiyTopBar.kt 100%)      |
|  [Logo] Localiiiy                   [Search] [Theme] [+] [Bell] [Chat] |
|         * Live GPS Neighborhood                                   |
+-------------------------------------------------------------------+
|                                                                   |
|  ACTIVE DECK VIEWPORT (Zero Duplicate Top Banners or Fake Demos)  |
|                                                                   |
|  1. PULSE   -> 6 Discovery Chips (All, Trending, Nearby,          |
|                Connected, Official, SOS) + Live Firestore Posts   |
|  2. RADAR   -> Interactive SVG Sweep Canvas + Range Pills         |
|                (500m, 2km, 10km, 50km, Global) + Ghost Mode       |
|  3. MARKET  -> Sub-tabs (All/Goods, Services, AI Price Trends,    |
|                Watchlist, Safe-Haven Escrow) + Live Listings      |
|  4. CLIPS   -> Clean Top Header (All, Trending, Nearby, Connected,|
|                Auto-Scroll, Sound) + Bottom-Left Proximity Badge  |
|  5. STUDIO  -> Sub-tabs (All Cinema, Trending, Local Indie,       |
|                Series, AI Studio, Analytics, Copyright LCC)       |
|  6. SPACE   -> Sovereign ID Card, Connections Count, Official     |
|                Google Play & Apple Store (Soon) Badges,           |
|                Settings, & "Delete My Account Permanently"        |
|                                                                   |
+-------------------------------------------------------------------+
|  FLOATING DECK SWITCHER BAR (Matches DeckSwitcherBar.kt 100%)     |
|  ( [Pulse]   [Radar]   [Market]   [Clips]   [Studio] )   (Space)  |
+-------------------------------------------------------------------+
```

1. **Sign-In & 11-Section Registration Gate:**
   - Displays the Localiiiy logo, a single clean store download box (**Download the app from Google Play Store** button + **Apple App Store: Available Soon** badge), Sign-In / 11-Section Register toggle, Google OAuth Sign-In, Ghost Spectator Mode, and a direct **"Delete My Account Permanently"** link at the bottom.
   - Removes the `"Instant Verified Neighbor Login (Alex Rivera)"` demo shortcut and duplicate Apple Store badges.
2. **Exact Android Top Bar & Bottom Floating Deck Switcher:**
   - **Top Bar:** Left column displays the Localiiiy Pin/Orbit logo, `"Localiiiy"` title, and live GPS neighborhood subtitle. Right row contains the 5 Material 3 circular SVG icon buttons (`Search`, `Theme Palette`, `+ Create`, `Notifications`, `Messages`) with no cluttered top sticky green banners or currency/profile pills.
   - **Bottom Bar:** Frosted glass capsule with the 5 Material 3 SVG deck icons (`Pulse`, `Radar`, `Market`, `Clips`, `Studio`) plus the circular `Space` (`Profile`) node on the right.
3. **100% Clean Live Data Across All 6 Decks:**
   - **Pulse:** Live Firestore `posts` feed with `Neighbor` / `City` / `Earth` reach badges, composer modal, and clean Material 3 empty state when no posts match a filter.
   - **Radar:** Interactive radar canvas with Material 3 vector beacon blips, distance filter pills (`500m`, `2km`, `10km`, `50km`, `Global`), Ghost Mode toggle, and live neighbor beacons.
   - **Market:** Removes the static `$120 Vintage 35mm Film Camera`, `$12 Artisan Sourdough Loaves`, `$38 Ceramic Mug Set`, and fake Seattle service cards. Displays live Firestore `marketplace_items` and `services` with Safe-Haven Handshake Escrow (`4-digit OTP`), category filters, and `+ Sell Item` / `+ Offer Service` modals.
   - **Clips:** Removes the static cyclist stock image (`Pike Place Market / @maya_bakes`). Keeps the top header strictly dedicated to `Clips` title, `Auto-Scroll` / `Sound` toggles, and `All`, `Trending`, `Nearby`, `Connected` filter pills, with proximity badges anchored in the bottom-left info column and a `+ Upload Clip` modal.
   - **Studio:** Replaces static `$142.50` / `1,840h` demo cards with the exact Android `Studio` sub-tabs (`All Cinema`, `Trending`, `Local Indie`, `Series`, `AI Studio`, `Analytics`, `Copyright (LCC)`), live creator metrics starting at `0`, AI Viral Hook generator, and SHA-256 LCC timestamp generator.
   - **Space (Profile):** Displays user handle, Sovereign ID, `Connections` counter (strictly using `Connected` / `Connections` terminology), official **Google Play Store** & **Apple App Store (Available Soon)** card, and a direct **"Delete My Account Permanently"** action.
4. **Dedicated Permanent Account Deletion Portal (`/delete-account`):**
   - Accessible directly at `https://localiiiy.web.app/delete-account` (and linked from Sign-In, Space tab, and Privacy Policy).
   - Allows authenticated users (or users signing in with Email/Password or Google) to permanently and irreversibly delete their Firebase Authentication record, Firestore profile (`users/{uid}`), username reservation (`usernames/{handle}`), active radar beacon (`radar_beacons/{uid}`), and local browser data, or submit a verified deletion request.

---

## Verification & Polish
- **Android Build Verification:** Run `compile_applet` to ensure all Android Kotlin sources and bundled web assets compile with zero errors.
- **Terminology & Policy Audit:** Verify zero occurrences of forbidden social graph terms (using exclusively `Connect`, `Connected`, `Connections`), zero hardcoded secret keys in tracked files, and valid `/delete-account`, `/privacy`, and `/terms` routes.
