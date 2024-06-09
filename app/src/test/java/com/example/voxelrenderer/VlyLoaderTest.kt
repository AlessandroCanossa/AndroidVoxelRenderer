package com.example.voxelrenderer

import com.example.voxelrenderer.utils.Matrix3d
import com.example.voxelrenderer.utils.VlyLoader
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@Suppress("UNCHECKED_CAST")
fun <R> getPrivateMember(obj: Any, name: String): R {
    val numXField = obj.javaClass.getDeclaredField(name)
    numXField.isAccessible = true
    return numXField.get(obj) as R
}

class VlyLoaderTest {
    private val simpleModelIs = """
        grid_size: 1 1 3
        voxel_num: 3
        0 0 0 0
        0 0 1 1
        0 0 2 2
        0 238 0 0
        1 0 238 0
        2 0 0 238
    """.trimIndent()

    private lateinit var loader: VlyLoader

    @Test
    fun loadSimpleModel() {
        loader = VlyLoader(simpleModelIs.byteInputStream())
        loader.load()

        val numX = getPrivateMember<Int>(loader, "numX")
        val numY = getPrivateMember<Int>(loader, "numY")
        val numZ = getPrivateMember<Int>(loader, "numZ")
        val occupancyGrid = getPrivateMember<Matrix3d>(loader, "occupancyGrid")
        val colors = getPrivateMember<ArrayList<Triple<Int, Int, Int>>>(loader, "colors")


        assertEquals(1, numX)
        assertEquals(1, numY)
        assertEquals(3, numZ)
        assertEquals(
            3,
            occupancyGrid.size * occupancyGrid[0].size * occupancyGrid[0][0].size
        )
        assertEquals(3, colors.size)
    }

    @Test
    fun loadComplexModel() {
        val complexModel = this.javaClass.classLoader?.getResourceAsStream("assets/dragon.vly")
        if (complexModel != null) {
            loader = VlyLoader(complexModel)
            loader.load()

            val numX = getPrivateMember<Int>(loader, "numX")
            val numY = getPrivateMember<Int>(loader, "numY")
            val numZ = getPrivateMember<Int>(loader, "numZ")
            val occupancyGrid = getPrivateMember<Matrix3d>(loader, "occupancyGrid")
            val colors = getPrivateMember<ArrayList<Int>>(loader, "colors")

            assertEquals(126, numX)
            assertEquals(57, numY)
            assertEquals(89, numZ)
            assertEquals(
                126 * 57 * 89,
                occupancyGrid.size * occupancyGrid[0].size * occupancyGrid[0][0].size
            )
            assertEquals(200 * 4, colors.size)

        } else {
            fail("asset not found")
        }
    }
}