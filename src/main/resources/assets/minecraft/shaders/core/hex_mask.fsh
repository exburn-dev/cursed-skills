#version 150

uniform sampler2D Sampler0;
uniform float u_Hover;
uniform float u_SoftPx;
uniform vec2  u_QuadSize;
uniform vec4  u_Color;

in vec2 texCoord0;

out vec4 fragColor;

float sdHex(vec2 p, float r) {
    p = abs(p);
    return max(p.x * 0.8660254 + p.y * 0.5, p.y) - r;
}

float smooth1(float x){ return smoothstep(0.0, 1.0, clamp(x, 0.0, 1.0)); }

void main() {
    vec4 vColor = u_Color;

    vec4 base = texture(Sampler0, texCoord0) * vColor;

    float aspect = u_QuadSize.x / max(u_QuadSize.y, 1.0);
    vec2 p = (texCoord0 - vec2(0.5)) * 2.0;
    p.x *= aspect;

    float r = 0.9;

    float d = sdHex(p, r);

    float minDim = max(min(u_QuadSize.x, u_QuadSize.y), 1.0);
    float soft = u_SoftPx / minDim;

    float inside = 1.0 - smoothstep(0.0, soft, d);

    float glow = smooth1(u_Hover);
    vec3 lit  = mix(base.rgb, vec3(1.0), glow);

    float edge = 1.0 - smoothstep(-soft, soft, d);
    float rim  = edge * glow * 0.25;
    lit = mix(lit, vec3(1.0), rim);

    float alpha = base.a * inside;
    if (alpha <= 0.0) discard;

    fragColor = vec4(lit, alpha);
}