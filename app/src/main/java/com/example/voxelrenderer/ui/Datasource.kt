package com.example.voxelrenderer.ui

import com.example.voxelrenderer.R

object Datasource {
    fun loadModels(): List<Model> {
        return listOf(
            Model(R.string.simple_model, R.raw.simple, R.integer.simple_model_dist),
            Model(R.string.chrk_model, R.raw.chrk, R.integer.chrk_model_dist),
            Model(R.string.dragon_model, R.raw.dragon, R.integer.dragon_model_dist),
            Model(R.string.monu2_model, R.raw.monu2, R.integer.monu2_model_dist),
            Model(R.string.monu16_model, R.raw.monu16, R.integer.monu16_model_dist),
            Model(R.string.christmas_model, R.raw.christmas, R.integer.christmas_model_dist),
        )
    }
}