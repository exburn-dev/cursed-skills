package com.jujutsu.client.particle;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.ShaderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class ColoredSparkParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final ColoredSparkParticleEffect parameters;

    private float oldScale;
    private float currentScale;

    protected ColoredSparkParticle(ClientWorld clientWorld, double d, double e, double f, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, ColoredSparkParticleEffect parameters) {
        super(clientWorld, d, e, f);

        this.spriteProvider = spriteProvider;
        this.parameters = parameters;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        setSpriteForAge(spriteProvider);
        scale(parameters.getStartScale());
        Vector3f startColor = parameters.getColor().startColor();
        setColor(startColor.x, startColor.y, startColor.z);
        setAlpha(1f);
        setMaxAge(parameters.getLifetime());
        angle = parameters.getRoll().startRoll();
        oldScale = scale;
        currentScale = scale;
        Jujutsu.LOGGER.info("Type: {}", getType().hashCode());
    }

    @Override
    public void tick() {
        Vector3f color = getCurrentColor();
        setColor(color.x, color.y, color.z);

        oldScale = scale;
        currentScale *= parameters.getScaleModifier();

        prevAngle = angle;
        angle += parameters.getRoll().roll();

        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;

        move(velocityX, velocityY, velocityZ);

        super.tick();
    }

    public Vector3f getCurrentColor() {
        Vector3f startColor = parameters.getColor().startColor();
        Vector3f endColor = parameters.getColor().endColor();

        if(startColor.equals(endColor)) return startColor;
        if (maxAge <= 0) return new Vector3f(startColor);

        float t = Math.min(1.0f, (float) age / maxAge);
        return new Vector3f(
                MathHelper.lerp(t, startColor.x, endColor.x),
                MathHelper.lerp(t, startColor.y, endColor.y),
                MathHelper.lerp(t, startColor.z, endColor.z)
        );
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        //this.scale = MathHelper.lerp(tickDelta, oldScale, currentScale);

        //RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR); -- not bad
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        //12311

        super.buildGeometry(vertexConsumer, camera, tickDelta);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<ColoredSparkParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(FabricSpriteProvider fabricSpriteProvider) {
            this.spriteProvider = fabricSpriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(ColoredSparkParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            Vector3f velocity = parameters.getVelocity();
            return new ColoredSparkParticle(world, x, y, z, velocity.x, velocity.y, velocity.z, spriteProvider, parameters);
        }
    }
}
