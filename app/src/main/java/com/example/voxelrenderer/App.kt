package com.example.voxelrenderer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voxelrenderer.ui.Datasource
import com.example.voxelrenderer.ui.Model
import com.example.voxelrenderer.utils.Mesh
import com.example.voxelrenderer.utils.VlyLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class Screens {
    Selector,
    Renderer
}

@Composable
fun App(navController: NavHostController = rememberNavController(), modifier: Modifier = Modifier) {
    var mesh by remember {
        mutableStateOf<Mesh?>(null)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.Selector.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            composable(route = Screens.Selector.name) {
                ModelList(Datasource.loadModels(), loadModel = { model ->
                    runBlocking {
                        launch(Dispatchers.Default) {
                            mesh =
                                VlyLoader(context.assets.open(context.resources.getString(model.name))).apply {
                                    load()
                                }.parse()
                        }
                    }

                    navController.navigate(Screens.Renderer.name)
                })
            }
            composable(route = Screens.Renderer.name) {
                mesh?.let { it1 -> GlView(it1) }
                    ?: throw IllegalStateException("The mesh to be rendered cannot be null")
            }
        }
    }
}

@Composable
fun GlView(mesh: Mesh, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VoxelSurfaceView(context, mesh)
        },
    )
}

@Composable
fun ModelList(modelList: List<Model>, loadModel: (Model) -> Unit, modifier: Modifier = Modifier) {

    LazyColumn(modifier = modifier) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Select model to render",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
        }
        items(modelList) { model ->
            ModelCard(model = model, loadModel = loadModel)
        }
    }
}

@Composable
fun ModelCard(model: Model, loadModel: (Model) -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = {
            loadModel(model)
        }, modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = stringResource(id = model.name), fontWeight = FontWeight.Bold)
        }
    }
}
