package com.squirtles.musicroad.favorite

import com.squirtles.domain.pick.usecase.FetchFavoritePicksUseCase
import com.squirtles.favorite.usecase.DeleteFavoriteUseCase
import com.squirtles.order.usecase.GetFavoriteListOrderUseCase
import com.squirtles.order.usecase.SaveFavoriteListOrderUseCase
import com.squirtles.picklist.PickListViewModel
import com.squirtles.user.usecase.GetCurrentUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    fetchFavoritePicksUseCase: FetchFavoritePicksUseCase,
    getFavoriteListOrderUseCase: GetFavoriteListOrderUseCase,
    saveFavoriteListOrderUseCase: SaveFavoriteListOrderUseCase,
    deleteFavoriteUseCase: DeleteFavoriteUseCase,
    getCurrentUidUseCase: GetCurrentUidUseCase
) : PickListViewModel(
    fetchPickListUseCase = fetchFavoritePicksUseCase,
    getPickListOrderUseCase = getFavoriteListOrderUseCase,
    savePickListOrderUseCase = saveFavoriteListOrderUseCase,
    removePickUseCase = deleteFavoriteUseCase,
    getCurrentUidUseCase = getCurrentUidUseCase
)
