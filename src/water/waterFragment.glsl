#version 400 core

out vec4 out_Color;

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;

in vec3 toLightVector[4];

uniform float cameraNearPlane;
uniform float cameraFarPlane;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D distortionMap;
uniform sampler2D depthMap;

uniform float moveFactor;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];

uniform float shineDamper;
uniform float reflectivity;

const float waveStrength = 0.009;

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

    vec3 totalDiffusedLighting = vec3(0.0);
    vec3 totalSpecularLighting = vec3(0.0);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    for (int i=0;i<4;i++) {
        float distanceToLight = length(toLightVector[i]);
        float attenuationFactor = attenuation[i].x + (attenuation[i].y * distanceToLight) + (attenuation[i].z * distanceToLight * distanceToLight);

        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(normal, unitLightVector);
        float brightness = max(nDot1, 0.0);
        vec3 diffusedLight = (brightness * lightColor[i]) / attenuationFactor;
        totalDiffusedLighting = totalDiffusedLighting + diffusedLight;

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, normal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        float specularLight = max(specularFactor, 0.0);
        float dampedSpecularLight = pow(specularLight, shineDamper);
        vec3 finalSpecularLight = (dampedSpecularLight * reflectivity * lightColor[i]) / attenuationFactor;
        totalSpecularLighting = (totalSpecularLighting + finalSpecularLight) * clamp(waterDepth / 10.0, 0.0, 1.0);
    }
    totalDiffusedLighting = max(totalDiffusedLighting, 0.2);

    vec2 reflectTexCoords;
    reflectTexCoords.x = screenSpaceUpsideDown.x + totalDistortion.x;
    reflectTexCoords.y = screenSpaceUpsideDown.y + totalDistortion.y;

    vec2 refractTexCoords = screenSpace + totalDistortion;

    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, 0.001, 0.999);

    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    vec4 reflectColor = texture(reflectionTexture, screenSpaceUpsideDown);
    vec4 refractColor = texture(refractionTexture, refractTexCoords);

    vec3 directionToCamera = normalize(toCameraVector);
    float refractiveFactor = dot(directionToCamera, normal);
    refractiveFactor = refractiveFactor * (refractiveFactor / 2);

    out_Color = mix(reflectColor, refractColor, refractiveFactor);
    out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.12) + vec4(totalSpecularLighting, 0.0);
    out_Color.a = clamp(waterDepth / 5, 0.2, 1.0);
    out_Color = mix(out_Color, vec4(0.2, 0.4, 0.55, 1.0), clamp(waterDepth / 80.0, 0.0, 0.3));

}