# Project Guidelines & User Instructions

## Terminology & Social Graph
- **Never use "follow", "following", or "followers" in the app.**
- Always use **"Connected"**, **"Connect"**, or **"Connections"** for all social interactions, buttons, feed filters, profile counters, and user-facing copy.
- When filtering posts, clips, or notifications by connected users, the filter label and tab must strictly be named **"Connected"**.

## Clips Layout & Hyperlocal Overlays
- In `ClipsScreen.kt`, keep the top header clean and dedicated solely to the screen title, auto-scroll/sound toggles, and discovery filters (`All`, `Trending`, `Nearby`, `Connected`).
- Avoid placing floating badges or location overlays in the top area where they could overlap or obscure the filter pills or status bar.
- Any proximity, locality, or landmark badges must be anchored in the bottom-left info column directly above the creator details and caption.
