package myapplication.test.wattwisepro.model

data class MonthlyUsageCurrentResponse(
    val success: Boolean,
    val message: String,
    val data: MonthlyUsageCurrent? = null
)

data class MonthlyUsageCurrent(
    val id: Int?,
    val month: Int,
    val year: Int,
    val month_name: String?,
    val total_energy: Double,
    val total_power: Double?,
    val peak_power: Double?,
    val average_power: Double?
)

