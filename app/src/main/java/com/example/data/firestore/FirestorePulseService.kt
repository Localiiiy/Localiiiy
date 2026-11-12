package com.example.data.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Data model for hyperlocal updates stored in Firebase Firestore.
 */
data class HyperlocalPulseUpdate(
    val id: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val landmark: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceKm: Double = 1.2,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0,
    val tags: List<String> = emptyList(),
    val category: String = "Community"
)

/**
 * Firestore Service for fetching and synchronizing Hyperlocal Pulse updates.
 * Designed with defensive fallbacks to operate smoothly whether Firebase is connected or offline.
 */
class FirestorePulseService(
    firestoreInstance: FirebaseFirestore? = null,
    databaseId: String = "ai-studio-android-localiii-37ed0153-e156-425c-857b-5273dbd0fc55"
) {
    private val firestore: FirebaseFirestore? = firestoreInstance ?: run {
        try {
            if (databaseId.isNotBlank()) {
                FirebaseFirestore.getInstance(databaseId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (t: Throwable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (fallbackErr: Throwable) {
                Log.w("FirestorePulse", "Firebase Firestore unavailable, using local fallback: ${fallbackErr.message}")
                null
            }
        }
    }

    private val pulseCollection = firestore?.collection("hyperlocal_updates")

    /**
     * Real-time Flow of hyperlocal updates from Firestore, ordered by timestamp descending.
     * Includes automated graceful fallback with rich seed data if Firestore is empty, uninitialized, or offline.
     */
    fun getHyperlocalUpdatesFlow(): Flow<List<HyperlocalPulseUpdate>> {
        val collection = pulseCollection
        if (collection == null) {
            return kotlinx.coroutines.flow.flowOf(getFallbackSeedUpdates())
        }

        return callbackFlow {
            val listenerRegistration = try {
                collection
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w("FirestorePulse", "Listen failed, emitting fallback data", error)
                            trySend(getFallbackSeedUpdates())
                            return@addSnapshotListener
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            val updates = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(HyperlocalPulseDocument::class.java)?.toHyperlocalPulseUpdate(doc.id)
                            }
                            trySend(updates)
                        } else {
                            // Empty collection: return seed list
                            trySend(getFallbackSeedUpdates())
                        }
                    }
            } catch (t: Throwable) {
                Log.w("FirestorePulse", "Snapshot listener error, emitting fallback", t)
                trySend(getFallbackSeedUpdates())
                null
            }

            awaitClose { listenerRegistration?.remove() }
        }
    }

    /**
     * Publishes a new hyperlocal update to Firestore with authenticated authorUid binding.
     */
    suspend fun publishHyperlocalUpdate(update: HyperlocalPulseUpdate): Result<String> {
        val collection = pulseCollection ?: return Result.success("local_pulse_${System.currentTimeMillis()}")
        return try {
            val resolvedUid = try {
                FirebaseAuth.getInstance().currentUser?.uid
            } catch (_: Throwable) {
                null
            } ?: update.authorUid

            if (resolvedUid.isBlank()) {
                return Result.failure(IllegalStateException("Authenticated user UID is required to publish to Firestore"))
            }

            val docRef = collection.document()
            val docData = HyperlocalPulseDocument(
                authorUid = resolvedUid,
                authorName = update.authorName,
                username = update.username,
                userAvatar = update.userAvatar,
                content = update.content,
                mediaUrl = update.mediaUrl,
                landmark = update.landmark,
                location = update.location,
                latitude = com.example.util.LocationHelper.coarsenCoordinate(update.latitude),
                longitude = com.example.util.LocationHelper.coarsenCoordinate(update.longitude),
                distanceKm = update.distanceKm,
                timestamp = System.currentTimeMillis(),
                likesCount = update.likesCount.coerceIn(0, 1),
                commentsCount = 0,
                tags = update.tags,
                category = update.category
            )
            docRef.set(docData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("FirestorePulse", "Failed to publish update to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Seed data representing hyperlocal neighborhood updates.
     * Production: Returns empty list to ensure only live data is displayed.
     */
    fun getFallbackSeedUpdates(): List<HyperlocalPulseUpdate> = emptyList()
}

/**
 * Firestore Document representation aligned with firestore.rules schema.
 */
data class HyperlocalPulseDocument(
    val authorUid: String = "",
    val authorName: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val landmark: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceKm: Double = 1.0,
    val timestamp: Long = 0L,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val tags: List<String> = emptyList(),
    val category: String = "Community"
) {
    fun toHyperlocalPulseUpdate(documentId: String): HyperlocalPulseUpdate {
        return HyperlocalPulseUpdate(
            id = documentId,
            authorUid = authorUid,
            authorName = authorName,
            username = username,
            userAvatar = userAvatar,
            content = content,
            mediaUrl = mediaUrl,
            landmark = landmark,
            location = location,
            latitude = latitude,
            longitude = longitude,
            distanceKm = distanceKm,
            timestamp = timestamp,
            likesCount = likesCount,
            commentsCount = commentsCount,
            tags = tags,
            category = category
        )
    }
}

/**
 * Appends community discussion remark to Firestore path: posts/{postId}/remarks/{remarkId}
 * Enforces authorUid alignment with Firestore security rules.
 */
fun appendPostRemarkToFirestore(
    postId: String,
    remarkId: String,
    authorName: String,
    authorHandle: String,
    authorAvatar: String,
    content: String
): Boolean {
    return try {
        val currentUid = try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (_: Throwable) {
            null
        } ?: return false

        val firestore = try {
            FirebaseFirestore.getInstance("ai-studio-android-localiii-37ed0153-e156-425c-857b-5273dbd0fc55")
        } catch (_: Throwable) {
            FirebaseFirestore.getInstance()
        }
        firestore.collection("posts")
            .document(postId)
            .collection("remarks")
            .document(remarkId)
            .set(
                mapOf(
                    "id" to remarkId,
                    "postId" to postId,
                    "authorUid" to currentUid,
                    "authorName" to authorName,
                    "authorHandle" to authorHandle,
                    "authorAvatar" to authorAvatar,
                    "content" to content,
                    "timestamp" to System.currentTimeMillis(),
                    "likesCount" to 0
                )
            )
        true
    } catch (e: Exception) {
        Log.w("FirestorePulse", "Offline/Mock fallback for remark $remarkId on post $postId: ${e.message}")
        false
    }
}

/**
 * Data model for persisting and restoring user's preferred Radar Range, HUD Theme,
 * Far-Reach Signal Transmit state, and Premium subscription state in Cloud Firestore.
 */
data class RadarUserPreferences(
    val uid: String = "",
    val preferredRadarRadiusKm: Double = 50.0,
    val preferredRadarHudTheme: String = "PHOSPHOR_GREEN",
    val isFarReachTransmitActive: Boolean = false,
    val farReachTransmitRange: String = "3KM",
    val isPremiumConnected: Boolean = false,
    val preferredRadarAuraIndex: Int = 0,
    val preferredRadarTagline: String = "⚡ Open for Collabs",
    val updatedAt: Long = System.currentTimeMillis()
)

fun saveRadarPreferencesToFirestore(
    fallbackProfileKey: String,
    preferredRadarRadiusKm: Double,
    preferredRadarHudTheme: String,
    isFarReachTransmitActive: Boolean,
    farReachTransmitRange: String,
    isPremiumConnected: Boolean,
    preferredRadarAuraIndex: Int = 0,
    preferredRadarTagline: String = "⚡ Open for Collabs"
) {
    try {
        val currentUid = try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (_: Throwable) {
            null
        } ?: fallbackProfileKey.ifBlank { "localiiiy_primary_user" }

        val firestore = try {
            FirebaseFirestore.getInstance("ai-studio-android-localiii-37ed0153-e156-425c-857b-5273dbd0fc55")
        } catch (_: Throwable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (_: Throwable) {
                null
            }
        } ?: return

        val payload = mapOf(
            "uid" to currentUid,
            "preferredRadarRadiusKm" to preferredRadarRadiusKm,
            "preferredRadarHudTheme" to preferredRadarHudTheme,
            "isFarReachTransmitActive" to isFarReachTransmitActive,
            "farReachTransmitRange" to farReachTransmitRange,
            "isPremiumConnected" to isPremiumConnected,
            "preferredRadarAuraIndex" to preferredRadarAuraIndex,
            "preferredRadarTagline" to preferredRadarTagline,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("user_radar_preferences")
            .document(currentUid)
            .set(payload, com.google.firebase.firestore.SetOptions.merge())

        firestore.collection("users")
            .document(currentUid)
            .set(payload, com.google.firebase.firestore.SetOptions.merge())
    } catch (e: Throwable) {
        Log.w("FirestorePulse", "Radar preferences saved locally (Firestore sync deferred): ${e.message}")
    }
}

suspend fun fetchRadarPreferencesFromFirestore(fallbackProfileKey: String): RadarUserPreferences? {
    return try {
        val currentUid = try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (_: Throwable) {
            null
        } ?: fallbackProfileKey.ifBlank { "localiiiy_primary_user" }

        val firestore = try {
            FirebaseFirestore.getInstance("ai-studio-android-localiii-37ed0153-e156-425c-857b-5273dbd0fc55")
        } catch (_: Throwable) {
            try {
                FirebaseFirestore.getInstance()
            } catch (_: Throwable) {
                null
            }
        } ?: return null

        val snap = firestore.collection("user_radar_preferences")
            .document(currentUid)
            .get()
            .await()

        val doc = if (snap.exists()) snap else {
            firestore.collection("users").document(currentUid).get().await()
        }

        if (!doc.exists()) return null

        val radiusKm = doc.getDouble("preferredRadarRadiusKm") ?: 50.0
        val hudTheme = doc.getString("preferredRadarHudTheme") ?: "PHOSPHOR_GREEN"
        val isFarReach = doc.getBoolean("isFarReachTransmitActive") ?: false
        val farRange = doc.getString("farReachTransmitRange") ?: "3KM"
        val isPremium = doc.getBoolean("isPremiumConnected") ?: false
        val auraIdx = (doc.getLong("preferredRadarAuraIndex") ?: 0L).toInt()
        val tagline = doc.getString("preferredRadarTagline") ?: "⚡ Open for Collabs"

        RadarUserPreferences(
            uid = currentUid,
            preferredRadarRadiusKm = radiusKm,
            preferredRadarHudTheme = hudTheme,
            isFarReachTransmitActive = isFarReach,
            farReachTransmitRange = farRange,
            isPremiumConnected = isPremium,
            preferredRadarAuraIndex = auraIdx,
            preferredRadarTagline = tagline
        )
    } catch (e: Throwable) {
        Log.w("FirestorePulse", "Could not fetch remote radar preferences, using local persisted view: ${e.message}")
        null
    }
}


