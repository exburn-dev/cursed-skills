#version 150

in vec2 Position;
in vec2 UV0;

out vec2 texCoord0;

void main() {
    texCoord = UV0;
    gl_Position = vec4(Position, 0.0, 1.0);
}