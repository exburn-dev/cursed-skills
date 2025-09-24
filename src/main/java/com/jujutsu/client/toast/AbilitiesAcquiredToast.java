package com.jujutsu.client.toast;

import com.jujutsu.systems.ability.core.AbilityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class AbilitiesAcquiredToast implements Toast {
    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/recipe");
    private static final long DEFAULT_DURATION_MS = 5000L;

    private final List<AbilityType> abilities;

    public AbilitiesAcquiredToast(List<AbilityType> abilities) {
        this.abilities = abilities;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        TextRenderer textRenderer = manager.getClient().textRenderer;
        int textX = 10;
        context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, Text.translatable("toast.jujutsu.abilities_acquired.title", abilities.size()), textX, 7, -11534256, false);

        MutableText description = Text.empty();
        for(int i = 0; i < abilities.size(); i++) {
            AbilityType ability = abilities.get(i);
            MutableText toAppend = i == 0 ? ability.getName().copy() : Text.literal(", ").append(ability.getName());
            if(textRenderer.getWidth(description.copy().append(toAppend)) > 140) {
                description.append(Text.literal(" ..."));
                break;
            }
            description.append(toAppend);
        };

        context.drawText(textRenderer, description, textX, 18, -16777216, false);
        //AbilityType type = this.abilities.get((int)((double)startTime / Math.max(1.0, 5000.0 * manager.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size()) % (double)this.recipes.size()));
        //ItemStack itemStack = recipeEntry.value().createIcon();
        context.getMatrices().push();
        context.getMatrices().scale(0.6F, 0.6F, 1.0F);
        //context.drawItemWithoutEntity(itemStack, 3, 3);
        context.getMatrices().pop();
        //context.drawItemWithoutEntity(recipeEntry.value().getResult(manager.getClient().world.getRegistryManager()), 8, 8);
        return (double)startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getWidth() {
        return 150;
    }
}
