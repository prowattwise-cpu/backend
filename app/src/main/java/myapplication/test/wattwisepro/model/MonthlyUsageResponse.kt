package myapplication.test.wattwisepro.model

data class MonthlyUsageResponse(
    val success: Boolean,
    val message: String,
    val data: List<MonthlyUsage>? = null
)

data class MonthlyUsage(
    val id: Int?,
    val month: Int, // 1-12
    val year: Int,
    val month_name: String?,
    val total_energy: Double, // This is what we'll display (kWh)
    val total_power: Double?,
    val peak_power: Double?,
    val average_power: Double?,
    val created_at: String?,
    val updated_at: String?
)

