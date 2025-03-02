package com.squirtles.musicroad.mypick

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squirtles.musicroad.common.picklist.PickListScreenContents
import com.squirtles.musicroad.common.picklist.PickListType

@Composable
fun MyPickScreen(
    uid: String,
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    myPickListViewModel: MyPickListViewModel = hiltViewModel()
) {
    val uiState by myPickListViewModel.pickListUiState.collectAsStateWithLifecycle()
    val selectedPicksId by myPickListViewModel.selectedPicksId.collectAsStateWithLifecycle()
    var showOrderBottomSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        myPickListViewModel.fetchPickList(uid)
    }

    PickListScreenContents(
        uid = uid,
        showOrderBottomSheet = showOrderBottomSheet,
        selectedPicksId = selectedPicksId,
        pickListType = PickListType.CREATED,
        uiState = uiState,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
        setListOrder = myPickListViewModel::setListOrder,
        setOrderBottomSheetVisibility = { showOrderBottomSheet = it },
        selectAllPicks = myPickListViewModel::selectAllPicks,
        deselectAllPicks = myPickListViewModel::deselectAllPicks,
        toggleSelectedPick = myPickListViewModel::toggleSelectedPick,
        deleteSelectedPicks = myPickListViewModel::deleteSelectedPicks,
        getUid = { myPickListViewModel.getUid().toString() },
    )
}
