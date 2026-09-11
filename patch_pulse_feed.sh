sed -i '/val firestoreService = remember { FirestorePulseService() }/,/allMerged\n    }/c\
    val combinedPosts = posts\
' app/src/main/java/com/example/ui/components/PulseFeedComponent.kt
