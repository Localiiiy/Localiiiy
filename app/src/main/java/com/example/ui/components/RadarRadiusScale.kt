package com.example.ui.components

data class RadarRadiusOption(
    val km: Double,
    val shortLabel: String,
    val fullLabel: String,
    val category: String,
    val icon: String
)

object RadarRadiusPresets {
    val ALL_OPTIONS = listOf(
        RadarRadiusOption(1.0, "1 km", "1 KM • Hyperlocal Block", "Local", "🏡"),
        RadarRadiusOption(3.0, "3 km", "3 KM • Neighborhood", "Local", "📍"),
        RadarRadiusOption(5.0, "5 km", "5 KM • District", "City", "🏙️"),
        RadarRadiusOption(10.0, "10 km", "10 KM • City Center", "City", "🌆"),
        RadarRadiusOption(50.0, "50 km", "50 KM • Metro Region", "Metro", "🚗"),
        RadarRadiusOption(100.0, "100 km", "100 KM • State / Area", "State", "🌲"),
        RadarRadiusOption(500.0, "500 km", "500 KM • Regional", "Region", "🚄"),
        RadarRadiusOption(1000.0, "1000 km", "1,000 KM • Sub-Continent", "Zone", "✈️"),
        RadarRadiusOption(5000.0, "Country", "Country • Nationwide (5,000 KM)", "Country", "🇺🇸"),
        RadarRadiusOption(20000.0, "Earth 🌍", "Earth • Global / Worldwide (20,000 KM)", "Global", "🌍")
    )

    val QUICK_RADAR_OPTIONS = listOf(
        RadarRadiusOption(1.0, "1km", "1 KM", "Local", "🏡"),
        RadarRadiusOption(5.0, "5km", "5 KM", "City", "🏙️"),
        RadarRadiusOption(10.0, "10km", "10 KM", "City", "🌆"),
        RadarRadiusOption(50.0, "50km", "50 KM", "Metro", "🚗"),
        RadarRadiusOption(100.0, "100km", "100 KM", "State", "🌲"),
        RadarRadiusOption(500.0, "500km", "500 KM", "Region", "🚄"),
        RadarRadiusOption(1000.0, "1000km", "1000 KM", "Zone", "✈️"),
        RadarRadiusOption(5000.0, "Country", "Country", "Country", "🇺🇸"),
        RadarRadiusOption(20000.0, "Earth", "Earth 🌍", "Global", "🌍")
    )

    fun getLabelForKm(km: Double?): String {
        if (km == null) return "All (Global)"
        val match = ALL_OPTIONS.firstOrNull { it.km == km }
        return match?.shortLabel ?: "${km.toInt()} km"
    }
}
