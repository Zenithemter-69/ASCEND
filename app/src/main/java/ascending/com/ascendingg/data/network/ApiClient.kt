package ascending.com.ascendingg.data.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ascending.com.ascendingg.data.utils.PreferencesManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApiClient {

    private const val BASE_URL = "https://api.openai.com/v1/"
    private val gson = GsonBuilder().create()

    private fun getRetrofit(context: Context): Retrofit {
        val prefs = PreferencesManager(context)
        val apiKey = prefs.getApiKey() ?: throw IllegalStateException("OpenAI API key not set")

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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    suspend fun getAiResponse(context: Context, message: String): String {
        val retrofit = getRetrofit(context)
        val service = retrofit.create(AiApiService::class.java)

        val requestBody = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                ChatMessage("system", "You are ASCEND AI Coach for the Elite Looksmaxxing Guide (Age 17). Provide concise, actionable advice on habits, workouts, study, and progress tracking. Keep responses under 300 words."),
                ChatMessage("user", message)
            ),
            max_tokens = 500,
            temperature = 0.7
        )

        return suspendCoroutine { continuation ->
            // Execute synchronously within coroutine context
            try {
                val response = service.chatCompletion(requestBody).execute()
                if (response.isSuccessful && response.body() != null) {
                    val content = response.body()!!.choices.firstOrNull()?.message?.content ?: "No response"
                    continuation.resume(content)
                } else {
                    continuation.resume("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                continuation.resume("Network failure: ${e.message}")
            }
        }
    }

    interface AiApiService {
        @retrofit2.http.POST("chat/completions")
        suspend fun chatCompletion(
            @retrofit2.http.Body body: ChatRequest
        ): ChatResponse
    }

    data class ChatRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val max_tokens: Int,
        val temperature: Double
    )

    data class ChatMessage(
        val role: String,
        val content: String
    )

    data class ChatResponse(
        val choices: List<Choice>
    )

    data class Choice(
        val message: ChatMessage,
        val finish_reason: String
    )
}