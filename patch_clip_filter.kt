                val finalClips = clips.filter { clip ->
                    when (scaleDial) {
                        "NEIGHBOR" -> (clip.distanceKm ?: 999.0) <= 5.0
                        "CITY" -> (clip.distanceKm ?: 999.0) <= 50.0
                        "EARTH" -> true
                        else -> true
                    }
                }
                finalClips.forEach { clip ->
