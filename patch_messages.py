import os

fpath = "app/src/main/java/com/example/ui/components/DirectMessagesBottomSheet.kt"
with open(fpath, "r") as f:
    content = f.read()

old_lazy = """                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(conversations, key = { it.conversationId }) { conv ->"""

new_lazy = """                val expiryHours = if (userProfile.isPremiumSubscribed) 48 else 3
                val currentTime = System.currentTimeMillis()
                val visibleConversations = conversations.filter { conv ->
                    val ageHours = (currentTime - conv.timestamp) / (1000.0 * 60 * 60)
                    ageHours <= expiryHours
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(visibleConversations, key = { it.conversationId }) { conv ->"""

content = content.replace(old_lazy, new_lazy)

with open(fpath, "w") as f:
    f.write(content)
