package com.mistersam.app.ai

import com.mistersam.app.data.Level
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * المعلم الذكي (اختياري): محادثة حقيقية عبر Claude API.
 * بدون مفتاح يعمل التطبيق بأسئلة جاهزة من محتوى الدرس.
 */
object Teacher {

    data class Msg(val fromUser: Boolean, val text: String)

    private fun system(level: Level, topic: String) = """
        You are Mr. Sam, a warm, patient English teacher talking with an Arabic-speaking learner.
        Learner level: ${level.code}. Today's conversation topic: "$topic".
        Rules:
        - Reply in simple English that fits the level. Keep replies to 1-3 short sentences.
        - Always end with one easy follow-up question that keeps the conversation going.
        - If the learner makes a mistake, first give a short correction like: Better: "I went to school." then continue naturally.
        - For A1/A2 learners you may add a very short Arabic hint in parentheses when a word is hard.
        - Be encouraging. Never write long lectures or lists.
    """.trimIndent()

    suspend fun reply(
        apiKey: String,
        level: Level,
        topic: String,
        history: List<Msg>
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val messages = JSONArray().apply {
                put(JSONObject().put("role", "user").put("content", "Hi Mr. Sam, I'm ready for my lesson."))
                history.forEach {
                    put(
                        JSONObject()
                            .put("role", if (it.fromUser) "user" else "assistant")
                            .put("content", it.text)
                    )
                }
            }
            val body = JSONObject()
                .put("model", "claude-sonnet-5-5")
                .put("max_tokens", 300)
                .put("system", system(level, topic))
                .put("messages", messages)

            val conn = (URL("https://api.anthropic.com/v1/messages").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 45_000
                doOutput = true
                setRequestProperty("content-type", "application/json")
                setRequestProperty("x-api-key", apiKey)
                setRequestProperty("anthropic-version", "2023-06-01")
            }
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().use { it.readText() }
            if (code !in 200..299) error("HTTP $code")
            JSONObject(text).getJSONArray("content").getJSONObject(0).getString("text").trim()
        }
    }

    /** رد احتياطي بدون إنترنت: تشجيع + السؤال التالي من النص الجاهز. */
    fun scripted(script: List<String>, turn: Int): String {
        val praise = listOf("Great!", "Nice answer!", "Well done!", "Good job!")
        return praise[turn % praise.size] + " " + script.getOrElse(turn) { "Tell me more about that!" }
    }
}
