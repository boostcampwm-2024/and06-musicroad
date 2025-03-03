package com.squirtles.musicroad.favorite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squirtles.musicroad.common.picklist.PickListType
import com.squirtles.musicroad.common.picklist.PickListScreenContents

@Composable
fun FavoriteScreen(
    uid: String,
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    favoriteListViewModel: FavoriteListViewModel = hiltViewModel()
) {
    val uiState by favoriteListViewModel.pickListUiState.collectAsStateWithLifecycle()
    val selectedPicksId by favoriteListViewModel.selectedPicksId.collectAsStateWithLifecycle()
    var showOrderBottomSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        favoriteListViewModel.fetchPickList(uid)
    }

    PickListScreenContents(
        uid = uid,
        showOrderBottomSheet = showOrderBottomSheet,
        selectedPicksId = selectedPicksId,
        pickListType = PickListType.FAVORITE,
        uiState = uiState,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        setListOrder = favoriteListViewModel::setListOrder,
        setOrderBottomSheetVisibility = { showOrderBottomSheet = it },
        selectAllPicks = favoriteListViewModel::selectAllPicks,
        deselectAllPicks = favoriteListViewModel::deselectAllPicks,
        toggleSelectedPick = favoriteListViewModel::toggleSelectedPick,
        deleteSelectedPicks = favoriteListViewModel::deleteSelectedPicks,
        getUid = {
            favoriteListViewModel.getUid().toString()
        }
    )
}
