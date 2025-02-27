package com.squirtles.mediaservice

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

interface MediaControllerProvider {
    val mediaControllerFlow: Flow<MediaController>
    val audioSessionFlow: Flow<Int>
}

/* mediaControllerFuture: MediaController를 비동기적으로 제공하는 ListenableFuture */
@UnstableApi
@Singleton
class MediaControllerProviderImpl @Inject constructor(
    private val audioSessionId: Int,
    mediaControllerFuture: ListenableFuture<MediaController>
) : MediaControllerProvider {

    /* mediaControllerFlow가 처음 구독될 때 callbackFlow가 실행 */
    override val mediaControllerFlow: Flow<MediaController> = callbackFlow {
        /* Futures.addCallback을 사용하여 mediaControllerFuture의 결과를 기다림  */
        Futures.addCallback(
            mediaControllerFuture,
            object : FutureCallback<MediaController> {

                /* MediaController 객체가 준비되면 trySend(result)를 통해 Flow로 결과를 전송
                즉, mediaControllerFlow를 구독하고 있는 곳에 MediaController 객체가 전달 */
                override fun onSuccess(result: MediaController) {
                    result.setPlaybackSpeed(1.0f)
                    trySend(result)
                }

                override fun onFailure(t: Throwable) {
                    cancel(CancellationException(t.message))
                }
            },
            MoreExecutors.directExecutor()
        )

        awaitClose { }
    }

    override val audioSessionFlow = callbackFlow {
        trySend(audioSessionId)
        awaitClose { }
    }
}
