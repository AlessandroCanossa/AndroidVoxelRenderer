package com.example.voxelrenderer.utils

import android.opengl.GLES32
import android.util.Log
import java.io.InputStream

class Shader(isV: InputStream, isF: InputStream) {
    private var shaderHandle: Int = 0

    init {
        shaderHandle = ShaderCompiler.createProgram(isV, isF)
    }

    fun use(body: () -> Unit) {
        GLES32.glUseProgram(shaderHandle)
        body()
        GLES32.glUseProgram(0)
    }

    fun getUniformLocation(name: String): Int {
        return GLES32.glGetUniformLocation(shaderHandle, name)
    }
}