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

    private var MVPLoc: Int = 0

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
            uniform mat4 MVP;
            out vec3 colorVarying;
            
            void main() {
                colorVarying = color;
                gl_Position = MVP* vec4(vPos, 1);
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

        //--1--|--2---|
        //vx,vy,r,g,b
//         val verticesAndColors = floatArrayOf(
//            -1f, -1f, 1f, 0f, 0f,
//             1f, -1f, 0f, 1f, 0f,
//             1f, 1f, 0f, 0f, 1f,
//             -1f, 1f, 1f, 0f, 1f
//        )
//
//        val indices = intArrayOf(
//            0, 1, 2,
//            3, 2, 0
//        )

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

        val vbo = IntArray(2)

        gl.glGenBuffers(2, vbo, 0)
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

        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, vbo[1])
        gl.glBufferData(
            gl.GL_ELEMENT_ARRAY_BUFFER,
            Int.SIZE_BYTES * indexData.capacity(),
            indexData,
            gl.GL_STATIC_DRAW
        )

        gl.glBindVertexArray(0)

        MVPLoc = gl.glGetUniformLocation(shaderHandle, "MVP");

//        gl.glEnable(gl.GL_CULL_FACE)
//        gl.glCullFace(gl.GL_BACK)
//        gl.glFrontFace(gl.GL_CW)
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
        Matrix.setIdentityM(modelM, 0)
        Matrix.translateM(modelM, 0, 0f, 0f, -1f)
        Matrix.scaleM(modelM, 0, 0.5f, 0.5f, 0.5f)
        Matrix.rotateM(modelM, 0, angle, 0f, 1f, 0f)

        Matrix.multiplyMM(temp, 0, projM, 0, viewM, 0)
        Matrix.multiplyMM(MVP, 0, temp, 0, modelM, 0)

        gl.glUseProgram(shaderHandle)
        gl.glBindVertexArray(vao[0])
//        gl.glUniform3fv(colorHandle, 1, floatArrayOf(1.0f, 1.0f, 1.0f), 0)

        gl.glUniformMatrix4fv(MVPLoc, 1, false, MVP, 0)
        gl.glDrawElements(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_INT, 0)

        Matrix.setIdentityM(modelM, 0)
        Matrix.translateM(modelM, 0, 0f, -1f, -1f)
        Matrix.scaleM(modelM, 0, 0.5f, 0.5f, 0.5f)
        Matrix.rotateM(modelM, 0, angle, 0f, 1f, 0f)

        Matrix.multiplyMM(temp, 0, projM, 0, viewM, 0)
        Matrix.multiplyMM(MVP, 0, temp, 0, modelM, 0)
        gl.glUniformMatrix4fv(MVPLoc, 1, false, MVP, 0)
        gl.glDrawElements(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_INT, 0)

        Matrix.setIdentityM(modelM, 0)
        Matrix.translateM(modelM, 0, 0f, 1f, -1f)
        Matrix.scaleM(modelM, 0, 0.5f, 0.5f, 0.5f)
        Matrix.rotateM(modelM, 0, angle, 0f, 1f, 0f)

        Matrix.multiplyMM(temp, 0, projM, 0, viewM, 0)
        Matrix.multiplyMM(MVP, 0, temp, 0, modelM, 0)
        gl.glUniformMatrix4fv(MVPLoc, 1, false, MVP, 0)
        gl.glDrawElements(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_INT, 0)

        gl.glBindVertexArray(0)
        gl.glUseProgram(0)
    }
}