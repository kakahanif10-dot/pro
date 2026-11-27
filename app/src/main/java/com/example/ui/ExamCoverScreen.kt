package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ExamPreferences
import com.example.data.ExamQuestionBank
import com.example.model.CloudSyncState
import com.example.model.ExamAttempt
import com.example.model.ExamProgress
import com.example.model.UiState
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.EmeraldCorrectContainer
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldCorrectContainer
import com.example.ui.theme.OnIndigoContainer
import com.example.ui.theme.OnSkyContainer
import com.example.ui.theme.SkyContainer
import com.example.ui.theme.SkyPrimary
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExamCoverScreen(
    activeExam: ActiveExamState,
    attemptsState: UiState<List<ExamAttempt>>,
    syncState: CloudSyncState,
    preferences: ExamPreferences,
    onUpdateName: (String) -> Unit,
    onUpdateNumber: (String) -> Unit,
    onStartOrResume: () -> Unit,
    onStartFresh: () -> Unit,
    onOpenAttempt: (ExamAttempt) -> Unit,
    onDeleteAttempt: (String) -> Unit,
    onSetTextScale: (Float) -> Unit,
    onSetShowTimer: (Boolean) -> Unit,
    onSetDarkTheme: (Boolean) -> Unit
) {
    var showInstructions by remember { mutableStateOf(true) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showNameError by remember { mutableStateOf(false) }
    var attemptPendingDelete by remember { mutableStateOf<ExamAttempt?>(null) }

    val hasInProgressDraft = activeExam.status == ExamProgress.STATUS_IN_PROGRESS &&
        activeExam.answeredCount > 0
    val firstName = activeExam.studentName.trim().split(Regex("\\s+")).firstOrNull().orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(SkyPrimary, IndigoSecondary)
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoStories,
                                contentDescription = stringResource(R.string.cd_app_logo),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Ruang Ujian Siswa • Offline Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    SyncStatusBadge(
                        syncState = syncState,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Pengaturan Tampilan"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 620.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Colourful White Student Hero Card
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            RainbowAccentRibbon()
                            Column(modifier = Modifier.padding(20.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = IndigoContainer
                                ) {
                                    Text(
                                        text = "KELAS X / FASE E • SEMESTER GANJIL",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = OnIndigoContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (firstName.isNotBlank()) {
                                        "Semangat Ujian, $firstName!"
                                    } else {
                                        "Ujian Akhir Semester Bahasa Inggris"
                                    },
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tahun Pelajaran 2026/2027 • Kerjakan dengan tenang dan teliti. Progres tersimpan otomatis di perangkatmu.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PastelInfoChip(
                                        icon = Icons.Rounded.Assignment,
                                        label = "50 Pilihan Ganda",
                                        containerColor = SkyContainer,
                                        contentColor = OnSkyContainer
                                    )
                                    PastelInfoChip(
                                        icon = Icons.Rounded.Schedule,
                                        label = "90 Menit",
                                        containerColor = AmberContainer,
                                        contentColor = OnAmberContainer
                                    )
                                    PastelInfoChip(
                                        icon = Icons.Rounded.Stars,
                                        label = "2 Poin / Soal",
                                        containerColor = EmeraldCorrectContainer,
                                        contentColor = OnEmeraldCorrectContainer
                                    )
                                    PastelInfoChip(
                                        icon = Icons.Rounded.BookmarkAdded,
                                        label = "Simpan Otomatis",
                                        containerColor = CoralContainer,
                                        contentColor = OnCoralContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Student Identity & Action Card
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Identitas Peserta",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = activeExam.studentName,
                                onValueChange = {
                                    if (showNameError && it.isNotBlank()) showNameError = false
                                    onUpdateName(it)
                                },
                                label = { Text("Nama Lengkap") },
                                placeholder = { Text("Tulis nama lengkap kamu") },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Person, contentDescription = null)
                                },
                                isError = showNameError,
                                supportingText = if (showNameError) {
                                    { Text("Tulis nama lengkap kamu dulu agar muncul di hasil skor") }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_name_input")
                            )

                            OutlinedTextField(
                                value = activeExam.studentNumber,
                                onValueChange = onUpdateNumber,
                                label = { Text("No. Absen / Kelas (Opsional)") },
                                placeholder = { Text("Contoh: 14 / X-E2") },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Badge, contentDescription = null)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_number_input")
                            )

                            if (hasInProgressDraft) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = SkyContainer.copy(alpha = 0.55f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Progres Terakhir",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = OnSkyContainer
                                            )
                                            Text(
                                                text = "${activeExam.answeredCount} / 50 Soal • ${formatTimer(activeExam.remainingSeconds)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (activeExam.remainingSeconds < 0L) Color(0xFFDC2626) else SkyPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { activeExam.answeredCount / 50f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Button(
                                onClick = {
                                    if (activeExam.studentName.isBlank()) {
                                        showNameError = true
                                    } else {
                                        showNameError = false
                                        onStartOrResume()
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("start_exam_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hasInProgressDraft) {
                                        "Lanjutkan Ujian (${activeExam.answeredCount}/50)"
                                    } else {
                                        "Mulai Mengerjakan Soal"
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            if (hasInProgressDraft) {
                                OutlinedButton(
                                    onClick = { showResetConfirmDialog = true },
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("restart_exam_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Mulai Ulang dari Soal 1",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }

                // Petunjuk Umum (exact 4 items from PDF)
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showInstructions = !showInstructions }
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Petunjuk Umum",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Icon(
                                    imageVector = if (showInstructions) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = if (showInstructions) "Tutup petunjuk" else "Buka petunjuk"
                                )
                            }
                            AnimatedVisibility(visible = showInstructions) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
                                ) {
                                    ExamQuestionBank.generalInstructions.forEachIndexed { idx, line ->
                                        val badgeBg = when (idx % 4) {
                                            0 -> SkyContainer
                                            1 -> IndigoContainer
                                            2 -> AmberContainer
                                            else -> EmeraldCorrectContainer
                                        }
                                        val badgeFg = when (idx % 4) {
                                            0 -> OnSkyContainer
                                            1 -> OnIndigoContainer
                                            2 -> OnAmberContainer
                                            else -> OnEmeraldCorrectContainer
                                        }
                                        Row(verticalAlignment = Alignment.Top) {
                                            Surface(
                                                shape = CircleShape,
                                                color = badgeBg,
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${idx + 1}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = badgeFg
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = line,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Riwayat Nilai Kamu
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Riwayat Nilai Kamu",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                when (attemptsState) {
                    is UiState.Loading -> {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = attemptsState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                    is UiState.Success -> {
                        val list = attemptsState.data
                        if (list.isEmpty()) {
                            item {
                                Surface(
                                    shape = MaterialTheme.shapes.large,
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Assignment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Belum ada riwayat nilai",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Setelah menyelesaikan seluruh 50 soal, skormu akan otomatis tercatat di sini.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(list, key = { it.id }) { attempt ->
                                AttemptHistoryCard(
                                    attempt = attempt,
                                    onOpen = { onOpenAttempt(attempt) },
                                    onDelete = { attemptPendingDelete = attempt }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Pengaturan Tampilan") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ukuran Teks Soal", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${(preferences.textScale * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = preferences.textScale,
                            onValueChange = onSetTextScale,
                            valueRange = 0.9f..1.3f,
                            steps = 3
                        )
                    }
                    HorizontalDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tampilkan Timer 90 Menit", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Hitung mundur waktu ujian di layar soal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preferences.showTimer,
                            onCheckedChange = onSetShowTimer
                        )
                    }
                    HorizontalDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mode Gelap", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Ubah latar belakang ke tampilan gelap",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preferences.darkTheme,
                            onCheckedChange = onSetDarkTheme
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Mulai Ulang Ujian?") },
            text = {
                Text("Semua ${activeExam.answeredCount} jawaban sementara pada sesi ini akan dihapus dan waktu direset ke 90 menit.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        onStartFresh()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset & Mulai Baru")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    attemptPendingDelete?.let { attempt ->
        AlertDialog(
            onDismissRequest = { attemptPendingDelete = null },
            title = { Text("Hapus Riwayat Skor?") },
            text = {
                Text("Riwayat skor ${attempt.score}/100 milik ${attempt.studentName} akan dihapus dari perangkat.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAttempt(attempt.id)
                        attemptPendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { attemptPendingDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun PastelInfoChip(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = CircleShape,
        color = containerColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun AttemptHistoryCard(
    attempt: ExamAttempt,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
    val dateText = attempt.completedAt?.toDate()?.let { dateFormatter.format(it) } ?: "Baru saja"
    val scorePassed = attempt.score >= 70L

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        onClick = onOpen,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attempt_card_${attempt.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (scorePassed) EmeraldCorrectContainer else AmberContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${attempt.score}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (scorePassed) OnEmeraldCorrectContainer else OnAmberContainer
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attempt.studentName.ifBlank { "Siswa Kelas X" },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${attempt.correctCount}/50 Benar • ${formatDurationIndonesian(attempt.durationSeconds)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (scorePassed) EmeraldCorrect else MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Hapus riwayat",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
