package com.example.ui.components

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.EditorialHeart
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*

/**
 * Interactive Map Screen powered by Google Maps SDK that displays real-time 'Moments' pins
 * for the Localiiiy Live Radar feature.
 */
@Composable
fun GoogleMapMomentsComponent(
    posts: List<PostEntity>,
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity>,
    selectedRadiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    onPostClick: (PostEntity) -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onUserProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var googleMapInstance by remember { mutableStateOf<GoogleMap?>(null) }
    var selectedPostMoment by remember { mutableStateOf<PostEntity?>(null) }
    var selectedUserMoment by remember { mutableStateOf<OtherUserEntity?>(null) }
    var mapType by remember { mutableStateOf(GoogleMap.MAP_TYPE_NORMAL) }
    var showMapTypeMenu by remember { mutableStateOf(false) }

    // User coordinates
    val userLatLng = remember(userProfile.latitude, userProfile.longitude) {
        LatLng(
            if (userProfile.latitude != 0.0) userProfile.latitude else LocationHelper.DEFAULT_LAT,
            if (userProfile.longitude != 0.0) userProfile.longitude else LocationHelper.DEFAULT_LNG
        )
    }

    // MapView lifecycle management
    val mapView = rememberMapViewWithLifecycle()

    // Sync markers & radar radius circle when map is ready or data changes
    LaunchedEffect(googleMapInstance, posts, nearbyUsers, selectedRadiusKm, mapType) {
        val map = googleMapInstance ?: return@LaunchedEffect

        map.mapType = mapType
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false

        map.clear()

        // 1. Add Radar Proximity Scanning Circle
        val radiusMeters = (selectedRadiusKm * 1000).coerceAtLeast(500.0)
        map.addCircle(
            CircleOptions()
                .center(userLatLng)
                .radius(radiusMeters)
                .fillColor(0x220D9488) // Translucent teal fill
                .strokeColor(0xFF0D9488.toInt()) // Solid teal stroke
                .strokeWidth(3f)
        )

        // 2. Add Center User Marker (My Location)
        map.addMarker(
            MarkerOptions()
                .position(userLatLng)
                .title("Your Current Location")
                .snippet(userProfile.locationName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        // 3. Add Real-Time Moments Pins (Posts)
        posts.forEachIndexed { index, post ->
            val lat = post.latitude ?: (userLatLng.latitude + getDeterministicOffsetLat(post.id, index))
            val lng = post.longitude ?: (userLatLng.longitude + getDeterministicOffsetLng(post.id, index))
            val position = LatLng(lat, lng)

            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(post.username)
                    .snippet(post.landmark ?: post.location ?: "Hyperlocal Moment")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )
            marker?.tag = Pair("POST", post.id)
        }

        // 4. Add Live Neighbor / Creator Sparks Pins
        nearbyUsers.forEachIndexed { index, user ->
            val lat = user.latitude
            val lng = user.longitude
            val position = LatLng(lat, lng)

            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(user.fullName)
                    .snippet("${user.landmark ?: user.locationName} • In Radar Orbit")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )
            marker?.tag = Pair("USER", user.username)
        }

        // Marker Click Listener
        map.setOnMarkerClickListener { marker ->
            val tag = marker.tag as? Pair<*, *>
            if (tag != null) {
                when (tag.first) {
                    "POST" -> {
                        val postId = tag.second as Long
                        selectedPostMoment = posts.find { it.id == postId }
                        selectedUserMoment = null
                    }
                    "USER" -> {
                        val username = tag.second as String
                        selectedUserMoment = nearbyUsers.find { it.username == username }
                        selectedPostMoment = null
                    }
                }
            }
            marker.showInfoWindow()
            false
        }

        // Map Click Listener (Deselect)
        map.setOnMapClickListener {
            selectedPostMoment = null
            selectedUserMoment = null
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("google_map_moments_screen")) {
        // 1. Google Maps SDK MapView
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { map ->
                        googleMapInstance = map
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14.2f))
                    }
                }
            },
            modifier = Modifier.fillMaxSize().testTag("google_map_canvas")
        )

        // 2. Top Radar Header Overlay: Active Count & Radius Selector
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(LocaliiiyAccentMint)
                            )
                            Text(
                                text = "Live Radar Moments Map",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${posts.size} Moments active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LocaliiiyPrimaryTeal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Proximity Range Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val radiusOptions = listOf(1.0, 3.0, 5.0, 10.0)
                        radiusOptions.forEach { r ->
                            val isSelected = selectedRadiusKm == r
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = if (isSelected) null else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable {
                                        onRadiusChange(r)
                                        googleMapInstance?.let { map ->
                                            val zoom = when (r) {
                                                1.0 -> 15.2f
                                                3.0 -> 14.0f
                                                5.0 -> 13.0f
                                                else -> 12.0f
                                            }
                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoom))
                                        }
                                    }
                                    .testTag("radar_radius_chip_${r.toInt()}km")
                            ) {
                                Text(
                                    text = "${r.toInt()} km",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Floating Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Recenter on User Location
            FloatingActionButton(
                onClick = {
                    googleMapInstance?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(userLatLng, 14.5f)
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = LocaliiiyPrimaryTeal,
                modifier = Modifier
                    .size(46.dp)
                    .testTag("map_recenter_button"),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = "Center on my location",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Map Layer / Style Toggle
            Box {
                FloatingActionButton(
                    onClick = { showMapTypeMenu = !showMapTypeMenu },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("map_layers_button"),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Layers,
                        contentDescription = "Map Style",
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMapTypeMenu,
                    onDismissRequest = { showMapTypeMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Standard Map") },
                        leadingIcon = { Icon(Icons.Default.Map, null) },
                        onClick = {
                            mapType = GoogleMap.MAP_TYPE_NORMAL
                            showMapTypeMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Satellite Imagery") },
                        leadingIcon = { Icon(Icons.Default.Satellite, null) },
                        onClick = {
                            mapType = GoogleMap.MAP_TYPE_SATELLITE
                            showMapTypeMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hybrid") },
                        leadingIcon = { Icon(Icons.Default.Terrain, null) },
                        onClick = {
                            mapType = GoogleMap.MAP_TYPE_HYBRID
                            showMapTypeMenu = false
                        }
                    )
                }
            }

            // Zoom In
            SmallFloatingActionButton(
                onClick = { googleMapInstance?.animateCamera(CameraUpdateFactory.zoomIn()) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
            }

            // Zoom Out
            SmallFloatingActionButton(
                onClick = { googleMapInstance?.animateCamera(CameraUpdateFactory.zoomOut()) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
            }
        }

        // 4. Floating 'Moment' Detail Card at Bottom
        AnimatedVisibility(
            visible = selectedPostMoment != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            selectedPostMoment?.let { post ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_moment_detail_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(post.mediaUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = post.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onPostClick(post) }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(post.userAvatar)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = post.username,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = post.username,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Landmark & Distance Tag
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = LocaliiiyPrimaryTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${post.landmark ?: post.location} • ${LocationHelper.formatDistanceLabel(post.distanceKm)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = LocaliiiyPrimaryTeal,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = post.caption,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Dismiss button
                            IconButton(
                                onClick = { selectedPostMoment = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions: Like & View Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimatedLikeButton(
                                isLiked = post.isLiked,
                                onLikeClick = { onLikePost(post) },
                                likesCount = post.likesCount,
                                showCount = true,
                                symbolSize = 20.sp,
                                touchTargetSize = 36.dp,
                                testTag = "map_post_like_${post.id}"
                            )

                            Button(
                                onClick = { onPostClick(post) },
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Text(
                                    text = "View Moment Details",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Floating Nearby Neighbor Card (if user pin tapped)
        AnimatedVisibility(
            visible = selectedUserMoment != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            selectedUserMoment?.let { user ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(user.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = user.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${user.landmark ?: user.locationName} • ${LocationHelper.formatDistanceLabel(user.distanceKm)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = LocaliiiyPrimaryTeal,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = user.bio,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = { onUserProfileClick(user.username) },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Profile", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Lifecycle-aware MapView remember helper.
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    return mapView
}

private fun getDeterministicOffsetLat(id: Long, index: Int): Double {
    val spread = (index % 5 - 2) * 0.0032
    return spread + ((id % 7 - 3) * 0.0011)
}

private fun getDeterministicOffsetLng(id: Long, index: Int): Double {
    val spread = ((index * 2) % 5 - 2) * 0.0035
    return spread + ((id % 5 - 2) * 0.0013)
}
