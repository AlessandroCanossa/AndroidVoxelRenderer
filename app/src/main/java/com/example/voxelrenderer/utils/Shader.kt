package com.example.voxelrenderer.utils

import android.opengl.GLES32
import java.io.InputStream

class Shader(isV: InputStream, isF: InputStream) {
    private var mShaderHandle: Int = 0

    init {
        mShaderHandle = ShaderCompiler.createProgram(isV, isF)
    }

    fun use(body: () -> Unit) {
        GLES32.glUseProgram(mShaderHandle)
        body()
        GLES32.glUseProgram(0)
    }

    fun getUniformLocation(name: String): Int {
        return GLES32.glGetUniformLocation(mShaderHandle, name)
    }
}