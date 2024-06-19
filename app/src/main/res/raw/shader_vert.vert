#version 320 es

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 offset;
layout (location = 3) in vec3 normal;

uniform mat4 VP;
uniform mat4 rotation;

out vec3 Color;
out vec3 Normal;
out vec3 FragPos;

void main() {
    Color = color;
    Normal = normal;
    FragPos = (rotation * vec4(vPos + offset, 1)).xyz;
    gl_Position = VP * rotation * vec4(vPos + offset, 1);
}
