package com.example.voxelrenderer

import android.opengl.Matrix
import com.example.voxelrenderer.utils.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES32 as gl

class VoxelRenderer : BasicRenderer() {
    private var vao = intArrayOf(0)
    private var shaderHandle = 0

    private var VPLoc: Int = 0

    private val viewM = FloatArray(16)
    private val modelM = FloatArray(16)
    private val projM = FloatArray(16)
    private val MVP = FloatArray(16)
    private val temp = FloatArray(16)

    private var angle: Float = 0.0f

    init {
        Matrix.setIdentityM(viewM, 0)
        Matrix.setIdentityM(modelM, 0)
        Matrix.setIdentityM(projM, 0)
        Matrix.setIdentityM(MVP, 0)
    }

    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl10, config)

        val vertexShader = """
            #version 320 es
            layout(location = 1) in vec3 vPos;
            layout(location = 2) in vec3 color;
            layout(location = 3) in mat4 model;
            
            uniform mat4 VP;
            
            out vec3 colorVarying;
            
            void main() {
                colorVarying = color;
                gl_Position = VP * model * vec4(vPos, 1);
            }
        """.trimIndent()

        val fragmentShader = """
           #version 320 es
           precision mediump float;
           
           in vec3 colorVarying;
           out vec4 fragColor;
           
           void main() {
               fragColor = vec4(colorVarying, 1);
           }
        """.trimIndent()

        shaderHandle = ShaderCompiler.createProgram(vertexShader, fragmentShader)

        val cubeVertices = floatArrayOf(
            // front face
            1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
            -1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            // back face
            1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 0.0f,
            -1.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f
        )
        val indices = intArrayOf(
            0, 1, 2, 0, 2, 3, // front face
            4, 5, 6, 4, 6, 7, // back face
            0, 4, 7, 0, 7, 3, // right face
            1, 5, 6, 1, 6, 2, // left face
            0, 1, 5, 0, 5, 4, // top face
            3, 2, 6, 3, 6, 7, // bottom face
        )

        val matrices = (0 until 10).map {
            val matrix1 = FloatArray(16)
            Matrix.setIdentityM(matrix1, 0)
            Matrix.translateM(matrix1, 0, 0f, it.toFloat() / 2, -it.toFloat())
            Matrix.scaleM(matrix1, 0, 0.5f, 0.5f, 0.5f)
            matrix1
        }.flatMap { it.toList() }.toFloatArray()


        val vertexData = ByteBuffer.allocateDirect(cubeVertices.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(cubeVertices)
                position(0)
            }
        }

        val indexData = ByteBuffer.allocateDirect(indices.size * Int.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asIntBuffer().apply {
                put(indices)
                position(0)
            }
        }

        val matricesData = ByteBuffer.allocateDirect(matrices.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(matrices)
                position(0)
            }
        }

        val vbo = IntArray(1)
        val ebo = IntArray(1)
        val modelVBO = IntArray(1)

        gl.glGenBuffers(1, vbo, 0)
        gl.glGenBuffers(1, ebo, 0)
        gl.glGenBuffers(1, modelVBO, 0)
        gl.glGenVertexArrays(1, vao, 0)

        gl.glBindVertexArray(vao[0])
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * vertexData.capacity(),
            vertexData,
            gl.GL_STATIC_DRAW
        )
        gl.glVertexAttribPointer(1, 3, gl.GL_FLOAT, false, 6 * Float.SIZE_BYTES, 0)
        gl.glVertexAttribPointer(
            2, 3, gl.GL_FLOAT, false, 6 * Float.SIZE_BYTES, 3 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(1)
        gl.glEnableVertexAttribArray(2)

        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            Int.SIZE_BYTES * indexData.capacity(),
            indexData,
            gl.GL_STATIC_DRAW
        )

        // instanced data (model matrices)
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, modelVBO[0])
        gl.glBufferData(
            gl.GL_ARRAY_BUFFER,
            Float.SIZE_BYTES * matricesData.capacity(),
            matricesData,
            gl.GL_STATIC_DRAW
        )
        gl.glVertexAttribPointer(3, 4, gl.GL_FLOAT, false, 16 * Float.SIZE_BYTES, 0)
        gl.glEnableVertexAttribArray(3)
        gl.glVertexAttribPointer(
            4,
            4,
            gl.GL_FLOAT,
            false,
            16 * Float.SIZE_BYTES,
            4 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(4)
        gl.glVertexAttribPointer(
            5,
            4,
            gl.GL_FLOAT,
            false,
            16 * Float.SIZE_BYTES,
            8 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(5)
        gl.glVertexAttribPointer(
            6,
            4,
            gl.GL_FLOAT,
            false,
            16 * Float.SIZE_BYTES,
            12 * Float.SIZE_BYTES
        )
        gl.glEnableVertexAttribArray(6)
        gl.glVertexAttribDivisor(3, 1)
        gl.glVertexAttribDivisor(4, 1)
        gl.glVertexAttribDivisor(5, 1)
        gl.glVertexAttribDivisor(6, 1)
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0)

        gl.glBindVertexArray(0)

        VPLoc = gl.glGetUniformLocation(shaderHandle, "VP");

        gl.glEnable(gl.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gL10: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gL10, width, height)
        val h = if (height == 0) 1 else height
        val aspect = (width.toFloat()) / (h.toFloat())

        Matrix.perspectiveM(projM, 0, 45f, aspect, 0.1f, 100f)
        Matrix.setLookAtM(
            viewM, 0,
            0f, 0f, 4f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

    }

    override fun onDrawFrame(gl10: GL10?) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT or gl.GL_DEPTH_BUFFER_BIT)

        angle += 0.5f

        val translation = FloatArray(16)

        Matrix.multiplyMM(temp, 0, projM, 0, viewM, 0)

        gl.glUseProgram(shaderHandle)
        gl.glBindVertexArray(vao[0])

        gl.glUniformMatrix4fv(VPLoc, 1, false, temp, 0)

        gl.glDrawElementsInstanced(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_INT, 0, 10)

        gl.glBindVertexArray(0)
        gl.glUseProgram(0)
    }
}