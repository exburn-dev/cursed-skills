package com.jujutsu.screen;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.keybind.AbilityKeyBinding;
import com.jujutsu.client.keybind.ModKeybindings;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AbilitiesKeybindingsScreen extends Screen {
    private static final Identifier BACKGROUND = Jujutsu.id("screen/abilities_menu/background");
    private static final Identifier PANEL_BACKGROUND = Jujutsu.id("screen/abilities_menu/panel_background");
    private static final Identifier ABILITY_BACKGROUND = Jujutsu.id("screen/abilities_menu/ability_background");
    private static final Identifier SELECTED_ABILITY_BACKGROUND = Jujutsu.id("screen/abilities_menu/selected_ability_background");
    private static final Identifier PAGE_SWITCH_BACKGROUND = Jujutsu.id("screen/abilities_menu/page_switch_background");
    private static final Identifier PAGE_SWITCH_ARROW_LEFT = Jujutsu.id("screen/abilities_menu/page_switch_arrow_left");
    private static final Identifier PAGE_SWITCH_ARROW_RIGHT = Jujutsu.id("screen/abilities_menu/page_switch_arrow_right");

    private AbilitiesBindingsPanel panel;
    private AbilityKeyBindingButton activeButton = null;

    public AbilitiesKeybindingsScreen() {
        super(Text.translatable("screen.jujutsu.abilities_keybindings"));
    }

    @Override
    protected void init() {
        super.init();
        MinecraftClient client = MinecraftClient.getInstance();
        IAbilitiesHolder holder = (IAbilitiesHolder) client.player;
        List<AbilityKeyBindingButton> buttons = new ArrayList<>();

        int x = getX() + 4;
        int y = getY() + getHeight() / 2 - 60;
        int width = 0;
        for(int i = 0; i < ModKeybindings.abilityBindings.size(); i++) {
            AbilityKeyBinding binding = ModKeybindings.abilityBindings.get(i);
            AbilityInstance instance = holder.getAbilityInstance(binding.getAbilitySlot());
            if (instance == null) continue;
            width = Math.max(width, textRenderer.getWidth(instance.getType().getName()));
        }
        width += 5;

        int xOffset = 100 / 2 - width / 2;
        for(int i = 0; i < ModKeybindings.abilityBindings.size(); i++) {
            AbilityKeyBinding binding = ModKeybindings.abilityBindings.get(i);
            AbilityInstance instance = holder.getAbilityInstance(binding.getAbilitySlot());
            if(instance == null) continue;

            buttons.add(new AbilityKeyBindingButton(binding, instance, x + xOffset, y + 10 + (20 + 5) * i, width, 20 , Text.literal("bind")));
        }

        panel = new AbilitiesBindingsPanel(Math.max((int) Math.ceil((double) buttons.size() / 4), 1), x, y, 100, 120, Text.literal("abilities"));
        panel.addButtons(buttons);
        this.addDrawable((context, mouseX, mouseY, delta) -> {
            int x1 = getX() + panel.getWidth() + 8;
            int y1 = getY() + 6;

            int abilitiesTitleHeight = panel.getY() - getY() - 10;
            context.drawGuiTexture(PANEL_BACKGROUND, x, y1, panel.getWidth(), abilitiesTitleHeight);

            int centerX = x1 + (getWidth() - 100 - 8) / 2;
            context.drawGuiTexture(PANEL_BACKGROUND, x1, y1, getWidth() - panel.getWidth() - 8, getHeight() - 12);

            y1 += 8;

            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.jujutsu.abilities_menu.abilities"), x + panel.getWidth() / 2, y1, 0xFFFFFF);

            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.jujutsu.abilities_menu.ability_info"),  centerX, y1, 0xFFFFFF);
            if(activeButton != null) {
                AbilityType type = activeButton.instance.getType();
                context.drawCenteredTextWithShadow(textRenderer, type.getName().copyContentOnly().setStyle(type.getStyle()), centerX, y1 + 10, 0xFFFFFF);

                TextColor textColor = activeButton.instance.getType().getStyle().getColor();
                int color = textColor != null ? ColorHelper.Argb.getArgb(255, ColorHelper.Argb.getRed(textColor.getRgb()), ColorHelper.Argb.getGreen(textColor.getRgb()), ColorHelper.Argb.getBlue(textColor.getRgb())) : 0xFFFFFFFF;
                context.fill(centerX - textRenderer.getWidth(type.getName()) / 2 - 16, y1 + textRenderer.fontHeight + 11, centerX + textRenderer.getWidth(type.getName()) / 2 + 16, y1 + textRenderer.fontHeight + 12, color);

                x1 += 6;

                context.drawText(textRenderer, Text.translatable("screen.jujutsu.abilities_menu.bound_key"), x1, y1 + 30, 0xFFFFFF, true);

                //context.drawTextWrapped(textRenderer, StringVisitable.plain(type.getDescription().getString()), x1, y1 + 30 + 24, getWidth() - 100 - 19, 0xFFFFFF);
            }
        });

        this.addDrawableChild(new AbilityBoundKeyButton(getX() + getWidth() - 40, getY() + 6 + 8 + 24, 40, 20, Text.literal("boundkey")));
        this.addDrawableChild(new AbilityDescription(getX() + panel.getWidth() + 14, getY() + 12 + 8 + 24 + 24, getWidth() - panel.getWidth() - 19, 10));

        this.addDrawableChild(new PageSwitchButton(true, x, y + panel.getHeight() + 18, 16, 16, Text.literal("pageswitch")));
        this.addDrawableChild(new PageSwitchButton(false, x + panel.getWidth() - 16, y + panel.getHeight() + 18, 16, 16, Text.literal("pageswitch")));

        this.addDrawableChild(panel);
    }

    private int getX() {
        return width / 2 - getWidth() / 2;
    }

    private int getY() {
        return height / 2 - getHeight() / 2;
    }

    private int getWidth() {
        return 250;
    }

    private int getHeight() {
        return 200;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private class AbilitiesBindingsPanel extends ClickableWidget {
        private static final int BUTTONS_PER_PAGE = 4;
        private final List<AbilityKeyBindingButton> buttons = new ArrayList<>();
        private final int pageCount;

        private int page = 0;

        public AbilitiesBindingsPanel(int pageCount, int i, int j, int k, int l, Text text) {
            super(i, j, k, l, text);
            this.pageCount = pageCount;
        }

        public void nextPage() {
            if(page + 1 >= pageCount) {
                page = 0;
                return;
            }
            this.page++;
        }

        public void previousPage() {
            if(page - 1 < 0) {
                page = pageCount - 1;
                return;
            }
            this.page--;
        }

        public void addButtons(Collection<AbilityKeyBindingButton> buttons) {
            this.buttons.addAll(buttons);
        }

        public void addButton(AbilityKeyBindingButton button) {
            this.buttons.add(button);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.visible) {
                context.drawGuiTexture(PANEL_BACKGROUND, this.getX(), this.getY(), 0, this.getWidth(), this.getHeight());

                for(int i = 0; i < Math.min(BUTTONS_PER_PAGE, buttons.size()); i++) {
                    buttons.get(i + page * 4).render(context, mouseX, mouseY, delta);
                }

                context.drawCenteredTextWithShadow(textRenderer, Text.literal(String.format("%d / %d", page + 1, pageCount)), this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() + 22, 0xFFFFFF);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (AbilityKeyBindingButton abilityKeyBindingButton : buttons) {
                abilityKeyBindingButton.mouseClicked(mouseX, mouseY, button);
            }
            return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            for (AbilityKeyBindingButton button : buttons) {
                button.keyReleased(keyCode, scanCode, modifiers);
            }
            return super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

    private class PageSwitchButton extends ClickableWidget {
        private final boolean left;

        public PageSwitchButton(boolean left, int x, int y, int width, int height, Text message) {
            super(x, y, width, height, message);
            this.left = left;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawGuiTexture(PAGE_SWITCH_BACKGROUND, this.getX(), getY(), this.getWidth(), this.getHeight());

            context.drawGuiTexture(left ? PAGE_SWITCH_ARROW_LEFT : PAGE_SWITCH_ARROW_RIGHT, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(left) {
                panel.previousPage();
            }
            else {
                panel.nextPage();
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

    private class AbilityBoundKeyButton extends ClickableWidget {
        private boolean bounding = false;

        public AbilityBoundKeyButton(int x, int y, int width, int height, Text message) {
            super(x, y, width, height, message);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if(activeButton == null) return;

            Text key = getButtonText();

            int buttonWidth = getButtonWidth(key);
            context.drawGuiTexture(ABILITY_BACKGROUND, this.getX() - buttonWidth / 2, this.getY(), buttonWidth, this.getHeight());

            context.drawCenteredTextWithShadow(textRenderer, key, this.getX(), this.getY() + this.getHeight() / 2 - textRenderer.fontHeight / 2, 0xFFFFFF);
        }

        private int getButtonWidth(Text buttonText) {
            return MathHelper.clamp(textRenderer.getWidth(buttonText) + 6, getWidth(), 90);
        }

        private Text getButtonText() {
            Text key;
            if(!bounding) {
                key = activeButton.binding.getBoundKeyLocalizedText();
            }
            else {
                key = Text.literal("<   >");
                double spacing = 1.0 + 3.0 * Math.abs( ((Util.getMeasuringTimeMs() / 400.0 ) % 4.0) / 2.0 - 1.0);
                if(spacing >= 2 && spacing < 3) {
                    key = Text.literal("<    >");
                } else if (spacing >= 3) {
                    key = Text.literal("<     >");
                }
            }
            return key;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(activeButton == null) return false;
            int buttonWidth = getButtonWidth(getButtonText());
            int x1 = this.getX() - buttonWidth / 2;
            int y1 = this.getY();
            int x2 = x1 + buttonWidth;
            int y2 = y1 + this.getHeight();

            if(isValidClickButton(button) && mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
                bounding = !bounding;
                this.playDownSound(client.getSoundManager());
                return true;
            }
            else {
                bounding = false;
            }

            return false;
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if(bounding) {
                activeButton.binding.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
                bounding = false;
            }
            return super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

    private class AbilityDescription extends ClickableWidget {
        private boolean collapsed;

        public AbilityDescription(int x, int y, int width, int height) {
            super(x, y, width, height, Text.literal("abilitydescription"));
        }

        @Override
        public int getHeight() {
            if(collapsed || getDescription().isEmpty()) return super.getHeight();

            List<OrderedText> lines = textRenderer.wrapLines(getDescription().get(), this.getWidth());
            return lines.size() * (textRenderer.fontHeight + 1) + super.getHeight();
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            if(getDescription().isEmpty()) return;

            context.drawText(textRenderer, Text.translatable("screen.jujutsu.abilities_menu.description"), this.getX(), this.getY(), 0xFFFFFF, true);

            Text arrow = collapsed ? Text.literal(">") : Text.literal("▼");
            context.drawCenteredTextWithShadow(textRenderer, arrow, getCollapseButtonX() + getCollapseButtonSize() / 2, this.getY(), 0xFFFFFF);

            if(collapsed) return;

            context.drawTextWrapped(textRenderer, getDescription().get(), this.getX(), this.getY() + this.getCollapseButtonSize() + 2, this.getWidth(), 0xFFFFFF);
        }

        private Optional<Text> getDescription() {
            return activeButton == null ? Optional.empty() : Optional.of(activeButton.instance.getType().getDescription());
        }

        private int getCollapseButtonX() {
            return this.getX() + this.getWidth() - getCollapseButtonSize();
        }

        private int getCollapseButtonY() {
            return this.getY();
        }

        private int getCollapseButtonSize() {
            return this.height;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int buttonX = getCollapseButtonX();
            int buttonX1 = buttonX + getCollapseButtonSize();
            int buttonY = getCollapseButtonY();
            int buttonY1 = buttonY + getCollapseButtonSize();
            if(mouseX >= buttonX && mouseX <= buttonX1 && mouseY >= buttonY && mouseY <= buttonY1) {
                this.collapsed = !collapsed;
                return super.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

    private class AbilityKeyBindingButton extends ClickableWidget {
        private final AbilityKeyBinding binding;
        private final AbilityInstance instance;

        public AbilityKeyBindingButton(AbilityKeyBinding binding, AbilityInstance instance, int x, int y, int width, int height, Text message) {
            super(x, y, width, height, message);
            this.binding = binding;
            this.instance = instance;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient client = MinecraftClient.getInstance();
            boolean isSelected =  activeButton == this;
            Identifier backgroundTexture = isSelected ? SELECTED_ABILITY_BACKGROUND : ABILITY_BACKGROUND;

            context.drawGuiTexture(backgroundTexture, getX(), getY(), 0, getWidth(), getHeight());

            context.drawCenteredTextWithShadow(client.textRenderer, instance.getType().getName(), getX() + getWidth() / 2, getY() + getHeight() / 2 - client.textRenderer.fontHeight / 2 + (isSelected ? 2 : 0), 0xFFFFFF);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(isMouseOver(mouseX, mouseY)) {
                AbilitiesKeybindingsScreen.this.activeButton = this;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
