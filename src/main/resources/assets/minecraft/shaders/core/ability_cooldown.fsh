#version 150

uniform sampler2D Sampler0;
uniform float Progress;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);

    if (texCoord0.y > Progress) {
        discard;
    }

    fragColor = color;
}
