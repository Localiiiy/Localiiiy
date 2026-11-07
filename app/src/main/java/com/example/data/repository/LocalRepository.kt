package com.example.data.repository

import com.example.data.model.CreatorClip
import com.example.data.model.MarketListing
import com.example.data.model.Pulse
import com.example.data.model.RadarItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Pradeep Chandra",
            handle = "@pradeep_local",
            avatarUrl = "https://picsum.photos/seed/user_avatar/200/200",
            bio = "Neighborhood Explorer & Local Guide | Passionate about local coffee, tech & community pulse.",
            neighborhood = "Downtown Earth Hub",
            points = 1420,
            badges = listOf("Neighborhood Champion", "Top Creator", "Proximity Scout", "Early Supporter"),
            proximityAlertsEnabled = true,
            totalPulses = 24,
            totalClips = 12,
            totalListings = 5
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _selectedNeighborhood = MutableStateFlow("Downtown Earth Hub")
    val selectedNeighborhood: StateFlow<String> = _selectedNeighborhood.asStateFlow()

    private val _pulses = MutableStateFlow(
        listOf(
            Pulse(
                id = "p1",
                authorName = "Maya Lin",
                authorHandle = "@mayalin_crafts",
                authorAvatarUrl = "https://picsum.photos/seed/maya/150/150",
                locationName = "Green Leaf Plaza",
                timestampFormatted = "12m ago",
                textContent = "Fresh organic sourdough and local wildflower honey set up at the Plaza Market today! Stop by before 4 PM for complimentary espresso tasting. ☕🍯",
                imageUrl = "https://picsum.photos/seed/sourdough/600/400",
                likesCount = 38,
                commentsCount = 9,
                category = "Event",
                userDistanceKm = 0.3
            ),
            Pulse(
                id = "p2",
                authorName = "Neighborhood Watch",
                authorHandle = "@earth_pulse_safety",
                authorAvatarUrl = "https://picsum.photos/seed/watch/150/150",
                locationName = "Oak Avenue & 4th St",
                timestampFormatted = "35m ago",
                textContent = "⚠️ Community Alert: Road maintenance on 4th Street between Oak & Elm. Expect mild traffic delays until 2:00 PM. Alternate route via Pine St.",
                imageUrl = null,
                likesCount = 19,
                commentsCount = 4,
                category = "Alert",
                userDistanceKm = 0.8
            ),
            Pulse(
                id = "p3",
                authorName = "Alex Rivera",
                authorHandle = "@alex_soundstudio",
                authorAvatarUrl = "https://picsum.photos/seed/alex/150/150",
                locationName = "The Echo Sound Studio",
                timestampFormatted = "1h ago",
                textContent = "Hosting an open synth & acoustic jam session tonight at 7 PM! All local musicians welcome. Bring your instruments or just come vibe. 🎸🎶",
                imageUrl = "https://picsum.photos/seed/jam_session/600/400",
                likesCount = 64,
                commentsCount = 18,
                category = "General",
                userDistanceKm = 1.2
            ),
            Pulse(
                id = "p4",
                authorName = "Elena Vance",
                authorHandle = "@elena_eco",
                authorAvatarUrl = "https://picsum.photos/seed/elena/150/150",
                locationName = "Sunset Ridge Park",
                timestampFormatted = "3h ago",
                textContent = "Pro-Tip: The community garden harvested extra heirloom tomatoes! Free pickup box near the main pavilion gates. 🍅🌱",
                imageUrl = "https://picsum.photos/seed/tomatoes/600/400",
                likesCount = 52,
                commentsCount = 11,
                category = "Tip",
                userDistanceKm = 1.5
            )
        )
    )
    val pulses: StateFlow<List<Pulse>> = _pulses.asStateFlow()

    private val _radarItems = MutableStateFlow(
        listOf(
            RadarItem(
                id = "r1",
                title = "Artisan Roasters & Cafe",
                category = "Spot",
                angleDegrees = 45f,
                distanceMeters = 250,
                rating = 4.9,
                imageUrl = "https://picsum.photos/seed/cafe_radar/400/300",
                address = "122 Earth Ave, Suite B",
                snippet = "Specialty single-origin pour-overs and fresh matcha pastries."
            ),
            RadarItem(
                id = "r2",
                title = "Live Jazz in the Courtyard",
                category = "Event",
                angleDegrees = 135f,
                distanceMeters = 480,
                rating = 4.8,
                imageUrl = "https://picsum.photos/seed/jazz_radar/400/300",
                address = "Grand Square Amphitheater",
                snippet = "Live quartet performance featuring local saxophone legend Marcus Vance."
            ),
            RadarItem(
                id = "r3",
                title = "Eco-Craft Workshop Studio",
                category = "Creator",
                angleDegrees = 220f,
                distanceMeters = 320,
                rating = 4.9,
                imageUrl = "https://picsum.photos/seed/craft_radar/400/300",
                address = "88 Pottery Lane",
                snippet = "Handmade ceramic pottery classes and customized glasswork."
            ),
            RadarItem(
                id = "r4",
                title = "Vintage Mechanical Keyboard Pop-up",
                category = "Market",
                angleDegrees = 310f,
                distanceMeters = 610,
                rating = 4.7,
                imageUrl = "https://picsum.photos/seed/keyboard_radar/400/300",
                address = "Studio 404, Tech Alley",
                snippet = "Custom keycaps, lubricated switches, and custom retro builds."
            )
        )
    )
    val radarItems: StateFlow<List<RadarItem>> = _radarItems.asStateFlow()

    private val _creatorClips = MutableStateFlow(
        listOf(
            CreatorClip(
                id = "c1",
                creatorName = "Chef Samira",
                creatorHandle = "@samira_cooks",
                creatorAvatarUrl = "https://picsum.photos/seed/samira/150/150",
                title = "Secrets to making authentic local wood-fired pizza in 60 seconds! 🍕🔥",
                videoThumbnailUrl = "https://picsum.photos/seed/pizza_clip/600/900",
                locationName = "Samira Woodfired Kitchen",
                likesCount = 1240,
                sharesCount = 310,
                commentsCount = 89,
                taggedProduct = "Handcrafted Pizza Oven Special"
            ),
            CreatorClip(
                id = "c2",
                creatorName = "David Kinetic",
                creatorHandle = "@david_skate",
                creatorAvatarUrl = "https://picsum.photos/seed/david/150/150",
                title = "Sunset line through Waterfront Skate Park! Clean kickflip off the 5-set 🛹🌅",
                videoThumbnailUrl = "https://picsum.photos/seed/skate_clip/600/900",
                locationName = "Waterfront Park",
                likesCount = 2890,
                sharesCount = 540,
                commentsCount = 142
            ),
            CreatorClip(
                id = "c3",
                creatorName = "Botanical Living",
                creatorHandle = "@green_sanctuary",
                creatorAvatarUrl = "https://picsum.photos/seed/plant_creator/150/150",
                title = "Restoring an 80-year-old Bonsai tree at the neighborhood greenhouse 🌿🪴",
                videoThumbnailUrl = "https://picsum.photos/seed/bonsai_clip/600/900",
                locationName = "Greenhouse Sanctuary",
                likesCount = 1750,
                sharesCount = 210,
                commentsCount = 67,
                taggedProduct = "Rare Tropical Plant Cuttings"
            )
        )
    )
    val creatorClips: StateFlow<List<CreatorClip>> = _creatorClips.asStateFlow()

    private val _marketListings = MutableStateFlow(
        listOf(
            MarketListing(
                id = "m1",
                sellerName = "Aria Chen",
                sellerRating = 4.9,
                sellerAvatarUrl = "https://picsum.photos/seed/aria/150/150",
                title = "Handmade Solid Walnut Coffee Table",
                price = 185.0,
                category = "Handmade",
                imageUrl = "https://picsum.photos/seed/walnut_table/600/400",
                description = "Custom crafted from locally sourced reclaimed Oregon walnut. Sealed with non-toxic satin wax finish. Dimensions: 42 x 22 inches.",
                locationDistanceKm = 0.6,
                condition = "Artisan Made"
            ),
            MarketListing(
                id = "m2",
                sellerName = "Lucas Wright",
                sellerRating = 4.8,
                sellerAvatarUrl = "https://picsum.photos/seed/lucas/150/150",
                title = "Fujifilm X-T30 Mirrorless Camera + 18-55mm Lens",
                price = 620.0,
                category = "Electronics",
                imageUrl = "https://picsum.photos/seed/fuji_camera/600/400",
                description = "Mint condition, low shutter count (< 4k). Includes original box, 2 batteries, leather wrist strap, and SanDisk 64GB card.",
                locationDistanceKm = 1.1,
                condition = "Like New"
            ),
            MarketListing(
                id = "m3",
                sellerName = "Organic Earth Farm",
                sellerRating = 5.0,
                sellerAvatarUrl = "https://picsum.photos/seed/farm_seller/150/150",
                title = "Fresh Organic Honeycomb & Lavender Basket",
                price = 28.0,
                category = "Food",
                imageUrl = "https://picsum.photos/seed/honey_basket/600/400",
                description = "Pure raw honeycomb harvested yesterday along with dried lavender sachets and handmade beeswax candles.",
                locationDistanceKm = 0.4,
                condition = "Fresh"
            ),
            MarketListing(
                id = "m4",
                sellerName = "Retro Finds Lab",
                sellerRating = 4.7,
                sellerAvatarUrl = "https://picsum.photos/seed/retro_seller/150/150",
                title = "1970s Restored Vinyl Turntable System",
                price = 240.0,
                category = "Vintage",
                imageUrl = "https://picsum.photos/seed/turntable/600/400",
                description = "Fully serviced belt-drive turntable with built-in preamp and wooden enclosure. Crisp analog sound!",
                locationDistanceKm = 1.8,
                condition = "Restored"
            )
        )
    )
    val marketListings: StateFlow<List<MarketListing>> = _marketListings.asStateFlow()

    fun updateNeighborhood(newNeighborhood: String) {
        _selectedNeighborhood.value = newNeighborhood
        _userProfile.update { it.copy(neighborhood = newNeighborhood) }
    }

    fun toggleProximityAlerts() {
        _userProfile.update { it.copy(proximityAlertsEnabled = !it.proximityAlertsEnabled) }
    }

    fun toggleLikePulse(pulseId: String) {
        _pulses.update { list ->
            list.map { pulse ->
                if (pulse.id == pulseId) {
                    val newLiked = !pulse.isLiked
                    pulse.copy(
                        isLiked = newLiked,
                        likesCount = if (newLiked) pulse.likesCount + 1 else pulse.likesCount - 1
                    )
                } else pulse
            }
        }
    }

    fun toggleLikeClip(clipId: String) {
        _creatorClips.update { list ->
            list.map { clip ->
                if (clip.id == clipId) {
                    val newLiked = !clip.isLiked
                    clip.copy(
                        isLiked = newLiked,
                        likesCount = if (newLiked) clip.likesCount + 1 else clip.likesCount - 1
                    )
                } else clip
            }
        }
    }

    fun toggleSaveMarketListing(listingId: String) {
        _marketListings.update { list ->
            list.map { item ->
                if (item.id == listingId) item.copy(isSaved = !item.isSaved) else item
            }
        }
    }

    fun addPulse(titleText: String, category: String, imageUrl: String? = null) {
        val newPulse = Pulse(
            id = "p_${System.currentTimeMillis()}",
            authorName = _userProfile.value.name,
            authorHandle = _userProfile.value.handle,
            authorAvatarUrl = _userProfile.value.avatarUrl,
            locationName = _selectedNeighborhood.value,
            timestampFormatted = "Just now",
            textContent = titleText,
            imageUrl = imageUrl,
            likesCount = 1,
            commentsCount = 0,
            category = category,
            userDistanceKm = 0.1,
            isLiked = true
        )
        _pulses.update { listOf(newPulse) + it }
        _userProfile.update { it.copy(totalPulses = it.totalPulses + 1, points = it.points + 25) }
    }

    fun addMarketListing(
        title: String,
        price: Double,
        category: String,
        description: String,
        condition: String,
        imageUrl: String
    ) {
        val newListing = MarketListing(
            id = "m_${System.currentTimeMillis()}",
            sellerName = _userProfile.value.name,
            sellerRating = 5.0,
            sellerAvatarUrl = _userProfile.value.avatarUrl,
            title = title,
            price = price,
            category = category,
            imageUrl = imageUrl,
            description = description,
            locationDistanceKm = 0.2,
            condition = condition
        )
        _marketListings.update { listOf(newListing) + it }
        _userProfile.update { it.copy(totalListings = it.totalListings + 1, points = it.points + 50) }
    }

    fun addCreatorClip(title: String, location: String, videoThumbnailUrl: String) {
        val newClip = CreatorClip(
            id = "c_${System.currentTimeMillis()}",
            creatorName = _userProfile.value.name,
            creatorHandle = _userProfile.value.handle,
            creatorAvatarUrl = _userProfile.value.avatarUrl,
            title = title,
            videoThumbnailUrl = videoThumbnailUrl,
            locationName = location,
            likesCount = 1,
            sharesCount = 0,
            commentsCount = 0,
            isLiked = true
        )
        _creatorClips.update { listOf(newClip) + it }
        _userProfile.update { it.copy(totalClips = it.totalClips + 1, points = it.points + 100) }
    }
}
