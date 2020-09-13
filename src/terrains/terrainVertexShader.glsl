#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_coords;
out vec3 pass_cameraPosition;
out vec3 surfaceNormal;
out vec3 toCameraVector;
out float distanceFromCamera;

out vec3 toPointLightVectors[4];

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;

uniform vec4 clippingPlane;

uniform vec3 pointLightPositions[4];



void main(void) {

    vec4 localSpace = vec4(position, 1.0);
    vec4 worldSpace = modelMatrix * localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    vec4 clipSpace = projectionMatrix * cameraSpace;

    distanceFromCamera = length(cameraSpace.xyz);
    toCameraVector = cameraPosition - worldSpace.xyz;

    gl_ClipDistance[0] = dot(worldSpace, clippingPlane);

    gl_Position = clipSpace;

    pass_coords = textureCoords;
    pass_cameraPosition = cameraPosition;

    surfaceNormal = (modelMatrix * vec4(normal, 0.0)).xyz;

    for (int i=0;i<4;i++) {
        toPointLightVectors[i] = pointLightPositions[i] - worldSpace.xyz;
    }

}