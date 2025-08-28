#version 150

uniform sampler2D Sampler0;
uniform float u_Hover;
uniform vec4  u_Color;

in vec2 texCoord0;

out vec4 fragColor;

float smooth1(float x) {
    return smoothstep(0.0, 1.0, clamp(x, 0.0, 1.0));
}

void main() {
    vec4 base = texture(Sampler0, texCoord0) * u_Color;

    float glow = smooth1(u_Hover);

    vec3 lit = mix(base.rgb, vec3(1.0), glow);

    fragColor = vec4(lit, base.a);
}