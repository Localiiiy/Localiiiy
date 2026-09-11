sed -i '/private val _globalSearchQuery/d' app/src/main/java/com/example/ui/LocaliViewModel.kt
sed -i '/val globalSearchQuery:/d' app/src/main/java/com/example/ui/LocaliViewModel.kt
sed -i '/fun setGlobalSearchQuery/,+4d' app/src/main/java/com/example/ui/LocaliViewModel.kt

sed -i '/private val _blockedUsernames/i \
    private val _globalSearchQuery = MutableStateFlow("")\
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()\
\
    fun setGlobalSearchQuery(query: String) {\
        _globalSearchQuery.value = query\
        _marketplaceSearchQuery.value = query\
        _studioSearchQuery.value = query\
    }\
' app/src/main/java/com/example/ui/LocaliViewModel.kt
