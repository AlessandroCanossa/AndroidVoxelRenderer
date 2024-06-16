#version 320 es

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec3 Color;
layout (location = 2) in vec3 offset;

uniform mat4 VP;

out vec3 color;

void main() {
    color = Color;
    gl_Position = VP * vec4(vPos + offset, 1);
}
