                val finalFilteredPosts = posts.filter { post ->
                    when (scaleDial) {
                        "NEIGHBOR" -> (post.distanceKm ?: 999.0) <= 5.0
                        "CITY" -> (post.distanceKm ?: 999.0) <= 50.0
                        "EARTH" -> true
                        else -> true
                    }
                }
                finalFilteredPosts.forEach { post ->
