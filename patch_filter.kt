    // Filter posts by query or distance
    val filteredPosts = remember(posts, searchQuery, scaleDial, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else posts.filter { post ->
            val matchesQuery = if (searchQuery.isBlank()) true else {
                post.caption.contains(searchQuery, ignoreCase = true) ||
                post.username.contains(searchQuery, ignoreCase = true) ||
                (post.location?.contains(searchQuery, ignoreCase = true) == true) ||
                (post.landmark?.contains(searchQuery, ignoreCase = true) == true)
            }
            val matchesRadius = when (scaleDial) {
                "NEIGHBOR" -> (post.distanceKm ?: 999.0) <= 5.0
                "CITY" -> (post.distanceKm ?: 999.0) <= 50.0
                "EARTH" -> true
                else -> true
            }
            matchesQuery && matchesRadius
        }
    }
