package com.jujutsu.entity.renderer;

import com.jujutsu.entity.ReversalRedEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class ReversalRedEntityRenderer extends EntityRenderer<ReversalRedEntity> {
    public ReversalRedEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(ReversalRedEntity entity) {
        return null;
    }
}
