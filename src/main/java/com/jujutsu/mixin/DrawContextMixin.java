package com.jujutsu.mixin;

import com.jujutsu.Jujutsu;
import com.jujutsu.event.client.TooltipRenderingEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw(Ljava/lang/Runnable;)V"))
    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci, @Local(ordinal = 2) int width, @Local(ordinal = 3) int height, @Local(ordinal = 6) int tooltipX, @Local(ordinal = 7) int tooltipY) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client != null && client.currentScreen instanceof HandledScreen<?> handledScreen) {
            Slot focusedSlot = ((HandledScreenAccessor) handledScreen).getFocusedSlot();
            if(focusedSlot == null || focusedSlot.getStack().isEmpty()) return;

            ItemStack hoveredStack = focusedSlot.getStack();
            DrawContext context = (DrawContext) (Object) this;

            TooltipRenderingEvents.RENDER_TOOLTIP_EVENT.invoker().interact(context, hoveredStack, tooltipX - 3, tooltipY - 3, width + 3 + 3, height + 3 + 3);
        }
    }
}
