#version 400 core

in vec3 position;

out vec3 textureCoords;
out vec3 toCameraVector;
out vec3 pass_cameraPosition;
out float distanceFromCamera;

uniform vec3 cameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void) {
    vec4 localSpace = vec4(position, 1.0);
    vec4 worldSpace = localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    vec4 clipSpace = projectionMatrix * cameraSpace;

    gl_Position = clipSpace;

    toCameraVector = cameraPosition - worldSpace.xyz;
    distanceFromCamera = length(cameraSpace.xyz);

    textureCoords = position;
    pass_cameraPosition = cameraPosition;
}