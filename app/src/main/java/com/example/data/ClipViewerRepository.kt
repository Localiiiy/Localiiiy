package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ClipViewerRepository {
    // Mock viewer roster: Map of clipId to list of userIds who viewed it
    private val viewerRosters = mutableMapOf<Long, MutableList<String>>()
    
    // Aggregate view counts
    private val viewCounts = mutableMapOf<Long, Int>()

    fun recordView(clipId: Long, activeUserId: String, isGhostMode: Boolean) {
        // Increment the aggregate video play count
        val currentCount = viewCounts.getOrDefault(clipId, 0)
        viewCounts[clipId] = currentCount + 1

        // Without writing the active user ID to the viewer roster when isGhostMode == true
        if (!isGhostMode) {
            val roster = viewerRosters.getOrPut(clipId) { mutableListOf() }
            if (!roster.contains(activeUserId)) {
                roster.add(activeUserId)
            }
        }
    }
    
    fun getViewCount(clipId: Long): Int = viewCounts.getOrDefault(clipId, 0)
    
    fun getViewerRoster(clipId: Long): List<String> = viewerRosters.getOrDefault(clipId, emptyList())
}
