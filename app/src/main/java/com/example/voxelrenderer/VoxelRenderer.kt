package com.example.voxelrenderer

import android.opengl.Matrix
import android.util.Log
import com.example.voxelrenderer.utils.Mesh
import com.example.voxelrenderer.utils.Shader
import com.example.voxelrenderer.utils.VlyLoader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES32 as gl

class VoxelRenderer : BasicRenderer() {
    private lateinit var shader: Shader
    private val vao = IntArray(1)

    private var VPLoc: Int = 0
    private var colorLoc: Int = 0

    private val viewM = FloatArray(16)
    private val projM = FloatArray(16)
    private val temp = FloatArray(16)

    private lateinit var meshes: List<Mesh>

    private var prevTime = System.nanoTime()
    private var numFrame = 0

    init {
        Matrix.setIdentityM(viewM, 0)
        Matrix.setIdentityM(projM, 0)
    }

    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl10, config)

        shader = Shader(
            context.assets.open("shader.vert"),
            context.assets.open("shader.frag")
        )

        val loader = VlyLoader(context.assets.open("simple.vly"))
        loader.load()

        meshes = loader.parse().onEach { it.setupMesh() }

        VPLoc = shader.getUniformLocation("VP")
        colorLoc = shader.getUniformLocation("color")

        gl.glEnable(gl.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl10, width, height)
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

        countFPS()

        Matrix.multiplyMM(temp, 0, projM, 0, viewM, 0)

        shader.use {
            meshes.forEach { mesh ->
                mesh.draw { color ->
                    gl.glUniformMatrix4fv(VPLoc, 1, false, temp, 0)
                    gl.glUniform3f(colorLoc, color.r, color.g, color.b)
                }
            }
        }
    }

    private fun countFPS() {
        val currentTime = System.nanoTime()
        numFrame++
        if (currentTime - prevTime >= 1_000_000_000) {
            Log.v(TAG, "FPS: $numFrame")
            numFrame = 0
            prevTime = currentTime
        }
    }
}