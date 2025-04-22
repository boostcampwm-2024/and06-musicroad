package com.squirtles.mypick

import com.squirtles.order.usecase.GetMyPickListOrderUseCase
import com.squirtles.order.usecase.SaveMyPickListOrderUseCase
import com.squirtles.domain.pick.usecase.DeletePickUseCase
import com.squirtles.domain.pick.usecase.FetchMyPicksUseCase
import com.squirtles.picklist.PickListViewModel
import com.squirtles.user.usecase.GetCurrentUidUseCase
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
