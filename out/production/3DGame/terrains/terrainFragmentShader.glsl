#version 400 core

in vec2 pass_coords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform float shineDamper;
uniform float reflectivity;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];

//uniform vec3 skyColor;

void main(void) {

    vec2 tiledCoords = pass_coords * 40.0;
    vec4 blendMapColor = texture(blendMap, pass_coords);
    float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
    vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
    vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;
    vec4 finalTextureColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffusedLighting = vec3(0.0);
    vec3 totalSpecularLighting = vec3(0.0);

    for (int i=0;i<4;i++) {
        float distanceToLight = length(toLightVector[i]);
        float attenuationFactor = attenuation[i].x + (attenuation[i].y * distanceToLight) + (attenuation[i].z * distanceToLight * distanceToLight);

        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal, unitLightVector);
        float brightness = max(nDot1, 0.0);
        vec3 diffusedLight = (brightness * lightColor[i]) / attenuationFactor;
        totalDiffusedLighting = totalDiffusedLighting + diffusedLight;

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        float specularLight = max(specularFactor, 0.0);
        float dampedSpecularLight = pow(specularLight, shineDamper);
        vec3 finalSpecularLight = (dampedSpecularLight * reflectivity * lightColor[i]) / attenuationFactor;
        totalSpecularLighting = totalSpecularLighting + finalSpecularLight;
    }
    totalDiffusedLighting = max(totalDiffusedLighting, 0.3);

    vec4 lightedTextureColor = vec4(totalDiffusedLighting, 1.0) * finalTextureColor + vec4(totalSpecularLighting, 1.0);
    //out_color = mix(vec4(skyColor, 1.0), lightedTextureColor, visibility);
    out_color = lightedTextureColor;
}