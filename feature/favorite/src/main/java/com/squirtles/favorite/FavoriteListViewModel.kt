package com.squirtles.favorite

import com.squirtles.favorite.usecase.DeleteFavoriteUseCase
import com.squirtles.order.usecase.GetFavoriteListOrderUseCase
import com.squirtles.order.usecase.SaveFavoriteListOrderUseCase
import com.squirtles.pick.usecase.FetchFavoritePicksUseCase
import com.squirtles.picklist.PickListViewModel
import com.squirtles.user.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    fetchFavoritePicksUseCase: FetchFavoritePicksUseCase,
    getFavoriteListOrderUseCase: GetFavoriteListOrderUseCase,
    saveFavoriteListOrderUseCase: SaveFavoriteListOrderUseCase,
    deleteFavoriteUseCase: DeleteFavoriteUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : PickListViewModel(
    fetchPickListUseCase = fetchFavoritePicksUseCase,
    getPickListOrderUseCase = getFavoriteListOrderUseCase,
    savePickListOrderUseCase = saveFavoriteListOrderUseCase,
    removePickUseCase = deleteFavoriteUseCase,
    getCurrentUserUseCase = getCurrentUserUseCase
)
