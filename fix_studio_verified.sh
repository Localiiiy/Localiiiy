sed -i '/text = video.creatorFullName,/,/modifier = Modifier.size(14.dp)/c\
                                    Text(\
                                        text = video.creatorFullName,\
                                        fontWeight = FontWeight.Bold,\
                                        fontSize = 14.sp\
                                    )\
                                    if (video.isCreatorVerified) {\
                                        Icon(\
                                            imageVector = Icons.Default.CheckCircle,\
                                            contentDescription = "Verified Creator",\
                                            tint = MaterialTheme.colorScheme.primary,\
                                            modifier = Modifier.size(14.dp)\
                                        )\
                                    }' app/src/main/java/com/example/ui/screens/StudioScreen.kt
