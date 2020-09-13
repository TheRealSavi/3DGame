#version 400 core

in vec3 textureCoords;
in vec3 toCameraVector;
in vec3 pass_cameraPosition;
in float distanceFromCamera;
out vec4 out_color;

uniform samplerCube cubeMap;
uniform samplerCube cubeMap2;
uniform float blendFactor;

uniform vec3 directionalLightDirection;
uniform float fogDensity;

vec3 applyFog(vec3 rgb, float distance, vec3 rayDir, vec3 rayPos, vec3 sunDir) {

    float fogAmmount = (0.0015625 / fogDensity) * exp(-rayPos.y * fogDensity) * (1.0 - exp(-distance * rayDir.y * fogDensity)) / rayDir.y;
    fogAmmount = clamp(fogAmmount, 0.0, 1.0);

    float sunAmmount = clamp(dot(rayDir, sunDir), 0.0, 1.0 );
    vec3 fogColor = mix( vec3(0.5, 0.5, 0.7), vec3(1.0, 0.9, 0.7), sunAmmount);

    return mix(rgb, fogColor, fogAmmount);

}

void main(void) {
    vec4 texture1 = texture(cubeMap, textureCoords);
    vec4 texture2 = texture(cubeMap2, textureCoords);
    vec4 finalColor = mix(texture1, texture2, blendFactor);

    out_color = vec4(applyFog(finalColor.rgb, distanceFromCamera, normalize(-toCameraVector), pass_cameraPosition, normalize(directionalLightDirection) ), 1.0);
}