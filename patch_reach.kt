    var blastRadiusIndex by remember { mutableFloatStateOf(0f) }
    val blastRadiusLabels = listOf("Hyper-Local (5km)", "City-Wide (50km)", "National", "Global (Earth)")
    val blastRadiusDescriptions = listOf(
        "Guaranteed feed placement for nearby neighbors. Ideal for local sales.",
        "Expands reach to the entire metro area. Good for high-ticket items.",
        "Broad national visibility. Shipping recommended.",
        "Algorithmic bypass: Open distribution to the entire world network."
    )
