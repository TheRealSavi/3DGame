#version 400 core

out vec4 out_Color;

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;

in vec3 toLightVector[4];

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D distortionMap;

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

    vec2 distortion1 = (texture(distortionMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rb * 2.0 - 1.0) * waveStrength;
    vec2 distortion2 = (texture(distortionMap, vec2(-0.5 * textureCoords.x + moveFactor, textureCoords.y + moveFactor)).rb * 2.0 - 1.0) * waveStrength;
    vec2 totalDistortion = distortion1 + distortion2;

    vec3 normal = normalize(vec3(totalDistortion.x, totalDistortion.y, distortion1.x));

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
        totalSpecularLighting = totalSpecularLighting + finalSpecularLight;
    }
    totalDiffusedLighting = max(totalDiffusedLighting, 0.2);

    vec2 reflectTexCoords;
    reflectTexCoords.x = screenSpaceUpsideDown.x + totalDistortion.x;
    reflectTexCoords.y = screenSpaceUpsideDown.y + totalDistortion.y;

    vec2 refractTexCoords = screenSpace + totalDistortion;

    vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
    vec4 refractColor = texture(refractionTexture, refractTexCoords);

    vec3 directionToCamera = normalize(toCameraVector);
    float refractiveFactor = dot(directionToCamera, vec3(0.0, 1.0, 0.0));
    refractiveFactor = pow(refractiveFactor, 2);

    out_Color = mix(reflectColor, refractColor, refractiveFactor);
    out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), 0.1) + vec4(totalSpecularLighting, 0.0);;

}