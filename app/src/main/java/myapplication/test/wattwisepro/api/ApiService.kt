package myapplication.test.wattwisepro.api

import myapplication.test.wattwisepro.model.LoginRequest
import myapplication.test.wattwisepro.model.LoginResponse
import myapplication.test.wattwisepro.model.SignUpRequest
import myapplication.test.wattwisepro.model.SignUpResponse
import myapplication.test.wattwisepro.model.UserProfileResponse
import myapplication.test.wattwisepro.model.DailyUsageResponse
import myapplication.test.wattwisepro.model.WeeklyUsageResponse
import myapplication.test.wattwisepro.model.MonthlyUsageResponse
import myapplication.test.wattwisepro.model.DailyUsageDateResponse
import myapplication.test.wattwisepro.model.MonthlyUsageCurrentResponse
import myapplication.test.wattwisepro.model.YearTotalUsageResponse
import myapplication.test.wattwisepro.model.RawUsageLatestResponse
import myapplication.test.wattwisepro.model.HourlyUsageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/signup")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>
    
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    
    @GET("api/user/{userId}")
    fun getUserProfile(@Path("userId") userId: Int): Call<UserProfileResponse>
    
    @GET("api/daily-usage/week")
    fun getDailyUsageWeek(): Call<DailyUsageResponse>
    
    @GET("api/weekly-usage/recent")
    fun getWeeklyUsageRecent(): Call<WeeklyUsageResponse>
    
    @GET("api/monthly-usage/year")
    fun getMonthlyUsageYear(): Call<MonthlyUsageResponse>
    
    @GET("api/daily-usage/date/{date}")
    fun getDailyUsageByDate(@Path("date") date: String): Call<DailyUsageDateResponse>
    
    @GET("api/monthly-usage/current")
    fun getMonthlyUsageCurrent(): Call<MonthlyUsageCurrentResponse>
    
    @GET("api/monthly-usage/year-total")
    fun getYearTotalUsage(): Call<YearTotalUsageResponse>
    
    @GET("api/raw-usage/latest")
    fun getLatestRawUsage(): Call<RawUsageLatestResponse>
    
    @GET("api/daily-usage/hourly/{date}")
    fun getHourlyUsage(@Path("date") date: String): Call<HourlyUsageResponse>
}


