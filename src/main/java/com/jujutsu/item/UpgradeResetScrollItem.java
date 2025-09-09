package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.upgrade.*;
import com.jujutsu.util.AbilitiesHolderUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class UpgradeResetScrollItem extends Item implements IBorderTooltipItem, ModelWithIcon {
    public UpgradeResetScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(world.isClient()) return TypedActionResult.pass(stack);

        IAbilitiesHolder holder = (IAbilitiesHolder) user;
        UpgradesData data = holder.getUpgradesData();

        int points = (int) Math.floor(countSpentPoints(data) * 0.5);
        holder.setUpgradesData(new UpgradesData(data.upgradesId(), data.points() + points, data.purchasedUpgrades()));

        AbilitiesHolderUtils.removeAbilityUpgrades((ServerPlayerEntity) user);

        user.getItemCooldownManager().set(this, 20);
        user.sendMessage(Text.literal("Refunded points: " + points));

        stack.decrement(1);

        return TypedActionResult.success(stack);
    }

    private float countSpentPoints(UpgradesData data) {
        List<AbilityUpgradeBranch> branches = AbilityUpgradesReloadListener.INSTANCE.getBranches(data.upgradesId());
        float points = 0;

        for(AbilityUpgradeBranch branch: branches) {
            if(!data.purchasedUpgrades().containsKey(branch.id())) continue;

            Identifier purchasedUpgrade = data.purchasedUpgrades().get(branch.id());
            for(AbilityUpgrade upgrade: branch.upgrades()) {
                if(upgrade.id().equals(purchasedUpgrade)) {
                    points += upgrade.cost();
                    break;
                }
            }
        }

        return points;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.jujutsu.upgrade_reset_scroll.resets_upgrades").formatted(Formatting.YELLOW));
        tooltip.add(Text.translatable("item.jujutsu.upgrade_reset_scroll.returns_points", "50%").formatted(Formatting.YELLOW));

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public TooltipBorderData getBorderData() {
        return new TooltipBorderData.Builder(Jujutsu.getId("tooltip/upgrade_reset_scroll/main"))
                .addUpperTile(Jujutsu.getId("tooltip/upgrade_reset_scroll/upper_tile"))
                .addUpperDecorTile(Jujutsu.getId("tooltip/upgrade_reset_scroll/upper_decor_tile"))
                .addBottomTile(Jujutsu.getId("tooltip/upgrade_reset_scroll/bottom_tile"))
                .addBottomDecorTile(Jujutsu.getId("tooltip/upgrade_reset_scroll/bottom_decor_tile"))
                .build();
    }

    @Override
    public int getOffset() {
        return 3;
    }

    @Override
    public Identifier getModel() {
        return Jujutsu.getId("upgrade_reset_scroll_3d");
    }
}
