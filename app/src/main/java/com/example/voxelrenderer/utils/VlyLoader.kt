package com.example.voxelrenderer.utils

import android.graphics.Bitmap
import android.graphics.Color.rgb
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream


typealias Translation = FloatArray
typealias TranslationList = ArrayList<Translation>

data class Voxel(val x: Int, val y: Int, val z: Int, val value: Int)

class VlyLoader(private val inputStream: InputStream) {
    private lateinit var occupancyGrid: ArrayList<Voxel>
    private var numX = 0
    private var numY = 0
    private var numZ = 0
    private var numVoxels = 0
    private val colors = ArrayList<Int>()

    private lateinit var bitmap: Bitmap

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
                occupancyGrid.add(Voxel(x, y, z, value))
                voxelCount++
            } else {
                colors.add(rgb(data[1], data[2], data[3]))
            }
        }
    }

    fun parse() {
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

        val translations = HashMap<Int, TranslationList>()

        val prova = occupancyGrid.groupBy { it.value }

        prova.forEach { t, u ->
//            val translationList = ArrayList<FloatArray>()
//            u.forEach { voxel ->
//                val x = voxel.x.toFloat()
//                val y = voxel.y.toFloat()
//                val z = voxel.z.toFloat()
//                translationList.add(floatArrayOf(x, y, z))
//            }
//            translations[t] = translationList
        }


//        val texCoords = floatArrayOf(
//            1.0f, 1.0f,
//            0.0f, 1.0f,
//            0.0f, 0.0f,
//            1.0f, 0.0f,
//            1.0f, 1.0f,
//            0.0f, 1.0f,
//            0.0f, 0.0f,
//            1.0f, 0.0f,
//        )
//        val width = 1.0 / colors.size.toDouble()
//
//
//
//        bitmap =
//            Bitmap.createBitmap(colors.toIntArray(), colors.size, 1, Bitmap.Config.ARGB_8888)
    }
}