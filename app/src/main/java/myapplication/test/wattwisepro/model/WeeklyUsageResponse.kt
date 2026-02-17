package myapplication.test.wattwisepro.model

data class WeeklyUsageResponse(
    val success: Boolean,
    val message: String,
    val data: List<WeeklyUsage>? = null
)

data class WeeklyUsage(
    val id: Int?,
    val week_start_date: String, // Format: YYYY-MM-DD
    val week_end_date: String, // Format: YYYY-MM-DD
    val week_number: Int,
    val year: Int,
    val total_energy: Double, // This is what we'll display (kWh)
    val total_power: Double?,
    val peak_power: Double?,
    val average_power: Double?,
    val created_at: String?,
    val updated_at: String?
)

