#version 330 core

// Values in position:
// x,y = position in world coordinates.
// z,w = UV.
layout (location = 0) in vec4 position;
layout (location = 1) in vec4 _colorTint;

out vec2 textureRegion;
out vec4 colorTint;

uniform mat4 projection;

void main(void)
{
    gl_Position = projection * vec4(position.x, position.y, 0.0f, 1.0f);
    textureRegion = vec2(position.z, position.w);
    colorTint = _colorTint;
}