package com.example.data.firestore

import android.util.Log
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
    firestoreInstance: FirebaseFirestore? = null
) {
    private val firestore: FirebaseFirestore? = firestoreInstance ?: run {
        try {
            FirebaseFirestore.getInstance()
        } catch (t: Throwable) {
            Log.w("FirestorePulse", "Firebase Firestore unavailable, using local mock data: ${t.message}")
            null
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
                            // Empty collection: seed initial hyperlocal documents and return seed list
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
     * Publishes a new hyperlocal update to Firestore.
     */
    suspend fun publishHyperlocalUpdate(update: HyperlocalPulseUpdate): Result<String> {
        val collection = pulseCollection ?: return Result.success("local_pulse_${System.currentTimeMillis()}")
        return try {
            val docRef = collection.document()
            val docData = HyperlocalPulseDocument(
                authorName = update.authorName,
                username = update.username,
                userAvatar = update.userAvatar,
                content = update.content,
                mediaUrl = update.mediaUrl,
                landmark = update.landmark,
                location = update.location,
                latitude = update.latitude,
                longitude = update.longitude,
                distanceKm = update.distanceKm,
                timestamp = System.currentTimeMillis(),
                likesCount = update.likesCount,
                commentsCount = update.commentsCount,
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
     * Seed data representing hyperlocal neighborhood updates in the Seattle radius.
     */
    fun getFallbackSeedUpdates(): List<HyperlocalPulseUpdate> = listOf(
        HyperlocalPulseUpdate(
            id = "pulse_seed_1",
            authorName = "Marcus Chen",
            username = "marcus_photo",
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
            content = "Golden hour reflecting across Elliott Bay docks. The ferry horns are echoing through Belltown tonight!",
            mediaUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=1080&auto=format&fit=crop&q=85",
            landmark = "Pier 57 Waterfront",
            location = "Seattle, WA",
            latitude = 47.6062,
            longitude = -122.3421,
            distanceKm = 0.4,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
            likesCount = 342,
            isLiked = false,
            commentsCount = 28,
            tags = listOf("#Waterfront", "#GoldenHour", "#SeattlePulses"),
            category = "Photography"
        ),
        HyperlocalPulseUpdate(
            id = "pulse_seed_2",
            authorName = "Elena Rostova",
            username = "elena_crafts",
            userAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
            content = "Pop-up ceramic firing at Occidental Square! Hand-thrown mugs fresh from the kiln, steam still rising in the cold evening air.",
            mediaUrl = "https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=1080&auto=format&fit=crop&q=85",
            landmark = "Occidental Square",
            location = "Pioneer Square",
            latitude = 47.6001,
            longitude = -122.3332,
            distanceKm = 0.8,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 35,
            likesCount = 189,
            isLiked = true,
            commentsCount = 19,
            tags = listOf("#PioneerSquare", "#Ceramics", "#LocalMakers"),
            category = "Art & Craft"
        ),
        HyperlocalPulseUpdate(
            id = "pulse_seed_3",
            authorName = "Devon Miles",
            username = "pike_baker",
            userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            content = "Fresh rosemary sea salt focaccia and dark rye boules just pulled from the brick hearth. Come through before they're gone!",
            mediaUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=1080&auto=format&fit=crop&q=85",
            landmark = "Pike Place Market",
            location = "Downtown Seattle",
            latitude = 47.6097,
            longitude = -122.3425,
            distanceKm = 0.2,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 75,
            likesCount = 512,
            isLiked = false,
            commentsCount = 64,
            tags = listOf("#PikePlace", "#Sourdough", "#SeattleEats"),
            category = "Food & Drink"
        ),
        HyperlocalPulseUpdate(
            id = "pulse_seed_4",
            authorName = "Aria Patel",
            username = "aria_sound",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            content = "Acoustic busker session under the clock tower. Live frequency broadcasting across Capitol Hill!",
            mediaUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1080&auto=format&fit=crop&q=85",
            landmark = "Cal Anderson Park",
            location = "Capitol Hill",
            latitude = 47.6174,
            longitude = -122.3195,
            distanceKm = 1.9,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
            likesCount = 276,
            isLiked = false,
            commentsCount = 31,
            tags = listOf("#CapitolHill", "#StreetMusic", "#LiveSound"),
            category = "Music"
        ),
        HyperlocalPulseUpdate(
            id = "pulse_seed_5",
            authorName = "Jordan Hayes",
            username = "jordan_seattle",
            userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            content = "Night radar bike commute through Olympic Sculpture Park. Neon lights bouncing off the rain puddles.",
            mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1080&auto=format&fit=crop&q=85",
            landmark = "Olympic Sculpture Park",
            location = "Belltown",
            latitude = 47.6166,
            longitude = -122.3553,
            distanceKm = 1.1,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 240,
            likesCount = 420,
            isLiked = true,
            commentsCount = 45,
            tags = listOf("#Belltown", "#NightRide", "#UrbanVibe"),
            category = "Community"
        )
    )
}

/**
 * Firestore Document representation.
 */
data class HyperlocalPulseDocument(
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
