package com.jujutsu.entity.renderer;

import com.jujutsu.entity.LapseBlueEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class LapseBlueEntityRenderer extends EntityRenderer<LapseBlueEntity> {
    public LapseBlueEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(LapseBlueEntity entity) {
        return null;
    }
}
