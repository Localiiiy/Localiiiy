import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

# Add state variables
state_vars = '''
    val haptic = LocalHapticFeedback.current
    var isCinemaMode by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1f) }
    var showCaptionsSheet by remember { mutableStateOf(false) }
    var showSoundtrackSheet by remember { mutableStateOf(false) }
    
    // Modify video progress tween based on playbackSpeed (mock behavior)
'''
content = content.replace('    val heartScale = remember { Animatable(1f) }', state_vars + '    val heartScale = remember { Animatable(1f) }')

# Update handleDoubleTap to include haptic
handle_double_tap = '''
    fun handleDoubleTap() {
        showBigHeart = true
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
'''
content = content.replace('    fun handleDoubleTap() {\n        showBigHeart = true', handle_double_tap)

# Update pointerInput block
pointer_input_old = '''            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { handleDoubleTap() },
                    onTap = { togglePlayPause() }
                )
            }'''
pointer_input_new = '''            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { handleDoubleTap() },
                    onTap = { 
                        if (isCinemaMode) isCinemaMode = false else togglePlayPause() 
                    },
                    onPress = {
                        playbackSpeed = 2f
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        tryAwaitRelease()
                        playbackSpeed = 1f
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom > 1.2f && !isCinemaMode) {
                        isCinemaMode = true
                    }
                }
            }'''
content = content.replace(pointer_input_old, pointer_input_new)

# Wrap overlays with AnimatedVisibility(visible = !isCinemaMode)
# Actually, I can apply alpha modifier to the gradient overlay and the other overlays.
# But it's easier to just wrap them.
# The gradient overlay:
gradient_overlay_old = '''        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )'''
gradient_overlay_new = '''        // Gradient overlay
        AnimatedVisibility(
            visible = !isCinemaMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )
        }'''
content = content.replace(gradient_overlay_old, gradient_overlay_new)

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
