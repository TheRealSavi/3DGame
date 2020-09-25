#version 400 core

in vec2 textureCoords;
in float isUnderWater;

out vec4 out_color;

uniform sampler2D colorTexture;
uniform sampler2D distortionMap;

uniform float moveFactor;

const float waveStrength = 0.008;

void main(void){

    vec2 distortion1 = (texture(distortionMap, vec2(textureCoords.x + moveFactor / 2, textureCoords.y + moveFactor / 2)).rb * 2.0 - 1.0) * waveStrength;
    vec2 totalDistortion = (distortion1);

    out_color = texture(colorTexture, textureCoords + (totalDistortion * isUnderWater));

    out_color.rgb = mix(out_color.rgb, vec3(0.06, 0.38, 0.55), isUnderWater * 0.7);
}