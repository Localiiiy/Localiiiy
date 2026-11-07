package com.example

import com.example.data.InitialData
import com.example.data.PrivacySettingsEntity
import com.example.data.StudioVideoEntity
import com.example.ui.screens.ClipsFeedFilter
import org.junit.Assert.*
import org.junit.Test

class PlayStoreComplianceTest {

    @Test
    fun verifyAppBuildMetadata() {
        assertEquals("com.aistudio.localiiiy.live", AppBuildInfo.APP_PACKAGE)
        assertEquals("2.2.0", AppBuildInfo.APP_VERSION)
    }

    @Test
    fun verifyClipsFilterUsesConnectedTerminology() {
        val labels = ClipsFeedFilter.entries.map { it.label }
        assertTrue("ClipsFeedFilter must include 'Connected'", labels.contains("Connected"))
        assertFalse("ClipsFeedFilter must never use 'Following'", labels.any { it.contains("follow", ignoreCase = true) })
    }

    @Test
    fun verifyPrivacyAndGhostModeDefaults() {
        val settings = PrivacySettingsEntity()
        assertTrue("GPS obfuscation should be enabled by default for anti-stalking safety", settings.gpsObfuscationEnabled)
        assertFalse("Ghost mode should default to false until toggled", settings.isGhostMode)
        assertTrue("Location radar should default to enabled", settings.isLocationRadarEnabled)
        assertEquals(1, InitialData.defaultPrivacySettings.id)
    }

    @Test
    fun verifyStudioVideoAndProfileTerminology() {
        val sampleVideo = StudioVideoEntity(
            title = "Neighborhood Pulse 4K",
            description = "Hyperlocal community broadcast",
            videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80",
            durationSeconds = 180,
            category = "Neighborhood & Culture",
            creatorUsername = "local_creator",
            creatorFullName = "Local Creator",
            creatorAvatar = ""
        )
        assertFalse(
            "Studio video creatorConnectedCount must not use 'follow' terminology",
            sampleVideo.creatorConnectedCount.contains("follow", ignoreCase = true)
        )
        assertEquals(0, InitialData.defaultProfile.connectionsCount)
        assertEquals(0, InitialData.defaultProfile.connectedCount)
    }
}
