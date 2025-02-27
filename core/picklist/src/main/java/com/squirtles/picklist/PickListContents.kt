package com.squirtles.picklist

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import com.squirtles.common.ui.Constants.COLOR_STOPS
import com.squirtles.common.ui.Constants.DEFAULT_PADDING
import com.squirtles.common.ui.CountText
import com.squirtles.common.ui.DefaultTopAppBar
import com.squirtles.common.ui.VerticalSpacer
import com.squirtles.common.ui.theme.Primary
import com.squirtles.model.Order

@Composable
fun PickListContents(
    showOrderBottomSheet: Boolean,
    pickListType: PickListType,
    uiState: PickListUiState,
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    setListOrder: (Order) -> Unit,
    setOrderBottomSheetVisibility: (Boolean) -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(
                    when (pickListType) {
                        PickListType.FAVORITE -> R.string.favorite_picks_top_app_bar_title
                        PickListType.CREATED -> R.string.my_picks_top_app_bar_title
                    }
                ),
                onBackClick = onBackClick
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = DEFAULT_PADDING),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CountText(
                                totalCount = pickList.size,
                                defaultColor = White,
                            )

                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .clip(CircleShape)
                                    .clickable { setOrderBottomSheetVisibility(true) }
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
                                modifier = Modifier.fillMaxSize(),
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
                                modifier = Modifier.weight(1f)
                            ) {
                                items(
                                    count = pickList.count(),
                                    key = { pickList[it].id },
                                    itemContent = {
                                        PickListItem(
                                            song = pickList[it].song,
                                            isCreatedByOthers = pickListType == PickListType.FAVORITE,
                                            createUserName = pickList[it].createdBy.userName,
                                            favoriteCount = pickList[it].favoriteCount,
                                            comment = pickList[it].comment,
                                            createdAt = pickList[it].createdAt,
                                            onItemClick = { onItemClick(pickList[it].id) }
                                        )
                                    }
                                )
                            }
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
            onOrderClick = { order ->
                setListOrder(order)
            },
        )
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
