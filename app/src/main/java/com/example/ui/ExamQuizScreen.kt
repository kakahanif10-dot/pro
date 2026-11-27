package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ExamPreferences
import com.example.data.ExamQuestionBank
import com.example.model.CloudSyncState
import com.example.model.ExamProgress
import com.example.model.OptionKey
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.EmeraldCorrectContainer
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldCorrectContainer

private enum class QuestionMapFilter(val label: String) {
    ALL("Semua (50)"),
    UNANSWERED("Belum Dijawab"),
    FLAGGED("Ragu-ragu")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamQuizScreen(
    activeExam: ActiveExamState,
    syncState: CloudSyncState,
    preferences: ExamPreferences,
    onSelectAnswer: (Int, OptionKey) -> Unit,
    onToggleFlag: (Int) -> Unit,
    onGoToQuestion: (Int) -> Unit,
    onExitToCover: () -> Unit,
    onSubmitExam: () -> Unit
) {
    BackHandler { onExitToCover() }

    val questionIndex = (activeExam.currentQuestion - 1).coerceIn(0, ExamProgress.TOTAL_QUESTIONS - 1)
    val question = ExamQuestionBank.questions[questionIndex]
    val passage = question.passageId?.let { ExamQuestionBank.passages[it] }
    val selectedOption = OptionKey.fromCode(activeExam.answers.getOrNull(questionIndex))
    val isFlagged = activeExam.flaggedQuestions.contains(question.number)

    var passageExpanded by remember(question.passageId) { mutableStateOf(true) }
    var showQuestionMapSheet by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var mapFilter by remember { mutableStateOf(QuestionMapFilter.ALL) }

    val stripListState = rememberLazyListState()
    LaunchedEffect(question.number) {
        val targetIndex = (question.number - 2).coerceAtLeast(0)
        stripListState.animateScrollToItem(targetIndex)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onExitToCover,
                        modifier = Modifier.testTag("back_to_cover_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Kembali ke Beranda"
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (preferences.showTimer) {
                            val isOvertime = activeExam.remainingSeconds < 0L
                            val isWarning = activeExam.remainingSeconds in 0L..300L
                            val pillBg = when {
                                isOvertime -> Color(0xFFFEE2E2)
                                isWarning -> AmberContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                            val pillFg = when {
                                isOvertime -> Color(0xFFDC2626)
                                isWarning -> OnAmberContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                            Surface(
                                shape = CircleShape,
                                color = pillBg,
                                border = if (isOvertime) BorderStroke(1.5.dp, Color(0xFFEF4444)) else null
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Timer,
                                        contentDescription = null,
                                        tint = pillFg,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatTimer(activeExam.remainingSeconds),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = pillFg
                                    )
                                }
                            }
                        }
                        SyncStatusBadge(syncState = syncState)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showQuestionMapSheet = true },
                        modifier = Modifier.testTag("open_question_grid_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GridView,
                            contentDescription = stringResource(R.string.cd_question_grid)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (activeExam.answeredCount == ExamProgress.TOTAL_QUESTIONS) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = EmeraldCorrectContainer,
                            onClick = { showSubmitDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .testTag("all_answered_submit_banner")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = null,
                                    tint = OnEmeraldCorrectContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Seluruh 50 soal terjawab! Ketuk untuk lihat skor",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnEmeraldCorrectContainer
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 640.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        OutlinedButton(
                            onClick = { onGoToQuestion(question.number - 1) },
                            enabled = question.number > 1,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .height(48.dp)
                                .weight(1f)
                                .testTag("prev_question_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kembali", maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { onToggleFlag(question.number) },
                            shape = MaterialTheme.shapes.medium,
                            colors = if (isFlagged) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = AmberContainer,
                                    contentColor = OnAmberContainer
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isFlagged) AmberTertiary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("flag_question_button")
                        ) {
                            Icon(
                                imageVector = if (isFlagged) Icons.Rounded.Flag else Icons.Rounded.OutlinedFlag,
                                contentDescription = stringResource(R.string.cd_flag_question),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ragu")
                        }

                        if (question.number < ExamProgress.TOTAL_QUESTIONS) {
                            Button(
                                onClick = { onGoToQuestion(question.number + 1) },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .height(48.dp)
                                    .weight(1.1f)
                                    .testTag("next_question_button")
                            ) {
                                Text("Lanjut", maxLines = 1)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = { showSubmitDialog = true },
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldCorrect,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .weight(1.1f)
                                    .testTag("finish_exam_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Selesai", maxLines = 1)
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Progress & Quick Question Strip
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Soal ${question.number} dari ${ExamProgress.TOTAL_QUESTIONS}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${activeExam.answeredCount}/50 Terjawab",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        LinearProgressIndicator(
                            progress = { activeExam.answeredCount / ExamProgress.TOTAL_QUESTIONS.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            state = stripListState,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ExamQuestionBank.questions, key = { it.number }) { q ->
                                val isCurrent = q.number == question.number
                                val isAnswered = OptionKey.fromCode(
                                    activeExam.answers.getOrNull(q.number - 1)
                                ) != null
                                val isQFlagged = activeExam.flaggedQuestions.contains(q.number)

                                val pillBg = when {
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    isQFlagged -> AmberContainer
                                    isAnswered -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                val pillFg = when {
                                    isCurrent -> MaterialTheme.colorScheme.onPrimary
                                    isQFlagged -> OnAmberContainer
                                    isAnswered -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = pillBg,
                                    border = if (isCurrent) {
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                    } else {
                                        null
                                    },
                                    onClick = { onGoToQuestion(q.number) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${q.number}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isCurrent || isAnswered) FontWeight.Bold else FontWeight.Medium,
                                            color = pillFg
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Scrollable Question Body
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    if (passage != null) {
                        PassageCard(
                            passage = passage,
                            textScale = preferences.textScale,
                            expanded = passageExpanded,
                            onToggleExpand = { passageExpanded = !passageExpanded }
                        )
                    }

                    // Question Prompt Card
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "Pertanyaan #${question.number}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                if (isFlagged) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AmberContainer
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Flag,
                                                contentDescription = null,
                                                tint = OnAmberContainer,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Ragu-ragu",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = OnAmberContainer,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "${question.number}. ${question.prompt}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = (17.5f * preferences.textScale).sp,
                                    lineHeight = (25f * preferences.textScale).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 4 Multiple-Choice Options (a, b, c, d)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OptionKey.entries.forEach { optionKey ->
                            val optionText = question.options[optionKey].orEmpty()
                            val isSelected = selectedOption == optionKey
                            OptionCard(
                                optionKey = optionKey,
                                text = optionText,
                                selected = isSelected,
                                textScale = preferences.textScale,
                                onClick = { onSelectAnswer(question.number, optionKey) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showQuestionMapSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val filteredQuestions = remember(mapFilter, activeExam.answers, activeExam.flaggedQuestions) {
            ExamQuestionBank.questions.filter { q ->
                when (mapFilter) {
                    QuestionMapFilter.ALL -> true
                    QuestionMapFilter.UNANSWERED ->
                        OptionKey.fromCode(activeExam.answers.getOrNull(q.number - 1)) == null
                    QuestionMapFilter.FLAGGED ->
                        activeExam.flaggedQuestions.contains(q.number)
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showQuestionMapSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Peta 50 Soal Ujian",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${activeExam.answeredCount}/50 Terjawab • ${activeExam.flaggedQuestions.size} Ragu-ragu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            showQuestionMapSheet = false
                            showSubmitDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldCorrect),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.testTag("sheet_submit_button")
                    ) {
                        Text("Kumpulkan")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuestionMapFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = mapFilter == filter,
                            onClick = { mapFilter = filter },
                            label = { Text(filter.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredQuestions.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Text(
                            text = "Tidak ada soal pada kategori ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        items(filteredQuestions, key = { it.number }) { q ->
                            val isCurrent = q.number == question.number
                            val ans = OptionKey.fromCode(activeExam.answers.getOrNull(q.number - 1))
                            val flagged = activeExam.flaggedQuestions.contains(q.number)

                            val bg = when {
                                flagged -> AmberContainer
                                ans != null -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val fg = when {
                                flagged -> OnAmberContainer
                                ans != null -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = bg,
                                border = if (isCurrent) {
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    null
                                },
                                onClick = {
                                    onGoToQuestion(q.number)
                                    showQuestionMapSheet = false
                                },
                                modifier = Modifier.height(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (ans != null) "${q.number}.${ans.code}" else "${q.number}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = fg
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSubmitDialog) {
        val unansweredCount = ExamProgress.TOTAL_QUESTIONS - activeExam.answeredCount
        val firstEmpty = activeExam.firstUnansweredNumber
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Text(
                    text = if (unansweredCount == 0) {
                        "Kumpulkan Semua 50 Jawaban?"
                    } else {
                        "Masih Ada $unansweredCount Soal Kosong"
                    }
                )
            },
            text = {
                Text(
                    text = if (unansweredCount == 0) {
                        "Seluruh 50 soal sudah terjawab. Skor akhirmu akan langsung muncul setelah dikumpulkan."
                    } else {
                        "Kamu baru menjawab ${activeExam.answeredCount} dari 50 soal. Disarankan melengkapi seluruh soal sebelum mengumpulkan agar skor maksimal."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        onSubmitExam()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldCorrect),
                    modifier = Modifier.testTag("confirm_submit_exam_button")
                ) {
                    Text(if (unansweredCount == 0) "Lihat Skor" else "Tetap Kumpulkan")
                }
            },
            dismissButton = {
                if (firstEmpty != null) {
                    TextButton(
                        onClick = {
                            showSubmitDialog = false
                            onGoToQuestion(firstEmpty)
                        }
                    ) {
                        Text("Ke Soal #$firstEmpty")
                    }
                } else {
                    TextButton(onClick = { showSubmitDialog = false }) {
                        Text("Periksa Lagi")
                    }
                }
            }
        )
    }
}

@Composable
private fun OptionCard(
    optionKey: OptionKey,
    text: String,
    selected: Boolean,
    textScale: Float,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(180),
        label = "option_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(180),
        label = "option_border"
    )

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = bgColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("option_${optionKey.code}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = optionKey.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "${optionKey.label}. $text",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = (15.5f * textScale).sp,
                    lineHeight = (22f * textScale).sp
                ),
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
