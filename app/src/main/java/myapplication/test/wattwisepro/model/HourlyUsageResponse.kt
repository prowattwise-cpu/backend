package myapplication.test.wattwisepro.model

data class HourlyUsageItem(
    val hour: Int,
    val wattage: Double,
    val max_power: Double,
    val min_power: Double,
    val record_count: Int
)

data class HourlyUsageResponse(
    val success: Boolean,
    val message: String,
    val data: List<HourlyUsageItem>,
    val date: String
)

