package com.example.data

import com.example.model.ExamQuestion
import com.example.model.OptionKey
import com.example.model.QuestionPassage

object ExamQuestionBank {

    val generalInstructions: List<String> = listOf(
        "Berdoalah sebelum mengerjakan soal dan tulis nama serta nomor absen pada lembar jawaban.",
        "Pilih satu jawaban yang paling tepat (a, b, c, atau d) dengan menyilang atau menghitamkan lembar jawaban.",
        "Kerjakan soal yang kamu anggap mudah terlebih dahulu.",
        "Periksa kembali jawabanmu sebelum dikumpulkan. Setiap jawaban benar bernilai 2 poin."
    )

    val passages: Map<String, QuestionPassage> = mapOf(
        "text_1" to QuestionPassage(
            id = "text_1",
            title = "Text 1",
            instruction = "Read Text 1 for questions 35–38.",
            body = "Imagine you are in a noisy train station and your friend is on the other side of the platform. You cannot shout, so you wave your hand and point to the train. Your friend understands immediately. This is nonverbal communication: sending messages without words. We use our faces, hands, and bodies every day. A smile shows happiness, a frown shows worry or anger, and a thumbs-up means “good job” in many countries. However, not all gestures have the same meaning everywhere. For example, the “OK” sign is friendly in many places but rude in a few countries. This is why travellers should learn a little about local gestures before they visit."
        ),
        "text_2" to QuestionPassage(
            id = "text_2",
            title = "Text 2",
            instruction = "Read Text 2 for questions 39–42.",
            header = "ANNOUNCEMENT",
            body = "SMAN 1 Harapan will hold a “Clean River Day” on Saturday, 12 December, at 7 a.m. All Grade X students are going to meet at the school gate. Students should wear sports uniforms and bring gloves and a bottle of drinking water. The school will provide trash bags. Because the river is polluted by plastic waste, this activity aims to protect the environment. Students who want to join must register with their class leader before Wednesday."
        ),
        "text_3" to QuestionPassage(
            id = "text_3",
            title = "Text 3",
            instruction = "Read Text 3 for questions 43–46.",
            body = "Hi Sinta,\nHow are you? I’m writing to tell you about my plans for the holiday. Next week, my family is visiting my grandparents in Malang. We’ve bought the train tickets already. I’m going to help my grandfather in his small garden because he is 70 years old and cannot work too much. I think I will also take many photos of the mountains. What about you? Are you doing anything special?\nWrite back soon!\nBest wishes,\nDani"
        ),
        "text_4" to QuestionPassage(
            id = "text_4",
            title = "Text 4",
            instruction = "Read Text 4 for questions 47–50.",
            body = "Many teenagers spend more than four hours a day on their phones. Some of them are chatting with friends, while others are watching videos or playing games. Experts say that phones are useful, but too much screen time can make teenagers tired and less focused at school. Therefore, doctors advise young people to put their phones away one hour before bedtime. Some schools are also starting phone-free lessons so that students can talk to each other face to face."
        )
    )

    val questions: List<ExamQuestion> = listOf(
        ExamQuestion(
            number = 1,
            prompt = "My father ____ the news every morning.",
            options = mapOf(
                OptionKey.A to "watches",
                OptionKey.B to "watch",
                OptionKey.C to "is watching",
                OptionKey.D to "watching"
            ),
            correctAnswer = OptionKey.A,
            explanation = "“Every morning” menandakan kebiasaan, jadi present simple. Subjek my father (he) memakai verb + es: watches."
        ),
        ExamQuestion(
            number = 2,
            prompt = "Be quiet! The baby ____.",
            options = mapOf(
                OptionKey.A to "sleeps",
                OptionKey.B to "sleep",
                OptionKey.C to "is sleeping",
                OptionKey.D to "slept"
            ),
            correctAnswer = OptionKey.C,
            explanation = "“Be quiet!” menandakan kejadian yang sedang berlangsung, jadi present continuous: is sleeping."
        ),
        ExamQuestion(
            number = 3,
            prompt = "____ you ____ English on Saturdays?",
            options = mapOf(
                OptionKey.A to "Do / study",
                OptionKey.B to "Does / study",
                OptionKey.C to "Are / studying",
                OptionKey.D to "Do / studies"
            ),
            correctAnswer = OptionKey.A,
            explanation = "“On Saturdays” adalah kebiasaan. Pertanyaan present simple untuk you memakai Do, lalu verb dasar: Do you study."
        ),
        ExamQuestion(
            number = 4,
            prompt = "Rina ____ her homework at the moment.",
            options = mapOf(
                OptionKey.A to "doesn’t do",
                OptionKey.B to "isn’t doing",
                OptionKey.C to "don’t doing",
                OptionKey.D to "not doing"
            ),
            correctAnswer = OptionKey.B,
            explanation = "“At the moment” = sekarang, jadi present continuous negatif: is not (isn’t) + V-ing."
        ),
        ExamQuestion(
            number = 5,
            prompt = "I ____ the answer to this question.",
            options = mapOf(
                OptionKey.A to "am knowing",
                OptionKey.B to "knowing",
                OptionKey.C to "knows",
                OptionKey.D to "know"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Know adalah stative verb sehingga tidak dipakai dengan -ing. Subjek I memakai know."
        ),
        ExamQuestion(
            number = 6,
            prompt = "The sun ____ in the east.",
            options = mapOf(
                OptionKey.A to "rise",
                OptionKey.B to "rises",
                OptionKey.C to "is rising",
                OptionKey.D to "rose"
            ),
            correctAnswer = OptionKey.B,
            explanation = "Fakta umum memakai present simple. Subjek the sun (it) memakai verb + s: rises."
        ),
        ExamQuestion(
            number = 7,
            prompt = "Which sentence uses the present continuous for a temporary situation?",
            options = mapOf(
                OptionKey.A to "She is working at her uncle’s shop this month.",
                OptionKey.B to "She works in a bank.",
                OptionKey.C to "She works hard every day.",
                OptionKey.D to "She works from Monday to Friday."
            ),
            correctAnswer = OptionKey.A,
            explanation = "“This month” menunjukkan keadaan sementara, jadi present continuous (is working). Pilihan lain adalah kebiasaan."
        ),
        ExamQuestion(
            number = 8,
            prompt = "Choose the correct sentence.",
            options = mapOf(
                OptionKey.A to "He don’t play the guitar.",
                OptionKey.B to "He doesn’t plays the guitar.",
                OptionKey.C to "He doesn’t play the guitar.",
                OptionKey.D to "He not play the guitar."
            ),
            correctAnswer = OptionKey.C,
            explanation = "Subjek he memakai doesn’t, dan setelah doesn’t verb kembali ke bentuk dasar (play)."
        ),
        ExamQuestion(
            number = 9,
            prompt = "You are Dika’s classmate, ____?",
            options = mapOf(
                OptionKey.A to "aren’t you",
                OptionKey.B to "are you",
                OptionKey.C to "don’t you",
                OptionKey.D to "isn’t he"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Kalimat positif dengan are, tag negatif: aren’t you."
        ),
        ExamQuestion(
            number = 10,
            prompt = "She doesn’t live near here, ____?",
            options = mapOf(
                OptionKey.A to "doesn’t she",
                OptionKey.B to "is she",
                OptionKey.C to "do she",
                OptionKey.D to "does she"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Kalimat negatif, tag positif dengan auxiliary yang sama: does she."
        ),
        ExamQuestion(
            number = 11,
            prompt = "The students were late, ____?",
            options = mapOf(
                OptionKey.A to "weren’t they",
                OptionKey.B to "were they",
                OptionKey.C to "didn’t they",
                OptionKey.D to "wasn’t they"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Kalimat positif past dengan were, tag negatif: weren’t they. The students diganti they."
        ),
        ExamQuestion(
            number = 12,
            prompt = "Your uncle works in a hospital, ____?",
            options = mapOf(
                OptionKey.A to "isn’t he",
                OptionKey.B to "does he",
                OptionKey.C to "don’t he",
                OptionKey.D to "doesn’t he"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Present simple tanpa auxiliary, subjek your uncle = he, jadi tag negatif: doesn’t he."
        ),
        ExamQuestion(
            number = 13,
            prompt = "Let’s finish the task now, ____?",
            options = mapOf(
                OptionKey.A to "will we",
                OptionKey.B to "don’t we",
                OptionKey.C to "shall we",
                OptionKey.D to "do we"
            ),
            correctAnswer = OptionKey.C,
            explanation = "Ajakan Let’s selalu memakai tag shall we."
        ),
        ExamQuestion(
            number = 14,
            prompt = "They can’t swim, ____?",
            options = mapOf(
                OptionKey.A to "can’t they",
                OptionKey.B to "can they",
                OptionKey.C to "do they",
                OptionKey.D to "are they"
            ),
            correctAnswer = OptionKey.B,
            explanation = "Kalimat negatif dengan modal can’t, tag positif: can they."
        ),
        ExamQuestion(
            number = 15,
            prompt = "Everybody enjoys music, ____?",
            options = mapOf(
                OptionKey.A to "doesn’t he",
                OptionKey.B to "isn’t it",
                OptionKey.C to "do they",
                OptionKey.D to "don’t they"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Everybody memakai pronoun they pada tag, kalimat positif sehingga tag negatif: don’t they."
        ),
        ExamQuestion(
            number = 16,
            prompt = "There are many students in the hall, ____?",
            options = mapOf(
                OptionKey.A to "are there",
                OptionKey.B to "aren’t they",
                OptionKey.C to "aren’t there",
                OptionKey.D to "don’t there"
            ),
            correctAnswer = OptionKey.C,
            explanation = "Pada kalimat there are, kata there dipakai lagi pada tag. Positif, tag negatif: aren’t there."
        ),
        ExamQuestion(
            number = 17,
            prompt = "A child between one and three years old who has just learned to walk is called a ____.",
            options = mapOf(
                OptionKey.A to "baby",
                OptionKey.B to "teenager",
                OptionKey.C to "pensioner",
                OptionKey.D to "toddler"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Toddler adalah balita yang baru belajar berjalan, sekitar 1–3 tahun."
        ),
        ExamQuestion(
            number = 18,
            prompt = "“Mrs. Lina is in her early thirties.” She is about ____ years old.",
            options = mapOf(
                OptionKey.A to "31",
                OptionKey.B to "26",
                OptionKey.C to "38",
                OptionKey.D to "45"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Early thirties berarti awal usia 30-an, sekitar 30–33 tahun."
        ),
        ExamQuestion(
            number = 19,
            prompt = "Which list is in the correct order from youngest to oldest?",
            options = mapOf(
                OptionKey.A to "teenager, toddler, middle-aged, elderly",
                OptionKey.B to "elderly, middle-aged, teenager, toddler",
                OptionKey.C to "toddler, teenager, middle-aged, elderly",
                OptionKey.D to "toddler, middle-aged, teenager, elderly"
            ),
            correctAnswer = OptionKey.C,
            explanation = "Urutan hidup: toddler (balita), teenager (remaja), middle-aged (paruh baya), elderly (lansia)."
        ),
        ExamQuestion(
            number = 20,
            prompt = "A person who has stopped working because of age is a ____.",
            options = mapOf(
                OptionKey.A to "pensioner",
                OptionKey.B to "toddler",
                OptionKey.C to "teenager",
                OptionKey.D to "child"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Pensioner adalah pensiunan, yaitu orang yang sudah berhenti bekerja karena usia."
        ),
        ExamQuestion(
            number = 21,
            prompt = "Which activity is a main cause of deforestation?",
            options = mapOf(
                OptionKey.A to "Planting trees in the city",
                OptionKey.B to "Recycling old paper",
                OptionKey.C to "Cutting down forests to make farms and roads",
                OptionKey.D to "Using solar panels"
            ),
            correctAnswer = OptionKey.C,
            explanation = "Deforestation adalah penggundulan hutan, disebabkan antara lain penebangan hutan untuk lahan dan jalan."
        ),
        ExamQuestion(
            number = 22,
            prompt = "The government asks people to ____ plastic bags because they take hundreds of years to break down.",
            options = mapOf(
                OptionKey.A to "increase",
                OptionKey.B to "pollute",
                OptionKey.C to "waste",
                OptionKey.D to "reduce"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Untuk melindungi lingkungan, kita mengurangi (reduce) pemakaian kantong plastik."
        ),
        ExamQuestion(
            number = 23,
            prompt = "Solar and wind power are examples of ____ energy.",
            options = mapOf(
                OptionKey.A to "fossil",
                OptionKey.B to "renewable",
                OptionKey.C to "extinct",
                OptionKey.D to "polluted"
            ),
            correctAnswer = OptionKey.B,
            explanation = "Renewable energy adalah energi terbarukan seperti tenaga surya dan angin. Fossil fuels tidak terbarukan."
        ),
        ExamQuestion(
            number = 24,
            prompt = "If an animal is ____, there are no more of its kind alive.",
            options = mapOf(
                OptionKey.A to "endangered",
                OptionKey.B to "recycled",
                OptionKey.C to "extinct",
                OptionKey.D to "renewable"
            ),
            correctAnswer = OptionKey.C,
            explanation = "Extinct berarti sudah punah. Endangered berarti terancam punah tetapi masih ada."
        ),
        ExamQuestion(
            number = 25,
            prompt = "Which activity helps save energy?",
            options = mapOf(
                OptionKey.A to "Turning off unused lights",
                OptionKey.B to "Leaving the TV on all day",
                OptionKey.C to "Using the air conditioner with the window open",
                OptionKey.D to "Leaving the fan on in an empty room"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Mematikan lampu yang tidak dipakai menghemat energi. Tiga pilihan lain memboroskan energi."
        ),
        ExamQuestion(
            number = 26,
            prompt = "What does “global warming” mean?",
            options = mapOf(
                OptionKey.A to "The Earth is becoming colder every year.",
                OptionKey.B to "There are more forests than before.",
                OptionKey.C to "The Earth’s temperature is slowly rising.",
                OptionKey.D to "Rivers are becoming cleaner."
            ),
            correctAnswer = OptionKey.C,
            explanation = "Global warming adalah pemanasan global, yaitu suhu bumi yang perlahan naik."
        ),
        ExamQuestion(
            number = 27,
            prompt = "A: I forgot my pen. B: Don’t worry. I ____ you mine.",
            options = mapOf(
                OptionKey.A to "lent",
                OptionKey.B to "am lending",
                OptionKey.C to "lend",
                OptionKey.D to "will lend"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Tawaran atau keputusan spontan saat berbicara memakai will + verb dasar."
        ),
        ExamQuestion(
            number = 28,
            prompt = "My class ____ on a school trip to Bandung next Friday. The bus is already booked.",
            options = mapOf(
                OptionKey.A to "is going",
                OptionKey.B to "went",
                OptionKey.C to "go",
                OptionKey.D to "has gone"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Rencana yang sudah diatur (bus sudah dipesan) memakai present continuous: is going."
        ),
        ExamQuestion(
            number = 29,
            prompt = "Look at that car! It ____ into the tree!",
            options = mapOf(
                OptionKey.A to "will crashing",
                OptionKey.B to "crashes",
                OptionKey.C to "crashed",
                OptionKey.D to "is going to crash"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Ada bukti yang terlihat sekarang, jadi prediksi memakai be going to + verb dasar."
        ),
        ExamQuestion(
            number = 30,
            prompt = "I think Indonesia ____ the match tomorrow.",
            options = mapOf(
                OptionKey.A to "will win",
                OptionKey.B to "won",
                OptionKey.C to "winning",
                OptionKey.D to "is win"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Prediksi berdasarkan pendapat (I think) memakai will + verb dasar."
        ),
        ExamQuestion(
            number = 31,
            prompt = "Which sentence is correct?",
            options = mapOf(
                OptionKey.A to "She is going buy a laptop next month.",
                OptionKey.B to "She will to buy a laptop next month.",
                OptionKey.C to "She going to buy a laptop next month.",
                OptionKey.D to "She is going to buy a laptop next month."
            ),
            correctAnswer = OptionKey.D,
            explanation = "Rumusnya am/is/are + going to + verb dasar. Pilihan lain kehilangan is atau to, atau salah memakai to setelah will."
        ),
        ExamQuestion(
            number = 32,
            prompt = "I have saved enough money. I ____ a new phone.",
            options = mapOf(
                OptionKey.A to "will to buy",
                OptionKey.B to "am buy",
                OptionKey.C to "am going to buy",
                OptionKey.D to "buying"
            ),
            correctAnswer = OptionKey.C,
            explanation = "Rencana yang sudah diputuskan sebelum berbicara memakai be going to + verb dasar."
        ),
        ExamQuestion(
            number = 33,
            prompt = "Don’t worry. I ____ you with your homework.",
            options = mapOf(
                OptionKey.A to "helped",
                OptionKey.B to "helping",
                OptionKey.C to "helps",
                OptionKey.D to "will help"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Tawaran bantuan spontan memakai will + verb dasar."
        ),
        ExamQuestion(
            number = 34,
            prompt = "A: What ____ tonight? B: I’m meeting my friends at seven.",
            options = mapOf(
                OptionKey.A to "are you doing",
                OptionKey.B to "do you do",
                OptionKey.C to "will you do",
                OptionKey.D to "did you do"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Pertanyaan tentang rencana yang sudah diatur memakai present continuous, sesuai jawaban I’m meeting."
        ),
        ExamQuestion(
            number = 35,
            prompt = "What is the main idea of Text 1?",
            options = mapOf(
                OptionKey.A to "Train stations are always noisy.",
                OptionKey.B to "Nonverbal communication sends messages without words, and gestures can differ between places.",
                OptionKey.C to "The OK sign is always rude.",
                OptionKey.D to "Travellers should avoid talking to local people."
            ),
            correctAnswer = OptionKey.B,
            explanation = "Teks membahas komunikasi tanpa kata dan fakta bahwa gestur bisa berbeda makna di berbagai tempat.",
            passageId = "text_1"
        ),
        ExamQuestion(
            number = 36,
            prompt = "Why do you wave your hand and point to the train in the example?",
            options = mapOf(
                OptionKey.A to "Because you cannot shout in a noisy place.",
                OptionKey.B to "Because your friend is angry.",
                OptionKey.C to "Because you want to take a photo.",
                OptionKey.D to "Because the train is late."
            ),
            correctAnswer = OptionKey.A,
            explanation = "Kalimat kedua menyatakan kamu tidak bisa berteriak di stasiun yang ramai, sehingga memakai gestur.",
            passageId = "text_1"
        ),
        ExamQuestion(
            number = 37,
            prompt = "According to the text, a frown shows ____.",
            options = mapOf(
                OptionKey.A to "happiness",
                OptionKey.B to "good job",
                OptionKey.C to "friendship",
                OptionKey.D to "worry or anger"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Teks: a smile shows happiness, a frown shows worry or anger.",
            passageId = "text_1"
        ),
        ExamQuestion(
            number = 38,
            prompt = "The word “immediately” in the text is closest in meaning to ____.",
            options = mapOf(
                OptionKey.A to "slowly",
                OptionKey.B to "at once",
                OptionKey.C to "sadly",
                OptionKey.D to "carefully"
            ),
            correctAnswer = OptionKey.B,
            explanation = "Immediately = segera, seketika = at once.",
            passageId = "text_1"
        ),
        ExamQuestion(
            number = 39,
            prompt = "What is the purpose of Text 2?",
            options = mapOf(
                OptionKey.A to "To sell drinking water",
                OptionKey.B to "To announce a football match",
                OptionKey.C to "To ask students to stay at home",
                OptionKey.D to "To inform students about a river-cleaning activity"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Pengumuman memberi tahu kegiatan Clean River Day, yaitu membersihkan sungai.",
            passageId = "text_2"
        ),
        ExamQuestion(
            number = 40,
            prompt = "Where will the students meet?",
            options = mapOf(
                OptionKey.A to "At the river",
                OptionKey.B to "At the school gate",
                OptionKey.C to "In the hall",
                OptionKey.D to "In the library"
            ),
            correctAnswer = OptionKey.B,
            explanation = "Kalimat kedua: all Grade X students are going to meet at the school gate.",
            passageId = "text_2"
        ),
        ExamQuestion(
            number = 41,
            prompt = "What must students bring?",
            options = mapOf(
                OptionKey.A to "Gloves and a bottle of drinking water",
                OptionKey.B to "Trash bags and gloves",
                OptionKey.C to "Plants and a bottle",
                OptionKey.D to "A camera and a bucket"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Siswa membawa sarung tangan dan air minum. Kantong sampah disediakan sekolah.",
            passageId = "text_2"
        ),
        ExamQuestion(
            number = 42,
            prompt = "The word “polluted” in the text is closest in meaning to ____.",
            options = mapOf(
                OptionKey.A to "made clean",
                OptionKey.B to "made cheaper",
                OptionKey.C to "made bigger",
                OptionKey.D to "made dirty"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Polluted = tercemar = made dirty, sesuai konteks sungai yang kotor oleh sampah plastik.",
            passageId = "text_2"
        ),
        ExamQuestion(
            number = 43,
            prompt = "Why is Dani going to help his grandfather in the garden?",
            options = mapOf(
                OptionKey.A to "Because the garden is very big.",
                OptionKey.B to "Because he wants to sell flowers.",
                OptionKey.C to "Because his grandfather is elderly and cannot work too much.",
                OptionKey.D to "Because his grandfather is a teenager."
            ),
            correctAnswer = OptionKey.C,
            explanation = "Teks menyebut kakeknya berusia 70 tahun dan tidak bisa bekerja terlalu banyak.",
            passageId = "text_3"
        ),
        ExamQuestion(
            number = 44,
            prompt = "How will Dani’s family travel to Malang?",
            options = mapOf(
                OptionKey.A to "By plane",
                OptionKey.B to "By bus",
                OptionKey.C to "By car",
                OptionKey.D to "By train"
            ),
            correctAnswer = OptionKey.D,
            explanation = "Teks: We’ve bought the train tickets already.",
            passageId = "text_3"
        ),
        ExamQuestion(
            number = 45,
            prompt = "Which statement is TRUE about Dani?",
            options = mapOf(
                OptionKey.A to "He is staying at home next week.",
                OptionKey.B to "He will take photos of the sea.",
                OptionKey.C to "His family is visiting his grandparents next week.",
                OptionKey.D to "He lives with his grandparents."
            ),
            correctAnswer = OptionKey.C,
            explanation = "Kalimat ketiga: my family is visiting my grandparents in Malang next week. Foto yang diambil adalah foto gunung.",
            passageId = "text_3"
        ),
        ExamQuestion(
            number = 46,
            prompt = "Why does Dani write “What about you?”",
            options = mapOf(
                OptionKey.A to "He is angry with Sinta.",
                OptionKey.B to "He wants Sinta to visit Malang.",
                OptionKey.C to "He needs Sinta’s help.",
                OptionKey.D to "He wants to know Sinta’s holiday plans."
            ),
            correctAnswer = OptionKey.D,
            explanation = "Setelah menceritakan rencananya, Dani menanyakan kegiatan Sinta.",
            passageId = "text_3"
        ),
        ExamQuestion(
            number = 47,
            prompt = "What is the main idea of Text 4?",
            options = mapOf(
                OptionKey.A to "Phones are cheaper for teenagers.",
                OptionKey.B to "Teenagers use phones a lot, and too much screen time can cause problems.",
                OptionKey.C to "Schools should sell phones.",
                OptionKey.D to "Teenagers never watch videos."
            ),
            correctAnswer = OptionKey.B,
            explanation = "Teks membahas lamanya remaja memakai ponsel dan dampak layar yang berlebihan.",
            passageId = "text_4"
        ),
        ExamQuestion(
            number = 48,
            prompt = "What do doctors advise young people to do?",
            options = mapOf(
                OptionKey.A to "Play games before bedtime.",
                OptionKey.B to "Chat with friends all night.",
                OptionKey.C to "Put their phones away one hour before bedtime.",
                OptionKey.D to "Buy new phones every year."
            ),
            correctAnswer = OptionKey.C,
            explanation = "Teks: doctors advise young people to put their phones away one hour before bedtime.",
            passageId = "text_4"
        ),
        ExamQuestion(
            number = 49,
            prompt = "Too much screen time can make teenagers ____.",
            options = mapOf(
                OptionKey.A to "tired and less focused at school",
                OptionKey.B to "happier and more active",
                OptionKey.C to "taller and stronger",
                OptionKey.D to "better at sports"
            ),
            correctAnswer = OptionKey.A,
            explanation = "Menurut para ahli, terlalu lama menatap layar membuat remaja lelah dan kurang fokus di sekolah.",
            passageId = "text_4"
        ),
        ExamQuestion(
            number = 50,
            prompt = "Why are some schools starting phone-free lessons?",
            options = mapOf(
                OptionKey.A to "Because phones are too expensive.",
                OptionKey.B to "So that students can talk to each other directly.",
                OptionKey.C to "Because teachers want to play games.",
                OptionKey.D to "Because students are too tired to study."
            ),
            correctAnswer = OptionKey.B,
            explanation = "Kalimat terakhir: phone-free lessons agar siswa dapat berbicara langsung (face to face).",
            passageId = "text_4"
        )
    )

    fun calculateCorrectCount(answers: List<String>): Int {
        var correct = 0
        for (index in questions.indices) {
            val selected = OptionKey.fromCode(answers.getOrNull(index))
            if (selected == questions[index].correctAnswer) {
                correct++
            }
        }
        return correct
    }

    fun calculateScore(correctCount: Int): Int = correctCount * 2
}
