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

    private var mVPLoc: Int = 0
    private var mProjSkyboxLoc: Int = 0
    private var mViewSkyboxLoc: Int = 0

    private val mViewM = FloatArray(16)
    private val mProjM = FloatArray(16)
    private val mRotationM = FloatArray(16)
    private val mTemp = FloatArray(16)

    private var mPrevTime = System.nanoTime()
    private var mNumFrame = 0

    private val skybox = Skybox()

    @Volatile
    var mScaleFactor = 1.0f

    @Volatile
    var mRotation = 0.0f


    init {
        Matrix.setIdentityM(mViewM, 0)
        Matrix.setIdentityM(mProjM, 0)
        Matrix.setIdentityM(mRotationM, 0)
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

//        mVPLoc = mShader.getUniformLocation("VP")
//        mProjSkyboxLoc = skyboxShader.getUniformLocation("projection")
//        mViewSkyboxLoc = skyboxShader.getUniformLocation("view")

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

        countFPS()

        Matrix.setLookAtM(
            mViewM, 0,
            0f, 0f, mInitialDistance / mScaleFactor,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        Matrix.setIdentityM(mRotationM, 0)
        Matrix.rotateM(mRotationM, 0, mRotation, 0f, 1f, 0f)
        Matrix.multiplyMM(mTemp, 0, mProjM, 0, mViewM, 0)
        Matrix.multiplyMM(mTemp, 0, mTemp, 0, mRotationM, 0)

        mShader.use {
            mShader.setMat4("VP", mTemp)
//            gl.glUniformMatrix4fv(mVPLoc, 1, false, mTemp, 0)
            mMesh.draw()
        }

        gl.glDepthFunc(gl.GL_LEQUAL)
        skybox.bindTexture()
        skyboxShader.use {
            Matrix.setLookAtM(
                mTemp, 0,
                0f, 0f, 1f,
                0f, 0f, 0f,
                0f, 1f, 0f
            )
            skyboxShader.setMat4("view", mTemp)
            skyboxShader.setMat4("projection", mProjM)
//            gl.glUniformMatrix4fv(mProjSkyboxLoc, 1, false, mProjM, 0)
//            gl.glUniformMatrix4fv(mViewSkyboxLoc, 1, false, mTemp, 0)
            skybox.draw()
        }
        gl.glDepthFunc(gl.GL_LESS)
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