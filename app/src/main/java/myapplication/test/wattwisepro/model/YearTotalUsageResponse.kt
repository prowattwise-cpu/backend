package myapplication.test.wattwisepro.model

data class YearTotalUsageResponse(
    val success: Boolean,
    val message: String,
    val data: YearTotalUsage? = null
)

data class YearTotalUsage(
    val year: Int,
    val total_energy: Double,
    val total_power: Double?,
    val peak_power: Double?,
    val average_power: Double?,
    val months_count: Int
)

