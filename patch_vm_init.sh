sed -i '/detectCurrentLocation(application.applicationContext)/i \
        // Sync Firestore to Room\
        viewModelScope.launch(Dispatchers.IO) {\
            val firestoreService = com.example.data.firestore.FirestorePulseService()\
            firestoreService.getHyperlocalUpdatesFlow().collect { updates ->\
                val cacheEntities = updates.map { update ->\
                    PulseCacheEntity(\
                        id = update.id,\
                        authorName = update.authorName,\
                        username = update.username,\
                        userAvatar = update.userAvatar,\
                        content = update.content,\
                        mediaUrl = update.mediaUrl,\
                        landmark = update.landmark,\
                        location = update.location,\
                        latitude = update.latitude,\
                        longitude = update.longitude,\
                        distanceKm = update.distanceKm,\
                        timestamp = update.timestamp,\
                        likesCount = update.likesCount,\
                        isLiked = update.isLiked,\
                        commentsCount = update.commentsCount,\
                        tags = update.tags.joinToString(",")\
                    )\
                }\
                repository.refreshPulseCache(cacheEntities)\
            }\
        }\
' app/src/main/java/com/example/ui/LocaliViewModel.kt
