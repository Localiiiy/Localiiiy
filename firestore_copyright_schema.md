# Localiiiy Firestore Schema & Security Architecture
## Content Copyright, Licensing & Strike Resolution Engine

### 1. Document Collections Overview

```
databases/{database}/documents
├── content_fingerprints/{fingerprintId}     # Registered cryptographic + acoustic + perceptual pHash signatures
├── copyright_claims/{claimId}              # YouTube-style 1-tap claim shift to unlisted quarantine
├── counter_justifications/{justificationId} # 7-day uploader evidence submission
├── copyright_strikes/{strikeId}             # 90-day active penalty ledger (3 strikes = permanent suspension)
└── revenue_reroutes/{rerouteId}            # Immutable payout redirection to original creators
```

---

### 2. Firestore Cloud Functions & Server-Authoritative Triggers

1. **`onClaimCreated` Trigger**:
   - Immediately updates `hyperlocal_updates/{pulseId}` or `clips/{clipId}` with `isQuarantined: true`, `quarantineReason: "COPYRIGHT_CLAIM"`.
   - Sends Firebase Cloud Messaging (FCM) high-priority push to the uploader:
     `"Copyright claim filed on your content by @original_owner. You have 7 days to submit justification."`
   - Schedules a Cloud Tasks delayed job at `counterNotificationDeadline` (7 days). If no justification submitted, auto-resolves to strike.

2. **`onRevenueAccrued` Trigger**:
   - When ads, views, or marketplace commission events are calculated for a piece of content:
   - Query `copyright_claims` where `targetContentId == contentId` and `status in ['PENDING_JUSTIFICATION', 'JUSTIFICATION_SUBMITTED', 'STRIKE_ISSUED']`.
   - If match found:
     - Sets uploader payout = `$0.00`.
     - Directs 100% of accrued earnings to `wallets/{claimantUid}` via an entry in `revenue_reroutes`.

3. **`onStrikeCreated` Trigger**:
   - Counts all documents in `copyright_strikes` where `offenderUid == uid` and `expiresAt > request.time`.
   - If count >= 3:
     - Updates `users/{userId}` with `uploadPrivilegesSuspended: true`, `marketListingSuspended: true`.
     - Revokes active FCM upload session tokens.

---

### 3. Comprehensive Firestore Security Rules Integration

See `/firestore.rules` for client-side enforcement:
- Non-claimants cannot alter claim states.
- Uploader can only write within the 7-day window.
- Revenue rerouting records are write-gated to server admin only (`allow write: if false`).
