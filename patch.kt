        // Sell an Item Dialog / Sheet
        if (showSellDialog) {
            MarketSellMultiDialog(
                onDismiss = onCloseSellDialog,
                onPublishListing = onPublishItem,
                onPublishPost = onPublishBuySellPost,
                onPublishClip = onPublishBuySellClip,
                currentCurrency = currentCurrency
            )
        }
