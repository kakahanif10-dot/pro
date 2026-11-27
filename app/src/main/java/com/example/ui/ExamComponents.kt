package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CloudSyncState
import com.example.model.QuestionPassage
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.EmeraldCorrectContainer
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldCorrectContainer
import com.example.ui.theme.PinkRibbon
import com.example.ui.theme.SkyPrimary
import java.util.Locale

@Composable
fun RainbowAccentRibbon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        SkyPrimary,
                        IndigoSecondary,
                        PinkRibbon,
                        AmberTertiary,
                        EmeraldCorrect
                    )
                )
            )
    )
}

@Composable
fun SyncStatusBadge(
    syncState: CloudSyncState,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, icon, label) = when (syncState) {
        CloudSyncState.SYNCED_CLOUD -> Quad(
            EmeraldCorrectContainer,
            OnEmeraldCorrectContainer,
            Icons.Rounded.BookmarkAdded,
            "Cache Aktif"
        )
        CloudSyncState.SYNCING -> Quad(
            EmeraldCorrectContainer,
            OnEmeraldCorrectContainer,
            Icons.Rounded.BookmarkAdded,
            "Menyimpan..."
        )
        CloudSyncState.OFFLINE_SAVED -> Quad(
            AmberContainer,
            OnAmberContainer,
            Icons.Rounded.CloudOff,
            "Mode Offline"
        )
    }

    val animatedBg by animateColorAsState(
        targetValue = bgColor,
        animationSpec = tween(250),
        label = "sync_bg"
    )
    val animatedFg by animateColorAsState(
        targetValue = fgColor,
        animationSpec = tween(250),
        label = "sync_fg"
    )
    val syncCd = stringResource(R.string.cd_sync_status)

    Surface(
        shape = CircleShape,
        color = animatedBg,
        modifier = modifier.semantics { contentDescription = "$syncCd: $label" }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = animatedFg,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = animatedFg,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PassageCard(
    passage: QuestionPassage,
    textScale: Float,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    )
                    .clickable(onClick = onToggleExpand)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = passage.instruction,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (expanded) "Ketuk untuk sembunyikan teks" else "Ketuk untuk baca ${passage.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Tutup bacaan" else "Buka bacaan",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    if (!passage.header.isNullOrBlank()) {
                        Text(
                            text = passage.header,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(
                        text = passage.body,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14.5f * textScale).sp,
                            lineHeight = (22f * textScale).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

fun formatTimer(totalSeconds: Long): String {
    val sign = if (totalSeconds < 0L) "-" else ""
    val absSec = kotlin.math.abs(totalSeconds)
    val mins = absSec / 60L
    val secs = absSec % 60L
    return String.format(Locale.US, "%s%02d:%02d", sign, mins, secs)
}

fun formatDurationIndonesian(totalSeconds: Long): String {
    val safe = totalSeconds.coerceAtLeast(0L)
    val mins = safe / 60L
    val secs = safe % 60L
    return when {
        mins > 0 && secs > 0 -> "$mins mnt $secs dtk"
        mins > 0 -> "$mins menit"
        else -> "$secs detik"
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
