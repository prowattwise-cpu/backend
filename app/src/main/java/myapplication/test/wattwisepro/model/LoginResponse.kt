package myapplication.test.wattwisepro.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserData? = null
)

data class UserData(
    val userId: Int,
    val name: String,
    val email: String,
    val address: String?,
    val householdType: String?,
    val city: String?,
    val subdivision: String?,
    val phoneNumber: String?
)
