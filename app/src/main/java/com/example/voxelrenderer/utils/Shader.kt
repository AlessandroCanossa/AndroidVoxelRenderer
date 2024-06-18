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

    fun setMat4(name: String, mat: FloatArray) {
        assert(mat.size == 16)
        GLES32.glUniformMatrix4fv(getUniformLocation(name), 1, false, mat, 0)
    }

    fun setInt(name: String, value: Int) {
        GLES32.glUniform1i(getUniformLocation(name), value)
    }
}