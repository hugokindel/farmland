#version 330 core

in vec2 textureRegion;
in vec4 colorTint;

out vec4 color;

uniform sampler2D textureSampler;
uniform float alpha;
uniform int type;

void main(void)
{
	vec4 tint = vec4(colorTint.x, colorTint.y, colorTint.z, alpha * colorTint.w);
	vec4 textureColor = texture(textureSampler, textureRegion);

	if (type == 0) {
		color = tint * textureColor;
	} else {
		color = tint * textureColor.rrrr;
	}
}