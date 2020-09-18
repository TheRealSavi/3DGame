#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec3 surfaceNormal;
out vec2 pass_coords;
out vec3 cameraRayVector;
out vec3 pass_cameraPosition;

out vec3 toPointLightVectors[4];

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;

uniform vec3 pointLightPositions[4];

uniform vec4 clippingPlane;

void main(void) {

    vec4 localSpace = vec4(position, 1.0);
    vec4 worldSpace = modelMatrix * localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    vec4 clipSpace = projectionMatrix * cameraSpace;

    gl_Position = clipSpace;

    cameraRayVector = worldSpace.xyz - cameraPosition;

    pass_cameraPosition = cameraPosition;

    pass_coords = textureCoords;

    for (int i=0;i<4;i++) {
        toPointLightVectors[i] = pointLightPositions[i] - worldSpace.xyz;
    }

    gl_ClipDistance[0] = dot(worldSpace, clippingPlane);

    surfaceNormal = (modelMatrix * vec4(normal, 0.0)).xyz;
}