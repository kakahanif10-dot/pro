package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.LocalPreferencesRepository
import com.example.data.NetworkMonitor
import com.example.ui.ExamCoverScreen
import com.example.ui.ExamQuizScreen
import com.example.ui.ExamResultScreen
import com.example.ui.ExamScreenRoute
import com.example.ui.ExamViewModel
import com.example.ui.theme.UasInggrisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            StudentExamApp()
        }
    }
}

@Composable
fun StudentExamApp() {
    val context = LocalContext.current
    val networkMonitor = remember(context) { NetworkMonitor(context.applicationContext) }

    DisposableEffect(networkMonitor) {
        onDispose { networkMonitor.release() }
    }

    val viewModel: ExamViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val localStore = LocalPreferencesRepository(app)
                ExamViewModel(
                    localStore = localStore,
                    networkMonitor = networkMonitor
                )
            }
        }
    )

    val route by viewModel.route.collectAsStateWithLifecycle()
    val activeExam by viewModel.activeExam.collectAsStateWithLifecycle()
    val attemptsState by viewModel.attemptsState.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionMessage) {
        val msg = actionMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

    UasInggrisTheme(darkTheme = preferences.darkTheme) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val currentRoute = route) {
                    is ExamScreenRoute.Cover -> {
                        ExamCoverScreen(
                            activeExam = activeExam,
                            attemptsState = attemptsState,
                            syncState = syncState,
                            preferences = preferences,
                            onUpdateName = viewModel::updateStudentName,
                            onUpdateNumber = viewModel::updateStudentNumber,
                            onStartOrResume = viewModel::startOrResumeExam,
                            onStartFresh = viewModel::startFreshExam,
                            onOpenAttempt = viewModel::openAttemptResult,
                            onDeleteAttempt = viewModel::deleteAttempt,
                            onSetTextScale = viewModel::setTextScale,
                            onSetShowTimer = viewModel::setShowTimer,
                            onSetDarkTheme = viewModel::setDarkTheme
                        )
                    }

                    is ExamScreenRoute.Quiz -> {
                        ExamQuizScreen(
                            activeExam = activeExam,
                            syncState = syncState,
                            preferences = preferences,
                            onSelectAnswer = viewModel::selectAnswer,
                            onToggleFlag = viewModel::toggleFlagQuestion,
                            onGoToQuestion = viewModel::goToQuestion,
                            onExitToCover = viewModel::exitQuizToCover,
                            onSubmitExam = viewModel::submitExam
                        )
                    }

                    is ExamScreenRoute.Result -> {
                        ExamResultScreen(
                            attempt = currentRoute.attempt,
                            syncState = syncState,
                            onRetakeExam = viewModel::startFreshExam,
                            onBackToCover = viewModel::backToCoverFromResult
                        )
                    }
                }
            }
        }
    }
}
