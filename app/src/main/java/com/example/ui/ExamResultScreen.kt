package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamQuestionBank
import com.example.model.CloudSyncState
import com.example.model.ExamAttempt
import com.example.model.OptionKey
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldCorrectContainer
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnCoralContainer
import com.example.ui.theme.OnEmeraldCorrectContainer
import com.example.ui.theme.OnIndigoContainer
import com.example.ui.theme.OnSkyContainer
import com.example.ui.theme.SkyContainer
import com.example.ui.theme.SkyPrimary
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    attempt: ExamAttempt,
    syncState: CloudSyncState,
    onRetakeExam: () -> Unit,
    onBackToCover: () -> Unit
) {
    BackHandler { onBackToCover() }

    val correctCount = attempt.correctCount.toInt()
    val unansweredCount = ExamQuestionBank.questions.count { q ->
        OptionKey.fromCode(attempt.answers.getOrNull(q.number - 1)) == null
    }
    val wrongCount = (50 - correctCount - unansweredCount).coerceAtLeast(0)

    val predicateLabel = when {
        attempt.score >= 90L -> "Luar Biasa! (Predikat A)"
        attempt.score >= 75L -> "Bagus Sekali! (Predikat B)"
        attempt.score >= 60L -> "Cukup Baik (Predikat C)"
        else -> "Tetap Semangat Latihan!"
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
    val completedDateText = attempt.completedAt?.toDate()?.let { dateFormatter.format(it) } ?: "Baru saja"

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackToCover) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Kembali ke Beranda"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Hasil Ujian Kamu",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    SyncStatusBadge(
                        syncState = syncState,
                        modifier = Modifier.padding(end = 12.dp)
                    )
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
                    .widthIn(max = 640.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Colourful White Student Score Hero Card (Screenshot-Ready)
                item {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("score_summary_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            RainbowAccentRibbon()
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "BUKTI HASIL UAS BAHASA INGGRIS • KELAS X",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldCorrectContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.EmojiEvents,
                                            contentDescription = null,
                                            tint = OnEmeraldCorrectContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = predicateLabel,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = OnEmeraldCorrectContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    shape = CircleShape,
                                    border = BorderStroke(4.dp, SkyPrimary),
                                    color = Color.Transparent,
                                    modifier = Modifier.size(126.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        SkyContainer.copy(alpha = 0.7f),
                                                        EmeraldCorrectContainer.copy(alpha = 0.7f)
                                                    )
                                                )
                                            )
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${attempt.score}",
                                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 46.sp),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SkyPrimary,
                                                modifier = Modifier.testTag("final_score_text")
                                            )
                                            Text(
                                                text = "dari 100",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(5.dp),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        IdentityResultRow(
                                            label = "Nama Siswa",
                                            value = attempt.studentName.ifBlank { "Siswa Kelas X" }
                                        )
                                        IdentityResultRow(
                                            label = "No. Absen / Kelas",
                                            value = attempt.studentNumber.ifBlank { "—" }
                                        )
                                        IdentityResultRow(
                                            label = "Waktu Selesai",
                                            value = completedDateText
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    PastelStatBox(
                                        label = "Benar",
                                        value = "$correctCount",
                                        containerColor = EmeraldCorrectContainer,
                                        contentColor = OnEmeraldCorrectContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    PastelStatBox(
                                        label = "Salah",
                                        value = "$wrongCount",
                                        containerColor = CoralContainer,
                                        contentColor = OnCoralContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    PastelStatBox(
                                        label = "Kosong",
                                        value = "$unansweredCount",
                                        containerColor = AmberContainer,
                                        contentColor = OnAmberContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    PastelStatBox(
                                        label = "Durasi",
                                        value = formatTimer(attempt.durationSeconds),
                                        containerColor = IndigoContainer,
                                        contentColor = OnIndigoContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Silakan screenshot layar ini sebagai bukti nilai ke guru.",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SkyPrimary
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = onBackToCover,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Home,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Beranda")
                                    }

                                    Button(
                                        onClick = onRetakeExam,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("retake_exam_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Kerjakan Lagi")
                                    }
                                }
                            }
                        }
                    }
                }

                // Rekap Pilihan Jawaban Siswa (Tanpa Kunci Jawaban / Pembahasan)
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Rekap Jawabanmu (1–50)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ringkasan pilihan yang kamu kirim pada ujian ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (row in 0 until 10) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        for (col in 0 until 5) {
                                            val number = row * 5 + col + 1
                                            val chosen = OptionKey.fromCode(attempt.answers.getOrNull(number - 1))

                                            val cellBg = if (chosen != null) {
                                                SkyContainer
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            }
                                            val cellFg = if (chosen != null) {
                                                OnSkyContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = cellBg,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "$number. ${chosen?.code ?: "—"}",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = cellFg
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun PastelStatBox(
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

@Composable
private fun IdentityResultRow(
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}
