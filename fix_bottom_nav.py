import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

spaces_nav = '''
            // Spaces Tab
            LocaliiiyNavItem(
                icon = if (currentTab == MainNavigationTab.SPACES) Icons.Default.Groups else Icons.Outlined.Groups,
                label = "Spaces",
                isSelected = currentTab == MainNavigationTab.SPACES,
                onClick = { onTabSelected(MainNavigationTab.SPACES) },
                testTag = "nav_tab_spaces"
            )
'''

content = content.replace(
    '            // Profile Tab',
    spaces_nav + '\n            // Profile Tab'
)

# Also ensure Icons.Default.Groups is imported.
import_groups = '''import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Groups'''

if 'import androidx.compose.material.icons.filled.Groups' not in content:
    content = content.replace(
        'import androidx.compose.material.icons.filled.PlayCircle',
        'import androidx.compose.material.icons.filled.PlayCircle\n' + import_groups
    )

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
