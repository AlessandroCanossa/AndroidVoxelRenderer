package com.example.voxelrenderer.utils

val cubeVertices = floatArrayOf(
    // front face
    -0.5f, 0.5f, 0.5f,
    0.5f, 0.5f, 0.5f,
    -0.5f, -0.5f, 0.5f,
    0.5f, -0.5f, 0.5f,
    // back face
    -0.5f, 0.5f, -0.5f,
    0.5f, 0.5f, -0.5f,
    -0.5f, -0.5f, -0.5f,
    0.5f, -0.5f, -0.5f,
)
val indices = intArrayOf(
    0, 1, 2, 1, 3, 2, // front face
    4, 6, 5, 5, 6, 7, // back face
    1, 5, 3, 5, 7, 3, // right face
    4, 0, 2, 4, 2, 6, // left face
    0, 4, 1, 1, 4, 5, // top face
    3, 6, 2, 3, 7, 6, // bottom face
)

val skyboxVertices = floatArrayOf(
    // positions
    -1f, 1f, 1f, // top-left near
    1f, 1f, 1f, // top-right near
    -1f, -1f, 1f, // bottom-left near
    1f, -1f, 1f, // bottom-right near

    -1f, 1f, -1f, // top-left far
    1f, 1f, -1f, // top-right far
    -1f, -1f, -1f, // bottom-left far
    1f, -1f, -1f, // bottom-right far
)

val skyboxIndices = intArrayOf(
    1, 3, 0,
    0, 3, 2,

    4, 6, 5,
    5, 6, 7,

    0, 2, 4,
    4, 2, 6,

    5, 7, 1,
    1, 7, 3,

    5, 1, 4,
    4, 1, 0,

    6, 2, 7,
    7, 2, 3
)
