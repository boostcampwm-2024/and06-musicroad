package com.squirtles.musicroad.common.picklist

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import com.squirtles.domain.model.Order
import com.squirtles.domain.model.Pick
import com.squirtles.musicroad.R
import com.squirtles.musicroad.common.Constants.COLOR_STOPS
import com.squirtles.musicroad.common.Constants.DEFAULT_PADDING
import com.squirtles.musicroad.common.CountText
import com.squirtles.musicroad.common.DefaultTopAppBar
import com.squirtles.musicroad.common.VerticalSpacer
import com.squirtles.musicroad.common.picklist.components.DeleteSelectedPickDialog
import com.squirtles.musicroad.common.picklist.components.EditModeAction
import com.squirtles.musicroad.common.picklist.components.EditModeBottomButton
import com.squirtles.musicroad.common.picklist.components.OrderBottomSheet
import com.squirtles.musicroad.common.picklist.components.PickItem
import com.squirtles.musicroad.ui.theme.Primary
import com.squirtles.musicroad.ui.theme.White

@Composable
fun PickListScreenContents(
    uid: String,
    showOrderBottomSheet: Boolean,
    selectedPicksId: Set<String>,
    pickListType: PickListType,
    uiState: PickListUiState,
    getUid: () -> String,
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    setListOrder: (Order) -> Unit,
    setOrderBottomSheetVisibility: (Boolean) -> Unit,
    selectAllPicks: () -> Unit,
    deselectAllPicks: () -> Unit,
    toggleSelectedPick: (String) -> Unit,
    deleteSelectedPicks: (String) -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var isDeletePickDialogVisible by rememberSaveable { mutableStateOf(false) }

    val deactivateEditMode = remember {
        {
            isEditMode = false
            deselectAllPicks()
        }
    }
    val showDeletePickDialog = remember {
        {
            isDeletePickDialogVisible = true
        }
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(
                    when (pickListType) {
                        PickListType.FAVORITE -> R.string.favorite_picks_top_app_bar_title
                        PickListType.CREATED -> R.string.my_picks_top_app_bar_title
                    }
                ),
                onBackClick = onBackClick,
                actions = {
                    if (getUid() == uid) {
                        EditModeAction(
                            isEditMode = isEditMode,
                            enabled = uiState is PickListUiState.Success,
                            isSelectedEmpty = selectedPicksId.isEmpty(),
                            activateEditMode = { isEditMode = true },
                            selectAllPicks = { selectAllPicks() },
                            deselectAllPicks = { deselectAllPicks() },
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = COLOR_STOPS))
                .padding(innerPadding)
                .then(if (isLandscape) Modifier.displayCutoutPadding() else Modifier)
        ) {
            when (uiState) {
                PickListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                        indicatorColor = Primary
                    )
                }

                is PickListUiState.Success -> {
                    val pickList = uiState.pickList
                    val order = uiState.order

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        PickList(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            isEditMode = isEditMode,
                            pickListType = pickListType,
                            pickList = pickList,
                            selectedPicksId = selectedPicksId,
                            order = order,
                            onOrderClick = {
                                setOrderBottomSheetVisibility(true)
                            },
                            onItemClick = onItemClick,
                            onEditModeItemClick = toggleSelectedPick,
                        )

                        if (isEditMode) {
                            EditModeBottomButton(
                                enabled = selectedPicksId.isNotEmpty(),
                                deactivateEditMode = deactivateEditMode,
                                showDeletePickDialog = showDeletePickDialog,
                            )
                        }
                    }
                }

                PickListUiState.Error -> {
                    Text(
                        text = stringResource(R.string.error_loading_pick_list),
                        modifier = Modifier.align(Alignment.Center),
                        color = White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    // TODO: 다시하기 버튼 같은 거 만들어서 요청 다시 하게 할 수 있도록 만드는 것도 고려해보기
                }
            }
        }
    }

    if (showOrderBottomSheet) {
        OrderBottomSheet(
            isFavoritePicks = pickListType == PickListType.FAVORITE,
            currentOrder = (uiState as PickListUiState.Success).order,
            onDismissRequest = { setOrderBottomSheetVisibility(false) },
            onOrderClick = setListOrder,
        )
    }

    if (isDeletePickDialogVisible) {
        DeleteSelectedPickDialog(
            selectedPickCount = selectedPicksId.size,
            pickListType = pickListType,
            onDismissRequest = { isDeletePickDialogVisible = false },
            onDeletePickClick = {
                isEditMode = false
                isDeletePickDialogVisible = false
                deleteSelectedPicks(uid)
            },
        )
    }
}

@Composable
private fun PickList(
    modifier: Modifier,
    isEditMode: Boolean,
    pickListType: PickListType,
    pickList: List<Pick>,
    selectedPicksId: Set<String>,
    order: Order,
    onOrderClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onEditModeItemClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DEFAULT_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountText(
            totalCount = if (isEditMode) selectedPicksId.size else pickList.size,
            countLabel = stringResource(
                if (isEditMode) R.string.selected_count_text else R.string.total_count_text
            ),
            defaultColor = White,
        )

        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .clickable { onOrderClick() }
        ) {
            Text(
                text = getOrderString(
                    pickListType = pickListType,
                    order = order
                ),
                modifier = Modifier.padding(
                    horizontal = DEFAULT_PADDING / 2,
                    vertical = DEFAULT_PADDING / 4
                ),
                color = White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    VerticalSpacer(8)

    if (pickList.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    when (pickListType) {
                        PickListType.FAVORITE -> R.string.favorite_picks_empty
                        PickListType.CREATED -> R.string.my_picks_empty
                    }
                ),
                color = White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = pickList,
                key = { it.id }
            ) { pick ->
                PickItem(
                    isEditMode = isEditMode,
                    isSelected = selectedPicksId.contains(pick.id),
                    song = pick.song,
                    createdByOthers = pickListType == PickListType.FAVORITE,
                    createUserName = pick.createdBy.userName,
                    favoriteCount = pick.favoriteCount,
                    comment = pick.comment,
                    createdAt = pick.createdAt,
                    onItemClick = {
                        if (isEditMode) onEditModeItemClick(pick.id) else onItemClick(pick.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun getOrderString(pickListType: PickListType, order: Order): String {
    return "${
        stringResource(
            when (order) {
                Order.LATEST ->
                    when (pickListType) {
                        PickListType.FAVORITE -> R.string.latest_favorite_order
                        PickListType.CREATED -> R.string.latest_create_order
                    }

                Order.OLDEST ->
                    when (pickListType) {
                        PickListType.FAVORITE -> R.string.oldest_favorite_order
                        PickListType.CREATED -> R.string.oldest_create_order
                    }

                Order.FAVORITE_DESC -> R.string.favorite_count_desc
            }
        )
    }  ▼"
}
