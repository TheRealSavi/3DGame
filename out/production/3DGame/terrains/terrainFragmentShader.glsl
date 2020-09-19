#version 400 core

in vec3 surfaceNormal;
in vec2 pass_coords;
in vec3 cameraRayVector;
in vec3 pass_cameraPosition;

in vec3 toPointLightVectors[4];

out vec4 out_color;

uniform float shineDamper;
uniform float reflectivity;
uniform float fogDensity;

uniform vec3 pointLightColors[4];
uniform vec3 pointLightAttenuations[4];

uniform vec3 directionalLightDirections[4];
uniform vec3 directionalLightColors[4];

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

vec3 applyFog(vec3 rgb, float distance, vec3 rayDir, vec3 rayPos, vec3 sunDir) {

    float fogAmmount = (0.0015625 / fogDensity) * exp(-rayPos.y * fogDensity) * (1.0 - exp(-distance * rayDir.y * fogDensity)) / rayDir.y;
    fogAmmount = clamp(fogAmmount, 0.0, 1.0);

    float sunAmmount =  max(dot(rayDir, sunDir), 0.0);
    vec3 fogColor = mix(vec3(0.5, 0.5, 0.7), vec3(1.0, 0.9, 0.7), sunAmmount);

    return mix(rgb, fogColor, fogAmmount);

}

void main(void) {
    float distanceFromCamera = length(cameraRayVector);

    vec2 tiledCoords = pass_coords * 40.0;
    vec4 blendMapColor = texture(blendMap, pass_coords);
    float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
    vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
    vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;
    vec4 finalTextureColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

    vec3 normal = normalize(surfaceNormal);

    vec3 totalDiffusedLighting = vec3(0.0);
    vec3 totalSpecularLighting = vec3(0.0);
    vec3 cameraRayDirection = normalize(cameraRayVector);

    //point light diffused and specular lighting calculations
    for (int i=0;i<4;i++) {
        float distanceToLight = length(toPointLightVectors[i]);
        float attenuationFactor = pointLightAttenuations[i].x + (pointLightAttenuations[i].y * distanceToLight) + (pointLightAttenuations[i].z * distanceToLight * distanceToLight);

        vec3 rayToPointLight = normalize(toPointLightVectors[i]);
        vec3 rayFromPointLight = -rayToPointLight;

        float normalDot1 = dot(normal, rayFromPointLight);
        float brightness = max(normalDot1, 0.0);

        vec3 diffusedLight = (brightness * pointLightColors[i]) / attenuationFactor;
        totalDiffusedLighting = totalDiffusedLighting + diffusedLight;

        vec3 reflectedLightDirection = reflect(rayFromPointLight, normal);

        float specularFactor = dot(reflectedLightDirection, cameraRayDirection);
        float specularLight = max(specularFactor, 0.0);
        float dampedSpecularLight = pow(specularLight, shineDamper);
        vec3 finalSpecularLight = (dampedSpecularLight * reflectivity * pointLightColors[i]) / attenuationFactor;
        totalSpecularLighting = totalSpecularLighting + finalSpecularLight;
    }

    //directional light diffused and specular lighting calculations
    for (int i=0;i<4;i++) {
        vec3 rayFromLight = normalize(directionalLightDirections[i]);
        vec3 rayToLight = -rayFromLight;

        float normalDot1 = dot(normal, rayFromLight);
        float brightness = max(normalDot1, 0.0);

        vec3 diffusedLight = (brightness * directionalLightColors[i]);
        totalDiffusedLighting = totalDiffusedLighting + diffusedLight;

        vec3 reflectedLightDirection = reflect(rayFromLight, normal);

        float specularFactor = dot(reflectedLightDirection, cameraRayDirection);
        float specularLight = max(specularFactor, 0.0);
        float dampedSpecularLight = pow(specularLight, shineDamper);
        vec3 finalSpecularLight = (dampedSpecularLight * reflectivity * directionalLightColors[i]);
        totalSpecularLighting = totalSpecularLighting + finalSpecularLight;
    }

    totalDiffusedLighting = max(totalDiffusedLighting, 0.2);
    totalSpecularLighting = max(totalSpecularLighting, 0.0);

    vec4 lightedTextureColor = vec4(totalDiffusedLighting, 1.0) * finalTextureColor + vec4(totalSpecularLighting, 1.0);

    out_color = vec4(applyFog(lightedTextureColor.rgb, distanceFromCamera, cameraRayDirection, pass_cameraPosition, normalize(directionalLightDirections[0])), 1.0);
}

