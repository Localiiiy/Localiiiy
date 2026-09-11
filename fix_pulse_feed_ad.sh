sed -i 's/onImpression = { onAdImpression(ad.id) },/currentCurrency = currentCurrency,\
                        currentLanguage = currentLanguage,\
                        onAdImpression = { onAdImpression(ad.id) },/' app/src/main/java/com/example/ui/components/PulseFeedComponent.kt
sed -i 's/onClick = { onAdClick(ad.id) }/onAdClick = { onAdClick(ad.id) }/' app/src/main/java/com/example/ui/components/PulseFeedComponent.kt
