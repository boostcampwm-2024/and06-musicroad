package com.squirtles.domain.applemusic

/**
 * 400 에러가 여러 종류가 있는데 이를 구분할 용도로 만든 예외 클래스
 */
sealed class AppleMusicException(override val message: String) : Exception() {
    data class InvalidParameterException(override val message: String) :
        AppleMusicException(message)

    data class NotFoundException(override val message: String = "No such resource") :
        AppleMusicException(message)
}
