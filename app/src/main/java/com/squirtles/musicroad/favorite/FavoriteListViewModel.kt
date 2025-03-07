package com.squirtles.musicroad.favorite

import com.squirtles.domain.favorite.usecase.DeleteFavoriteUseCase
import com.squirtles.domain.pick.usecase.FetchFavoritePicksUseCase
import com.squirtles.domain.order.usecase.GetFavoriteListOrderUseCase
import com.squirtles.domain.order.usecase.SaveFavoriteListOrderUseCase
import com.squirtles.domain.user.usecase.GetCurrentUserUseCase
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
