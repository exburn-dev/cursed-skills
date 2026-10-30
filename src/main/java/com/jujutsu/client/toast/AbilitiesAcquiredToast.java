package com.jujutsu.client.toast;

import com.jujutsu.api.ability.core.AbilityType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class AbilitiesAcquiredToast implements Toast {
    public static final int TOAST_WIDTH = 150;
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("toast/recipe");
    private static final long DEFAULT_DURATION_MS = 5000L;

    private final List<AbilityType> abilities;
    private double notificationDisplayTimeMultiplier = 1.0f;
    private Visibility wantedVisiblity = Visibility.SHOW;

    public AbilitiesAcquiredToast(List<AbilityType> abilities) {
        this.abilities = abilities;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l) {
        int textX = 10;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width(), height(), width(), height());
        guiGraphics.drawString(font, Component.translatable("toast.jujutsu.abilities_acquired.title",
                abilities.size()), textX, 7, -11534256, false);

        MutableComponent description = Component.empty();
        for(int i = 0; i < abilities.size(); i++) {
            AbilityType ability = abilities.get(i);
            Component toAppend = i == 0 ? ability.getName().copy() : Component.literal(", ").append(ability.getName());
            if(font.width(description.copy().append(toAppend)) > 140) {
                description.append(Component.literal(" ..."));
                break;
            }
            description.append(toAppend);
        };

        guiGraphics.drawString(font, description, textX, 18, -16777216, false);
        //AbilityType type = this.abilities.get((int)((double)startTime / Math.max(1.0, 5000.0 * manager.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size()) % (double)this.recipes.size()));
        //ItemStack itemStack = recipeEntry.value().createIcon();
        Matrix3x2fStack matrices = guiGraphics.pose();
        matrices.pushMatrix();
        matrices.scale(0.6F, 0.6F);
        //context.drawItemWithoutEntity(itemStack, 3, 3);
        matrices.popMatrix();
        //context.drawItemWithoutEntity(recipeEntry.value().getResult(manager.getClient().world.getRegistryManager()), 8, 8);

        wantedVisiblity = (double)l >= DEFAULT_DURATION_MS * notificationDisplayTimeMultiplier ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int width() {
        return TOAST_WIDTH;
    }

    @Override
    public Visibility getWantedVisibility() {
        return wantedVisiblity;
    }

    @Override
    public void update(ToastManager toastManager, long l) {
        notificationDisplayTimeMultiplier = toastManager.getNotificationDisplayTimeMultiplier();
    }
}
