package com.example.voxelrenderer

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.voxelrenderer.ui.theme.VoxelRendererTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoxelRendererTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    GlView(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun GlView(modifier: Modifier) {
    AndroidView(factory = { context ->
        GLSurfaceView(context).apply {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val reqGlesVersion = activityManager.deviceConfigurationInfo.reqGlEsVersion

            val supported = if (reqGlesVersion >= 0x30000) {
                3
            } else if (reqGlesVersion >= 0x20000) {
                2
            } else {
                1
            }

            setEGLContextClientVersion(supported)
            preserveEGLContextOnPause = true

            val renderer = VoxelRenderer()
            setRenderer(renderer)
        }
    })
}