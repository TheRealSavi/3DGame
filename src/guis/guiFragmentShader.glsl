#version 140

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;

void main(void){

    vec4 textureColor = texture(guiTexture, textureCoords);
    if (textureColor.a > 0.2) {
        textureColor.a = 0.5;
    }
    out_Color = textureColor;
}