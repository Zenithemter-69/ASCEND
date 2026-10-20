package ascending.com.ascendingg.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ascending.com.ascendingg.data.utils.PreferencesManager

object ApiClient {

    private const val BASE_URL = "https://api.openai.com/v1/"

    private fun getRetrofit(context: android.content.Context): Retrofit {
        val prefs = PreferencesManager(context)
        val apiKey = prefs.getApiKey() ?: throw IllegalStateException("OpenAI API key not set")

        val logging = HttpLoggingInterceptor { println(it) }
        logging.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    fun getAiResponse(context: android.content.Context, message: String): String {
        val retrofit = getRetrofit(context)
        val service = retrofit.create(AiApiService::class.java)

        return suspendCoroutine { continuation ->
            service.chatCompletion(message).enqueue(object : retrofit2.Callback<String> {
                override fun onResponse(response: retrofit2.Response<String>) {
                    if (response.isSuccessful && response.body() != null) {
                        continuation.resume(response.body()!!)
                    } else {
                        continuation.resume("Error: ${response.message()}")
                    }
                }

                override fun onFailure(failure: retrofit2.Failure) {
                    continuation.resume("Network failure: ${failure.message}")
                }
            })
        }
    }

    interface AiApiService {
        @retrofit2.http.POST("chat/completions")
        suspend fun chatCompletion(
            @retrofit2.http.Body body: String
        ): String
    }
}