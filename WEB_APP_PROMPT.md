# Localiiiy Web App Parity Prompt

## Objective
Rebuild the Localiiiy web application (`index.html`, `sw.js`, and associated assets) to achieve 100% functional and visual parity with the Android application. The web app must mirror the "Hyperlocal Pulse" experience, adhering to the strict terminology and design language defined in the Android codebase.

## 1. Visual Identity & Branding
- **Logo**: Implement the "Teal Eye Pin" logo exactly. A white location pin with a teal 'eye' center and stylized arcs on a blue-teal gradient. Use SVG for crispness and scalability.
- **Color Palette**:
    - Primary Teal: `#0D9488`
    - Accent Mint: `#CCFBF1`
    - Deep Navy: `#0F172A`
    - Background: Warm Neutrals / Off-White (`#F8FAFC`).
- **Typography**: Use Material 3 type scales. Pair a clean Sans-Serif (e.g., Plus Jakarta Sans) for body text with a Display font for headings.

## 2. Core Modules (The Five Lenses)
The web app must implement five main navigation screens/lenses:

### A. Pulse Feed (Home)
- **Filters**: "All", "Trending", "Nearby", "Connected".
- **Terminology**: Strictly use "Connect" and "Connected" (NEVER "Follow").
- **Dial Filter**: Implement a "Distance Scale" dial (Neighbor/City/Earth) as seen in `PulseFeedComponent.kt`.
- **Content Cards**:
    - **PostCard**: User info, distance badge, caption, media, interaction bar (Like, Comment, Share, Save).
    - **Flash Pulse**: Ephemeral posts with a decaying timer UI.
    - **Pulse Polls**: Interactive community polls.
- **Stories**: A horizontal tray at the top for ephemeral moments.

### B. Clips (Vertical Video)
- **Layout**: Full-screen vertical video pager.
- **Interactions**: Double-tap to heart, vertical swipe to next clip.
- **Overlays**: Creator details, caption, and "Connected" status in the bottom-left. Location badges anchored above creator details.

### C. Radar (Discovery)
- **Map/Radar View**: A visual representation of nearby users and active "Pulses".
- **Ghost Mode**: A toggle to browse anonymously (Ghost Mode watermark in the UI).
- **Proximity Alerts**: Visual cues for items or people within a 1km radius.

### D. Marketplace (Buy & Sell)
- **Categories**: All, Cars, Electronics, Furniture, Services, etc.
- **Tabs**: Goods, Services, Posts, Clips, Watchlist.
- **Distance Filtering**: "Walkable Deals" (< 2km) filter.
- **Item Listings**: Large images, price, condition, seller reputation, and distance.

### E. Studio (Long-Form Content)
- **Catalog**: 16:9 video cards for videos > 60s.
- **Player**: Theater mode with comments, description, and related videos.
- **Creator Dashboard**: View stats (views, connections, earnings) and "Bounty Board".

## 3. Data & State Management
- **Schema Alignment**: Ensure the JavaScript objects used in `index.html` match the `PostEntity`, `ClipEntity`, and `MarketplaceItemEntity` schemas in `LocaliiiyModels.kt`.
- **Local Persistence**: Use `IndexedDB` or `localStorage` to simulate the Room database for offline-first capabilities.
- **Privacy**: Respect "Ghost Mode" and precise location sharing flags.

## 4. Design Guidelines
- **Edge-to-Edge**: The UI should feel immersive on mobile browsers.
- **Touch Targets**: Minimum 48dp for all interactive elements.
- **Material 3**: Use M3 components (Cards, Chips, FABs, NavigationBar).
- **Zero-Algorithm Toggle**: Provide a toggle for a purely chronological feed.

## 5. Deployment
- The web app is served via GitHub Pages/Firebase. Use relative paths (`./`) for all assets.
- Update the Web App Manifest (`manifest.json`) and Service Worker (`sw.js`) to ensure it is a high-quality PWA.
