package myapplication.test.wattwisepro.model

data class RawUsageLatestResponse(
    val success: Boolean,
    val message: String,
    val data: RawUsageLatest? = null
)

data class RawUsageLatest(
    val voltage: String,
    val current: String,
    val power: String,
    val energy: String,
    val timestamp: String?
)

