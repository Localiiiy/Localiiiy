    // Filter Posts by All, Trending, Nearby, Following, Latest + Blast Radius Dial
    val displayedPosts = remember(posts, selectedFilter, selectedRadiusKm, scaleDial) {
        val baseFiltered = posts.filter { post ->
            when (scaleDial) {
                "NEIGHBOR" -> (post.distanceKm ?: 999.0) <= 5.0
                "CITY" -> (post.distanceKm ?: 999.0) <= 50.0
                "EARTH" -> true
                else -> true
            }
        }
        
        when (selectedFilter) {
            PulseFeedFilter.ALL -> baseFiltered.sortedByDescending { it.timestamp }
            PulseFeedFilter.TRENDING -> baseFiltered.sortedByDescending { (it.likesCount * 3) + it.commentsCount }
            PulseFeedFilter.NEARBY -> {
                val radius = selectedRadiusKm ?: 3.0
                val nearby = baseFiltered.filter { (it.distanceKm ?: 99.0) <= radius }
                if (nearby.isNotEmpty()) nearby.sortedBy { it.distanceKm ?: 99.0 }
                else baseFiltered.sortedBy { it.distanceKm ?: 99.0 }
            }
            PulseFeedFilter.CONNECTED -> {
                val connected = baseFiltered.filter { it.isFollowing }
                if (connected.isNotEmpty()) connected.sortedByDescending { it.timestamp }
                else baseFiltered.take(5)
            }
            PulseFeedFilter.LATEST -> baseFiltered.sortedByDescending { it.timestamp }
        }
    }
