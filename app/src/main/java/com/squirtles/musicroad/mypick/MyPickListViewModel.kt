package com.squirtles.musicroad.mypick

import com.squirtles.domain.order.usecase.GetMyPickListOrderUseCase
import com.squirtles.domain.order.usecase.SaveMyPickListOrderUseCase
import com.squirtles.domain.pick.usecase.DeletePickUseCase
import com.squirtles.domain.pick.usecase.FetchMyPicksUseCase
import com.squirtles.domain.usecase.user.GetCurrentUidUseCase
import com.squirtles.musicroad.common.picklist.PickListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPickListViewModel @Inject constructor(
    fetchMyPicksUseCase: FetchMyPicksUseCase,
    getMyPickListOrderUseCase: GetMyPickListOrderUseCase,
    saveMyPickListOrderUseCase: SaveMyPickListOrderUseCase,
    deletePickUseCase: DeletePickUseCase,
    getCurrentUidUseCase: GetCurrentUidUseCase
) : PickListViewModel(
    fetchPickListUseCase = fetchMyPicksUseCase,
    getPickListOrderUseCase = getMyPickListOrderUseCase,
    savePickListOrderUseCase = saveMyPickListOrderUseCase,
    removePickUseCase = deletePickUseCase,
    getCurrentUidUseCase = getCurrentUidUseCase
)
