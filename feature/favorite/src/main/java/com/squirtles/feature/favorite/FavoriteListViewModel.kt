package com.squirtles.feature.favorite

import com.squirtles.domain.favorite.usecase.DeleteFavoriteUseCase
import com.squirtles.domain.order.usecase.GetFavoriteListOrderUseCase
import com.squirtles.domain.order.usecase.SaveFavoriteListOrderUseCase
import com.squirtles.domain.pick.usecase.FetchFavoritePicksUseCase
import com.squirtles.core.picklist.PickListViewModel
import com.squirtles.domain.user.usecase.GetCurrentUidUseCase
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
