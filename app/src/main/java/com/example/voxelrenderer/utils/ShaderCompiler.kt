package com.example.voxelrenderer.utils

import android.util.Log
import java.io.IOException
import java.io.InputStream
import android.opengl.GLES32 as gl


object ShaderCompiler {
    private val TAG = "SHADER_COMPILER"
    fun createComputeProgram(src: String): Int {
        var res = -1

        val compileStatus = IntArray(1)

        val handle = compileComputeShader(gl.GL_COMPUTE_SHADER, src, compileStatus)
        if (handle < 0) {
            return res
        }

        gl.glAttachShader(res, handle)
        gl.glLinkProgram(res)

        val linkStatus = IntArray(1)
        gl.glGetProgramiv(res, gl.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            Log.e(TAG, "Linking error: ${gl.glGetProgramInfoLog(res)}")
            gl.glDeleteProgram(res)
            res = 0
        }

        if (res == 0) {
            return 0
        }
        Log.v(TAG, "Program compiled and linked successfully in handle $res")
        gl.glDetachShader(res, handle)
        gl.glDeleteShader(handle)

        return res
    }

    /**
     * @throws IOException
     */
    fun createProgram(isV: InputStream, isF: InputStream): Int {
        val vsSrc = isV.bufferedReader().use { it.readText() }.toString()
        isV.close()

        val fsSrc = isF.bufferedReader().use { it.readText() }.toString()
        isF.close()

        return createProgram(vsSrc, fsSrc)
    }

    fun createProgram(vertexShader: String, fragmentShader: String): Int {
        var res = -1

        val compileStatus = IntArray(1)

        val hVS = compileShader(gl.GL_VERTEX_SHADER, vertexShader, compileStatus)
        if (hVS == -1) {
            return res
        }
        val hFS = compileShader(gl.GL_FRAGMENT_SHADER, fragmentShader, compileStatus)
        if (hFS == -1) {
            return res
        }

        res = gl.glCreateProgram()
        if (res == 0) {
            return 0
        }

        gl.glAttachShader(res, hVS)
        gl.glAttachShader(res, hFS)
        gl.glLinkProgram(res)

        val linkStatus = IntArray(1)
        gl.glGetProgramiv(res, gl.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            Log.e(TAG, "Linking error: ${gl.glGetProgramInfoLog(res)}")
            gl.glDeleteProgram(res)
            res = 0
        }

        if (res == 0) {
            return 0
        }

        Log.v(TAG, "Program compiled and linked successfully in handle $res")
        gl.glDetachShader(res, hVS)
        gl.glDetachShader(res, hFS)
        gl.glDeleteShader(hVS)
        gl.glDeleteShader(hFS)

        return res
    }

    private fun compileComputeShader(
        shaderStage: Int, src: String, compileStatus: IntArray
    ): Int {
        val handle = gl.glCreateShader(shaderStage)

        if (handle == 0) {
            return 0
        }

        gl.glShaderSource(handle, src)
        gl.glCompileShader(handle)
        gl.glGetShaderiv(handle, gl.GL_COMPILE_STATUS, compileStatus, 0)

        val shaderType: String = when (shaderStage) {
            gl.GL_VERTEX_SHADER -> "Vertex"
            gl.GL_FRAGMENT_SHADER -> "Fragment"
            gl.GL_COMPUTE_SHADER -> "Compute"
            else -> ""
        }

        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error in $shaderType shader: ${gl.glGetShaderInfoLog(handle)}")
            gl.glDeleteShader(handle)
            return -1
        }

        return handle
    }

    private fun compileShader(
        shaderStage: Int, src: String, compileStatus: IntArray
    ): Int {
        val handle = gl.glCreateShader(shaderStage)

        if (handle == 0) {
            return 0
        }

        gl.glShaderSource(handle, src)
        gl.glCompileShader(handle)
        gl.glGetShaderiv(handle, gl.GL_COMPILE_STATUS, compileStatus, 0)

        val shaderType: String = when (shaderStage) {
            gl.GL_VERTEX_SHADER -> "Vertex"
            gl.GL_FRAGMENT_SHADER -> "Fragment"
            gl.GL_COMPUTE_SHADER -> "Compute"
            else -> ""
        }

        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error in $shaderType shader: ${gl.glGetShaderInfoLog(handle)}")
            gl.glDeleteShader(handle)
            return -1
        }

        return handle
    }
}