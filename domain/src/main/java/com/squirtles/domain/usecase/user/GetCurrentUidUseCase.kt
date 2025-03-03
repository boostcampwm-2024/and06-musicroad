package com.squirtles.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class GetCurrentUidUseCase @Inject constructor() {
    operator fun invoke() = FirebaseAuth.getInstance().currentUser?.uid
}
