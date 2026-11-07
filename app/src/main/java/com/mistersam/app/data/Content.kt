package com.mistersam.app.data

private fun w(en: String, ar: String, ex: String) = Word(en, ar, ex)
private fun q(text: String, answer: Int, vararg options: String) = Quiz(text, options.toList(), answer)
private fun p(en: String, ar: String) = Phrase(en, ar)

/**
 * محتوى الدروس المضمّن (يعمل بدون إنترنت).
 * أضف دروسًا جديدة بنفس القالب؛ التطبيق يمرّ عليها بالترتيب ويكرّرها عند النهاية.
 */
object Content {

    private val a1 = listOf(
        Lesson(
            theme = "التعارف",
            words = listOf(
                w("hello", "مرحبًا", "Hello, my name is Sam."),
                w("name", "اسم", "What is your name?"),
                w("friend", "صديق", "He is my friend."),
                w("teacher", "معلّم", "She is a teacher."),
                w("student", "طالب", "I am a student."),
                w("country", "بلد", "My country is beautiful."),
                w("from", "من (الأصل)", "I am from Yemen."),
                w("nice", "لطيف", "Nice to meet you.")
            ),
            grammar = Grammar(
                title = "am / is / are",
                explain = "نستخدم am مع I، و is مع he / she / it، و are مع you / we / they.",
                examples = listOf("I am a student.", "She is a teacher.", "They are friends."),
                quiz = listOf(
                    q("She ___ my friend.", 0, "is", "am", "are"),
                    q("I ___ from Yemen.", 1, "is", "am", "are"),
                    q("We ___ students.", 2, "is", "am", "are")
                )
            ),
            listening = Listening(
                "Hello! My name is Sara. I am from Egypt. I am a teacher. Nice to meet you!",
                q("Where is Sara from?", 0, "Egypt", "Yemen", "Sudan")
            ),
            speaking = listOf(
                p("Hello, my name is Sam.", "مرحبًا، اسمي سام."),
                p("Nice to meet you.", "تشرفت بمعرفتك."),
                p("Where are you from?", "من أين أنت؟"),
                p("I am a student.", "أنا طالب.")
            ),
            topic = "Introducing yourself",
            script = listOf(
                "Hi! I'm Mr. Sam. What's your name?",
                "Nice to meet you! Where are you from?",
                "That sounds great! Are you a student or do you work?",
                "Wonderful! Why do you want to learn English?"
            )
        ),
        Lesson(
            theme = "العائلة",
            words = listOf(
                w("mother", "أم", "My mother is kind."),
                w("father", "أب", "My father is a doctor."),
                w("brother", "أخ", "I have one brother."),
                w("sister", "أخت", "She is my sister."),
                w("family", "عائلة", "I love my family."),
                w("son", "ابن", "He is her son."),
                w("daughter", "ابنة", "They have a daughter."),
                w("parents", "الوالدان", "My parents live in Aden.")
            ),
            grammar = Grammar(
                title = "my / your / his / her",
                explain = "صفات الملكية تأتي قبل الاسم: my (لي)، your (لك)، his (له)، her (لها).",
                examples = listOf("This is my mother.", "His name is Ali.", "Her sister is a doctor."),
                quiz = listOf(
                    q("Sara is a girl. This is ___ book.", 0, "her", "his", "my"),
                    q("Ali is a boy. ___ name is Ali.", 1, "Her", "His", "Your"),
                    q("You have a car. It is ___ car.", 2, "my", "her", "your")
                )
            ),
            listening = Listening(
                "I have a big family. I have two brothers and one sister. My father is a doctor. My mother is a teacher.",
                q("How many brothers does he have?", 1, "One", "Two", "Three")
            ),
            speaking = listOf(
                p("This is my family.", "هذه عائلتي."),
                p("I have two brothers.", "لدي أخوان."),
                p("My father is a doctor.", "والدي طبيب."),
                p("She is my sister.", "هي أختي.")
            ),
            topic = "Talking about your family",
            script = listOf(
                "Let's talk about family! Do you have any brothers or sisters?",
                "Interesting! What does your father or mother do?",
                "Who do you spend the most time with in your family?",
                "Lovely! Can you tell me one nice thing about your family?"
            )
        ),
        Lesson(
            theme = "الطعام والمشروبات",
            words = listOf(
                w("water", "ماء", "I want some water."),
                w("bread", "خبز", "We eat bread every day."),
                w("rice", "أرز", "I like rice and chicken."),
                w("chicken", "دجاج", "The chicken is delicious."),
                w("apple", "تفاحة", "She eats an apple."),
                w("tea", "شاي", "Do you like tea?"),
                w("hungry", "جائع", "I am hungry."),
                w("thirsty", "عطشان", "He is thirsty.")
            ),
            grammar = Grammar(
                title = "Do you like ...?  /  I don't like ...",
                explain = "للسؤال: Do you like ...؟ وللإجابة: Yes, I do. أو No, I don't. وللنفي: I don't like.",
                examples = listOf("Do you like tea?", "Yes, I do.", "I don't like fish."),
                quiz = listOf(
                    q("___ you like rice?", 0, "Do", "Does", "Are"),
                    q("Do you like bread? Yes, I ___.", 1, "am", "do", "like"),
                    q("I ___ like fish. (لا أحب)", 0, "don't", "not", "no")
                )
            ),
            listening = Listening(
                "Hello! I am hungry. I want bread and chicken. I also want water, but I don't want tea.",
                q("What does he NOT want?", 1, "Water", "Tea", "Bread")
            ),
            speaking = listOf(
                p("I am hungry.", "أنا جائع."),
                p("I like rice and chicken.", "أحب الأرز والدجاج."),
                p("Do you like tea?", "هل تحب الشاي؟"),
                p("Can I have some water, please?", "هل يمكنني الحصول على بعض الماء من فضلك؟")
            ),
            topic = "Favorite food and drinks",
            script = listOf(
                "I'm so hungry! What is your favorite food?",
                "Yummy! Do you like tea or coffee?",
                "What do you usually eat for breakfast?",
                "Great! What food do you not like?"
            )
        ),
        Lesson(
            theme = "الروتين اليومي",
            words = listOf(
                w("wake up", "يستيقظ", "I wake up at six."),
                w("breakfast", "فطور", "I eat breakfast at seven."),
                w("work", "يعمل / عمل", "She works in a school."),
                w("study", "يدرس", "He studies English."),
                w("go", "يذهب", "I go to work by bus."),
                w("sleep", "ينام", "I sleep at ten."),
                w("morning", "صباح", "Good morning!"),
                w("night", "ليل", "Good night!")
            ),
            grammar = Grammar(
                title = "Present Simple (المضارع البسيط)",
                explain = "مع I / you / we / they نستخدم الفعل كما هو. ومع he / she / it نضيف s (أو es) للفعل.",
                examples = listOf("I wake up at six.", "She works in a school.", "He studies English."),
                quiz = listOf(
                    q("He ___ English every day.", 1, "study", "studies", "studying"),
                    q("I ___ breakfast at seven.", 0, "eat", "eats", "eating"),
                    q("She ___ to school.", 1, "go", "goes", "going")
                )
            ),
            listening = Listening(
                "I wake up at six o'clock. I eat breakfast at seven. I go to work at eight. In the evening I study English. I sleep at ten.",
                q("When does he study English?", 1, "In the morning", "In the evening", "At night")
            ),
            speaking = listOf(
                p("I wake up at six.", "أستيقظ في السادسة."),
                p("I eat breakfast at seven.", "أتناول الفطور في السابعة."),
                p("She goes to work.", "هي تذهب إلى العمل."),
                p("I study English every day.", "أدرس الإنجليزية كل يوم.")
            ),
            topic = "Your daily routine",
            script = listOf(
                "Tell me about your day. What time do you wake up?",
                "Good! What do you do in the morning?",
                "And what do you do in the evening?",
                "What time do you usually go to sleep?"
            )
        ),
        Lesson(
            theme = "الوقت والأيام",
            words = listOf(
                w("time", "وقت", "What time is it?"),
                w("hour", "ساعة", "The class is one hour."),
                w("minute", "دقيقة", "Wait a minute, please."),
                w("today", "اليوم", "Today is Saturday."),
                w("tomorrow", "غدًا", "See you tomorrow."),
                w("yesterday", "أمس", "I was busy yesterday."),
                w("early", "مبكرًا", "I am early today."),
                w("late", "متأخرًا", "Sorry, I am late.")
            ),
            grammar = Grammar(
                title = "What time is it?",
                explain = "نسأل عن الوقت بـ What time is it? ونجيب It's + الساعة + o'clock. ونستخدم at مع الساعة وin مع الفترات.",
                examples = listOf("What time is it?", "It's three o'clock.", "The class starts at nine."),
                quiz = listOf(
                    q("___ time is it?", 0, "What", "Where", "Who"),
                    q("It is five ___.", 0, "o'clock", "clock", "hour"),
                    q("The lesson is ___ eight.", 2, "in", "on", "at")
                )
            ),
            listening = Listening(
                "The English class starts at nine o'clock. Today I am early. My friend is late. We study for one hour.",
                q("How long do they study?", 1, "Thirty minutes", "One hour", "Two hours")
            ),
            speaking = listOf(
                p("What time is it?", "كم الساعة؟"),
                p("It is nine o'clock.", "الساعة التاسعة."),
                p("I am early today.", "أنا مبكر اليوم."),
                p("See you tomorrow.", "أراك غدًا.")
            ),
            topic = "Plans for tomorrow",
            script = listOf(
                "Let's plan! What are you doing tomorrow?",
                "Sounds nice! What time does your day start?",
                "Do you have any free time tomorrow?",
                "Great! What would you like to do in your free time?"
            )
        )
    )

    private val a2 = listOf(
        Lesson(
            theme = "السفر",
            words = listOf(
                w("ticket", "تذكرة", "I bought a ticket online."),
                w("airport", "مطار", "The airport is far."),
                w("passport", "جواز سفر", "Don't forget your passport."),
                w("luggage", "أمتعة", "My luggage is heavy."),
                w("hotel", "فندق", "We stayed in a small hotel."),
                w("reservation", "حجز", "I have a reservation."),
                w("delayed", "متأخرة (رحلة)", "My flight was delayed."),
                w("journey", "رحلة", "It was a long journey.")
            ),
            grammar = Grammar(
                title = "Past Simple (الماضي البسيط)",
                explain = "للأفعال المنتظمة نضيف ed للفعل: visit → visited. ونستخدمه للحديث عن أحداث انتهت مثل yesterday وlast week.",
                examples = listOf("I visited my uncle yesterday.", "She watched a movie.", "We traveled to Turkey."),
                quiz = listOf(
                    q("Yesterday I ___ to the market.", 0, "walked", "walk", "walking"),
                    q("We ___ football last night.", 1, "play", "played", "plays"),
                    q("She ___ the door and went out.", 2, "close", "closing", "closed")
                )
            ),
            listening = Listening(
                "Last summer, I traveled to Turkey. I stayed in a small hotel near the sea. The flight was long, but the people were very kind.",
                q("Where did he stay?", 0, "In a small hotel", "With a friend", "In a tent")
            ),
            speaking = listOf(
                p("I traveled to Turkey last summer.", "سافرت إلى تركيا الصيف الماضي."),
                p("Where is the airport?", "أين المطار؟"),
                p("I have a reservation.", "لدي حجز."),
                p("My flight was delayed.", "رحلتي تأخرت.")
            ),
            topic = "A trip you remember",
            script = listOf(
                "Tell me about a trip you remember. Where did you go?",
                "Nice! Who did you travel with?",
                "What was the best part of the trip?",
                "Would you like to go there again? Why?"
            )
        ),
        Lesson(
            theme = "التسوّق",
            words = listOf(
                w("price", "سعر", "What is the price?"),
                w("cheap", "رخيص", "This shirt is cheap."),
                w("expensive", "غالٍ", "That phone is expensive."),
                w("discount", "خصم", "There is a discount today."),
                w("size", "مقاس", "What size do you wear?"),
                w("receipt", "إيصال", "Keep the receipt."),
                w("customer", "زبون", "The customer is waiting."),
                w("cash", "نقدًا", "I pay in cash.")
            ),
            grammar = Grammar(
                title = "Comparatives (المقارنة)",
                explain = "للكلمات القصيرة نضيف er + than: cheap → cheaper than. وللكلمات الطويلة نستخدم more + than: more expensive than.",
                examples = listOf("This shirt is cheaper than that one.", "A car is more expensive than a bike.", "This shop is better than that shop."),
                quiz = listOf(
                    q("This phone is ___ than that one.", 0, "cheaper", "cheap", "cheapest"),
                    q("A car is ___ than a bike.", 1, "expensiver", "more expensive", "most expensive"),
                    q("My bag is ___ than yours.", 2, "big", "more big", "bigger")
                )
            ),
            listening = Listening(
                "Excuse me, how much is this jacket? It is fifty dollars. That is too expensive. This one is thirty dollars and it is cheaper. I will take this one.",
                q("Which jacket does he buy?", 1, "The fifty dollar jacket", "The thirty dollar jacket", "Neither")
            ),
            speaking = listOf(
                p("How much is this?", "بكم هذا؟"),
                p("It is too expensive.", "إنه غالٍ جدًا."),
                p("Do you have a smaller size?", "هل لديكم مقاس أصغر؟"),
                p("I will take this one.", "سآخذ هذا.")
            ),
            topic = "Shopping and prices",
            script = listOf(
                "Welcome to my shop! What are you looking for today?",
                "Good choice! What size or color do you prefer?",
                "That is fifty dollars. Is it too expensive for you?",
                "Okay! Do you want to pay by cash or by card?"
            )
        ),
        Lesson(
            theme = "الصحة",
            words = listOf(
                w("headache", "صداع", "I have a headache."),
                w("doctor", "طبيب", "I need to see a doctor."),
                w("medicine", "دواء", "Take this medicine twice a day."),
                w("tired", "متعب", "I feel tired."),
                w("sick", "مريض", "He is sick today."),
                w("appointment", "موعد", "I have an appointment at ten."),
                w("healthy", "صحي", "Fruit is healthy."),
                w("rest", "راحة / يرتاح", "You should rest.")
            ),
            grammar = Grammar(
                title = "should / shouldn't (النصيحة)",
                explain = "نستخدم should + الفعل لإعطاء نصيحة، وshouldn't للنهي عن شيء. الفعل بعدها يأتي بدون to.",
                examples = listOf("You should rest.", "You should drink more water.", "You shouldn't eat too much sugar."),
                quiz = listOf(
                    q("You ___ drink more water.", 0, "should", "shoulds", "should to"),
                    q("He is sick. He ___ go to the doctor.", 1, "shouldn't", "should", "not should"),
                    q("You ___ eat too much sugar.", 2, "should", "must to", "shouldn't")
                )
            ),
            listening = Listening(
                "I don't feel well today. I have a headache and I am very tired. My doctor says I should rest and drink more water.",
                q("What should he do?", 2, "Go to work", "Eat more sugar", "Rest and drink water")
            ),
            speaking = listOf(
                p("I have a headache.", "عندي صداع."),
                p("I need to see a doctor.", "أحتاج لرؤية طبيب."),
                p("You should drink more water.", "عليك أن تشرب ماءً أكثر."),
                p("I have an appointment at ten.", "لدي موعد في العاشرة.")
            ),
            topic = "Health and advice",
            script = listOf(
                "You look tired today. How are you feeling?",
                "I'm sorry to hear that. What do you usually do when you feel sick?",
                "Good idea! What healthy habits do you have?",
                "Excellent! What advice would you give to a friend who sleeps late?"
            )
        )
    )

    private val b1 = listOf(
        Lesson(
            theme = "العمل والمقابلات",
            words = listOf(
                w("experience", "خبرة", "I have five years of experience."),
                w("skill", "مهارة", "Communication is an important skill."),
                w("salary", "راتب", "The salary is good."),
                w("deadline", "موعد نهائي", "The deadline is Friday."),
                w("colleague", "زميل عمل", "My colleague helped me."),
                w("manager", "مدير", "I will ask my manager."),
                w("apply", "يتقدّم (لوظيفة)", "I want to apply for this job."),
                w("improve", "يُحسّن", "I want to improve my English.")
            ),
            grammar = Grammar(
                title = "Present Perfect (المضارع التام)",
                explain = "نستخدم have / has + التصريف الثالث للفعل للحديث عن خبرة أو شيء بدأ في الماضي ومستمر أثره. ونستخدم for مع المدة وsince مع نقطة البداية.",
                examples = listOf("I have worked here for three years.", "She has never traveled abroad.", "Have you ever used this software?"),
                quiz = listOf(
                    q("I ___ in this company since 2020.", 1, "work", "have worked", "am worked"),
                    q("She ___ finished the report yet.", 0, "hasn't", "don't", "isn't"),
                    q("___ you ever been to London?", 2, "Did", "Are", "Have")
                )
            ),
            listening = Listening(
                "Thank you for coming. I see you have worked as an accountant for six years. Have you ever managed a team? Yes, I have managed a team of four people since last year.",
                q("How long has he worked as an accountant?", 1, "Four years", "Six years", "One year")
            ),
            speaking = listOf(
                p("I have six years of experience.", "لدي ست سنوات من الخبرة."),
                p("I am good at solving problems.", "أنا جيد في حل المشكلات."),
                p("Could you tell me about the salary?", "هل يمكنك إخباري عن الراتب؟"),
                p("I would like to apply for this position.", "أود التقدم لهذه الوظيفة.")
            ),
            topic = "A job interview",
            script = listOf(
                "Welcome to the interview. Please tell me a little about yourself.",
                "Thank you. What are your biggest strengths?",
                "Have you ever worked in a team? Tell me about it.",
                "Why do you want to work with our company?"
            )
        ),
        Lesson(
            theme = "التقنية",
            words = listOf(
                w("download", "يُنزّل", "I downloaded the app."),
                w("update", "تحديث", "Please update your phone."),
                w("password", "كلمة مرور", "Never share your password."),
                w("battery", "بطارية", "My battery is low."),
                w("device", "جهاز", "I use three devices."),
                w("online", "عبر الإنترنت", "I study online."),
                w("software", "برنامج", "This software is free."),
                w("privacy", "خصوصية", "Privacy is important.")
            ),
            grammar = Grammar(
                title = "First Conditional (الشرط الأول)",
                explain = "للحديث عن نتيجة محتملة في المستقبل: If + مضارع بسيط، will + فعل.",
                examples = listOf("If it rains, I will stay home.", "If you study, you will pass.", "If the battery dies, I will charge it."),
                quiz = listOf(
                    q("If you ___ hard, you will succeed.", 0, "work", "will work", "worked"),
                    q("If it rains, we ___ at home.", 1, "stay", "will stay", "stayed"),
                    q("If I ___ time, I will call you.", 2, "will have", "had", "have")
                )
            ),
            listening = Listening(
                "My phone battery is almost empty. If I don't charge it now, it will turn off during the meeting. I also need to update the software, but I will do that tonight.",
                q("When will he update the software?", 2, "Right now", "During the meeting", "Tonight")
            ),
            speaking = listOf(
                p("My battery is almost empty.", "بطاريتي شبه فارغة."),
                p("If you update the app, it will work better.", "إذا حدّثت التطبيق فسيعمل بشكل أفضل."),
                p("Please keep your password private.", "من فضلك أبقِ كلمة مرورك سرية."),
                p("I usually study English online.", "عادةً أدرس الإنجليزية عبر الإنترنت.")
            ),
            topic = "Technology in daily life",
            script = listOf(
                "Let's talk technology. Which device do you use the most?",
                "Interesting! What apps do you use every day?",
                "If you could invent a new app, what would it do?",
                "Do you think technology makes our life easier? Why?"
            )
        )
    )

    fun lessons(level: Level): List<Lesson> = when (level) {
        Level.A1 -> a1
        Level.A2 -> a2
        Level.B1 -> b1
    }

    fun lesson(level: Level, dayIndex: Int): Lesson {
        val list = lessons(level)
        return list[dayIndex % list.size]
    }

    val allWords: Map<String, Word> by lazy {
        (a1 + a2 + b1).flatMap { it.words }.associateBy { it.en }
    }
}
