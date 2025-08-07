package com.jujutsu.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class TooltipRenderingEvents {
    public static final Event<RenderTooltipCallback> RENDER_TOOLTIP_EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.class,
            (listeners) -> (context, stack, x, y, width, height) -> {
                for(RenderTooltipCallback listener: listeners) {
                    ActionResult result = listener.interact(context, stack, x, y, width, height);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    @FunctionalInterface
    public interface RenderTooltipCallback {
        ActionResult interact(DrawContext context, ItemStack stack, int x, int y, int width, int height);
    }
}
