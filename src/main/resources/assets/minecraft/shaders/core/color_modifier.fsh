#version 150

uniform sampler2D Sampler0;
uniform vec2 ScreenSize;
uniform float Strength;
uniform float BrightnessStrength;
uniform vec3 Color;

in vec2 texCoord0;
out vec4 fragColor;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec2 uv = vec2(texCoord0.x, 1.0 - texCoord0.y);
    vec4 sceneColor = texture(Sampler0, uv);

//    float r = mix(Color.r, sceneColor.r, Strength);
//    float g = mix(Color.g, sceneColor.g, Strength);
//    float b = mix(Color.b, sceneColor.b, Strength);

    float r = Color.r * sceneColor.r;
    float g = Color.g * sceneColor.g;
    float b = Color.b * sceneColor.b;

    r = mix(sceneColor.r, r, Strength);
    g = mix(sceneColor.g, g, Strength);
    b = mix(sceneColor.b, b, Strength);

//    vec3 hsv = rgb2hsv(vec3(r, g, b));
//    hsv.z += 0.1;
//    vec3 finalColor = hsv2rgb(hsv);

    float brightness = 1 + 2.2 * BrightnessStrength;

    fragColor = vec4(r * brightness, g * brightness, b * brightness, 1);
}