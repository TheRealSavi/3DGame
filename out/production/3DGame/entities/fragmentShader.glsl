#version 400 core

in vec2 pass_coords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_color;

uniform sampler2D sampler;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];

uniform float shineDamper;
uniform float reflectivity;

//uniform vec3 skyColor;

void main(void) {

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
    totalDiffusedLighting = max(totalDiffusedLighting, 0.2);

    vec4 textureColor = texture(sampler, pass_coords);
    if (textureColor.a < 0.5) {
        discard;
    }

    vec4 lightedTexture = vec4(totalDiffusedLighting, 1.0) * textureColor + vec4(totalSpecularLighting, 1.0);
    //out_color = mix(vec4(skyColor, 1.0), lightedTexture, visibility);
    out_color = lightedTexture;
}