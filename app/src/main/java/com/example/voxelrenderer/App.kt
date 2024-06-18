package com.example.voxelrenderer

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voxelrenderer.ui.Datasource
import com.example.voxelrenderer.ui.Model
import com.example.voxelrenderer.ui.VoxelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Screens {
    Selector,
    Loader,
    Renderer
}

@Composable
fun App(
    appViewModel: VoxelViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { it ->
        NavHost(
            navController = navController,
            startDestination = Screens.Selector.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            composable(route = Screens.Selector.name) {
                ModelList(Datasource.loadModels(), { thisModel ->
                    appViewModel.setModel(thisModel)
                    navController.navigate(Screens.Loader.name)
                })
            }
            composable(route = Screens.Loader.name) {
                if (appViewModel.hasModel()) {
                    LoadingScreen(appViewModel, onModelLoaded = {
                        Log.d("App", "Model loaded, navigating to renderer")
                        navController.navigate(Screens.Renderer.name)
                    })
                } else {
                    navController.navigate(Screens.Selector.name)
                }
            }

            composable(route = Screens.Renderer.name) {
                if (appViewModel.hasMesh()) {
                    GlView(
                        viewModel = appViewModel, onBackGesture = {
                            navController.navigate(Screens.Selector.name) {
                                appViewModel.clearMesh()
                                appViewModel.clearModel()
                                popUpTo(Screens.Selector.name)
                            }
                        })
                } else {
                    navController.navigate(Screens.Selector.name)
                    appViewModel.clearModel()
                }
            }
        }
    }
}

@Composable
fun GlView(
    onBackGesture: () -> Unit,
    viewModel: VoxelViewModel,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val state by viewModel.voxelState.collectAsState()
    var stopRenderer by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val distance = integerResource(id = state.model!!.defaultDistance)
    val view = remember {
        VoxelSurfaceView(
            context = context,
            mesh = state.mesh!!,
            initialDistance = distance.toFloat()
        )
    }


    BackHandler(true) {
        stopRenderer = true
        onBackGesture()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("GlView", "Resuming")
                    view.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("GlView", "Pausing")
                    view.onPause()
                }

                else -> {
                    Log.d("GlView", "Event: $event")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }

    if (stopRenderer) {
        return
    }

    AndroidView(
        modifier = modifier,
        factory = {
            view
        },
    )
}

@Composable
fun LoadingScreen(
    viewModel: VoxelViewModel,
    onModelLoaded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.voxelState.collectAsState()
    // If the model is already loaded, call the callback
    if (state.mesh != null) {
        onModelLoaded()
    }

    val context = LocalContext.current
    // Load the model in a background thread
    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) {
            viewModel.loadMesh(context)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Loading model...")
        Spacer(modifier = Modifier.height(20.dp))
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
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
