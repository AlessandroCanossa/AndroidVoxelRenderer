#version 320 es

precision mediump float;

uniform vec3 lightColor;
uniform vec3 lightPos;

in vec3 Normal;
in vec3 Color;
in vec3 FragPos;
out vec4 fragColor;

void main() {
    float ambientStrength = .6;
    vec3 ambient = ambientStrength * lightColor;
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 result = (ambient + diffuse) * Color;
    fragColor = vec4(result, 1);
}
