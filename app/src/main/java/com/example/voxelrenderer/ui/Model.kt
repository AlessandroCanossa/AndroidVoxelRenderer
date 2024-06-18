package com.example.voxelrenderer.ui

import androidx.annotation.IntegerRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class Model(
    @StringRes val name: Int,
    @RawRes val model: Int,
    @IntegerRes val defaultDistance: Int
)

