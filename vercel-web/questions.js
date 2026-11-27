export const GENERAL_INSTRUCTIONS = [
  "Berdoalah sebelum mengerjakan soal dan tulis nama serta nomor absen pada lembar jawaban.",
  "Pilih satu jawaban yang paling tepat (a, b, c, atau d) dengan menyilang atau menghitamkan lembar jawaban.",
  "Kerjakan soal yang kamu anggap mudah terlebih dahulu.",
  "Periksa kembali jawabanmu sebelum dikumpulkan. Setiap jawaban benar bernilai 2 poin."
];

export const PASSAGES = {
  text_1: {
    id: "text_1",
    title: "Text 1",
    instruction: "Read Text 1 for questions 35–38.",
    header: null,
    body: "Imagine you are in a noisy train station and your friend is on the other side of the platform. You cannot shout, so you wave your hand and point to the train. Your friend understands immediately. This is nonverbal communication: sending messages without words. We use our faces, hands, and bodies every day. A smile shows happiness, a frown shows worry or anger, and a thumbs-up means “good job” in many countries. However, not all gestures have the same meaning everywhere. For example, the “OK” sign is friendly in many places but rude in a few countries. This is why travellers should learn a little about local gestures before they visit."
  },
  text_2: {
    id: "text_2",
    title: "Text 2",
    instruction: "Read Text 2 for questions 39–42.",
    header: "ANNOUNCEMENT",
    body: "SMAN 1 Harapan will hold a “Clean River Day” on Saturday, 12 December, at 7 a.m. All Grade X students are going to meet at the school gate. Students should wear sports uniforms and bring gloves and a bottle of drinking water. The school will provide trash bags. Because the river is polluted by plastic waste, this activity aims to protect the environment. Students who want to join must register with their class leader before Wednesday."
  },
  text_3: {
    id: "text_3",
    title: "Text 3",
    instruction: "Read Text 3 for questions 43–46.",
    header: null,
    body: "Hi Sinta,\nHow are you? I’m writing to tell you about my plans for the holiday. Next week, my family is visiting my grandparents in Malang. We’ve bought the train tickets already. I’m going to help my grandfather in his small garden because he is 70 years old and cannot work too much. I think I will also take many photos of the mountains. What about you? Are you doing anything special?\nWrite back soon!\nBest wishes,\nDani"
  },
  text_4: {
    id: "text_4",
    title: "Text 4",
    instruction: "Read Text 4 for questions 47–50.",
    header: null,
    body: "Many teenagers spend more than four hours a day on their phones. Some of them are chatting with friends, while others are watching videos or playing games. Experts say that phones are useful, but too much screen time can make teenagers tired and less focused at school. Therefore, doctors advise young people to put their phones away one hour before bedtime. Some schools are also starting phone-free lessons so that students can talk to each other face to face."
  }
};

export const QUESTIONS = [
  {
    number: 1,
    prompt: "My father ____ the news every morning.",
    options: { a: "watches", b: "watch", c: "is watching", d: "watching" },
    correct: "a"
  },
  {
    number: 2,
    prompt: "Be quiet! The baby ____.",
    options: { a: "sleeps", b: "sleep", c: "is sleeping", d: "slept" },
    correct: "c"
  },
  {
    number: 3,
    prompt: "____ you ____ English on Saturdays?",
    options: { a: "Do / study", b: "Does / study", c: "Are / studying", d: "Do / studies" },
    correct: "a"
  },
  {
    number: 4,
    prompt: "Rina ____ her homework at the moment.",
    options: { a: "doesn’t do", b: "isn’t doing", c: "don’t doing", d: "not doing" },
    correct: "b"
  },
  {
    number: 5,
    prompt: "I ____ the answer to this question.",
    options: { a: "am knowing", b: "knowing", c: "knows", d: "know" },
    correct: "d"
  },
  {
    number: 6,
    prompt: "The sun ____ in the east.",
    options: { a: "rise", b: "rises", c: "is rising", d: "rose" },
    correct: "b"
  },
  {
    number: 7,
    prompt: "Which sentence uses the present continuous for a temporary situation?",
    options: {
      a: "She is working at her uncle’s shop this month.",
      b: "She works in a bank.",
      c: "She works hard every day.",
      d: "She works from Monday to Friday."
    },
    correct: "a"
  },
  {
    number: 8,
    prompt: "Choose the correct sentence.",
    options: {
      a: "He don’t play the guitar.",
      b: "He doesn’t plays the guitar.",
      c: "He doesn’t play the guitar.",
      d: "He not play the guitar."
    },
    correct: "c"
  },
  {
    number: 9,
    prompt: "You are Dika’s classmate, ____?",
    options: { a: "aren’t you", b: "are you", c: "don’t you", d: "isn’t he" },
    correct: "a"
  },
  {
    number: 10,
    prompt: "She doesn’t live near here, ____?",
    options: { a: "doesn’t she", b: "is she", c: "do she", d: "does she" },
    correct: "d"
  },
  {
    number: 11,
    prompt: "The students were late, ____?",
    options: { a: "weren’t they", b: "were they", c: "didn’t they", d: "wasn’t they" },
    correct: "a"
  },
  {
    number: 12,
    prompt: "Your uncle works in a hospital, ____?",
    options: { a: "isn’t he", b: "does he", c: "don’t he", d: "doesn’t he" },
    correct: "d"
  },
  {
    number: 13,
    prompt: "Let’s finish the task now, ____?",
    options: { a: "will we", b: "don’t we", c: "shall we", d: "do we" },
    correct: "c"
  },
  {
    number: 14,
    prompt: "They can’t swim, ____?",
    options: { a: "can’t they", b: "can they", c: "do they", d: "are they" },
    correct: "b"
  },
  {
    number: 15,
    prompt: "Everybody enjoys music, ____?",
    options: { a: "doesn’t he", b: "isn’t it", c: "do they", d: "don’t they" },
    correct: "d"
  },
  {
    number: 16,
    prompt: "There are many students in the hall, ____?",
    options: { a: "are there", b: "aren’t they", c: "aren’t there", d: "don’t there" },
    correct: "c"
  },
  {
    number: 17,
    prompt: "A child between one and three years old who has just learned to walk is called a ____.",
    options: { a: "baby", b: "teenager", c: "pensioner", d: "toddler" },
    correct: "d"
  },
  {
    number: 18,
    prompt: "“Mrs. Lina is in her early thirties.” She is about ____ years old.",
    options: { a: "31", b: "26", c: "38", d: "45" },
    correct: "a"
  },
  {
    number: 19,
    prompt: "Which list is in the correct order from youngest to oldest?",
    options: {
      a: "teenager, toddler, middle-aged, elderly",
      b: "elderly, middle-aged, teenager, toddler",
      c: "toddler, teenager, middle-aged, elderly",
      d: "toddler, middle-aged, teenager, elderly"
    },
    correct: "c"
  },
  {
    number: 20,
    prompt: "A person who has stopped working because of age is a ____.",
    options: { a: "pensioner", b: "toddler", c: "teenager", d: "child" },
    correct: "a"
  },
  {
    number: 21,
    prompt: "Which activity is a main cause of deforestation?",
    options: {
      a: "Planting trees in the city",
      b: "Recycling old paper",
      c: "Cutting down forests to make farms and roads",
      d: "Using solar panels"
    },
    correct: "c"
  },
  {
    number: 22,
    prompt: "The government asks people to ____ plastic bags because they take hundreds of years to break down.",
    options: { a: "increase", b: "pollute", c: "waste", d: "reduce" },
    correct: "d"
  },
  {
    number: 23,
    prompt: "Solar and wind power are examples of ____ energy.",
    options: { a: "fossil", b: "renewable", c: "extinct", d: "polluted" },
    correct: "b"
  },
  {
    number: 24,
    prompt: "If an animal is ____, there are no more of its kind alive.",
    options: { a: "endangered", b: "recycled", c: "extinct", d: "renewable" },
    correct: "c"
  },
  {
    number: 25,
    prompt: "Which activity helps save energy?",
    options: {
      a: "Turning off unused lights",
      b: "Leaving the TV on all day",
      c: "Using the air conditioner with the window open",
      d: "Leaving the fan on in an empty room"
    },
    correct: "a"
  },
  {
    number: 26,
    prompt: "What does “global warming” mean?",
    options: {
      a: "The Earth is becoming colder every year.",
      b: "There are more forests than before.",
      c: "The Earth’s temperature is slowly rising.",
      d: "Rivers are becoming cleaner."
    },
    correct: "c"
  },
  {
    number: 27,
    prompt: "A: I forgot my pen. B: Don’t worry. I ____ you mine.",
    options: { a: "lent", b: "am lending", c: "lend", d: "will lend" },
    correct: "d"
  },
  {
    number: 28,
    prompt: "My class ____ on a school trip to Bandung next Friday. The bus is already booked.",
    options: { a: "is going", b: "went", c: "go", d: "has gone" },
    correct: "a"
  },
  {
    number: 29,
    prompt: "Look at that car! It ____ into the tree!",
    options: { a: "will crashing", b: "crashes", c: "crashed", d: "is going to crash" },
    correct: "d"
  },
  {
    number: 30,
    prompt: "I think Indonesia ____ the match tomorrow.",
    options: { a: "will win", b: "won", c: "winning", d: "is win" },
    correct: "a"
  },
  {
    number: 31,
    prompt: "Which sentence is correct?",
    options: {
      a: "She is going buy a laptop next month.",
      b: "She will to buy a laptop next month.",
      c: "She going to buy a laptop next month.",
      d: "She is going to buy a laptop next month."
    },
    correct: "d"
  },
  {
    number: 32,
    prompt: "I have saved enough money. I ____ a new phone.",
    options: { a: "will to buy", b: "am buy", c: "am going to buy", d: "buying" },
    correct: "c"
  },
  {
    number: 33,
    prompt: "Don’t worry. I ____ you with your homework.",
    options: { a: "helped", b: "helping", c: "helps", d: "will help" },
    correct: "d"
  },
  {
    number: 34,
    prompt: "A: What ____ tonight? B: I’m meeting my friends at seven.",
    options: { a: "are you doing", b: "do you do", c: "will you do", d: "did you do" },
    correct: "a"
  },
  {
    number: 35,
    passageId: "text_1",
    prompt: "What is the main idea of Text 1?",
    options: {
      a: "Train stations are always noisy.",
      b: "Nonverbal communication sends messages without words, and gestures can differ between places.",
      c: "The OK sign is always rude.",
      d: "Travellers should avoid talking to local people."
    },
    correct: "b"
  },
  {
    number: 36,
    passageId: "text_1",
    prompt: "Why do you wave your hand and point to the train in the example?",
    options: {
      a: "Because you cannot shout in a noisy place.",
      b: "Because your friend is angry.",
      c: "Because you want to take a photo.",
      d: "Because the train is late."
    },
    correct: "a"
  },
  {
    number: 37,
    passageId: "text_1",
    prompt: "According to the text, a frown shows ____.",
    options: {
      a: "happiness",
      b: "good job",
      c: "friendship",
      d: "worry or anger"
    },
    correct: "d"
  },
  {
    number: 38,
    passageId: "text_1",
    prompt: "The word “immediately” in the text is closest in meaning to ____.",
    options: {
      a: "slowly",
      b: "at once",
      c: "sadly",
      d: "carefully"
    },
    correct: "b"
  },
  {
    number: 39,
    passageId: "text_2",
    prompt: "What is the purpose of Text 2?",
    options: {
      a: "To sell drinking water",
      b: "To announce a football match",
      c: "To ask students to stay at home",
      d: "To inform students about a river-cleaning activity"
    },
    correct: "d"
  },
  {
    number: 40,
    passageId: "text_2",
    prompt: "Where will the students meet?",
    options: {
      a: "At the river",
      b: "At the school gate",
      c: "In the hall",
      d: "In the library"
    },
    correct: "b"
  },
  {
    number: 41,
    passageId: "text_2",
    prompt: "What must students bring?",
    options: {
      a: "Gloves and a bottle of drinking water",
      b: "Trash bags and gloves",
      c: "Plants and a bottle",
      d: "A camera and a bucket"
    },
    correct: "a"
  },
  {
    number: 42,
    passageId: "text_2",
    prompt: "The word “polluted” in the text is closest in meaning to ____.",
    options: {
      a: "made clean",
      b: "made cheaper",
      c: "made bigger",
      d: "made dirty"
    },
    correct: "d"
  },
  {
    number: 43,
    passageId: "text_3",
    prompt: "Why is Dani going to help his grandfather in the garden?",
    options: {
      a: "Because the garden is very big.",
      b: "Because he wants to sell flowers.",
      c: "Because his grandfather is elderly and cannot work too much.",
      d: "Because his grandfather is a teenager."
    },
    correct: "c"
  },
  {
    number: 44,
    passageId: "text_3",
    prompt: "How will Dani’s family travel to Malang?",
    options: {
      a: "By plane",
      b: "By bus",
      c: "By car",
      d: "By train"
    },
    correct: "d"
  },
  {
    number: 45,
    passageId: "text_3",
    prompt: "Which statement is TRUE about Dani?",
    options: {
      a: "He is staying at home next week.",
      b: "He will take photos of the sea.",
      c: "His family is visiting his grandparents next week.",
      d: "He lives with his grandparents."
    },
    correct: "c"
  },
  {
    number: 46,
    passageId: "text_3",
    prompt: "Why does Dani write “What about you?”",
    options: {
      a: "He is angry with Sinta.",
      b: "He wants Sinta to visit Malang.",
      c: "He needs Sinta’s help.",
      d: "He wants to know Sinta’s holiday plans."
    },
    correct: "d"
  },
  {
    number: 47,
    passageId: "text_4",
    prompt: "What is the main idea of Text 4?",
    options: {
      a: "Phones are cheaper for teenagers.",
      b: "Teenagers use phones a lot, and too much screen time can cause problems.",
      c: "Schools should sell phones.",
      d: "Teenagers never watch videos."
    },
    correct: "b"
  },
  {
    number: 48,
    passageId: "text_4",
    prompt: "What do doctors advise young people to do?",
    options: {
      a: "Play games before bedtime.",
      b: "Chat with friends all night.",
      c: "Put their phones away one hour before bedtime.",
      d: "Buy new phones every year."
    },
    correct: "c"
  },
  {
    number: 49,
    passageId: "text_4",
    prompt: "Too much screen time can make teenagers ____.",
    options: {
      a: "tired and less focused at school",
      b: "happier and more active",
      c: "taller and stronger",
      d: "better at sports"
    },
    correct: "a"
  },
  {
    number: 50,
    passageId: "text_4",
    prompt: "Why are some schools starting phone-free lessons?",
    options: {
      a: "Because phones are too expensive.",
      b: "So that students can talk to each other directly.",
      c: "Because teachers want to play games.",
      d: "Because students are too tired to study."
    },
    correct: "b"
  }
];
