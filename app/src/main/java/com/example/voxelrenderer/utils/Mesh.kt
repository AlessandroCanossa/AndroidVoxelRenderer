package com.example.voxelrenderer.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.opengl.GLES32 as gl

data class Color(val r: Float, val g: Float, val b: Float)
class Mesh(
    private val vertices: FloatArray,
    private val indices: IntArray,
    private val color: Color,
    private val transformations: List<FloatArray>
) {
    private var vao = IntArray(1)
    private var vbo = IntArray(1)
    private var modelVbo = IntArray(1)
    private var ebo = IntArray(1)

    /**
     * Draw the mesh
     */
    fun draw(fn: (Color) -> Unit) {
        gl.glBindVertexArray(vao[0])
        fn(color)
        gl.glDrawElementsInstanced(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_INT, 0, transformations.size)

        gl.glBindVertexArray(0)
    }

    fun setupMesh() {
        val vertexData = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }

        val indexData = ByteBuffer.allocateDirect(indices.size * Int.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asIntBuffer().apply {
                    put(indices)
                    position(0)
                }
            }

        val matrices = transformations.flatMap { it.toList() }.toFloatArray()

        val matricesData = ByteBuffer.allocateDirect(matrices.size * Float.SIZE_BYTES)
            .run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(matrices)
                    position(0)
                }
            }

        gl.glGenBuffers(1, vbo, 0)
        gl.glGenBuffers(1, modelVbo, 0)
        gl.glGenBuffers(1, ebo, 0)
        gl.glGenVertexArrays(1, vao, 0)

        gl.glBindVertexArray(vao[0])
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0])

        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            vertexData.capacity() * Float.SIZE_BYTES,
            vertexData,
            gl.GL_STATIC_DRAW
        )
        gl.glEnableVertexAttribArray(0)
        gl.glVertexAttribPointer(0, 3, gl.GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)

        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            indexData.capacity() * Int.SIZE_BYTES,
            indexData,
            gl.GL_STATIC_DRAW
        )

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, modelVbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * matricesData.capacity(),
            matricesData,
            gl.GL_STATIC_DRAW
        )
        gl.glVertexAttribPointer(1, 4, gl.GL_FLOAT, false, 16 * Float.SIZE_BYTES, 0)
        gl.glEnableVertexAttribArray(1)
        gl.glVertexAttribPointer(
            2,
            4,
            gl.GL_FLOAT,
            false,
            16 * Float.SIZE_BYTES,
            4 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(2)
        gl.glVertexAttribPointer(
            3,
            4,
            gl.GL_FLOAT,
            false,
            16 * Float.SIZE_BYTES,
            8 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(3)
        gl.glVertexAttribPointer(
            4,
            4,
            gl.GL_FLOAT,
            false,
            16 * Float.SIZE_BYTES,
            12 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(4)
        gl.glVertexAttribDivisor(1, 1)
        gl.glVertexAttribDivisor(2, 1)
        gl.glVertexAttribDivisor(3, 1)
        gl.glVertexAttribDivisor(4, 1)
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0)

        gl.glBindVertexArray(0)
    }
}