package com.squirtles.musicroad.create

import android.app.Activity
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squirtles.model.Song
import com.squirtles.musicroad.R
import com.squirtles.musicroad.common.AlbumImage
import com.squirtles.musicroad.common.VerticalSpacer
import com.squirtles.musicroad.ui.theme.Black
import com.squirtles.musicroad.ui.theme.Dark
import com.squirtles.musicroad.ui.theme.Gray
import com.squirtles.musicroad.ui.theme.White

@Composable
fun CreatePickScreen(
    song: Song,
    onBackClick: () -> Unit,
    onCreateClick: (String) -> Unit,
    createPickViewModel: CreatePickViewModel = hiltViewModel(),
) {

    val comment = createPickViewModel.comment.collectAsStateWithLifecycle()
    val uiState by createPickViewModel.createPickUiState.collectAsStateWithLifecycle()
    var showCreateIndicator by rememberSaveable { mutableStateOf(false) }

    val dynamicBackgroundColor = Color(song.bgColor)
    val dynamicOnBackgroundColor = if (dynamicBackgroundColor.luminance() >= 0.5f) Black else White
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val windowInsetsController = WindowInsetsControllerCompat(window, view)
            val isLightStatusBar = dynamicBackgroundColor.luminance() >= 0.5f
            windowInsetsController.isAppearanceLightStatusBars = isLightStatusBar
        }
    }

    // 생성 중 인디케이터가 표시되고 있을 때는 시스템의 뒤로 가기 버튼 클릭을 무시
    BackHandler(enabled = showCreateIndicator) { }

    when (uiState) {
        CreateUiState.Default -> {
            CreatePickDisplay(
                song = song,
                comment = comment.value,
                dynamicBackgroundColor = dynamicBackgroundColor,
                dynamicOnBackgroundColor = dynamicOnBackgroundColor,
                onBackClick = {
                    createPickViewModel.resetComment()
                    onBackClick()
                },
                onCreateClick = {
                    createPickViewModel.onCreatePickClick()
                    showCreateIndicator = true
                },
                onCommentChange = createPickViewModel::onCommentChange
            )
        }

        is CreateUiState.Success -> {
            LaunchedEffect(Unit) {
                showCreateIndicator = false
                val pickId = (uiState as CreateUiState.Success<String>).data
                onCreateClick(pickId)
            }
        }

        CreateUiState.Error -> {
            // TODO()
            showCreateIndicator = false
            Text("생성 오류")
        }
    }

    if (showCreateIndicator) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black.copy(alpha = 0.5F))
                .clickable( // 클릭 효과 제거 및 클릭 이벤트 무시
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun CreatePickDisplay(
    song: Song,
    comment: String,
    dynamicBackgroundColor: Color,
    dynamicOnBackgroundColor: Color,
    onCommentChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
) {
    Scaffold(
        containerColor = dynamicBackgroundColor,
        topBar = {
            CreatePickScreenTopBar(
                dynamicOnBackgroundColor = dynamicOnBackgroundColor,
                onBackClick = onBackClick,
                onCreateClick = onCreateClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to dynamicBackgroundColor,
                            0.47f to Black
                        )
                    )
                )
        ) {
            CreatePickContent(
                song = song,
                comment = comment,
                onValueChange = onCommentChange,
                dynamicOnBackgroundColor = dynamicOnBackgroundColor,
            )
        }
    }
}

@Composable
private fun CreatePickContent(
    song: Song,
    comment: String,
    onValueChange: (String) -> Unit,
    dynamicOnBackgroundColor: Color,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = song.songName,
            color = dynamicOnBackgroundColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = song.artistName,
            color = dynamicOnBackgroundColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        AlbumImage(
            imageUrl = song.getImageUrlWithSize(RequestImageSize.width, RequestImageSize.height),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp)),
            contentDescription = song.albumName + stringResource(id = R.string.pick_album_description)
        )

        VerticalSpacer(40)

        CommentTextBox(
            comment = comment,
            onValueChange = onValueChange,
        )
    }
}

@Composable
private fun CommentTextBox(
    comment: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = comment,
        onValueChange = { textValue ->
            onValueChange(textValue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 30.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(White),
        placeholder = {
            Text(
                text = stringResource(id = R.string.pick_comment_placeholder),
                style = MaterialTheme.typography.bodyLarge.copy(Gray)
            )
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Dark,
            focusedContainerColor = Dark,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePickScreenTopBar(
    dynamicOnBackgroundColor: Color,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                text = stringResource(id = R.string.pick_app_bar_title),
                color = dynamicOnBackgroundColor,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.top_app_bar_back_description),
                    tint = dynamicOnBackgroundColor
                )
            }
        },
        actions = {
            IconButton(onClick = onCreateClick) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.pick_app_bar_upload_description),
                    tint = dynamicOnBackgroundColor
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Preview
@Composable
private fun CreatePickScreenPreview() {
    CreatePickDisplay(
        song = Song(
            id = "1778132734",
            songName = "Super Shy",
            artistName = "뉴진스",
            albumName = "NewJeans 'Super Shy' - Single",
            imageUrl = "https://i.scdn.co/image/ab67616d0000b2733d98a0ae7c78a3a9babaf8af",
            genreNames = listOf("K-Pop"),
            bgColor = "#8FC1E2".toColorInt(),
            externalUrl = "",
            previewUrl = ""
        ),
        onBackClick = {},
        comment = "TEST COMMENT",
        dynamicBackgroundColor = Color.White,
        dynamicOnBackgroundColor = Color.Gray,
        onCommentChange = {

        },
        onCreateClick = {

        }
    )
}

private val RequestImageSize = Size(720, 720)
private val DEFAULT_SONG = Song(
    id = "1778132734",
    songName = "",
    artistName = "",
    albumName = "",
    imageUrl = "",
    genreNames = listOf("K-Pop"),
    bgColor = "#8FC1E2".toColorInt(),
    externalUrl = "",
    previewUrl = ""
)
