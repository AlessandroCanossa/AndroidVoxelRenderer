#version 320 es

layout (location = 0) in vec3 vPos;
layout (location = 1) in mat4 model;

//uniform mat4 model;
uniform mat4 VP;
uniform vec3 color;

out vec3 colorVarying;

void main() {
    colorVarying = color;
    gl_Position = VP * model * vec4(vPos, 1);
}
