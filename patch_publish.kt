                Button(
                    onClick = {
                        val rawPrice = priceText.toDoubleOrNull() ?: 0.0
                        val parsedPrice = CurrencyHelper.convertToUSD(rawPrice, currentCurrency)
                        val reachText = "\n[Broadcast Reach: ${blastRadiusLabels[blastRadiusIndex.toInt()]}]"
                        val finalDesc = description.ifBlank { "Available for local pickup near $landmark on Localiiiy." } + reachText
                        when (creationFormat) {
                            SellCreationFormat.CATALOG_ITEM -> {
                                onPublishListing(
                                    title,
                                    finalDesc,
                                    parsedPrice,
                                    selectedCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    selectedDelivery,
                                    "Seattle",
                                    landmark
                                )
                            }
                            SellCreationFormat.BUY_SELL_POST -> {
                                onPublishPost(
                                    title,
                                    finalDesc,
                                    parsedPrice,
                                    selectedCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    selectedDelivery,
                                    "Seattle",
                                    landmark
                                )
                            }
                            SellCreationFormat.SHOWCASE_CLIP -> {
                                onPublishClip(
                                    title,
                                    finalDesc,
                                    parsedPrice,
                                    selectedCategory,
                                    selectedCondition,
                                    selectedPhotoUrl,
                                    soundTrack,
                                    "Seattle",
                                    landmark
                                )
                            }
                        }
                    },
