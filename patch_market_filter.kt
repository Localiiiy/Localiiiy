                val finalMarket = marketplaceItems.filter { item ->
                    when (scaleDial) {
                        "NEIGHBOR" -> (item.distanceKm ?: 999.0) <= 5.0
                        "CITY" -> (item.distanceKm ?: 999.0) <= 50.0
                        "EARTH" -> true
                        else -> true
                    }
                }
                finalMarket.forEach { item ->
