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
    private var mVao = IntArray(1)
    private var mVbo = IntArray(1)
    private var mColorVbo = IntArray(1)
    private var mModelVbo = IntArray(1)
    private var mEbo = IntArray(1)

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

        gl.glGenBuffers(1, mVbo, 0)
        gl.glGenBuffers(1, mColorVbo, 0)
        gl.glGenBuffers(1, mModelVbo, 0)
        gl.glGenBuffers(1, mEbo, 0)
        gl.glGenVertexArrays(1, mVao, 0)

        gl.glBindVertexArray(mVao[0])
        // ------------------
        // Vertex
        // ------------------
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mVbo[0])
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
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mColorVbo[0])
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
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, mEbo[0])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            indicesData.capacity() * Int.SIZE_BYTES,
            indicesData,
            gl.GL_STATIC_DRAW
        )
        // ------------------
        // Model matrices
        // ------------------
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, mModelVbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * matricesData.capacity(),
            matricesData,
            gl.GL_STATIC_DRAW
        )
        gl.glVertexAttribPointer(2, 3, gl.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)
        gl.glEnableVertexAttribArray(2)
//        gl.glVertexAttribPointer(
//            3,
//            4,
//            gl.GL_FLOAT,
//            false,
//            16 * Float.SIZE_BYTES,
//            4 * Float.SIZE_BYTES
//        )
//        gl.glEnableVertexAttribArray(3)
//        gl.glVertexAttribPointer(
//            4,
//            4,
//            gl.GL_FLOAT,
//            false,
//            16 * Float.SIZE_BYTES,
//            8 * Float.SIZE_BYTES
//        )
//        gl.glEnableVertexAttribArray(4)
//        gl.glVertexAttribPointer(
//            5,
//            4,
//            gl.GL_FLOAT,
//            false,
//            16 * Float.SIZE_BYTES,
//            12 * Float.SIZE_BYTES
//        )
//        gl.glEnableVertexAttribArray(5)
        gl.glVertexAttribDivisor(2, 1)
//        gl.glVertexAttribDivisor(3, 1)
//        gl.glVertexAttribDivisor(4, 1)
//        gl.glVertexAttribDivisor(5, 1)
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0)

        gl.glBindVertexArray(0)
    }
}