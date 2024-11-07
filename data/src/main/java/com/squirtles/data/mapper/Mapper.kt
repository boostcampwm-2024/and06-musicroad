package com.squirtles.data.mapper

import com.squirtles.data.datasource.remote.model.spotify.TempResponse
import com.squirtles.domain.model.Temp

internal fun TempResponse.toTemp(): Temp =
    Temp(
        content = content
    )