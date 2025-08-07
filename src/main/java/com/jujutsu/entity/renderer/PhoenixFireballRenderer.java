package com.jujutsu.entity.renderer;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.entity.PhoenixFireballEntity;
import com.jujutsu.util.ParticleUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class PhoenixFireballRenderer extends EntityRenderer<PhoenixFireballEntity> {
    private static final Identifier TEXTURE = Jujutsu.getId("textures/gui/square.png");

    public PhoenixFireballRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(PhoenixFireballEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        if(entity.age < 1) return;

        matrices.push();

        Supplier<ParticleEffect> particle = () -> new ColoredSparkParticleEffect(2, 0.95f,
                new ColoredSparkParticleEffect.ColorTransition(new Vector3f(1, 0, 0), new Vector3f(1, 0, 0)), 0, 0.1f, 2);
        ParticleUtils.createBall(particle, entity.getPos(), entity.getWorld(), 60, 0.25f, 0);

        matrices.pop();
    }

    @Override
    public Identifier getTexture(PhoenixFireballEntity entity) {
        return TEXTURE;
    }
}
