package com.jujutsu.screen;

import com.jujutsu.systems.ability.upgrade.AbilityUpgrade;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeBranch;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class AbilityUpgradesScreen extends Screen {
    private final List<AbilityUpgradeBranch> branches;

    private double dragOffsetX = 0;
    private double dragOffsetY = 0;
    private int zoomState = 1;

    private final int widgetWidth = 16;
    private final int widgetHeight = 16;
    private final int horizontalGap = 2;
    private final int verticalGap = 8;

    public AbilityUpgradesScreen(List<AbilityUpgradeBranch> branches) {
        super(Text.literal("upgrades"));

        this.branches = branches;
    }

    @Override
    protected void init() {
        super.init();

        int count = branches.size();

        int startX = this.width / 2 - (widgetWidth * count + horizontalGap * count) / 2;
        int startY = this.height / 2 - (widgetHeight * count + verticalGap * count) / 2;

        for(int i = 0; i < branches.size(); i++) {
            AbilityUpgradeBranch branch = branches.get(i);
            int y = startY + widgetHeight * i + verticalGap * i;

            for(int j = 0; j < branch.upgrades().size(); j++) {
                AbilityUpgrade upgrade = branch.upgrades().get(j);
                int x = startX + widgetWidth * j + horizontalGap * j;

                this.addDrawableChild(new AbilityUpgradeButton(upgrade, x, y, widgetWidth, widgetHeight, Text.literal("")));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();

        //float centerX = (float) (this.width / 2f + dragOffsetX);
        //float centerY = (float) (this.height / 2f + dragOffsetY);

        renderBackground(context, mouseX, mouseY, delta);

        float centerX = this.width / 2f;
        float centerY = this.height / 2f;

        matrices.push();

        matrices.translate(dragOffsetX, dragOffsetY, 0);

        matrices.translate(centerX, centerY, 0);
        matrices.scale(getScaleFactor(), getScaleFactor(), 1f);
        matrices.translate(-centerX, -centerY, 0);

        for(Element child: children()) {
            if(child instanceof Drawable drawable) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        }

        matrices.pop();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(button == 0) {
            dragOffsetX += deltaX;
            dragOffsetY += deltaY;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(verticalAmount > 0 && zoomState < 2) {
            zoomState += 1;
        }
        else if(verticalAmount < 0 && zoomState > 0) {
            zoomState -= 1;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private float getScaleFactor() {
        return switch (zoomState) {
            case 0 -> 0.75f;
            case 2 -> 1.25f;
            default -> 1f;
        };
    }

    private class AbilityUpgradeButton extends ClickableWidget {
        private final AbilityUpgrade upgrade;

        public AbilityUpgradeButton(AbilityUpgrade upgrade, int x, int y, int width, int height, Text message) {
            super(x, y, width, height, message);
            this.upgrade = upgrade;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFFFFF);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {

            super.onClick(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.active && this.visible && isMouseOver(mouseX, mouseY) && button == 0) {
                client.player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);
                return true;
            }
            return false;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            float scale = getScaleFactor();

            float centerX = this.width / 2f;
            float centerY = this.height / 2f;

            double adjX = (mouseX - centerX) * scale + centerX - dragOffsetX;
            double adjY = (mouseY - centerY) * scale + centerY - dragOffsetY;

            return this.active && this.visible &&
                    adjX >= this.getX() &&
                    adjY >= this.getY() &&
                    adjX < this.getX() + this.width &&
                    adjY < this.getY() + this.height;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
