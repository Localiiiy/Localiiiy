sed -i '/val feedPosts: StateFlow/,/\.stateIn/c\
    val feedPosts: StateFlow<List<PostEntity>> = combine(\
        repository.feedPosts,\
        repository.allPulseCache,\
        _blockedUsernames,\
        _globalSearchQuery\
    ) { dbPosts, cachePulses, blocked, query ->\
        val convertedCache = cachePulses.map { p ->\
            PostEntity(\
                id = (p.id.hashCode().toLong() and 0x7FFFFFFF) + 100000L,\
                username = p.username.ifBlank { p.authorName },\
                userAvatar = p.userAvatar,\
                userHandle = "@${p.username.ifBlank { \"local_creator\" }}",\
                mediaUrl = p.mediaUrl,\
                caption = p.content,\
                likesCount = p.likesCount,\
                commentsCount = p.commentsCount,\
                isLiked = p.isLiked,\
                isSaved = false,\
                timestamp = p.timestamp,\
                location = p.location,\
                landmark = p.landmark,\
                latitude = p.latitude,\
                longitude = p.longitude\
            )\
        }\
        val allMerged = (dbPosts + convertedCache)\
            .distinctBy { it.mediaUrl.ifBlank { it.caption } }\
            .filter { it.username !in blocked }\
            .sortedByDescending { it.timestamp }\
        if (query.isBlank()) allMerged else allMerged.filter { it.caption.contains(query, ignoreCase = true) || (it.location?.contains(query, ignoreCase = true) == true) }\
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())\
' app/src/main/java/com/example/ui/LocaliViewModel.kt
