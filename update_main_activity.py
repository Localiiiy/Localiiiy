import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Add SpacesScreen import
content = content.replace(
    'import com.example.ui.screens.ProfileScreen',
    'import com.example.ui.screens.ProfileScreen\nimport com.example.ui.screens.SpacesScreen'
)

# Insert SPACES in when block
spaces_when = '''
                MainNavigationTab.SPACES -> {
                    SpacesScreen(
                        currentUserId = viewModel.currentUserHandle
                    )
                }
'''
content = content.replace(
    '                MainNavigationTab.PROFILE -> {',
    spaces_when + '                MainNavigationTab.PROFILE -> {'
)

# Insert SPACES in Bottom Navigation
spaces_nav = '''
            NavigationBarItem(
                icon = if (currentTab == MainNavigationTab.SPACES) Icons.Default.Groups else Icons.Outlined.Groups,
                isSelected = currentTab == MainNavigationTab.SPACES,
                onClick = { onTabSelected(MainNavigationTab.SPACES) },
                label = "Spaces"
            )
'''

# Note: `Icons.Outlined.Groups` might need an import, or `Icons.Default.Groups` might need an import. Let's make sure.
# In `MainActivity.kt`, wait, the bottom navigation uses a custom row or NavigationBar.
