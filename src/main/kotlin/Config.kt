package org.example

data class ColorWeights(
    val redWeight: Int,
    val greenWeight: Int,
    val blueWeight: Int
) {
    val totalWeight get() = redWeight + greenWeight + blueWeight

    init {
        require(totalWeight == 256) { "Color weights must sum to 256" }
        require(redWeight > 0 && greenWeight > 0 && blueWeight > 0) { "All color weights must be greater than 0" }
    }
}

data class Config(
    val blockHeight: Int,
    val maxImageSizeForSequentialProcessing: Long,
    val characterRamp: String,
    val fontAspectRatio: Double,
    val colorWeights: ColorWeights,
    val background: Int,
) {
    companion object {
        fun default(): Config {
            return Config(
                32,
                // 1MB
                1_000_000L,
                " .:-=+*#%@",
                0.5,
                // Rec. 709
                ColorWeights(54, 183, 19),
                // Black
                0xFF000000.toInt(),
            )
        }
    }

    init {
        require(blockHeight > 0) { "Processing block height must be greater than 0" }
        require(maxImageSizeForSequentialProcessing > 0) { "Maximum image size for sequential processing must be greater than 0" }
        require(characterRamp.isNotEmpty()) { "Character ramp must not be empty" }
        require(fontAspectRatio > 0.0) { "Font aspect ratio must be positive" }
    }
}
