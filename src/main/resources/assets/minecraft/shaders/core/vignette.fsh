#version 150

uniform sampler2D Sampler0;
uniform vec2 ScreenSize;
uniform float VignetteStrength;
uniform vec3 Color;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec2 uv = vec2(texCoord0.x, 1.0 - texCoord0.y);
    vec4 sceneColor = texture(Sampler0, uv);

    vec2 center = vec2(0.5, 0.5);
//
//    // Нормализованная дистанция от центра (0 в центре, 1 в углах)
    float dist = distance(texCoord0, center) / 0.7071; // 0.7071 — макс. дистанция до угла
//
//    // Маска виньетки: 0 в центре, 1 в углах
    float vignetteMask = dist;
//
//    // Применяем силу виньетки
    vignetteMask = vignetteMask * VignetteStrength;
//
//    // Смешиваем с цветом виньетки
//    vec3 finalColor = mix(sceneColor.rgb, Color, vignetteMask);

    float r = mix(sceneColor.r, Color.r, vignetteMask);
    float g = mix(sceneColor.g, Color.g, vignetteMask);
    float b = mix(sceneColor.b, Color.b, vignetteMask);

    fragColor = vec4(r, g, b, 1);
}