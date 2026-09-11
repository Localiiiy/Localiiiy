    val allReels: StateFlow<List<ReelEntity>> = combine(repository.allReels, _blockedUsernames, _globalSearchQuery) { reels, blocked, query ->
        val filtered = reels.filter { it.username !in blocked }
        if (query.isBlank()) filtered else filtered.filter { 
            it.caption.contains(query, ignoreCase = true) || 
            (it.location?.contains(query, ignoreCase = true) == true) || 
            it.username.contains(query, ignoreCase = true) || 
            (it.landmark?.contains(query, ignoreCase = true) == true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
