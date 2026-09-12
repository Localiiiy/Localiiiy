# Project Guidelines & User Instructions

## Terminology & Social Graph
- **Never use "follow", "following", or "followers" in the app.**
- Always use **"Connected"**, **"Connect"**, or **"Connections"** for all social interactions, buttons, feed filters, profile counters, and user-facing copy.
- When filtering posts, clips, or notifications by connected users, the filter label and tab must strictly be named **"Connected"**.

## Clips Layout & Hyperlocal Overlays
- In `ClipsScreen.kt`, keep the top header clean and dedicated solely to the screen title, auto-scroll/sound toggles, and discovery filters (`All`, `Trending`, `Nearby`, `Connected`).
- Avoid placing floating badges or location overlays in the top area where they could overlap or obscure the filter pills or status bar.
- Any proximity, locality, or landmark badges must be anchored in the bottom-left info column directly above the creator details and caption.

## Creator Distribution & Global-Local Dual Reach
- **Creator Control**: Give creators control over content distribution. Posts and clips can be targeted to nearby/local users by default or preference (Neighbor / City), while retaining algorithmic distribution to reach a global/worldwide audience (Earth).
- **Ghost / Anonymous Browsing Mode**: Allow users to browse and observe without broadcasting or exposing their location/identity to other users on the radar or in active feeds.
- **Database & Architecture**: Ensure models and queries support distance-based filtering, audience distribution reach settings, and privacy/ghost flags.

