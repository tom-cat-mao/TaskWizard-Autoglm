package com.taskwizard.android.api

import com.taskwizard.android.data.OpenAIRequest
import com.taskwizard.android.data.OpenAIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LLMService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: OpenAIRequest): Response<OpenAIResponse>
}
