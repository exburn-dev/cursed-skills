package com.jujutsu.client.hud;

import com.jujutsu.registry.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.function.Supplier;

public class ShaderUtils {
    public static ShaderProgram testShader;
    public static ShaderProgram shader;
    public static ShaderProgram stunShader;
    public static ShaderProgram vignetteShader;

    private static Framebuffer effectFramebuffer;

    public static void init(ResourceFactory factory) throws IOException {
        MinecraftClient client = MinecraftClient.getInstance();
        testShader = new ShaderProgram(factory, "test", VertexFormats.POSITION_TEXTURE);
        shader = new ShaderProgram(factory, "reload_circle", VertexFormats.POSITION_TEXTURE);

        stunShader = new ShaderProgram(factory, "stun_screen_effect", VertexFormats.POSITION_TEXTURE);
        vignetteShader = new ShaderProgram(factory, "vignette", VertexFormats.POSITION_TEXTURE);
        effectFramebuffer = new SimpleFramebuffer(client.getFramebuffer().textureWidth, client.getFramebuffer().textureHeight, true, MinecraftClient.IS_SYSTEM_MAC);
    }

    public static void renderReloadCircle(DrawContext context, MatrixStack ms, float x, float y, float size, float progress, float innerRadius, Identifier texture, Vector3f color) {
        Supplier<ShaderProgram> shaderSup = () -> shader;

        RenderSystem.setShaderTexture(0, texture);

        if(shader.getUniform("Progress") != null && shader.getUniform("Resolution") != null && shader.getUniform("Color") != null && shader.getUniform("InnerRadius") != null) {
            shader.getUniform("Progress").set(progress);
            shader.getUniform("Resolution").set(new float[]{ size, size });
            shader.getUniform("Color").set(new float[]{ color.x, color.y, color.z });
            shader.getUniform("InnerRadius").set(innerRadius);
        }

        shader.addSampler("Sampler0", texture);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE);

        Matrix4f mat = ms.peek().getPositionMatrix();

        buf.vertex(mat, x, y, 0).texture(0, 0);
        buf.vertex(mat, x, y + size, 0).texture(0, 1);
        buf.vertex(mat, x + size, y, 0).texture(1, 0);
        buf.vertex(mat, x + size, y + size, 0).texture(1, 1);

        RenderSystem.setShader(shaderSup);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void renderStunEffect(float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || stunShader == null) return;

        if (!client.player.hasStatusEffect(ModEffects.STUN)) return;

        StatusEffectInstance effectInstance = client.player.getStatusEffect(ModEffects.STUN);

        RenderSystem.assertOnRenderThread();
        effectFramebuffer.beginWrite(true); // Перенаправляем вывод в текстуру
        client.getFramebuffer().draw(client.getFramebuffer().textureWidth, client.getFramebuffer().textureHeight, false);

        client.getFramebuffer().beginWrite(true); // Вернёмся обратно в обычный экран


        float time = (client.world.getTime() + tickDelta) / 20.0f;
        Window window = client.getWindow();
        int w = window.getScaledWidth();
        int h = window.getScaledHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, effectFramebuffer.getColorAttachment());
        RenderSystem.setShader(() -> stunShader);
        //stunShader.addSampler("Sampler0", texture);

        stunShader.getUniform("Time").set(time);
//        stunShader.getUniform("CurrentTime").set((float) effectInstance.getDuration());
//        stunShader.getUniform("EndTime").set(0f);
        //stunShader.getUniform("Resolution").set(new float[]{window.getScaledWidth(), window.getScaledHeight()});

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(0, 0, 0).texture(0, 0);
        buffer.vertex(0, h, 0).texture(0, 1);
        buffer.vertex(w, h, 0).texture(1, 1);
        buffer.vertex(w, 0, 0).texture(1, 0);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static void renderVignette(float strength, float r, float g, float b) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || stunShader == null) return;

        RenderSystem.assertOnRenderThread();
        effectFramebuffer.beginWrite(true); // Перенаправляем вывод в текстуру
        client.getFramebuffer().draw(client.getFramebuffer().textureWidth, client.getFramebuffer().textureHeight, false);

        client.getFramebuffer().beginWrite(true); // Вернёмся обратно в обычный экран

        Window window = client.getWindow();
        int w = window.getScaledWidth();
        int h = window.getScaledHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, effectFramebuffer.getColorAttachment());
        RenderSystem.setShader(() -> vignetteShader);

        vignetteShader.getUniform("VignetteStrength").set(strength);
        vignetteShader.getUniform("Color").set(new float[]{r, g, b});

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(0, 0, 0).texture(0, 0);
        buffer.vertex(0, h, 0).texture(0, 1);
        buffer.vertex(w, h, 0).texture(1, 1);
        buffer.vertex(w, 0, 0).texture(1, 0);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static void reload(ResourceManager manager) throws IOException {
        if (stunShader != null) {
            stunShader.close();
            stunShader = null;
        }
        if (vignetteShader != null) {
            vignetteShader.close();
            vignetteShader = null;
        }
        init(manager);
    }
}