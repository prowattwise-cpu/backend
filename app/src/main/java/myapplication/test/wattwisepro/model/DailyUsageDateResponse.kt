package myapplication.test.wattwisepro.model

data class DailyUsageDateResponse(
    val success: Boolean,
    val message: String,
    val data: DailyUsageDate? = null
)

data class DailyUsageDate(
    val id: Int?,
    val date: String, // Format: YYYY-MM-DD
    val total_energy: Double,
    val total_power: Double?,
    val peak_power: Double?,
    val average_power: Double?
)

