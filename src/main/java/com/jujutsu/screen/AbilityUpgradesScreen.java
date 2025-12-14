package com.jujutsu.screen;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.hud.ShaderUtils;
import com.jujutsu.network.payload.AbilityUpgradePurchasedPayload;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.systems.ability.upgrade.AbilityUpgrade;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeBranch;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class AbilityUpgradesScreen extends Screen {
    private final List<AbilityUpgradeBranch> branches;
    private int playerLastPurchasedBranch = 0;

    private UpgradesData upgradesData;

    private double dragX = 0;
    private double dragY = 0;
    private double scale = 1.0;

    private final List<AbilityUpgradeButton> buttons = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();

    public AbilityUpgradesScreen(List<AbilityUpgradeBranch> branches, UpgradesData upgradesData) {
        super(Text.literal(""));
        this.branches = branches;
        this.upgradesData = upgradesData;
        this.playerLastPurchasedBranch = AbilityUpgradeBranch.findPlayerLastPurchasedBranchIndex(branches, upgradesData);
        Jujutsu.LOGGER.info("Branches: {}\nUpgradesData: {}\nLastPurchasedBranch: {}", branches, upgradesData, playerLastPurchasedBranch);
    }

    @Override
    protected void init() {
        super.init();

        int widgetWidth = 32;
        int widgetHeight = 32;
        int horizontalGap = 24;
        int verticalGap = 24;

        int startX = width / 2 - widgetWidth - horizontalGap / 2;
        int startY = height - 100;

        for(int i = 0; i < branches.size(); i++) {
            AbilityUpgradeBranch branch = branches.get(i);
            int y = startY - widgetHeight * i - verticalGap * i;

            for(int j = 0; j < Math.min(branch.upgrades().size(), 2); j++) {
                AbilityUpgrade upgrade = branch.upgrades().get(j);
                int x = startX + widgetWidth * j + horizontalGap * j;
                boolean onlyOneUpgrade = branch.upgrades().size() == 1;
                x += onlyOneUpgrade ? widgetWidth : 0;

                AbilityUpgradeButton button = new AbilityUpgradeButton(branch, upgrade, x, y, widgetWidth, widgetHeight, Text.literal(""));

                reloadButtonStatus(button);

                buttons.add(button);
                if(!onlyOneUpgrade) {
                    lines.add(new Line(
                            new Vector2i(x + widgetWidth / 2, y + widgetHeight / 2),
                            new Vector2i(startX, y + widgetHeight / 2)
                    ));
                }
            }
        }
    }

    public void reload(UpgradesData upgradesData) {
        this.upgradesData = upgradesData;
        this.playerLastPurchasedBranch = AbilityUpgradeBranch.findPlayerLastPurchasedBranchIndex(branches, upgradesData);
        buttons.clear();
        lines.clear();
        init();
    }

    private void reloadButtonStatus(AbilityUpgradeButton button) {
        Identifier purchasedUpgrade = upgradesData.purchasedUpgrades().get(button.branch.id());
        if(purchasedUpgrade != null) {
            button.purchased = purchasedUpgrade.equals(button.upgrade.id());
            button.canBePurchased = false;
        }
        AbilityUpgradeBranch playerCurrentBranch = playerLastPurchasedBranch == -1 ? branches.getFirst() : branches.get(Math.min(playerLastPurchasedBranch + 1, branches.size() - 1));
        if(!button.branch.id().equals(playerCurrentBranch.id())) {
            button.canBePurchased = false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0 ,0, -20);
        renderBackground(context, mouseX, mouseY, delta);
        matrices.pop();

        RenderSystem.setShaderColor(1, 1, 1, 1);

        matrices.push();

        matrices.translate(dragX, dragY, 0);
        matrices.scale((float) scale, (float) scale, 1f);

        for (AbilityUpgradeButton b : buttons) {
            b.render(context, mouseX, mouseY, delta, dragX, dragY, scale);
        }

        matrices.translate(0, 0, -20);
        for(Line line: lines) {
            if(line.start.x == line.end.x) {
                context.drawVerticalLine(line.start.x, line.start.y, line.end.y, 0xFFFFFFFF);
            }
            else {
                context.drawHorizontalLine(line.start.x, line.end.x, line.start.y, 0xFFFFFFFF);
            }
        }
        matrices.translate(0, 0, 20);

        matrices.pop();

        matrices.push();
        matrices.translate(0, 0, 20);

        MutableText panelText = Text.translatable("screen.jujutsu.abilities_upgrades.points", upgradesData.points());
        int panelWidth = Math.max(48, client.textRenderer.getWidth(panelText)) + 4;
        int panelHeight = 16;
        int x = width / 2 - panelWidth / 2;
        int y = height - panelHeight / 2 - 16;
        context.fill(x , y, x + panelWidth, y + panelHeight, 0xFF454545);
        context.drawGuiTexture(Jujutsu.id("screen/abilities_upgrades/upgrade_tooltip_border"), x - 4, y - 4, panelWidth + 8, panelHeight + 8);
        context.drawText(client.textRenderer, panelText, x + 2, y + panelHeight / 2 - textRenderer.fontHeight / 2 + 1, 0xFFFFFFFF, true);

        matrices.translate(0, 0, -20);
        matrices.pop();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(AbilityUpgradeButton b: buttons) {
            b.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            dragX += deltaX;
            dragY += deltaY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double oldScale = scale;
        if (verticalAmount > 0) {
            scale *= 1.1;
        } else if (verticalAmount < 0) {
            scale /= 1.1;
        }

        scale = Math.max(0.5, Math.min(scale, 2.0));

        double dx = mouseX - dragX;
        double dy = mouseY - dragY;

        dragX -= (dx / oldScale - dx / scale);
        dragY -= (dy / oldScale - dy / scale);

        return true;
    }

    private class AbilityUpgradeButton extends ClickableWidget {
        private final AbilityUpgradeBranch branch;
        private final AbilityUpgrade upgrade;
        private final List<MutableText> tooltip;
        private final int tooltipWidth;

        private boolean purchased = false;
        private boolean canBePurchased = true;
        private float hoverProgress = 0;
        private float offsetX = 0;
        private int jiggleTime = 0;

        public AbilityUpgradeButton(AbilityUpgradeBranch branch, AbilityUpgrade upgrade, int x, int y, int width, int height, Text message) {
            super(x, y, width, height, message);
            this.upgrade = upgrade;
            this.branch = branch;

            Pair<List<MutableText>, Integer> tooltipInfo = getTooltip(client.textRenderer);
            this.tooltip = tooltipInfo.getLeft();
            this.tooltipWidth = tooltipInfo.getRight();
        }

        public void render(DrawContext context, int mouseX, int mouseY, float delta,
                           double dragX, double dragY, double scale) {
            double adjX = (mouseX - dragX) / scale;
            double adjY = (mouseY - dragY) / scale;
            boolean hovered = isMouseOver(adjX, adjY);
            MatrixStack matrices = context.getMatrices();

            matrices.push();

            matrices.translate(offsetX, 0, 0);

            if(hovered && hoverProgress == 0) {
                client.player.playSoundToPlayer(ModSounds.UI_HOVER, SoundCategory.MASTER, 1, 1);
            }

            if(jiggleTime > 0) {
                offsetX = (float) Math.sin(jiggleTime * 0.35) * 0.75f;
                jiggleTime--;
            }
            else if(offsetX != 0) {
                offsetX = 0;
            }

            float speed = 0.025f;
            hoverProgress += (hovered ? +1f : -1f) * speed;
            hoverProgress = Math.max(0f, Math.min(1f, hoverProgress));

            float softPx = 0.5f;
            float r, g, b;
            if(!purchased) {
                r = canBePurchased ? 0.35f : 0.075f;
                g = canBePurchased ? 0.35f : 0.075f;
                b = canBePurchased ? 0.35f : 0.075f;
            }
            else {
                r = 0.545f; g = 0.769f; b = 0.145f;
            }

            ShaderUtils.renderHexMask(context, context.getMatrices(), getX() - 6, getY() - 6, getWidth() + 12, hoverProgress, softPx, r, g, b, 0.5f, Jujutsu.id("textures/gui/square.png"));
            ShaderUtils.renderHexMask(context, context.getMatrices(), getX() - 4, getY() - 4, getWidth() + 8, hoverProgress, softPx, r + 0.4f, g + 0.4f, b + 0.50f, 1f, Jujutsu.id("textures/gui/square.png"));
            ShaderUtils.renderHexMask(context, context.getMatrices(), getX(), getY(), getWidth(), hoverProgress, softPx, r, g, b, 0.5f, Jujutsu.id("textures/gui/square.png"));

            renderIcon(context);

            matrices.pop();

            if(hovered) renderTooltip(context, mouseX, mouseY, delta);
        }

        private void renderIcon(DrawContext context) {
            MatrixStack matrices = context.getMatrices();
            matrices.translate(0, 0, -2);
            ShaderUtils.renderLitMask(context, matrices, getX() + 2, getY() + 2, getWidth() - 4, hoverProgress, 1, 1, 1, 0.85f, upgrade.icon().withSuffixedPath(".png"));
            matrices.translate(0, 0, 2);

            if(!purchased && canBePurchased) {

            }
            else if(purchased) {
                context.drawGuiTexture(Jujutsu.id("screen/abilities_upgrades/checkmark"), getX() + getWidth() / 2 - 12, getY() + getHeight() - 10, -1, 24, 24);
            }
            else if(!purchased && !canBePurchased) {
                context.drawGuiTexture(Jujutsu.id("screen/abilities_upgrades/lock"), getX() + getWidth() / 2 - 12, getY() + getHeight() - 10, -1, 24, 24);
            }
        }

        private void renderTooltip(DrawContext context, int mouseX, int mouseY, float delta) {
            double adjX = (mouseX - dragX) / scale;
            double adjY = (mouseY - dragY) / scale;

            TextRenderer textRenderer = client.textRenderer;

            int x = (int) (adjX + 20);
            int y = (int) (adjY - 5);

            int height = textRenderer.fontHeight * tooltip.size();
            context.fill(x, y, x + tooltipWidth, y + height, 0x8B454545);
            context.drawGuiTexture(Jujutsu.id("screen/abilities_upgrades/upgrade_tooltip_border"), x - 4, y - 4, tooltipWidth + 8, height + 8);

            for(int i = 0; i < tooltip.size(); i++) {
                MutableText text = tooltip.get(i);
                context.drawText(textRenderer, text, x, y + textRenderer.fontHeight * i, 0xFFFFFFFF, true);
            }
        }

        private Pair<List<MutableText>, Integer> getTooltip(TextRenderer textRenderer) {
            List<MutableText> tooltip = new ArrayList<>();
            MutableText costText = Text.translatable("screen.jujutsu.abilities_upgrades.cost", upgrade.cost()).formatted(Formatting.GOLD);
            int biggestWidth = textRenderer.getWidth(costText);

            tooltip.add(costText);
            tooltip.add(Text.literal(""));
            tooltip.addAll(upgrade.getAllDescriptions());

            for(MutableText text: tooltip) {
                biggestWidth = Math.max(biggestWidth, textRenderer.getWidth(text));
            }

            return new Pair<>(tooltip, biggestWidth);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double adjX = (mouseX - dragX) / scale;
            double adjY = (mouseY - dragY) / scale;

            if (isMouseOver(adjX, adjY) && button == 0) {
                if(!purchased && canBePurchased) {
                    client.player.playSoundToPlayer(ModSounds.UI_SUCCESS, SoundCategory.MASTER, 1, 1);
                    ClientPlayNetworking.send(new AbilityUpgradePurchasedPayload(branch.id(), upgrade.id()));

                    return true;
                }
                else {
                    if(jiggleTime <= 0) {
                        client.player.playSoundToPlayer(ModSounds.UI_FAILURE, SoundCategory.MASTER, 1, 1);
                    }

                    jiggleTime = 100;
                }
            }

            return false;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= this.getX() &&
                    mouseY >= this.getY() &&
                    mouseX < this.getX() + this.getWidth() &&
                    mouseY < this.getY() + this.getHeight();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {}
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    private record Line(Vector2i start, Vector2i end) {}
}
