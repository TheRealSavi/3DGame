#version 400 core

in vec2 position;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main(void){
    mat4 modelMatrixNoRotation = modelMatrix;
    modelMatrixNoRotation[0][0] = viewMatrix[0][0];
    modelMatrixNoRotation[0][1] = viewMatrix[1][0];
    modelMatrixNoRotation[0][2] = viewMatrix[2][0];
    modelMatrixNoRotation[1][0] = viewMatrix[0][1];
    modelMatrixNoRotation[1][2] = viewMatrix[2][1];
    modelMatrixNoRotation[2][0] = viewMatrix[0][2];
    modelMatrixNoRotation[2][1] = viewMatrix[1][2];
    modelMatrixNoRotation[2][2] = viewMatrix[2][2];

    vec4 localSpace = vec4(position, 0.0, 1.0);
    vec4 worldSpace = modelMatrixNoRotation * localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    vec4 clipSpace = projectionMatrix * cameraSpace;

    gl_Position = clipSpace;
}