#version 400 core

in vec2 textureCoords;

out vec4 out_color;

uniform sampler2D colorTexture;

const float contrast = 0.3;

void main(void){

    out_color = texture(colorTexture, textureCoords);
    out_color.rgb = (out_color.rgb - 0.5) * (1.0 + contrast) + 0.5;
}