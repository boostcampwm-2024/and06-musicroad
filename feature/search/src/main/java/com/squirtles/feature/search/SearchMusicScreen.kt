package com.squirtles.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.squirtles.core.common.ui.AlbumImage
import com.squirtles.core.common.ui.Constants.COLOR_STOPS
import com.squirtles.core.common.ui.Constants.REQUEST_IMAGE_SIZE_DEFAULT
import com.squirtles.core.common.ui.HorizontalSpacer
import com.squirtles.core.common.ui.VerticalSpacer
import com.squirtles.core.common.ui.theme.Gray
import com.squirtles.core.common.ui.theme.White
import com.squirtles.core.model.Song
import com.squirtles.feature.search.SearchUiConstants.DefaultPadding
import com.squirtles.feature.search.SearchUiConstants.ImageSize
import com.squirtles.feature.search.SearchUiConstants.ItemSpacing
import com.squirtles.feature.search.SearchUiConstants.SearchBarHeight
import com.squirtles.search.R

@Composable
fun SearchMusicScreen(
    onBackClick: () -> Unit,
    onItemClick: (Song) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val searchText by searchViewModel.searchText.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.searchUiState.collectAsStateWithLifecycle()
    val searchResult = searchViewModel.searchResult.collectAsLazyPagingItems()

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(top = 16.dp)
            ) {
                SearchTopBar(
                    keyword = searchText,
                    onValueChange = searchViewModel::onSearchTextChange,
                    onBackClick = onBackClick,
                    focusManager = focusManager
                )
            }
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = COLOR_STOPS))
                .padding(innerPadding)
                .addFocusCleaner(focusManager)
        ) {
            if (searchResult.loadState.refresh is LoadState.Loading ||
                searchResult.loadState.append is LoadState.Loading
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    trackColor = Gray
                )
            }

            // 검색 결과
            when (searchUiState) {
                is SearchUiState.HotResult -> {
                    // TODO HOT 리스트
                }

                is SearchUiState.SearchResult -> {
                    SearchResult(
                        searchResult = searchResult,
                        onItemClick = { song ->
                            focusManager.clearFocus()
                            onItemClick(song)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTopBar(
    keyword: String,
    onValueChange: (String) -> Unit,
    onBackClick: () -> Unit,
    focusManager: FocusManager
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SearchBarHeight)
            .padding(end = DefaultPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onBackClick()
            },
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.top_app_bar_back_description),
                tint = White
            )
        }
        HorizontalSpacer(8)

        OutlinedTextField(
            value = keyword,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(stringResource(id = R.string.search))
            },
            // 키보드 완료버튼 -> Search로 변경, 누르면 Search 동작 실행
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = White,
                focusedIndicatorColor = White,
                unfocusedIndicatorColor = White,
                focusedPlaceholderColor = Gray,
                unfocusedPlaceholderColor = White
            )
        )
    }
}

@Composable
private fun SearchResult(
    searchResult: LazyPagingItems<Song>,
    onItemClick: (Song) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        VerticalSpacer(20)

        TextWithColorAndStyle(
            text = stringResource(id = R.string.result),
            textColor = White,
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = DefaultPadding)
        )
        VerticalSpacer(20)

        LazyColumn(modifier = Modifier.padding(bottom = DefaultPadding)) {
            items(searchResult.itemCount) { index ->
                searchResult[index]?.let {
                    SongItem(it) {
                        onItemClick(it)
                    }
                }
            }
        }

        if (searchResult.loadState.refresh is LoadState.NotLoading) {
            if (searchResult.itemCount == 0) {
                Text(
                    text = stringResource(id = R.string.search_music_empty_description),
                    modifier = Modifier.padding(horizontal = DefaultPadding),
                    color = White
                )
            }
        } else if (searchResult.loadState.refresh is LoadState.Error) {
            Text(
                text = stringResource(id = R.string.search_music_error_description),
                modifier = Modifier.padding(horizontal = DefaultPadding),
                color = White
            )
        }
    }
}

@Composable
private fun SongItem(
    song: Song,
    onItemClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = White),
            ) {
                onItemClick()
            }
            .padding(horizontal = DefaultPadding, vertical = ItemSpacing / 2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(
            imageUrl = song.getImageUrlWithSize(REQUEST_IMAGE_SIZE_DEFAULT.width, REQUEST_IMAGE_SIZE_DEFAULT.height),
            modifier = Modifier
                .size(ImageSize)
                .clip(RoundedCornerShape(16.dp))
        )

        HorizontalSpacer(16)
        Column {
            TextWithColorAndStyle(
                text = song.songName,
                textColor = White,
                textStyle = MaterialTheme.typography.bodyLarge
            )
            TextWithColorAndStyle(
                text = song.artistName,
                textColor = Gray,
                textStyle = MaterialTheme.typography.bodyMedium
            )
            TextWithColorAndStyle(
                text = song.albumName,
                textColor = Gray,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TextWithColorAndStyle(
    text: String,
    textColor: Color,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = textStyle
    )
}

fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {}): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                doOnClear()
                focusManager.clearFocus()
            }
        )
    }
}

@Preview
@Composable
fun SongItemPreview() {
    val song = Song(
        id = "1",
        songName = "Ditto",
        artistName = "String",
        albumName = "Ditto",
        imageUrl = "https://i.scdn.co/image/ab67616d0000b2733d98a0ae7c78a3a9babaf8af",
        genreNames = listOf(),
        bgColor = android.graphics.Color.RED,
        externalUrl = "",
        previewUrl = "",
    )
    SongItem(song) {

    }
}
