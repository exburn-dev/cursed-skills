package com.jujutsu.client.feature;

import com.jujutsu.mixin.QuadrupedEntityModelAccessor;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class PigHeadFeatureRenderer<T extends Entity, M extends PigEntityModel<T>> extends FeatureRenderer<T, M> {
    private final EntityModelLoader loader;
    private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    public PigHeadFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.loader = loader;
        this.headModels = SkullBlockEntityRenderer.getModels(loader);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        LivingEntity livingEntity = (LivingEntity) entity;
        if(livingEntity.getCustomName() == null || !Objects.equals(livingEntity.getCustomName().getString(), "ALLAH")) return;

        ItemStack headStack = new ItemStack(Items.PIGLIN_HEAD);

        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();

        ModelPart head =  ((QuadrupedEntityModelAccessor) ((QuadrupedEntityModel<T>) this.getContextModel()) ).getHead();

        matrices.push();
        head.rotate(matrices);

        matrices.translate(0.0F, 0.26F, -0.3F);
        matrices.scale(1.1F, 1.1F, 1.1F);

        float scale = Math.abs((float) Math.sin(livingEntity.age / 40f)) + 1;
        matrices.scale(scale, scale, scale);

        float hue = (entity.age + tickDelta) * 0.01F % 1.0F;
        int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);

        SkullBlockEntityModel model = this.headModels.get(SkullBlock.Type.PIGLIN);

        RenderLayer layer = RenderLayer.getEntityTranslucent(Identifier.ofVanilla("textures/entity/piglin/piglin.png"));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);

        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, rgb);
        matrices.pop();
    }
}
