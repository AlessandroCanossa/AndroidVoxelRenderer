package com.example.voxelrenderer.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStream

data class Voxel(val x: Int, val y: Int, val z: Int, val value: Int)

class VlyLoader(private val mInputStream: InputStream) {
    private val mVoxels = ArrayList<Voxel>()
    private var mNumX = 0
    private var mNumY = 0
    private var mNumZ = 0
    private var mNumVoxels = 0
    private val mColors = ArrayList<MyColor>()

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

        val voxelLines = lines.subList(2, 2 + mNumVoxels)
        val colorLines = lines.subList(2 + mNumVoxels, lines.size)

        val time = System.nanoTime()
        runBlocking {
            launch(Dispatchers.Default) {
                voxelLines
                    .map { line ->
                        line.split(' ').map { it.toInt() }
                    }
                    .forEach { (x, y, z, value) ->
                        mVoxels.add(Voxel(x, y, z, value))
                    }
            }

            launch(Dispatchers.Default) {
                colorLines
                    .map { line ->
                        line.split(' ').map { it.toFloat() }
                    }
                    .forEach { (_, r, g, b) ->
                        mColors.add(
                            MyColor(
                                r / 255.0f,
                                g / 255.0f,
                                b / 255.0f
                            )
                        )
                    }
            }
        }

        Log.v("", "TIme: ${(System.nanoTime()-time)/1000000f}ms")

    }

    fun parse(): Mesh {
        val (translations, colors) =
            mVoxels.map {
                val x = it.x - mNumX / 2
                val y = it.y - mNumY / 2
                val z = it.z - mNumZ / 2

                val color = mColors[it.value].toFloatArray()

                Pair(floatArrayOf(-x.toFloat(), z.toFloat(), -y.toFloat()), color)
            }.unzip()


        return Mesh(cubeVertices, indices, colors, translations)
    }

}