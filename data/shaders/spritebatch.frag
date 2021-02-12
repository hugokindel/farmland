#version 330 core

in vec2 textureRegion;
in vec4 colorTint;

out vec4 color;

uniform sampler2D textureSampler;
uniform float alpha;

void main(void)
{
    color = vec4(colorTint.x, colorTint.y, colorTint.z, alpha * colorTint.w) *
    	texture(textureSampler, textureRegion);
};