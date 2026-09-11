            // AI Content Switch
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoFixHigh,
                    contentDescription = "AI Content",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI-Generated Content", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Label this post as created with AI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isAiContent,
                    onCheckedChange = { isAiContent = it }
                )
            }
