#version 320 es

precision mediump float;

in vec3 colorVarying;
out vec4 fragColor;

void main() {
    fragColor = vec4(colorVarying, 1);
}
