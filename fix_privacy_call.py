import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Add import for new screens if not exist
if 'import com.example.ui.screens.BlockedUsersScreen' not in content:
    content = content.replace('import com.example.ui.screens.PrivacySettingsScreen', 
    'import com.example.ui.screens.PrivacySettingsScreen\nimport com.example.ui.screens.BlockedUsersScreen\nimport com.example.ui.screens.ConnectionsManagerScreen')

# Find the block for PrivacySettingsScreen
# From `if (showPrivacySettings) {` down to `}`
# I will use regex to replace it
pattern = re.compile(r'    if \(showPrivacySettings\) \{.*?    \}', re.DOTALL)
match = pattern.search(content)

new_code = '''    if (showPrivacySettings) {
        var showBlockedUsers by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        var showConnectionsManager by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.closePrivacySettings() },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            if (showBlockedUsers) {
                BlockedUsersScreen(onNavigateBack = { showBlockedUsers = false })
            } else if (showConnectionsManager) {
                ConnectionsManagerScreen(onNavigateBack = { showConnectionsManager = false })
            } else {
                PrivacySettingsScreen(
                    onNavigateBack = { viewModel.closePrivacySettings() },
                    onNavigateToBlockedUsers = { showBlockedUsers = true },
                    onNavigateToConnections = { showConnectionsManager = true },
                    onDeleteAccount = { viewModel.deleteAccountAndPurgeData() }
                )
            }
        }
    }'''

if match:
    content = content.replace(match.group(0), new_code)
else:
    print("Could not match the privacy settings block")

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
