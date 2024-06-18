package com.example.voxelrenderer.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.opengl.GLES32 as gl

class Skybox {
    private val mVao = IntArray(1)
    val textureId = IntArray(1)
    private val TAG = "Skybox"

    fun draw() {
        gl.glBindVertexArray(mVao[0])
        gl.glDrawElements(gl.GL_TRIANGLES, skyboxIndices.size, gl.GL_UNSIGNED_INT, 0)
        gl.glBindVertexArray(0)
    }

    fun bindTexture() {
        gl.glActiveTexture(gl.GL_TEXTURE0)
        gl.glBindTexture(gl.GL_TEXTURE_CUBE_MAP, textureId[0])
    }

    fun setup() {
        val vertexData = ByteBuffer.allocateDirect(skyboxVertices.size * Float.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(skyboxVertices)
                    position(0)
                }
            }

        val indexData = ByteBuffer.allocateDirect(skyboxIndices.size * Int.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asIntBuffer().apply {
                    put(skyboxIndices)
                    position(0)
                }
            }

        val vbo = IntArray(1)
        val ebo = IntArray(1)
        gl.glGenVertexArrays(1, mVao, 0)
        gl.glGenBuffers(1, vbo, 0)
        gl.glGenBuffers(1, ebo, 0)

        gl.glBindVertexArray(mVao[0])

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            vertexData.capacity() * Float.SIZE_BYTES,
            vertexData,
            gl.GL_STATIC_DRAW
        )

        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            indexData.capacity() * Int.SIZE_BYTES,
            indexData,
            gl.GL_STATIC_DRAW
        )

        gl.glEnableVertexAttribArray(0)
        gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)

        gl.glBindVertexArray(0)
    }

    fun setupTexture(faces: Array<Int>, context: Context) {
        gl.glGenTextures(1, textureId, 0)
        gl.glBindTexture(gl.GL_TEXTURE_CUBE_MAP, textureId[0])

        if (textureId[0] == 0) {
            Log.e(TAG, "Failed to generate texture")
        }

        gl.glTexParameteri(gl.GL_TEXTURE_CUBE_MAP, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR)
        gl.glTexParameteri(gl.GL_TEXTURE_CUBE_MAP, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR)
        gl.glTexParameteri(gl.GL_TEXTURE_CUBE_MAP, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE)
        gl.glTexParameteri(gl.GL_TEXTURE_CUBE_MAP, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE)
        gl.glTexParameteri(gl.GL_TEXTURE_CUBE_MAP, gl.GL_TEXTURE_WRAP_R, gl.GL_CLAMP_TO_EDGE)

        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmaps = Array(faces.size) {
            val bitmap = BitmapFactory.decodeResource(context.resources, faces[it], options)
            if (bitmap == null) {
                Log.e(TAG, "Failed to load texture from resource ${faces[it]}")
            } else {
                Log.v(
                    TAG,
                    "bitmap of size ${bitmap.width}x${bitmap.height} loaded with format ${bitmap.config.name}"
                )
            }
            bitmap
        }

        for ((i, bitmap) in bitmaps.withIndex()) {
            GLUtils.texImage2D(gl.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0)
        }
        gl.glBindTexture(gl.GL_TEXTURE_CUBE_MAP, 0)

        bitmaps.forEach { it.recycle() }
    }
}