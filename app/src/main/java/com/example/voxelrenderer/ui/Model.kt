package com.example.voxelrenderer.ui

import androidx.annotation.IntegerRes
import androidx.annotation.StringRes

data class Model(
    @StringRes val name: Int,
    @IntegerRes val defaultDistance: Int
)

