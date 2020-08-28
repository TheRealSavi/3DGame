#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_coords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform float useFakeLighting;

const float fogDensity = 0.003;
const float fogGradient = 6;

void main(void) {

    vec4 localSpace = vec4(position, 1.0);
    vec4 worldSpace = transformationMatrix * localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    vec4 clipSpace = projectionMatrix * cameraSpace;
    gl_Position = clipSpace;

    pass_coords = textureCoords;

    vec3 actualNormal = normal;
    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1.0, 0.0);
    }
    surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;

    for (int i=0;i<4;i++) {
        toLightVector[i] = lightPosition[i] - worldSpace.xyz;
    }

    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldSpace.xyz;

    float distanceFromCamera = length(cameraSpace.xyz);
    visibility = exp(-pow((distanceFromCamera * fogDensity), fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);
}