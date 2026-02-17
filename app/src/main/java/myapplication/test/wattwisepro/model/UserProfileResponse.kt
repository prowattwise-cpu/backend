package myapplication.test.wattwisepro.model

data class UserProfileResponse(
    val success: Boolean,
    val message: String?,
    val data: UserProfile?
)

data class UserProfile(
    val UserID: Int,
    val Name: String?,
    val Email: String?,
    val Address: String?,
    val HouseholdType: String?,
    val City: String?,
    val Subdivision: String?,
    val PhoneNumber: String?
)

