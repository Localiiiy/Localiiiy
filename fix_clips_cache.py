import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

# Add LaunchedEffect for pre-caching
precache_code = '''
    // Background Video Preloading Queue
    LaunchedEffect(pagerState.currentPage, displayedClips) {
        val nextIndices = listOf(
            pagerState.currentPage + 1,
            pagerState.currentPage + 2
        ).filter { it < displayedClips.size }
        
        val urlsToCache = nextIndices.map { displayedClips[it].mediaUrl }
        if (urlsToCache.isNotEmpty()) {
            com.example.util.ExoPlayerCacheHelper.preCacheVideos(context, urlsToCache)
        }
    }

    if (isAutoScrollEnabled && displayedClips.isNotEmpty()) {'''

content = content.replace('    if (isAutoScrollEnabled && displayedClips.isNotEmpty()) {', precache_code)

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
