package com.squirtles.musicroad.favorite

import com.squirtles.domain.usecase.favorite.DeleteFavoriteUseCase
import com.squirtles.domain.usecase.favorite.FetchFavoritePicksUseCase
import com.squirtles.domain.usecase.order.GetFavoriteListOrderUseCase
import com.squirtles.domain.usecase.order.SaveFavoriteListOrderUseCase
import com.squirtles.domain.usecase.user.GetCurrentUidUseCase
import com.squirtles.musicroad.common.picklist.PickListViewModel
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
