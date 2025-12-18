package com.example.autoglm.core

import java.util.regex.Pattern
import com.example.autoglm.data.Action

object ResponseParser {

    data class ParseResult(
        val think: String?,
        val action: Action?
    )

    fun parse(content: String): ParseResult {
        // 1. Extract Think
        val think = extractTag(content, "think")
        
        // 2. Extract Answer
        val answer = extractTag(content, "answer") ?: content // Fallback to raw content if no tag
        
        // 3. Parse Action String (e.g., do(action="Tap", element=[123,456]))
        val action = parseActionString(answer)
        
        return ParseResult(think, action)
    }

    private fun extractTag(content: String, tag: String): String? {
        val pattern = Pattern.compile("<$tag>(.*?)</$tag>", Pattern.DOTALL)
        val matcher = pattern.matcher(content)
        return if (matcher.find()) {
            matcher.group(1)?.trim()
        } else {
            null
        }
    }

    private fun parseActionString(actionStr: String): Action? {
        // Handle finish(message="...")
        if (actionStr.startsWith("finish")) {
            val msg = extractParam(actionStr, "message")
            return Action("finish", null, msg)
        }

        // Handle do(...)
        if (actionStr.startsWith("do")) {
            val actionType = extractParam(actionStr, "action")
            
            // Extract coordinates: element=[x,y] or start=[x,y], end=[x,y]
            val element = extractCoordinates(actionStr, "element")
            
            // For Swipe
            val start = extractCoordinates(actionStr, "start")
            val end = extractCoordinates(actionStr, "end")
            
            // Extract text/message
            val text = extractParam(actionStr, "text") ?: extractParam(actionStr, "message") ?: extractParam(actionStr, "app")

            // Combine for Action object
            // If it's swipe, location needs 4 coords
            val location = if (start != null && end != null) {
                start + end
            } else {
                element
            }

            return Action(actionType, location, text)
        }

        return null
    }

    // Helper to extract param="value"
    private fun extractParam(text: String, key: String): String? {
        // Matches key="value" or key='value'
        val pattern = Pattern.compile("$key=[\"'](.*?)[\"']")
        val matcher = pattern.matcher(text)
        if (matcher.find()) return matcher.group(1)
        return null
    }

    // Helper to extract key=[x,y]
    private fun extractCoordinates(text: String, key: String): List<Int>? {
        val pattern = Pattern.compile("$key=\\[(\\d+),\\s*(\\d+)\\]")
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            try {
                val x = matcher.group(1)?.toInt()
                val y = matcher.group(2)?.toInt()
                if (x != null && y != null) return listOf(x, y)
            } catch (e: Exception) { }
        }
        return null
    }
}
