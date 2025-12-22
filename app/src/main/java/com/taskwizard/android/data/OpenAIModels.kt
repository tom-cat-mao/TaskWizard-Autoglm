package com.taskwizard.android.data

data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 1024,
    val temperature: Double = 0.5,
    val top_p: Double = 0.9
)

data class Message(
    val role: String,
    val content: Any // Can be String or List<ContentPart>
)

data class ContentPart(
    val type: String, // "text" or "image_url"
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String // "data:image/jpeg;base64,..."
)

data class OpenAIResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val index: Int,
    val message: MessageResponse,
    val finish_reason: String?
)

data class MessageResponse(
    val role: String,
    val content: String // Response content is usually string
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
