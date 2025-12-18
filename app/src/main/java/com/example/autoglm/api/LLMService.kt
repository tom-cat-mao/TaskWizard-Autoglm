package com.example.autoglm.api

import com.example.autoglm.data.OpenAIRequest
import com.example.autoglm.data.OpenAIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LLMService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: OpenAIRequest): Response<OpenAIResponse>
}
