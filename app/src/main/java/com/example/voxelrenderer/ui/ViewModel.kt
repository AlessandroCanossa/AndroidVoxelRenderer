package com.example.voxelrenderer.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.voxelrenderer.utils.Mesh
import com.example.voxelrenderer.utils.VlyLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VoxelViewModel : ViewModel() {
    private val _voxelState = MutableStateFlow(VoxelState())
    val voxelState: StateFlow<VoxelState> = _voxelState.asStateFlow()


    fun setModel(model: Model) {
        _voxelState.update { it.copy(model = model) }
    }

    fun loadMesh(context: Context) {
        val model = _voxelState.value.model ?: return
        // Load mesh
        // val mesh = VlyLoader.load(model.model)
        val mesh =
            VlyLoader(context.resources.openRawResource(model.model)).apply { load() }.parse()

        _voxelState.update {
            it.copy(mesh = mesh)
        }
    }

    fun clearMesh() {
        _voxelState.update { it.copy(mesh = null) }
    }

    fun clearModel() {
        _voxelState.update { it.copy(model = null) }
    }

    fun hasMesh(): Boolean {
        return voxelState.value.mesh != null
    }

    fun hasModel(): Boolean {
        return _voxelState.value.model != null
    }
}

data class VoxelState(
    val model: Model? = null,
    val mesh: Mesh? = null,
)