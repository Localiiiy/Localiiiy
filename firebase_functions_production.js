const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

/**
 * 7-Day Statutory Expiration Trigger:
 * Automatically issues a copyright strike if the uploader fails to submit 
 * a counter-justification within the 7-day window.
 */
exports.copyrightDeadlineEnforcer = functions.pubsub.schedule('every 24 hours').onRun(async (context) => {
    const now = admin.firestore.Timestamp.now();
    
    // Find claims that are PENDING_JUSTIFICATION and past the deadline
    const expiredClaims = await db.collection('copyright_claims')
        .where('status', '==', 'PENDING_JUSTIFICATION')
        .where('counterNotificationDeadline', '<=', now)
        .get();

    if (expiredClaims.empty) {
        console.log('No expired copyright claims found.');
        return null;
    }

    const batch = db.batch();

    expiredClaims.docs.forEach(doc => {
        const claim = doc.data();
        const claimId = doc.id;

        // 1. Mark claim as STRIKE_ISSUED
        batch.update(doc.ref, { 
            status: 'STRIKE_ISSUED',
            quarantined: true 
        });

        // 2. Issue a Copyright Strike
        const strikeId = `strike_${claim.uploaderUid}_${Date.now()}`;
        const strikeRef = db.collection('copyright_strikes').doc(strikeId);
        
        batch.set(strikeRef, {
            strikeId: strikeId,
            offenderUid: claim.uploaderUid,
            offenderHandle: claim.uploaderHandle,
            claimantUid: claim.claimantUid,
            claimId: claimId,
            reason: `Failure to respond to copyright claim within 7-day window: ${claimId}`,
            issuedAt: now,
            expiresAt: admin.firestore.Timestamp.fromMillis(now.toMillis() + (90 * 24 * 60 * 60 * 1000)), // 90 days
            isActive: true
        });
        
        console.log(`Issued strike ${strikeId} for claim ${claimId}`);
    });

    return batch.commit();
});

/**
 * Atomic Unique Username Reservation:
 * Ensures that usernames are unique across the platform using a dedicated 
 * root collection as a lock.
 */
exports.onUserCreate = functions.firestore.document('users/{userId}').onCreate(async (snapshot, context) => {
    const userData = snapshot.data();
    const username = userData.username;
    const userId = context.params.userId;

    if (!username) return null;

    const usernameRef = db.collection('usernames').doc(username.toLowerCase());

    return db.runTransaction(async (transaction) => {
        const usernameDoc = await transaction.get(usernameRef);
        
        if (usernameDoc.exists) {
            // Username already taken, delete the user or mark as invalid
            console.warn(`Username ${username} is already taken by ${usernameDoc.data().uid}`);
            return transaction.delete(snapshot.ref);
        } else {
            // Lock the username
            return transaction.set(usernameRef, {
                uid: userId,
                reservedAt: admin.firestore.Timestamp.now()
            });
        }
    });
});
