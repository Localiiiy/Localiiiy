                LiveRadarComponent(
                    userProfile = userProfile,
                    nearbyUsers = nearbyUsers,
                    nearbyPosts = filteredPosts,
                    nearbyClips = clips,
                    nearbyMarketItems = marketplaceItems,
                    selectedRadiusKm = if (scaleDial == "NEIGHBOR") 5.0 else if (scaleDial == "CITY") 50.0 else 50000.0,
                    isLocationEnabled = isLocationEnabled,
                    isPrivateAccount = isPrivateAccount || ghostMode,
                    hidePreciseLocationOnRadar = (privacySettings?.hidePreciseLocationOnRadar ?: false) || ghostMode,
                    radarObfuscatedRange = privacySettings?.radarObfuscatedRange ?: "3k",
                    radarCountryName = privacySettings?.radarCountryName ?: "United States",
                    onToggleHidePreciseLocation = onToggleHidePreciseLocation,
                    onSelectObfuscatedRange = onSelectObfuscatedRange,
                    isRefreshing = isRefreshing,
                    onRadiusChange = { radius ->
                        localRadiusKm = radius
                        onRadiusFilterChange(radius)
                    },
                    onLocationToggle = onLocationToggle,
                    onPrivateToggle = onPrivateToggle,
                    onOpenPrivacySettings = onOpenPrivacySettings,
                    onUserClick = { user -> onUserProfileClick(user.username) },
                    onPostClick = { post -> selectedDetailPost = post },
                    onWaveAtUser = onWaveAtUser
                )
