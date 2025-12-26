#version 150

uniform sampler2D Sampler0;
uniform vec2 ScreenSize;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec4 tex = texture(Sampler0, texCoord0);
    float glowStrength = 1;

    float dist = distance(texCoord0, vec2(0.5));
    float softEdge = smoothstep(0.5, 0.2, dist);

    vec3 glow = tex.rgb * glowStrength;

    fragColor = vec4(1, 1, 1, 1);
}