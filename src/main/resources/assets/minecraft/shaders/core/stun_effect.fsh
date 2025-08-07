#version 150

uniform sampler2D Sampler0;
uniform vec2 Resolution;
uniform float Time;
uniform float CurrentTime;
uniform float EndTime;

in vec2 texCoord0;
out vec4 fragColor;

const float ABERRATION_STRENGTH = 0.008;
const float BLUR_STRENGTH = 0.6;
const float NOISE_STRENGTH = 0.001;
const float FADE_IN_DURATION = 0.5;   // секунды
const float FADE_OUT_DURATION = 1.5;  // секунды

// Случайный шум (улучшенный)
float rand(vec2 uv) {
    return fract(sin(dot(uv.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float noise(vec2 uv) {
    vec2 i = floor(uv);
    vec2 f = fract(uv);
    f = f * f * (3.0 - 2.0 * f); // плавное интерполирование
    float a = rand(i);
    float b = rand(i + vec2(1.0, 0.0));
    float c = rand(i + vec2(0.0, 1.0));
    float d = rand(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// Вычисление силы эффекта (fade in/out)
float computeFade() {
    return 1;
}

void main() {
    vec2 uv = vec2(texCoord0.x, 1.0 - texCoord0.y);

    float strength = computeFade();

    // Хроматическая аберрация
    float offsetAmt = ABERRATION_STRENGTH + 0.01 * sin(Time * 2.0) + 0.01;
    vec2 offset = vec2(offsetAmt, 0.0);

    float r = texture(Sampler0, uv + offset).r;
    float g = texture(Sampler0, uv).g;
    float b = texture(Sampler0, uv - offset).b;
    vec3 color = vec3(r, g, b);

    // Горизонтальное размытие
    float blurSize = 1.0 / Resolution.x * 1.5;
    vec3 blur = vec3(0.0);
    blur += texture(Sampler0, uv - vec2(blurSize, 0.0)).rgb * 0.25;
    blur += texture(Sampler0, uv).rgb * 0.5;
    blur += texture(Sampler0, uv + vec2(blurSize, 0.0)).rgb * 0.25;

    color = mix(color, blur, BLUR_STRENGTH * strength);

    // Шум
    float n = (noise(uv * Resolution * 0.5 + Time * 5.0) - 0.5) * 2.0;
    color += n * NOISE_STRENGTH * strength;

    fragColor = vec4(color, 1.0);
}