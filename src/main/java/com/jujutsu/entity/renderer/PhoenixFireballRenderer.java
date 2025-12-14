package com.jujutsu.entity.renderer;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.entity.PhoenixFireballEntity;
import com.jujutsu.entity.model.PhoenixFireballModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PhoenixFireballRenderer extends EntityRenderer<PhoenixFireballEntity> {
    private static final Identifier TEXTURE = Jujutsu.id("textures/entity/phoenix_fireball.png");

    private final PhoenixFireballModel model;

    public PhoenixFireballRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new PhoenixFireballModel(ctx.getPart(PhoenixFireballModel.MODEL_LAYER));
    }

    @Override
    public void render(PhoenixFireballEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        MinecraftClient client = MinecraftClient.getInstance();

        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entity.getYaw(tickDelta)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-entity.getPitch(tickDelta)));

        matrices.translate(0, -2, 0);
        matrices.scale(2, 2, 2);

        model.render(matrices, vertexConsumer, 0xF000F0, OverlayTexture.DEFAULT_UV);

        matrices.pop();

        renderFire(matrices, vertexConsumers, entity, MathHelper.rotateAround(MathHelper.Y_AXIS, client.gameRenderer.getCamera().getRotation(), new Quaternionf()));

        Vector3f startColor = new Vector3f(1, 0, 0);
        Vector3f endColor = new Vector3f(1, 0.651f, 0);

        float t = entity.getRandom().nextFloat();
        Vector3f color = new Vector3f(
                MathHelper.lerp(t, startColor.x, endColor.x),
                MathHelper.lerp(t, startColor.y, endColor.y),
                MathHelper.lerp(t, startColor.z, endColor.z)
        );

        ColoredSparkParticleEffect particle = new ColoredSparkParticleEffect(2, 0.95f,
                new ColoredSparkParticleEffect.ColorTransition(color, color), 0, 0.1f, 40);

        Vec3d vec = entity.getRotationVector().normalize().multiply(-1 * entity.getRandom().nextDouble() * 0.15f);
        particle.setVelocity(vec.toVector3f());

        entity.getWorld().addParticle(particle, entity.getX() + entity.getRandom().nextDouble(), entity.getY() + entity.getRandom().nextDouble(), entity.getZ() + entity.getRandom().nextDouble(), 0, 0, 0);
    }

    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Quaternionf rotation) {
        Sprite sprite = ModelLoader.FIRE_0.getSprite();
        Sprite sprite2 = ModelLoader.FIRE_1.getSprite();
        matrices.push();
        float f = entity.getWidth() * 1.4F;
        matrices.scale(f, f, f);
        float g = 0.5F;
        float h = 0.0F;
        float i = entity.getHeight() / f;
        float j = 0.0F;
        matrices.multiply(rotation);
        matrices.translate(0.0F, 0.0F, 0.3F - (float)((int)i) * 0.02F);
        float k = 0.0F;
        int l = 0;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());

        for(MatrixStack.Entry entry = matrices.peek(); i > 0.0F; ++l) {
            Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
            float m = sprite3.getMinU();
            float n = sprite3.getMinV();
            float o = sprite3.getMaxU();
            float p = sprite3.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }

            drawFireVertex(entry, vertexConsumer, -g - 0.0F, 0.0F - j, k, o, p);
            drawFireVertex(entry, vertexConsumer, g - 0.0F, 0.0F - j, k, m, p);
            drawFireVertex(entry, vertexConsumer, g - 0.0F, 1.4F - j, k, m, n);
            drawFireVertex(entry, vertexConsumer, -g - 0.0F, 1.4F - j, k, o, n);
            i -= 0.45F;
            j -= 0.45F;
            g *= 0.9F;
            k -= 0.03F;
        }

        matrices.pop();
    }

    private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(entry, x, y, z).color(-1).texture(u, v).overlay(0, 10).light(240).normal(entry, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public Identifier getTexture(PhoenixFireballEntity entity) {
        return TEXTURE;
    }
}
