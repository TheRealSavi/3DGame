#version 400 core

in vec2 textureCoords;

out vec4 out_color;

uniform sampler2D player1texture;
uniform sampler2D player2texture;

void main(void){

    vec4 player1color = texture(player1texture, textureCoords);
    vec4 player2color = texture(player2texture, textureCoords);
    out_color = player1color * player2color;
}