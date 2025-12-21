package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.talent.TalentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class TalentResetScrollItem extends Item implements IBorderTooltipItem, ModelWithIcon {
    public TalentResetScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(world.isClient()) return TypedActionResult.pass(stack);

        TalentComponent component = TalentComponent.get(user);

        int points = (int) Math.floor(component.countSpentPoints() * 0.5);

        component.addPoints(points);
        component.removePurchasedTalents();

        user.getItemCooldownManager().set(this, 20);
        user.sendMessage(Text.literal("Refunded points: " + points));

        stack.decrement(1);

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.jujutsu.upgrade_reset_scroll.resets_upgrades").formatted(Formatting.YELLOW));
        tooltip.add(Text.translatable("item.jujutsu.upgrade_reset_scroll.returns_points", "50%").formatted(Formatting.YELLOW));

        super.appendTooltip(stack, context, tooltip, type);
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
