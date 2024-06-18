package com.example.voxelrenderer.utils

import android.graphics.Color
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.opengl.GLES32 as gl

data class MyColor(val r: Float, val g: Float, val b: Float) {
    fun toRgb(): Int {
        return Color.rgb(r, g, b)
    }

    fun toFloatArray(): FloatArray {
        return floatArrayOf(r, g, b)
    }
}

class Mesh(
    private val mVertices: FloatArray,
    private val mIndices: IntArray,
    private val mColors: List<FloatArray>,
    private val mTransformations: List<FloatArray>
) {
    private val mVao = IntArray(1)

    /**
     * Draw the mesh
     */
    fun draw() {
        gl.glBindVertexArray(mVao[0])
        gl.glDrawElementsInstanced(
            gl.GL_TRIANGLES,
            mIndices.size,
            gl.GL_UNSIGNED_INT,
            0,
            mTransformations.size
        )
        gl.glBindVertexArray(0)
    }

    fun setupMesh() {
        val vertexData = ByteBuffer.allocateDirect(mVertices.size * Float.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(mVertices)
                    position(0)
                }
            }


        val colorsData = run {
            val colors = mColors.flatMap { it.toList() }.toFloatArray()
            ByteBuffer.allocateDirect(colors.size * Float.SIZE_BYTES)
                .run {
                    order(ByteOrder.nativeOrder())
                    asFloatBuffer().apply {
                        put(colors)
                        position(0)
                    }
                }
        }

        val indicesData = ByteBuffer.allocateDirect(mIndices.size * Int.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asIntBuffer().apply {
                    put(mIndices)
                    position(0)
                }
            }


        val matricesData = run {
            val matrices = mTransformations.flatMap { it.toList() }.toFloatArray()
            ByteBuffer.allocateDirect(matrices.size * Float.SIZE_BYTES)
                .run {
                    order(ByteOrder.nativeOrder())
                    asFloatBuffer().apply {
                        put(matrices)
                        position(0)
                    }
                }
        }

        val vbo = IntArray(1)
        val colorVbo = IntArray(1)
        val modelVbo = IntArray(1)
        val ebo = IntArray(1)

        gl.glGenBuffers(1, vbo, 0)
        gl.glGenBuffers(1, colorVbo, 0)
        gl.glGenBuffers(1, modelVbo, 0)
        gl.glGenBuffers(1, ebo, 0)
        gl.glGenVertexArrays(1, mVao, 0)

        gl.glBindVertexArray(mVao[0])
        // ------------------
        // Vertex
        // ------------------
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            vertexData.capacity() * Float.SIZE_BYTES,
            vertexData,
            gl.GL_STATIC_DRAW
        )
        gl.glEnableVertexAttribArray(0)
        gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)
        // ------------------
        // Color
        // ------------------
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, colorVbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            colorsData.capacity() * Float.SIZE_BYTES,
            colorsData,
            gl.GL_STATIC_DRAW
        )
        gl.glEnableVertexAttribArray(1)
        gl.glVertexAttribPointer(1, 3, gl.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)
        gl.glVertexAttribDivisor(1, 1)
        // ------------------
        // Indices
        // ------------------
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            indicesData.capacity() * Int.SIZE_BYTES,
            indicesData,
            gl.GL_STATIC_DRAW
        )
        // ------------------
        // Model matrices
        // ------------------
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, modelVbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * matricesData.capacity(),
            matricesData,
            gl.GL_STATIC_DRAW
        )
        gl.glVertexAttribPointer(2, 3, gl.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)
        gl.glEnableVertexAttribArray(2)
        gl.glVertexAttribDivisor(2, 1)
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0)

        gl.glBindVertexArray(0)
    }
}