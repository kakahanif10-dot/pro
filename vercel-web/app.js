import { GENERAL_INSTRUCTIONS, PASSAGES, QUESTIONS } from "./questions.js";

const TOTAL_QUESTIONS = 50;
const TOTAL_SECONDS = 5400; // 90 menit

const STORAGE_KEYS = {
  PROGRESS: "uas_inggris_x_progress_v1",
  ATTEMPTS: "uas_inggris_x_attempts_v1",
  PREFS: "uas_inggris_x_prefs_v1"
};

function createEmptyAnswers() {
  return Array.from({ length: TOTAL_QUESTIONS }, () => "");
}

function loadJson(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

function saveJson(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch {
    // Ignore storage quota errors
  }
}

function sanitizeAnswers(arr) {
  return Array.from({ length: TOTAL_QUESTIONS }, (_, i) => {
    const v = typeof arr?.[i] === "string" ? arr[i].trim().toLowerCase() : "";
    return v === "a" || v === "b" || v === "c" || v === "d" ? v : "";
  });
}

function countAnswered(answers) {
  return answers.filter((v) => v === "a" || v === "b" || v === "c" || v === "d").length;
}

function calculateCorrectCount(answers) {
  let count = 0;
  for (let i = 0; i < TOTAL_QUESTIONS; i++) {
    if (answers[i] === QUESTIONS[i].correct) count++;
  }
  return count;
}

function formatTimer(sec) {
  const val = Math.trunc(Number(sec) || 0);
  const sign = val < 0 ? "-" : "";
  const abs = Math.abs(val);
  const m = String(Math.floor(abs / 60)).padStart(2, "0");
  const s = String(abs % 60).padStart(2, "0");
  return `${sign}${m}:${s}`;
}

function formatDurationId(sec) {
  const safe = Math.max(0, Math.floor(sec));
  const m = Math.floor(safe / 60);
  const s = safe % 60;
  if (m > 0 && s > 0) return `${m} mnt ${s} dtk`;
  if (m > 0) return `${m} menit`;
  return `${s} detik`;
}

function escapeHtml(str) {
  return String(str ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

const defaultProgress = {
  studentName: "",
  studentNumber: "",
  answers: createEmptyAnswers(),
  flaggedQuestions: [],
  currentQuestion: 1,
  remainingSeconds: TOTAL_SECONDS,
  status: "in_progress"
};

const storedProgress = loadJson(STORAGE_KEYS.PROGRESS, null);
const storedPrefs = loadJson(STORAGE_KEYS.PREFS, {
  textScale: 1,
  showTimer: true,
  darkTheme: false
});

const state = {
  screen: "cover", // "cover" | "quiz" | "result"
  isOnline: navigator.onLine,
  progress: storedProgress
    ? {
        ...defaultProgress,
        ...storedProgress,
        answers: sanitizeAnswers(storedProgress.answers),
        flaggedQuestions: Array.isArray(storedProgress.flaggedQuestions)
          ? storedProgress.flaggedQuestions.filter((n) => n >= 1 && n <= TOTAL_QUESTIONS)
          : []
      }
    : { ...defaultProgress },
  attempts: loadJson(STORAGE_KEYS.ATTEMPTS, []),
  activeResultAttempt: null,
  prefs: {
    textScale: Number(storedPrefs?.textScale) || 1,
    showTimer: storedPrefs?.showTimer !== false,
    darkTheme: Boolean(storedPrefs?.darkTheme)
  },
  showInstructions: true,
  passageExpanded: true,
  mapFilter: "all", // "all" | "unanswered" | "flagged"
  modal: null, // null | "map" | "submit" | "settings" | "reset"
  toast: null
};

let timerHandle = null;
let timerTickCounter = 0;
let toastHandle = null;

function applyThemeAndScale() {
  document.documentElement.dataset.theme = state.prefs.darkTheme ? "dark" : "light";
  document.documentElement.style.setProperty("--text-scale", String(state.prefs.textScale));
}

function showToast(msg) {
  state.toast = msg;
  renderToastOnly();
  clearTimeout(toastHandle);
  toastHandle = setTimeout(() => {
    state.toast = null;
    renderToastOnly();
  }, 3200);
}

function renderToastOnly() {
  let el = document.getElementById("app-toast");
  if (!state.toast) {
    if (el) el.remove();
    return;
  }
  if (!el) {
    el = document.createElement("div");
    el.id = "app-toast";
    el.className = "toast";
    document.body.appendChild(el);
  }
  el.textContent = state.toast;
}

function persistLocalProgress() {
  saveJson(STORAGE_KEYS.PROGRESS, state.progress);
}

function persistLocalAttempts() {
  saveJson(STORAGE_KEYS.ATTEMPTS, state.attempts);
}

function persistPrefs() {
  saveJson(STORAGE_KEYS.PREFS, state.prefs);
  applyThemeAndScale();
}

function deleteAttempt(attemptId) {
  state.attempts = state.attempts.filter((a) => a.id !== attemptId);
  persistLocalAttempts();
  render();
  showToast("Riwayat nilai dihapus dari cache browser.");
}

function startTimer() {
  stopTimer();
  timerTickCounter = 0;
  timerHandle = setInterval(() => {
    if (state.screen !== "quiz" || state.progress.status !== "in_progress") {
      stopTimer();
      return;
    }
    state.progress.remainingSeconds -= 1;
    timerTickCounter += 1;
    const timerEl = document.getElementById("quiz-timer-text");
    const pillEl = document.getElementById("quiz-timer-pill");
    if (timerEl) timerEl.textContent = formatTimer(state.progress.remainingSeconds);
    if (pillEl) {
      if (state.progress.remainingSeconds < 0) {
        pillEl.classList.add("overtime");
        pillEl.classList.remove("urgent");
      } else if (state.progress.remainingSeconds <= 300) {
        pillEl.classList.add("urgent");
        pillEl.classList.remove("overtime");
      } else {
        pillEl.classList.remove("urgent", "overtime");
      }
    }
    if (timerTickCounter % 10 === 0) {
      persistLocalProgress();
    }
  }, 1000);
}

function stopTimer() {
  if (timerHandle) {
    clearInterval(timerHandle);
    timerHandle = null;
  }
}

function startOrResumeExam() {
  const safeName = (state.progress.studentName || "").trim();
  if (!safeName) {
    showToast("Tulis nama lengkap kamu terlebih dahulu ya!");
    const nameEl = document.getElementById("input-student-name");
    if (nameEl) {
      nameEl.focus();
      nameEl.style.borderColor = "var(--coral)";
    }
    return;
  }

  const answered = countAnswered(state.progress.answers);
  if (state.progress.status === "completed" && answered === TOTAL_QUESTIONS) {
    startFreshExam();
    return;
  }
  if (state.progress.status === "completed") {
    state.progress.status = "in_progress";
    if (state.progress.remainingSeconds <= 0) {
      state.progress.remainingSeconds = TOTAL_SECONDS;
    }
  }
  state.screen = "quiz";
  state.modal = null;
  state.passageExpanded = true;
  persistLocalProgress();
  startTimer();
  render();
}

function startFreshExam() {
  state.progress = {
    ...state.progress,
    answers: createEmptyAnswers(),
    flaggedQuestions: [],
    currentQuestion: 1,
    remainingSeconds: TOTAL_SECONDS,
    status: "in_progress"
  };
  state.screen = "quiz";
  state.modal = null;
  state.passageExpanded = true;
  persistLocalProgress();
  startTimer();
  render();
}

function selectAnswer(questionNumber, optionCode) {
  state.progress.answers[questionNumber - 1] = optionCode;
  state.progress.status = "in_progress";
  persistLocalProgress();
  render();
}

function toggleFlag(questionNumber) {
  const set = new Set(state.progress.flaggedQuestions);
  if (set.has(questionNumber)) set.delete(questionNumber);
  else set.add(questionNumber);
  state.progress.flaggedQuestions = [...set].sort((a, b) => a - b);
  persistLocalProgress();
  render();
}

function goToQuestion(num) {
  const target = Math.min(TOTAL_QUESTIONS, Math.max(1, num));
  state.progress.currentQuestion = target;
  state.passageExpanded = true;
  persistLocalProgress();
  render();
  const activePill = document.querySelector(`.q-pill[data-num="${target}"]`);
  if (activePill) {
    activePill.scrollIntoView({ behavior: "smooth", inline: "center", block: "nearest" });
  }
}

function submitExam() {
  stopTimer();
  const answers = sanitizeAnswers(state.progress.answers);
  const correctCount = calculateCorrectCount(answers);
  const score = correctCount * 2;
  const durationSeconds = Math.max(0, TOTAL_SECONDS - state.progress.remainingSeconds);
  const safeName = (state.progress.studentName || "").trim() || "Siswa Kelas X";
  const safeNumber = (state.progress.studentNumber || "").trim();

  const attempt = {
    id: `att_${Date.now()}`,
    studentName: safeName.slice(0, 80),
    studentNumber: safeNumber.slice(0, 30),
    answers,
    correctCount,
    score,
    durationSeconds,
    completedAtMs: Date.now()
  };

  state.progress.status = "completed";
  state.progress.studentName = attempt.studentName;
  persistLocalProgress();

  state.attempts = [attempt, ...state.attempts];
  persistLocalAttempts();

  state.activeResultAttempt = attempt;
  state.modal = null;
  state.screen = "result";

  render();
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function renderCacheBadge() {
  return state.isOnline
    ? `<span class="sync-badge synced">Cache Browser Aktif</span>`
    : `<span class="sync-badge offline">Mode Offline</span>`;
}

function renderCover() {
  const answeredCount = countAnswered(state.progress.answers);
  const hasDraft = state.progress.status === "in_progress" && answeredCount > 0;
  const firstName = (state.progress.studentName || "").trim().split(/\s+/)[0];

  return `
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-left">
          <span class="brand-dot" aria-hidden="true">📘</span>
          <div style="min-width:0;">
            <h1 class="topbar-title">UAS Inggris Kelas X</h1>
            <div class="topbar-sub">Ruang Ujian Siswa • Offline Ready</div>
          </div>
        </div>
        <div class="topbar-actions">
          ${renderCacheBadge()}
          <button class="icon-btn" id="btn-open-settings" aria-label="Pengaturan Tampilan" title="Pengaturan">⚙️</button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <section class="hero-white-card">
        <div class="hero-ribbon"></div>
        <span class="hero-kicker">✨ KELAS X / FASE E • SEMESTER GANJIL</span>
        <h2 class="hero-title">${firstName ? `Semangat Ujian, ${escapeHtml(firstName)}! 👋` : "Ujian Akhir Semester Bahasa Inggris"}</h2>
        <p class="hero-sub">Tahun Pelajaran 2026/2027 • Kerjakan dengan tenang dan teliti. Progres tersimpan otomatis di cache browser tanpa perlu login.</p>
        <div class="chip-row">
          <span class="pastel-chip sky">📝 50 Pilihan Ganda</span>
          <span class="pastel-chip amber">⏱️ 90 Menit</span>
          <span class="pastel-chip mint">🎯 2 Poin / Soal</span>
          <span class="pastel-chip coral">💾 Auto Simpan</span>
        </div>
      </section>

      <section class="card">
        <h3 class="card-title">Identitas Peserta</h3>
        <div class="field-group">
          <label class="field-label" for="input-student-name">Nama Lengkap</label>
          <input class="input" id="input-student-name" type="text" maxlength="80" placeholder="Tulis nama lengkap kamu" value="${escapeHtml(state.progress.studentName)}" />
        </div>
        <div class="field-group">
          <label class="field-label" for="input-student-number">No. Absen / Kelas</label>
          <input class="input" id="input-student-number" type="text" maxlength="30" placeholder="Contoh: 14 / X-E2" value="${escapeHtml(state.progress.studentNumber)}" />
        </div>

        ${
          hasDraft
            ? `
            <div class="progress-box">
              <div style="display:flex;justify-content:space-between;font-size:13px;font-weight:700;">
                <span>Progres Terakhir</span>
                <span style="color:${state.progress.remainingSeconds < 0 ? "#dc2626" : "var(--sky)"};">${answeredCount} / 50 Soal • ${formatTimer(state.progress.remainingSeconds)}</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill" style="width:${(answeredCount / TOTAL_QUESTIONS) * 100}%"></div>
              </div>
            </div>
          `
            : ""
        }

        <button class="btn btn-primary" id="btn-start-exam">
          ${hasDraft ? `Lanjutkan Ujian (${answeredCount}/50)` : "Mulai Mengerjakan Soal"}
        </button>

        ${
          hasDraft
            ? `<button class="btn btn-outline" id="btn-ask-reset" style="margin-top:10px;">Mulai Ulang dari Nomor 1</button>`
            : ""
        }
      </section>

      <section class="card">
        <div style="display:flex;justify-content:space-between;align-items:center;cursor:pointer;" id="btn-toggle-instructions">
          <h3 class="card-title" style="margin:0;">Petunjuk Umum</h3>
          <span style="font-size:13px;font-weight:700;color:var(--sky);">${state.showInstructions ? "Tutup" : "Buka"}</span>
        </div>
        ${
          state.showInstructions
            ? `
            <div class="instruction-list" style="margin-top:14px;">
              ${GENERAL_INSTRUCTIONS.map(
                (item, idx) => `
                <div class="instruction-item">
                  <span class="instruction-num c${(idx % 4) + 1}">${idx + 1}</span>
                  <span>${escapeHtml(item)}</span>
                </div>
              `
              ).join("")}
            </div>
          `
            : ""
        }
      </section>

      <section class="card">
        <h3 class="card-title">Riwayat Nilai Kamu</h3>
        ${
          state.attempts.length === 0
            ? `<p style="margin:0;font-size:13.5px;color:var(--ink-muted);line-height:1.5;">Belum ada riwayat ujian. Setelah menyelesaikan seluruh 50 soal, skormu akan otomatis tercatat di cache browser.</p>`
            : `
            <div style="display:flex;flex-direction:column;gap:10px;">
              ${state.attempts
                .map((att) => {
                  const pass = att.score >= 70;
                  const dt = new Date(att.completedAtMs || Date.now()).toLocaleString("id-ID", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit"
                  });
                  return `
                  <div style="display:flex;align-items:center;gap:8px;">
                    <button class="history-item" data-open-attempt="${escapeHtml(att.id)}">
                      <span class="score-pill ${pass ? "pass" : "warn"}">${att.score}</span>
                      <div style="flex:1;min-width:0;">
                        <div style="font-weight:700;font-size:14.5px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">${escapeHtml(att.studentName || "Siswa Kelas X")}</div>
                        <div style="font-size:12.5px;font-weight:700;color:${pass ? "var(--mint)" : "var(--amber)"};">${att.correctCount}/50 Benar • ${formatDurationId(att.durationSeconds)}</div>
                        <div style="font-size:11.5px;color:var(--ink-muted);">${escapeHtml(dt)}</div>
                      </div>
                    </button>
                    <button class="icon-btn" data-delete-attempt="${escapeHtml(att.id)}" aria-label="Hapus riwayat" title="Hapus">🗑️</button>
                  </div>
                `;
                })
                .join("")}
            </div>
          `
        }
      </section>
    </main>
  `;
}

function renderQuiz() {
  const num = Math.min(TOTAL_QUESTIONS, Math.max(1, Number(state.progress.currentQuestion) || 1));
  const q = QUESTIONS[num - 1];
  const passage = q.passageId ? PASSAGES[q.passageId] : null;
  const selected = state.progress.answers[num - 1];
  const isFlagged = state.progress.flaggedQuestions.includes(num);
  const answeredCount = countAnswered(state.progress.answers);

  return `
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-left">
          <button class="icon-btn" id="btn-exit-quiz" aria-label="Kembali ke Beranda">←</button>
          ${
            state.prefs.showTimer
              ? `<span class="timer-pill ${state.progress.remainingSeconds < 0 ? "overtime" : state.progress.remainingSeconds <= 300 ? "urgent" : ""}" id="quiz-timer-pill">⏱ <span id="quiz-timer-text">${formatTimer(state.progress.remainingSeconds)}</span></span>`
              : ""
          }
          ${renderCacheBadge()}
        </div>
        <div class="topbar-actions">
          <button class="icon-btn" id="btn-open-map" aria-label="Peta 50 Soal" title="Daftar 50 Soal">⊞</button>
        </div>
      </div>
    </header>

    <div class="quiz-strip-wrap">
      <div class="quiz-strip-inner">
        <div class="quiz-strip-meta">
          <span style="color:var(--sky);">Soal ${num} dari ${TOTAL_QUESTIONS}</span>
          <span style="color:var(--ink-muted);">${answeredCount}/50 Terjawab</span>
        </div>
        <div style="padding:0 14px;">
          <div class="progress-track" style="margin-top:0;height:6px;">
            <div class="progress-fill" style="width:${(answeredCount / TOTAL_QUESTIONS) * 100}%"></div>
          </div>
        </div>
        <div class="quiz-strip-scroll">
          ${QUESTIONS.map((item) => {
            const ans = state.progress.answers[item.number - 1];
            const flg = state.progress.flaggedQuestions.includes(item.number);
            const cls = [
              "q-pill",
              item.number === num ? "active" : flg ? "flagged" : ans ? "answered" : ""
            ].join(" ");
            return `<button class="${cls}" data-num="${item.number}">${item.number}</button>`;
          }).join("")}
        </div>
      </div>
    </div>

    <main class="main-content has-bottom-bar">
      ${
        passage
          ? `
          <section class="passage-card">
            <button class="passage-header" id="btn-toggle-passage">
              <div>
                <div style="font-size:13.5px;font-weight:700;">📖 ${escapeHtml(passage.instruction)}</div>
                <div style="font-size:11.5px;opacity:0.85;">${state.passageExpanded ? "Ketuk untuk sembunyikan teks" : `Ketuk untuk baca ${escapeHtml(passage.title)}`}</div>
              </div>
              <span style="font-weight:700;">${state.passageExpanded ? "▲" : "▼"}</span>
            </button>
            ${
              state.passageExpanded
                ? `<div class="passage-body">${passage.header ? `<strong style="display:block;margin-bottom:6px;color:var(--sky);">${escapeHtml(passage.header)}</strong>` : ""}${escapeHtml(passage.body)}</div>`
                : ""
            }
          </section>
        `
          : ""
      }

      <section class="card">
        <div style="display:flex;justify-content:space-between;align-items:center;gap:8px;">
          <span class="pastel-chip sky" style="padding:4px 10px;">Pertanyaan #${num}</span>
          ${isFlagged ? `<span class="pastel-chip amber" style="padding:4px 10px;">⚑ Ragu-ragu</span>` : ""}
        </div>
        <p class="question-prompt">${num}. ${escapeHtml(q.prompt)}</p>
      </section>

      <section class="options-stack">
        ${["a", "b", "c", "d"]
          .map((code) => {
            const isSel = selected === code;
            return `
            <button class="option-btn ${isSel ? "selected" : ""}" data-option="${code}">
              <span class="option-badge">${isSel ? "✓" : code}</span>
              <span class="option-text">${code}. ${escapeHtml(q.options[code])}</span>
            </button>
          `;
          })
          .join("")}
      </section>
    </main>

    <footer class="bottom-bar">
      <div class="bottom-bar-inner">
        ${
          answeredCount === TOTAL_QUESTIONS
            ? `<button class="ready-banner" id="btn-ready-submit">🎉 Seluruh 50 soal terjawab! Ketuk untuk lihat skor</button>`
            : ""
        }
        <div class="bottom-nav-row">
          <button class="btn btn-outline" id="btn-prev-q" ${num <= 1 ? "disabled" : ""}>← Kembali</button>
          <button class="btn ${isFlagged ? "btn-amber" : "btn-outline"}" id="btn-flag-q" style="width:auto;padding:12px 14px;">⚑ Ragu</button>
          ${
            num < TOTAL_QUESTIONS
              ? `<button class="btn btn-primary" id="btn-next-q">Lanjut →</button>`
              : `<button class="btn btn-mint" id="btn-finish-q">✓ Selesai</button>`
          }
        </div>
      </div>
    </footer>
  `;
}

function renderResult() {
  const att = state.activeResultAttempt;
  if (!att) {
    state.screen = "cover";
    return renderCover();
  }

  const answeredCount = countAnswered(att.answers);
  const unanswered = TOTAL_QUESTIONS - answeredCount;
  const wrong = Math.max(0, TOTAL_QUESTIONS - att.correctCount - unanswered);
  const predicate =
    att.score >= 90
      ? "Luar Biasa! (Predikat A)"
      : att.score >= 75
        ? "Bagus Sekali! (Predikat B)"
        : att.score >= 60
          ? "Cukup Baik (Predikat C)"
          : "Tetap Semangat Latihan!";

  const dt = new Date(att.completedAtMs || Date.now()).toLocaleString("id-ID", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });

  return `
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-left">
          <button class="icon-btn" id="btn-result-home" aria-label="Beranda">←</button>
          <h1 class="topbar-title">Hasil Ujian Kamu</h1>
        </div>
        <div class="topbar-actions">
          ${renderCacheBadge()}
        </div>
      </div>
    </header>

    <main class="main-content">
      <section class="score-white-hero">
        <div class="hero-ribbon"></div>
        <span class="ss-exam-tag">BUKTI HASIL UAS BAHASA INGGRIS • KELAS X</span>
        <span class="pastel-chip mint">🏆 ${predicate}</span>

        <div class="score-circle">
          <span class="score-big">${att.score}</span>
          <span style="font-size:13px;font-weight:700;color:var(--ink-muted);">dari 100</span>
        </div>

        <div class="ss-identity-box">
          <div class="ss-row">
            <span class="ss-label">Nama Siswa</span>
            <span class="ss-val">${escapeHtml(att.studentName || "Siswa Kelas X")}</span>
          </div>
          <div class="ss-row">
            <span class="ss-label">No. Absen / Kelas</span>
            <span class="ss-val">${att.studentNumber ? escapeHtml(att.studentNumber) : "—"}</span>
          </div>
          <div class="ss-row">
            <span class="ss-label">Waktu Selesai</span>
            <span class="ss-val">${escapeHtml(dt)}</span>
          </div>
        </div>

        <div class="stats-grid">
          <div class="stat-box s1"><div class="stat-val">${att.correctCount}</div><div class="stat-lbl">Benar</div></div>
          <div class="stat-box s2"><div class="stat-val">${wrong}</div><div class="stat-lbl">Salah</div></div>
          <div class="stat-box s3"><div class="stat-val">${unanswered}</div><div class="stat-lbl">Kosong</div></div>
          <div class="stat-box s4"><div class="stat-val">${formatTimer(att.durationSeconds)}</div><div class="stat-lbl">Durasi</div></div>
        </div>

        <p class="ss-hint">📸 Silakan screenshot kartu ini sebagai bukti nilai ke guru.</p>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;">
          <button class="btn btn-outline" id="btn-result-cover">Beranda</button>
          <button class="btn btn-primary" id="btn-result-retake">Kerjakan Lagi</button>
        </div>
      </section>

      <section class="card">
        <h3 class="card-title" style="margin-bottom:4px;">Rekap Jawabanmu (1–50)</h3>
        <p style="margin:0 0 12px;font-size:12.5px;color:var(--ink-muted);">Ringkasan pilihan yang kamu kirim pada ujian ini.</p>
        <div class="student-recap-grid">
          ${QUESTIONS.map((q) => {
            const chosen = att.answers[q.number - 1];
            return `<div class="recap-cell ${chosen ? "filled" : "empty"}">${q.number}. ${chosen || "—"}</div>`;
          }).join("")}
        </div>
      </section>
    </main>
  `;
}

function renderModal() {
  if (!state.modal) return "";

  if (state.modal === "map") {
    const num = Number(state.progress.currentQuestion) || 1;
    const answeredCount = countAnswered(state.progress.answers);
    const list = QUESTIONS.filter((q) => {
      if (state.mapFilter === "unanswered") return !state.progress.answers[q.number - 1];
      if (state.mapFilter === "flagged") return state.progress.flaggedQuestions.includes(q.number);
      return true;
    });

    return `
      <div class="backdrop" id="modal-backdrop">
        <div class="sheet" role="dialog" aria-modal="true" aria-label="Peta 50 Soal">
          <div style="display:flex;justify-content:space-between;align-items:center;gap:8px;">
            <div>
              <h3 class="card-title" style="margin:0;">Peta 50 Soal</h3>
              <span style="font-size:12.5px;color:var(--ink-muted);">${answeredCount}/50 Terjawab • ${state.progress.flaggedQuestions.length} Ragu-ragu</span>
            </div>
            <button class="btn btn-mint" id="btn-sheet-submit" style="width:auto;min-height:40px;padding:8px 14px;font-size:13px;">Kumpulkan</button>
          </div>
          <div class="filter-row">
            <button class="filter-chip ${state.mapFilter === "all" ? "active" : ""}" data-mfilter="all">Semua (50)</button>
            <button class="filter-chip ${state.mapFilter === "unanswered" ? "active" : ""}" data-mfilter="unanswered">Belum Dijawab</button>
            <button class="filter-chip ${state.mapFilter === "flagged" ? "active" : ""}" data-mfilter="flagged">Ragu-ragu</button>
          </div>
          <div class="grid-5">
            ${
              list.length === 0
                ? `<div style="grid-column:1/-1;text-align:center;padding:24px;color:var(--ink-muted);font-size:13.5px;">Tidak ada soal pada kategori ini.</div>`
                : list
                    .map((q) => {
                      const ans = state.progress.answers[q.number - 1];
                      const flg = state.progress.flaggedQuestions.includes(q.number);
                      const cls = [
                        "map-cell",
                        flg ? "flagged" : ans ? "answered" : "",
                        q.number === num ? "current" : ""
                      ].join(" ");
                      return `<button class="${cls}" data-map-jump="${q.number}">${ans ? `${q.number}.${ans}` : q.number}</button>`;
                    })
                    .join("")
            }
          </div>
        </div>
      </div>
    `;
  }

  if (state.modal === "submit") {
    const answeredCount = countAnswered(state.progress.answers);
    const unanswered = TOTAL_QUESTIONS - answeredCount;
    const firstEmptyIdx = state.progress.answers.findIndex((a) => !a);
    const firstEmptyNum = firstEmptyIdx >= 0 ? firstEmptyIdx + 1 : null;

    return `
      <div class="backdrop center" id="modal-backdrop">
        <div class="dialog" role="dialog" aria-modal="true">
          <h3 class="card-title" style="margin:0;">
            ${unanswered === 0 ? "Kumpulkan Semua 50 Jawaban?" : `Masih Ada ${unanswered} Soal Kosong`}
          </h3>
          <p style="margin:0;font-size:14px;line-height:1.5;color:var(--ink-muted);">
            ${
              unanswered === 0
                ? "Seluruh 50 soal telah terjawab. Skor akhirmu akan langsung muncul setelah dikumpulkan."
                : `Kamu baru menjawab ${answeredCount} dari 50 soal. Sebaiknya lengkapi seluruh soal sebelum mengumpulkan.`
            }
          </p>
          <div style="display:flex;gap:10px;justify-content:flex-end;">
            ${
              firstEmptyNum
                ? `<button class="btn btn-outline" id="btn-jump-empty" data-empty="${firstEmptyNum}">Ke Soal #${firstEmptyNum}</button>`
                : `<button class="btn btn-outline" id="btn-close-modal">Periksa Lagi</button>`
            }
            <button class="btn btn-mint" id="btn-confirm-submit">${unanswered === 0 ? "Lihat Skor" : "Tetap Kumpulkan"}</button>
          </div>
        </div>
      </div>
    `;
  }

  if (state.modal === "settings") {
    return `
      <div class="backdrop center" id="modal-backdrop">
        <div class="dialog" role="dialog" aria-modal="true">
          <h3 class="card-title" style="margin:0;">Pengaturan Tampilan</h3>
          <div>
            <div style="display:flex;justify-content:space-between;font-size:13.5px;font-weight:600;margin-bottom:6px;">
              <span>Ukuran Teks Soal</span>
              <span id="label-text-scale" style="color:var(--sky);">${Math.round(state.prefs.textScale * 100)}%</span>
            </div>
            <input type="range" id="range-text-scale" min="0.9" max="1.3" step="0.1" value="${state.prefs.textScale}" style="width:100%;" />
          </div>
          <label style="display:flex;justify-content:space-between;align-items:center;font-size:14px;font-weight:600;cursor:pointer;">
            <span>Tampilkan Timer 90 Menit</span>
            <input type="checkbox" id="chk-show-timer" ${state.prefs.showTimer ? "checked" : ""} />
          </label>
          <label style="display:flex;justify-content:space-between;align-items:center;font-size:14px;font-weight:600;cursor:pointer;">
            <span>Mode Gelap (Dark Mode)</span>
            <input type="checkbox" id="chk-dark-theme" ${state.prefs.darkTheme ? "checked" : ""} />
          </label>
          <button class="btn btn-primary" id="btn-close-modal">Tutup</button>
        </div>
      </div>
    `;
  }

  if (state.modal === "reset") {
    return `
      <div class="backdrop center" id="modal-backdrop">
        <div class="dialog" role="dialog" aria-modal="true">
          <h3 class="card-title" style="margin:0;">Mulai Ulang Ujian?</h3>
          <p style="margin:0;font-size:14px;line-height:1.5;color:var(--ink-muted);">
            Semua jawaban sementara pada sesi ini akan dihapus dan waktu direset ke 90 menit.
          </p>
          <div style="display:flex;gap:10px;">
            <button class="btn btn-outline" id="btn-close-modal">Batal</button>
            <button class="btn btn-danger-outline" id="btn-confirm-reset">Reset & Mulai</button>
          </div>
        </div>
      </div>
    `;
  }

  return "";
}

function render() {
  const root = document.getElementById("app");
  if (!root) return;

  let html = "";
  if (state.screen === "cover") html = renderCover();
  else if (state.screen === "quiz") html = renderQuiz();
  else html = renderResult();

  root.innerHTML = html + renderModal();
  bindEvents();
  renderToastOnly();
}

function bindEvents() {
  document.getElementById("btn-open-settings")?.addEventListener("click", () => {
    state.modal = "settings";
    render();
  });

  const nameInput = document.getElementById("input-student-name");
  nameInput?.addEventListener("input", (e) => {
    state.progress.studentName = e.target.value.slice(0, 80);
    persistLocalProgress();
  });

  const numInput = document.getElementById("input-student-number");
  numInput?.addEventListener("input", (e) => {
    state.progress.studentNumber = e.target.value.slice(0, 30);
    persistLocalProgress();
  });

  document.getElementById("btn-start-exam")?.addEventListener("click", startOrResumeExam);
  document.getElementById("btn-ask-reset")?.addEventListener("click", () => {
    state.modal = "reset";
    render();
  });

  document.getElementById("btn-toggle-instructions")?.addEventListener("click", () => {
    state.showInstructions = !state.showInstructions;
    render();
  });

  document.querySelectorAll("[data-open-attempt]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-open-attempt");
      const found = state.attempts.find((a) => a.id === id);
      if (found) {
        state.activeResultAttempt = found;
        state.screen = "result";
        render();
        window.scrollTo({ top: 0, behavior: "smooth" });
      }
    });
  });

  document.querySelectorAll("[data-delete-attempt]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-delete-attempt");
      if (id) deleteAttempt(id);
    });
  });

  // Quiz events
  document.getElementById("btn-exit-quiz")?.addEventListener("click", () => {
    stopTimer();
    persistLocalProgress();
    state.screen = "cover";
    render();
  });

  document.getElementById("btn-open-map")?.addEventListener("click", () => {
    state.modal = "map";
    render();
  });

  document.getElementById("btn-toggle-passage")?.addEventListener("click", () => {
    state.passageExpanded = !state.passageExpanded;
    render();
  });

  document.querySelectorAll(".q-pill[data-num]").forEach((btn) => {
    btn.addEventListener("click", () => {
      goToQuestion(Number(btn.getAttribute("data-num")));
    });
  });

  document.querySelectorAll(".option-btn[data-option]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const code = btn.getAttribute("data-option");
      selectAnswer(Number(state.progress.currentQuestion) || 1, code);
    });
  });

  document.getElementById("btn-prev-q")?.addEventListener("click", () => {
    goToQuestion((Number(state.progress.currentQuestion) || 1) - 1);
  });

  document.getElementById("btn-next-q")?.addEventListener("click", () => {
    goToQuestion((Number(state.progress.currentQuestion) || 1) + 1);
  });

  document.getElementById("btn-flag-q")?.addEventListener("click", () => {
    toggleFlag(Number(state.progress.currentQuestion) || 1);
  });

  document.getElementById("btn-finish-q")?.addEventListener("click", () => {
    state.modal = "submit";
    render();
  });

  document.getElementById("btn-ready-submit")?.addEventListener("click", () => {
    state.modal = "submit";
    render();
  });

  // Result events
  document.getElementById("btn-result-home")?.addEventListener("click", () => {
    state.screen = "cover";
    render();
  });
  document.getElementById("btn-result-cover")?.addEventListener("click", () => {
    state.screen = "cover";
    render();
  });
  document.getElementById("btn-result-retake")?.addEventListener("click", startFreshExam);

  // Modal events
  document.getElementById("modal-backdrop")?.addEventListener("click", (e) => {
    if (e.target.id === "modal-backdrop") {
      state.modal = null;
      render();
    }
  });

  document.getElementById("btn-close-modal")?.addEventListener("click", () => {
    state.modal = null;
    render();
  });

  document.getElementById("btn-sheet-submit")?.addEventListener("click", () => {
    state.modal = "submit";
    render();
  });

  document.querySelectorAll("[data-mfilter]").forEach((btn) => {
    btn.addEventListener("click", () => {
      state.mapFilter = btn.getAttribute("data-mfilter");
      render();
    });
  });

  document.querySelectorAll("[data-map-jump]").forEach((btn) => {
    btn.addEventListener("click", () => {
      const n = Number(btn.getAttribute("data-map-jump"));
      state.modal = null;
      goToQuestion(n);
    });
  });

  document.getElementById("btn-jump-empty")?.addEventListener("click", (e) => {
    const n = Number(e.currentTarget.getAttribute("data-empty"));
    state.modal = null;
    goToQuestion(n);
  });

  document.getElementById("btn-confirm-submit")?.addEventListener("click", submitExam);
  document.getElementById("btn-confirm-reset")?.addEventListener("click", startFreshExam);

  document.getElementById("range-text-scale")?.addEventListener("input", (e) => {
    state.prefs.textScale = Number(e.target.value) || 1;
    persistPrefs();
    const lbl = document.getElementById("label-text-scale");
    if (lbl) lbl.textContent = `${Math.round(state.prefs.textScale * 100)}%`;
  });

  document.getElementById("chk-show-timer")?.addEventListener("change", (e) => {
    state.prefs.showTimer = Boolean(e.target.checked);
    persistPrefs();
  });

  document.getElementById("chk-dark-theme")?.addEventListener("change", (e) => {
    state.prefs.darkTheme = Boolean(e.target.checked);
    persistPrefs();
  });
}

window.addEventListener("online", () => {
  state.isOnline = true;
  render();
});

window.addEventListener("offline", () => {
  state.isOnline = false;
  render();
});

if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker.register("/sw.js").catch(() => {});
  });
}

applyThemeAndScale();
render();
