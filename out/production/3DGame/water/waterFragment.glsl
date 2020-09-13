#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;
in float distanceFromCamera;
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

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D distortionMap;
uniform sampler2D depthMap;

uniform float cameraNearPlane;
uniform float cameraFarPlane;

uniform float moveFactor;

const float waveStrength = 0.009;

vec3 applyFog(vec3 rgb, float distance, vec3 rayDir, vec3 rayPos, vec3 sunDir) {

    float fogAmmount = (0.0015625 / fogDensity) * exp(-rayPos.y * fogDensity) * (1.0 - exp(-distance * rayDir.y * fogDensity)) / rayDir.y;
    fogAmmount = clamp(fogAmmount, 0.0, 1.0);

    float sunAmmount =  max( dot(rayDir, sunDir), 0.0);
    vec3 fogColor = mix( vec3(0.5, 0.5, 0.7), vec3(1.0, 0.9, 0.7), sunAmmount);

    return mix(rgb, fogColor, fogAmmount);

}

void main(void) {

    vec2 normalizedDeviceSpace = (clipSpace.xy / clipSpace.w);

    vec2 screenSpaceUpsideDown = vec2(normalizedDeviceSpace.x, -normalizedDeviceSpace.y) / 2.0 + 0.5;
    vec2 screenSpace = normalizedDeviceSpace / 2.0 + 0.5;

    float depth = texture(depthMap, screenSpace).r;
    float distanceToBottom = 2.0 * cameraNearPlane * cameraFarPlane / (cameraFarPlane + cameraNearPlane - (2.0 * depth - 1.0) * (cameraFarPlane - cameraNearPlane));
    depth = gl_FragCoord.z;
    float distanceToWater = 2.0 * cameraNearPlane * cameraFarPlane / (cameraFarPlane + cameraNearPlane - (2.0 * depth - 1.0) * (cameraFarPlane - cameraNearPlane));
    float waterDepth = distanceToBottom - distanceToWater;

    vec2 distortion1 = (texture(distortionMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rb * 2.0 - 1.0) * waveStrength;
    vec2 distortion2 = (texture(distortionMap, vec2(-0.5 * textureCoords.x + moveFactor, textureCoords.y + moveFactor)).rb * 2.0 - 1.0) * waveStrength;
    vec2 totalDistortion = (distortion1 + distortion2) * clamp(waterDepth / 10.0, 0.0, 1.0);

    vec3 normal = normalize(vec3(totalDistortion.x, totalDistortion.y * 5.6, distortion1.x));
    vec3 unitNormal = normalize(normal);

    vec3 totalDiffusedLighting = vec3(0.0);
    vec3 totalSpecularLighting = vec3(0.0);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    //point light diffused and specular lighting calculations
    for (int i=0;i<4;i++) {
        float distanceToLight = length(toPointLightVectors[i]);
        float attenuationFactor = pointLightAttenuations[i].x + (pointLightAttenuations[i].y * distanceToLight) + (pointLightAttenuations[i].z * distanceToLight * distanceToLight);

        vec3 unitLightVector = normalize(toPointLightVectors[i]);
        float normalDot1 = dot(unitNormal, unitLightVector);
        float brightness = max(normalDot1, 0.0);

        vec3 diffusedLight = (brightness * pointLightColors[i]) / attenuationFactor;
        totalDiffusedLighting = totalDiffusedLighting + diffusedLight;

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        float specularLight = max(specularFactor, 0.0);
        float dampedSpecularLight = pow(specularLight, shineDamper);
        vec3 finalSpecularLight = (dampedSpecularLight * reflectivity * pointLightColors[i]) / attenuationFactor;
        totalSpecularLighting = totalSpecularLighting + finalSpecularLight;
    }

    //directional light diffused and specular lighting calculations
    for (int i=0;i<4;i++) {
        vec3 unitLightDirection = normalize(directionalLightDirections[i]);
        float normalDot1 = dot(unitNormal, unitLightDirection);
        float brightness = max(normalDot1, 0.0);

        vec3 diffusedLight = (brightness * directionalLightColors[i]);
        totalDiffusedLighting = totalDiffusedLighting + diffusedLight;

        vec3 reflectedLightDirection = reflect(-unitLightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        float specularLight = max(specularFactor, 0.0);
        float dampedSpecularLight = pow(specularLight, shineDamper);
        vec3 finalSpecularLight = (dampedSpecularLight * reflectivity * directionalLightColors[i]);
        totalSpecularLighting = totalSpecularLighting + finalSpecularLight;
    }

    totalDiffusedLighting = max(totalDiffusedLighting, 0.2);
    totalSpecularLighting = max(totalSpecularLighting, 0.1);

    vec2 reflectTexCoords;
    reflectTexCoords.x = screenSpaceUpsideDown.x + totalDistortion.x;
    reflectTexCoords.y = screenSpaceUpsideDown.y + totalDistortion.y;

    vec2 refractTexCoords = screenSpace + totalDistortion;

    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, 0.001, 0.999);

    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    vec4 reflectColor = texture(reflectionTexture, screenSpaceUpsideDown);
    vec4 refractColor = texture(refractionTexture, refractTexCoords);

    vec4 fogAdjustedRefractTexture = vec4(applyFog(refractColor.rgb, distanceFromCamera, normalize(-toCameraVector), pass_cameraPosition, normalize(vec3(directionalLightDirections[0])) ), 1.0);
    vec4 fogAdjustedReflectTexture = vec4(applyFog(reflectColor.rgb, distanceFromCamera, normalize(-toCameraVector), pass_cameraPosition, normalize(vec3(directionalLightDirections[0])) ), 1.0);

    vec3 directionToCamera = normalize(toCameraVector);
    float refractiveFactor = dot(directionToCamera, normal);
    refractiveFactor = pow(refractiveFactor, 3);

    vec4 fogAdjustedWaterColor = vec4(applyFog(vec4(0.0, 0.3, 0.5, 1.0).rgb, distanceFromCamera, normalize(-toCameraVector), pass_cameraPosition, normalize(vec3(directionalLightDirections[0])) ), 1.0);
    vec4 fogAdjustedDeepWaterColor = vec4(applyFog(vec4(0.2, 0.4, 0.55, 1.0).rgb, distanceFromCamera, normalize(-toCameraVector), pass_cameraPosition, normalize(vec3(directionalLightDirections[0])) ), 1.0);

    vec4 waterTexture = mix(refractColor, reflectColor, refractiveFactor);
    vec4 lightedTexture = vec4(totalDiffusedLighting, 1.0) * mix(waterTexture, fogAdjustedWaterColor, 0.12) + vec4(totalSpecularLighting, 0.0);
    vec4 depthAdjustedColorTexture = mix(lightedTexture, fogAdjustedDeepWaterColor, clamp(waterDepth / 80.0, 0.0, 0.3));

    float alpha = clamp(waterDepth / 5, 0.2, 1.0);

    out_color = vec4(depthAdjustedColorTexture.rgb, alpha);
}