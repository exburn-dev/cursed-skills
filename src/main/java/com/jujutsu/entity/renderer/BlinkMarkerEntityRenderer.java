package com.jujutsu.entity.renderer;

import com.jujutsu.Jujutsu;
import com.jujutsu.entity.BlinkMarkerEntity;
import com.jujutsu.entity.model.BlinkMarkerModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class BlinkMarkerEntityRenderer extends EntityRenderer<BlinkMarkerEntity> {
    public static final Identifier TEXTURE = Jujutsu.id("textures/entity/blink_marker.png");

    private final BlinkMarkerModel model;

    public BlinkMarkerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new BlinkMarkerModel(ctx.getPart(BlinkMarkerModel.MODEL_LAYER));
    }

    @Override
    public void render(BlinkMarkerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(entity.getOwnerUUID().isPresent() && client.player.getUuid().equals(entity.getOwnerUUID().get())) {

        }
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrices.translate(0, -1.5, 0);
        this.model.render(matrices, vertexConsumer, 255, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(BlinkMarkerEntity entity) {
        return TEXTURE;
    }
}
