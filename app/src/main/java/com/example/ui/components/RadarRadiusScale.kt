package com.example.ui.components

data class RadarRadiusOption(
    val km: Double,
    val shortLabel: String,
    val fullLabel: String,
    val category: String,
    val icon: String
)

data class ObfuscatedRangeOption(
    val key: String,
    val shortLabel: String,
    val fullLabel: String,
    val kmDisplay: String,
    val icon: String,
    val description: String,
    val virtualDistanceKm: Double
)

object RadarPrivacyPresets {
    val OBFUSCATED_RANGES = listOf(
        ObfuscatedRangeOption(
            key = "3k",
            shortLabel = "3k",
            fullLabel = "3K • Out of Range (3,000 KM)",
            kmDisplay = "3,000 KM",
            icon = "📍",
            description = "Shows you as 3,000 KM far out, beyond local neighborhood scans",
            virtualDistanceKm = 3000.0
        ),
        ObfuscatedRangeOption(
            key = "10K",
            shortLabel = "10K",
            fullLabel = "10K • Far Range (10,000 KM)",
            kmDisplay = "10,000 KM",
            icon = "✈️",
            description = "Shows you as 10,000 KM far away in sub-orbital distance",
            virtualDistanceKm = 10000.0
        ),
        ObfuscatedRangeOption(
            key = "100k",
            shortLabel = "100k",
            fullLabel = "100k • Exosphere Range (100,000 KM)",
            kmDisplay = "100,000 KM",
            icon = "🛰️",
            description = "Shows you 100,000 KM far out in satellite exosphere space",
            virtualDistanceKm = 100000.0
        ),
        ObfuscatedRangeOption(
            key = "500K",
            shortLabel = "500K",
            fullLabel = "500K • Deep Space (500,000 KM)",
            kmDisplay = "500,000 KM",
            icon = "🌕",
            description = "Shows you 500,000 KM far out in lunar orbit distance",
            virtualDistanceKm = 500000.0
        ),
        ObfuscatedRangeOption(
            key = "Country",
            shortLabel = "Country",
            fullLabel = "Country • Nationwide (United States)",
            kmDisplay = "Country Level",
            icon = "🇺🇸",
            description = "Hides exact city/street and displays only nationwide country level",
            virtualDistanceKm = 5000.0
        ),
        ObfuscatedRangeOption(
            key = "Earth",
            shortLabel = "Earth",
            fullLabel = "Earth • Planet Orbital (20,000 KM)",
            kmDisplay = "Earth Orbit",
            icon = "🌍",
            description = "Hides all local coordinates and displays you anywhere on planet Earth",
            virtualDistanceKm = 20000.0
        ),
        ObfuscatedRangeOption(
            key = "Galaxy",
            shortLabel = "Galaxy",
            fullLabel = "Galaxy • Milky Way Intergalactic",
            kmDisplay = "Intergalactic",
            icon = "🌌",
            description = "Projects your location deep in the Milky Way Galaxy beyond Earth",
            virtualDistanceKm = 10000000.0
        )
    )

    fun getOption(key: String): ObfuscatedRangeOption {
        return OBFUSCATED_RANGES.firstOrNull { it.key.equals(key, ignoreCase = true) }
            ?: OBFUSCATED_RANGES.first()
    }
}

object RadarRadiusPresets {
    val ALL_OPTIONS = listOf(
        RadarRadiusOption(1.0, "1 km", "1 KM • Hyperlocal Block", "Local", "🏡"),
        RadarRadiusOption(3.0, "3 km", "3 KM • Neighborhood", "Local", "📍"),
        RadarRadiusOption(5.0, "5 km", "5 KM • District", "City", "🏙️"),
        RadarRadiusOption(10.0, "10 km", "10 KM • City Center", "City", "🌆"),
        RadarRadiusOption(50.0, "50 km", "50 KM • Metro Region", "Metro", "🚗"),
        RadarRadiusOption(100.0, "100 km", "100 KM • State / Area", "State", "🌲"),
        RadarRadiusOption(500.0, "500 km", "500 KM • Regional", "Region", "🚄"),
        RadarRadiusOption(3000.0, "3k", "3,000 KM • Far Range", "Far", "📍"),
        RadarRadiusOption(10000.0, "10K", "10,000 KM • Sub-Orbital", "Orbit", "✈️"),
        RadarRadiusOption(100000.0, "100k", "100,000 KM • Exosphere", "Space", "🛰️"),
        RadarRadiusOption(500000.0, "500K", "500,000 KM • Deep Space", "Space", "🌕"),
        RadarRadiusOption(5000.0, "Country", "Country Name • Nationwide", "Country", "🇺🇸"),
        RadarRadiusOption(20000.0, "Earth 🌍", "Earth • Global / Worldwide", "Global", "🌍"),
        RadarRadiusOption(10000000.0, "Galaxy 🌌", "Galaxy • Intergalactic Scale", "Cosmic", "🌌")
    )

    val QUICK_RADAR_OPTIONS = listOf(
        RadarRadiusOption(1.0, "1km", "1 KM", "Local", "🏡"),
        RadarRadiusOption(3.0, "3km", "3 KM", "Local", "📍"),
        RadarRadiusOption(5.0, "5km", "5 KM", "City", "🏙️"),
        RadarRadiusOption(10.0, "10km", "10 KM", "City", "🌆"),
        RadarRadiusOption(50.0, "50km", "50 KM", "Metro", "🚗"),
        RadarRadiusOption(100.0, "100km", "100 KM", "State", "🌲"),
        RadarRadiusOption(500.0, "500km", "500 KM", "Region", "🚄"),
        RadarRadiusOption(3000.0, "3k", "3k", "Far", "📍"),
        RadarRadiusOption(10000.0, "10K", "10K", "Far", "✈️"),
        RadarRadiusOption(100000.0, "100k", "100k", "Space", "🛰️"),
        RadarRadiusOption(500000.0, "500K", "500K", "Space", "🌕"),
        RadarRadiusOption(5000.0, "Country", "Country", "Country", "🇺🇸"),
        RadarRadiusOption(20000.0, "Earth", "Earth", "Global", "🌍"),
        RadarRadiusOption(10000000.0, "Galaxy", "Galaxy", "Cosmic", "🌌")
    )

    fun getLabelForKm(km: Double?): String {
        if (km == null) return "All (Global)"
        val match = ALL_OPTIONS.firstOrNull { it.km == km }
        return match?.shortLabel ?: "${km.toInt()} km"
    }
}

data class SystematicDistanceOption(
    val key: String,
    val km: Double,
    val shortLabel: String,
    val fullLabel: String,
    val icon: String,
    val pulseDurationMs: Int,
    val description: String
)

object SystematicDistanceScale {
    const val COUNTRY_DEFAULT_KM = 5000.0
    const val EARTH_KM = 20000.0
    const val GALAXY_KM = 10000000.0

    fun getOptions(countryName: String? = null): List<SystematicDistanceOption> {
        val countryDisplay = if (!countryName.isNullOrBlank() && !countryName.equals("Country", ignoreCase = true)) {
            countryName
        } else {
            "Country"
        }
        return listOf(
            SystematicDistanceOption("3KM", 3.0, "3KM", "3 KM • Neighborhood", "📍", 1200, "Neighborhood range within 3 km"),
            SystematicDistanceOption("5KM", 5.0, "5KM", "5 KM • District", "🏙️", 1500, "District and local town scans within 5 km"),
            SystematicDistanceOption("50KM", 50.0, "50KM", "50 KM • Metro Region", "🚗", 2000, "Metropolitan area within 50 km"),
            SystematicDistanceOption("100KM", 100.0, "100KM", "100 KM • State / Area", "🌲", 2500, "Statewide or territorial reach within 100 km"),
            SystematicDistanceOption("500KM", 500.0, "500KM", "500 KM • Regional", "🚄", 3000, "Regional wide-area scan within 500 km"),
            SystematicDistanceOption("1000KM", 1000.0, "1000KM", "1000KM", "✈️", 3600, "Subcontinental reach within 1,000 km"),
            SystematicDistanceOption("COUNTRY", COUNTRY_DEFAULT_KM, countryDisplay, "$countryDisplay • Nationwide", "🇺🇸", 4200, "Nationwide reach across $countryDisplay"),
            SystematicDistanceOption("EARTH", EARTH_KM, "Earth", "Earth • Global / Planetary", "🌍", 5000, "Planetary reach across Earth (20,000 km)"),
            SystematicDistanceOption("GALAXY", GALAXY_KM, "Galaxy", "Galaxy • Intergalactic", "🌌", 6000, "Intergalactic reach across the Milky Way Galaxy")
        )
    }

    fun findOption(km: Double?, countryName: String? = null): SystematicDistanceOption {
        val options = getOptions(countryName)
        if (km == null) return options.first()
        return options.minByOrNull { kotlin.math.abs(it.km - km) } ?: options.first()
    }
}
