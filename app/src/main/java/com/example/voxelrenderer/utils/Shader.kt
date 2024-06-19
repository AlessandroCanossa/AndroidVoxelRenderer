package com.example.voxelrenderer.utils

import android.opengl.GLES32
import java.io.InputStream

class Shader(isV: InputStream, isF: InputStream) {
    private var mShaderHandle: Int = 0

    init {
        mShaderHandle = ShaderCompiler.createProgram(isV, isF)
    }

    fun use(body: (Shader) -> Unit) {
        GLES32.glUseProgram(mShaderHandle)
        body(this)
        GLES32.glUseProgram(0)
    }

    private fun getUniformLocation(name: String): Int {
        return GLES32.glGetUniformLocation(mShaderHandle, name)
    }

    fun setMat4(name: String, mat: FloatArray) {
        assert(mat.size == 16)
        GLES32.glUniformMatrix4fv(getUniformLocation(name), 1, false, mat, 0)
    }

    fun setInt(name: String, value: Int) {
        GLES32.glUniform1i(getUniformLocation(name), value)
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        GLES32.glUniform3f(getUniformLocation(name), x, y, z)
    }

    fun setVec3(name: String, vec: FloatArray) {
        assert(vec.size == 3)
        GLES32.glUniform3fv(getUniformLocation(name), 1, vec, 0)
    }
}