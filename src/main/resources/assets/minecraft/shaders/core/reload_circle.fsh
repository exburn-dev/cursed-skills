#version 150

uniform sampler2D Sampler0;
uniform float Progress;
uniform vec2 Resolution;
uniform vec3 Color;
uniform float InnerRadius;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a == 0.0) {
        discard;
    }

    vec2 center = Resolution * 0.5;
    vec2 p = texCoord0 * Resolution;
    vec2 d = p - center;
    float dist = length(d);

    float angle = atan(-d.y, d.x);
    if (angle < 0.0) angle += 6.2831853;

    angle = angle + 4.71238898;
    if (angle >= 6.2831853) angle -= 6.2831853;

    float filled = Progress * 6.2831853;

    float r = dist / (Resolution.x * 0.5);

    if (r < InnerRadius || r > 1.0 || angle > filled) {
        discard;
    }

    vec3 mixedColor = texColor.rgb * Color;
    fragColor = vec4(mixedColor, texColor.a);
}