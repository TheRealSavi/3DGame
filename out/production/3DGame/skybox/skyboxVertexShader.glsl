#version 400 core

in vec3 position;




out vec3 textureCoords;
out vec3 cameraRayVector;
out vec3 pass_cameraPosition;




uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;





void main(void) {

    vec4 localSpace = vec4(position, 1.0);
    vec4 worldSpace = localSpace;
    vec4 cameraSpace = viewMatrix * worldSpace;
    vec4 clipSpace = projectionMatrix * cameraSpace;

    gl_Position = clipSpace;

    cameraRayVector = worldSpace.xyz - cameraPosition;

    pass_cameraPosition = cameraPosition;

    textureCoords = position;
}