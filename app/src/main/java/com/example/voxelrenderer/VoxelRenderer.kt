package com.example.voxelrenderer

import android.opengl.Matrix
import android.util.Log
import com.example.voxelrenderer.utils.Mesh
import com.example.voxelrenderer.utils.Shader
import com.example.voxelrenderer.utils.Skybox
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES32 as gl

class VoxelRenderer(
    private var mMesh: Mesh,
    private var mInitialDistance: Float
) : BasicRenderer() {
    private lateinit var mShader: Shader
    private lateinit var skyboxShader: Shader

    private val mViewM = FloatArray(16)
    private val mProjM = FloatArray(16)
    private val mRotationM = FloatArray(16)
    private val mTemp = FloatArray(16)
    private val mSkyboxViewM = FloatArray(16)

    private var mPrevTime = System.nanoTime()
    private var mNumFrame = 0

    private val skybox = Skybox()

    private val lightPosition = floatArrayOf(-100.0f, 1000.0f, -400.0f)
    private val lightColor = floatArrayOf(1.0f, 1.0f, 1.0f)

    @Volatile
    var mScaleFactor = 1.0f

    @Volatile
    var mRotation = 0.0f


    init {
        Matrix.setIdentityM(mViewM, 0)
        Matrix.setIdentityM(mProjM, 0)
        Matrix.setIdentityM(mRotationM, 0)
        Matrix.setIdentityM(mTemp, 0)

        Matrix.setLookAtM(
            mSkyboxViewM, 0,
            0f, 0f, 1f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

    }

    override fun onSurfaceCreated(gl10: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl10, config)

        mMesh.setupMesh()

        mShader = Shader(
            context.resources.openRawResource(R.raw.shader_vert),
            context.resources.openRawResource(R.raw.shader_frag)
        )
        skyboxShader = Shader(
            context.resources.openRawResource(R.raw.skybox_vs),
            context.resources.openRawResource(R.raw.skybox_fs)
        )

        skybox.setup()
        skybox.setupTexture(
            arrayOf(
                R.drawable.right, R.drawable.left,
                R.drawable.top, R.drawable.bottom,
                R.drawable.front, R.drawable.back
            ),
            context,
        )

        mShader.use {
            it.setVec3("lightColor", lightColor)
            it.setVec3("lightPos", lightPosition)
        }

        skyboxShader.use {
            skybox.bindTexture()
            skyboxShader.setInt("skybox", 0)
            gl.glBindTexture(gl.GL_TEXTURE_CUBE_MAP, 0)
        }
        gl.glEnable(gl.GL_DEPTH_TEST)
        gl.glDepthFunc(gl.GL_LESS)
        gl.glEnable(gl.GL_CULL_FACE)
        gl.glCullFace(gl.GL_BACK)
        gl.glFrontFace(gl.GL_CCW)
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl10, width, height)
        val h = if (height == 0) 1 else height
        val aspect = (width.toFloat()) / (h.toFloat())

        Matrix.perspectiveM(mProjM, 0, 45f, aspect, 0.1f, 1000f)
    }

    override fun onDrawFrame(gl10: GL10?) {
        gl.glClear(gl.GL_COLOR_BUFFER_BIT or gl.GL_DEPTH_BUFFER_BIT)

        val time = System.nanoTime()

        Matrix.setLookAtM(
            mViewM, 0,
            0f, 0f, mInitialDistance / mScaleFactor,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        Matrix.setIdentityM(mRotationM, 0)
        Matrix.rotateM(mRotationM, 0, mRotation, 0f, 1f, 0f)
        Matrix.multiplyMM(mTemp, 0, mProjM, 0, mViewM, 0)

        mShader.use {
            it.setMat4("VP", mTemp)
            it.setMat4("rotation", mRotationM)
            mMesh.draw()
        }

        gl.glDepthFunc(gl.GL_LEQUAL)
        skybox.bindTexture()
        skyboxShader.use {
            it.setMat4("view", mSkyboxViewM)
            it.setMat4("projection", mProjM)
            it.setMat4("rotation", mRotationM)
            skybox.draw()
        }
        gl.glDepthFunc(gl.GL_LESS)

        Log.v(TAG, "Rendering time: ${(System.nanoTime() - time) / 1_000_000f}ms ")
    }

    private fun countFPS() {
        val currentTime = System.nanoTime()
        mNumFrame++
        if (currentTime - mPrevTime >= 1_000_000_000) {
            Log.v(TAG, "FPS: $mNumFrame")
            mNumFrame = 0
            mPrevTime = currentTime
        }
    }
}