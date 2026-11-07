package com.mistersam.app.data

import java.time.LocalDate

enum class Level(val code: String, val ar: String, val desc: String) {
    A1("A1", "مبتدئ", "أبدأ من الصفر تقريبًا"),
    A2("A2", "أساسي", "أفهم الجمل البسيطة وأريد التحدث أكثر"),
    B1("B1", "متوسط", "أتحدث في المواضيع اليومية وأريد الطلاقة")
}

data class Word(val en: String, val ar: String, val ex: String)

/** سؤال اختيار من متعدد. answer = فهرس الإجابة الصحيحة. */
data class Quiz(val q: String, val options: List<String>, val answer: Int)

data class Grammar(
    val title: String,
    val explain: String,
    val examples: List<String>,
    val quiz: List<Quiz>
)

data class Listening(val text: String, val quiz: Quiz)

data class Phrase(val en: String, val ar: String)

data class Lesson(
    val theme: String,
    val words: List<Word>,
    val grammar: Grammar,
    val listening: Listening,
    val speaking: List<Phrase>,
    val topic: String,
    /** أسئلة مستر سام الجاهزة للحوار عند عدم وجود مفتاح الذكاء الاصطناعي */
    val script: List<String>
)

/** فقرات الدرس الست. كل فقرة 10 دقائق = ساعة كاملة. */
enum class Seg(val title: String, val emoji: String, val hint: String) {
    WARMUP("إحماء ومراجعة", "🔥", "نراجع ما تعلمناه سابقًا"),
    VOCAB("مفردات اليوم", "📚", "٨ كلمات جديدة بالنطق والمثال"),
    GRAMMAR("قاعدة اليوم", "🧩", "شرح مبسّط وتمارين"),
    LISTEN("استماع وفهم", "🎧", "نص يُقرأ لك وسؤال عليه"),
    SPEAK("نطق", "🎤", "كرّر الجمل وسأقيّم نطقك"),
    CHAT("حوار مع المعلم", "💬", "محادثة حقيقية مع مستر سام")
}

data class Progress(
    val loaded: Boolean = false,
    val onboarded: Boolean = false,
    val name: String = "",
    val level: Level = Level.A1,
    val dayIndex: Int = 0,
    val streak: Int = 0,
    val lastDone: String = "",
    val xp: Int = 0,
    val totalMinutes: Int = 0,
    val apiKey: String = "",
    val reminderOn: Boolean = false,
    val reminderHour: Int = 20,
    val learned: Set<String> = emptySet()
) {
    val doneToday: Boolean get() = lastDone == LocalDate.now().toString()
}
