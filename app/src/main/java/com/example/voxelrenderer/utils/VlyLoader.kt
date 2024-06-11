package com.example.voxelrenderer.utils

import android.opengl.Matrix
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream

data class Voxel(val x: Int, val y: Int, val z: Int, val value: Int)

class VlyLoader(private val inputStream: InputStream) {
    private val voxels = ArrayList<Voxel>()
    private var numX = 0
    private var numY = 0
    private var numZ = 0
    private var numVoxels = 0
    private val colors = ArrayList<Color>()

    fun load() {
        val lines = inputStream.bufferedReader().use(
            BufferedReader::readLines
        )
        inputStream.close()

        lines[0].split(' ').run {
            if (!this[0].contains("grid_size") || this.size < 4) {
                Log.e("VlyLoader", "Wrong file type passed")
                throw Exception("Wrong file type passed")
            }

            numX = this[1].toInt()
            numY = this[2].toInt()
            numZ = this[3].toInt()
        }

        numVoxels = lines[1].split(' ').run {
            if (!this[0].contains("voxel_num") || this.size < 2) {
                Log.e("VlyLoader", "Vly file miss voxel num")
                throw Exception("Vly file miss voxel num")
            }

            this[1].toInt()
        }

        var voxelCount = 0
        lines.subList(2, lines.size).forEach { line ->
            val data = line.split(' ').map { it.toInt() }
            if (voxelCount < numVoxels) {
                val (x, y, z, value) = data
                voxels.add(Voxel(x, y, z, value))
                voxelCount++
            } else {
                colors.add(
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

        return voxels
            .groupBy { it.value }
            .map { (key, value) ->
                val translations = value.map {
                    val matrix = FloatArray(16)

                    // Translate the voxel to the center
                    val x = it.x - numX / 2
                    val y = it.y - numY / 2
                    val z = it.z - numZ / 2

                    Matrix.setIdentityM(matrix, 0)
//                    Matrix.scaleM(matrix, 0, 0.01f, 0.01f, 0.01f)
                    Matrix.translateM(matrix, 0, -x.toFloat(), z.toFloat(), -y.toFloat())
                    matrix
                }

                Mesh(cubeVertices, indices, colors[key], translations)
            }
    }
}