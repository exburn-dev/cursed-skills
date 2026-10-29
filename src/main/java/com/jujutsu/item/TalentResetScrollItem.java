package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.talent.TalentComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class TalentResetScrollItem extends Item implements IBorderTooltipItem, ModelWithIcon {
    public TalentResetScrollItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if(level.isClientSide()) return InteractionResult.PASS;

        TalentComponent component = TalentComponent.get(user);

        int points = (int) Math.floor(component.countSpentPoints() * 0.5);

        component.addPoints(points);
        component.removePurchasedTalents();

        user.getCooldowns().addCooldown(stack, 20);
        user.displayClientMessage(Component.literal("Refunded points: " + points), false);

        stack.shrink(1);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay,
                                Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if(!(Minecraft.getInstance().hasShiftDown())) {
            builder.accept(Component.translatable("item.jujutsu.technique_scroll.press_for_info",
                            Component.literal("<SHIFT>").withStyle(ChatFormatting.YELLOW))
                            .withStyle(ChatFormatting.GRAY));
            return;
        }

        builder.accept(Component.translatable("item.jujutsu.upgrade_reset_scroll.resets_upgrades").withStyle(ChatFormatting.YELLOW));
        builder.accept(Component.translatable("item.jujutsu.upgrade_reset_scroll.returns_points", "50%").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, builder, tooltipFlag);
    }

    @Override
    public TooltipBorderData getBorderData() {
        return new TooltipBorderData.Builder(Jujutsu.id("tooltip/upgrade_reset_scroll/main"))
                .addUpperTile(Jujutsu.id("tooltip/upgrade_reset_scroll/upper_tile"))
                .addUpperDecorTile(Jujutsu.id("tooltip/upgrade_reset_scroll/upper_decor_tile"))
                .addBottomTile(Jujutsu.id("tooltip/upgrade_reset_scroll/bottom_tile"))
                .addBottomDecorTile(Jujutsu.id("tooltip/upgrade_reset_scroll/bottom_decor_tile"))
                .build();
    }

    @Override
    public int getOffset() {
        return 3;
    }

    @Override
    public Identifier getModel() {
        return Jujutsu.id("upgrade_reset_scroll_3d");
    }
}
