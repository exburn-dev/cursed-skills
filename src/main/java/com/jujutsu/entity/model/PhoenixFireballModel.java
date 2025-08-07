package com.jujutsu.entity.model;

import com.jujutsu.entity.PhoenixFireballEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class PhoenixFireballModel extends EntityModel<PhoenixFireballEntity> {
	private final ModelPart main;
	private final ModelPart glow;
	public PhoenixFireballModel(ModelPart root) {
		this.main = root.getChild("main");
		this.glow = this.main.getChild("glow");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 14).cuboid(-2.5F, -3.5F, -3.5F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 21.0F, 0.5F));

		ModelPartData glow = main.addChild("glow", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -4.0F, -4.0F, 7.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(PhoenixFireballEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		main.render(matrices, vertices, light, overlay, color);
	}
}