package com.example.voxelrenderer.utils

import android.opengl.Matrix
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream

data class Voxel(val x: Int, val y: Int, val z: Int, val value: Int)

class VlyLoader(private val mInputStream: InputStream) {
    private val mVoxels = ArrayList<Voxel>()
    private var mNumX = 0
    private var mNumY = 0
    private var mNumZ = 0
    private var mNumVoxels = 0
    private val mColors = ArrayList<Color>()

    fun load() {
        val lines = mInputStream.bufferedReader().use(
            BufferedReader::readLines
        )
        mInputStream.close()

        lines[0].split(' ').run {
            if (!this[0].contains("grid_size") || this.size < 4) {
                Log.e("VlyLoader", "Wrong file type passed")
                throw Exception("Wrong file type passed")
            }

            mNumX = this[1].toInt()
            mNumY = this[2].toInt()
            mNumZ = this[3].toInt()
        }

        mNumVoxels = lines[1].split(' ').run {
            if (!this[0].contains("voxel_num") || this.size < 2) {
                Log.e("VlyLoader", "Vly file miss voxel num")
                throw Exception("Vly file miss voxel num")
            }

            this[1].toInt()
        }

        var voxelCount = 0
        lines.subList(2, lines.size).forEach { line ->
            val data = line.split(' ').map { it.toInt() }
            if (voxelCount < mNumVoxels) {
                val (x, y, z, value) = data
                mVoxels.add(Voxel(x, y, z, value))
                voxelCount++
            } else {
                mColors.add(
                    Color(
                        data[1].toFloat() / 255.0f,
                        data[2].toFloat() / 255.0f,
                        data[3].toFloat() / 255.0f
                    )
                )
            }
        }
    }

    fun parse(): List<Mesh> {
        val cubeVertices = floatArrayOf(
            // front face
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            // back face
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
        )
        val indices = intArrayOf(
            0, 1, 2, 0, 2, 3, // front face
            4, 5, 6, 4, 6, 7, // back face
            0, 4, 7, 0, 7, 3, // right face
            1, 5, 6, 1, 6, 2, // left face
            0, 1, 5, 0, 5, 4, // top face
            3, 2, 6, 3, 6, 7, // bottom face
        )

        return mVoxels
            .groupBy { it.value }
            .map { (key, value) ->
                val translations = value.map {
                    val matrix = FloatArray(16)

                    // Translate the voxel to the center
                    val x = it.x - mNumX / 2
                    val y = it.y - mNumY / 2
                    val z = it.z - mNumZ / 2

                    Matrix.setIdentityM(matrix, 0)
                    Log.d("VlyLoader", "x: $x, y: $z, z: $y")
                    Matrix.translateM(matrix, 0, -x.toFloat(), z.toFloat(), -y.toFloat())
                    Matrix.scaleM(matrix, 0, 0.5f, 0.5f, 0.5f)
                    matrix
                }

                Mesh(cubeVertices, indices, mColors[key], translations)
            }
    }
}