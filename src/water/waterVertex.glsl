#version 400 core

in vec2 position;

out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;

out vec3 toLightVector[4];

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;

uniform vec3 lightPosition[4];

const float tilingFactor = 6.0;

void main(void) {

    vec4 localSpace = vec4(position.x, 0.0, position.y, 1.0);
    vec4 worldSpace = modelMatrix * localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    clipSpace = projectionMatrix * cameraSpace;

    toCameraVector = cameraPosition - worldSpace.xyz;

    textureCoords = vec2(position.x / 2.0 + 0.5, position.y / 2.0 + 0.5) * tilingFactor;

    for (int i=0;i<4;i++) {
        toLightVector[i] = lightPosition[i] - worldSpace.xyz;
    }


    gl_Position = clipSpace;

}