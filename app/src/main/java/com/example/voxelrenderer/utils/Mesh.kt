package com.example.voxelrenderer.utils

import java.nio.ByteBuffer
import java.nio.IntBuffer
import android.opengl.GLES32 as gl

class Mesh(
    private val vertices: FloatArray,
    private val indices: IntArray,
    private val texture: IntArray
) {
    private var vao = intArrayOf(0)
    private var vbo = intArrayOf(0)
    private var ebo = intArrayOf(0)

    init {
        setupMesh()
    }

    /**
     * Draw the mesh
     */
    fun draw() {
        gl.glActiveTexture(gl.GL_TEXTURE0)
        gl.glBindTexture(gl.GL_TEXTURE_2D, texture[0])

        gl.glBindVertexArray(vao[0])
        gl.glDrawElements(gl.GL_TRIANGLES, indices.size, gl.GL_UNSIGNED_INT, 0)
        gl.glBindVertexArray(0)

        gl.glBindTexture(gl.GL_TEXTURE_2D, 0)
    }

    private fun setupMesh() {
        val vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .run {
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }

        val indexData = ByteBuffer.allocateDirect(indices.size * 4).run {
            asIntBuffer().apply {
                put(indices)
                position(0)
            }
        }

        gl.glGenVertexArrays(1, vao, 1)
        gl.glGenBuffers(1, vbo, 1)
        gl.glGenBuffers(1, ebo, 1)

        gl.glBindVertexArray(vao[0])
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0])

        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            vertices.size * 4,
            vertexData,
            gl.GL_STATIC_DRAW
        )

        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            indices.size * 4,
            indexData,
            gl.GL_STATIC_DRAW
        )

        // position attribute
        gl.glEnableVertexAttribArray(0)
        gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 8 * 4, 0)
        // texture attribute
        gl.glEnableVertexAttribArray(1)
        gl.glVertexAttribPointer(1, 2, gl.GL_FLOAT, false, 8 * 4, 3 * 4)

        gl.glBindVertexArray(0)
    }
}