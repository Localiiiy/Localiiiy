            if (createMode != CreateMode.MARKETPLACE) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Include precise location", fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Switch(
                        checked = !locationOptional,
                        onCheckedChange = { locationOptional = !it },
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
