#version 400 core

in vec2 textureCoords;

out vec4 out_color;

uniform sampler2D player1texture;

void main(void){
    vec4 player1color = texture(player1texture, textureCoords);
    out_color = player1color;
}