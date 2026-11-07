package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.CreatorClip
import com.example.data.model.MarketListing
import com.example.data.model.Pulse
import com.example.data.model.RadarItem
import com.example.data.model.UserProfile
import com.example.data.repository.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalViewModel(
    private val repository: LocalRepository = LocalRepository()
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val selectedNeighborhood: StateFlow<String> = repository.selectedNeighborhood
    val pulses: StateFlow<List<Pulse>> = repository.pulses
    val radarItems: StateFlow<List<RadarItem>> = repository.radarItems
    val creatorClips: StateFlow<List<CreatorClip>> = repository.creatorClips
    val marketListings: StateFlow<List<MarketListing>> = repository.marketListings

    private val _radarFilterCategory = MutableStateFlow("All")
    val radarFilterCategory: StateFlow<String> = _radarFilterCategory.asStateFlow()

    private val _pulseFilterCategory = MutableStateFlow("All")
    val pulseFilterCategory: StateFlow<String> = _pulseFilterCategory.asStateFlow()

    private val _marketCategory = MutableStateFlow("All")
    val marketCategory: StateFlow<String> = _marketCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setRadarFilter(category: String) {
        _radarFilterCategory.value = category
    }

    fun setPulseFilter(category: String) {
        _pulseFilterCategory.value = category
    }

    fun setMarketCategory(category: String) {
        _marketCategory.value = category
    }

    fun setNeighborhood(neighborhood: String) {
        repository.updateNeighborhood(neighborhood)
    }

    fun toggleProximityAlerts() {
        repository.toggleProximityAlerts()
    }

    fun toggleLikePulse(pulseId: String) {
        repository.toggleLikePulse(pulseId)
    }

    fun toggleLikeClip(clipId: String) {
        repository.toggleLikeClip(clipId)
    }

    fun toggleSaveMarketListing(listingId: String) {
        repository.toggleSaveMarketListing(listingId)
    }

    fun createPulse(text: String, category: String, imageUrl: String? = null) {
        if (text.isNotBlank()) {
            repository.addPulse(text, category, imageUrl)
        }
    }

    fun createMarketListing(
        title: String,
        price: Double,
        category: String,
        description: String,
        condition: String,
        imageUrl: String
    ) {
        if (title.isNotBlank() && price > 0) {
            repository.addMarketListing(title, price, category, description, condition, imageUrl)
        }
    }

    fun createCreatorClip(title: String, location: String, thumbnailUrl: String) {
        if (title.isNotBlank()) {
            repository.addCreatorClip(title, location, thumbnailUrl)
        }
    }
}
