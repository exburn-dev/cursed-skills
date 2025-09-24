package com.jujutsu.item;

import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RearmItem extends Item {
    public RearmItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(world.isClient()) return TypedActionResult.pass(stack);

        IAbilitiesHolder holder = (IAbilitiesHolder) user;

        if (holder.getSlots().isEmpty()) return TypedActionResult.pass(stack);
        for(AbilitySlot slot: holder.getSlots()) {
            AbilityInstance instance = holder.getAbilityInstance(slot);
            instance.setCooldownTime(0);
        }

        user.getItemCooldownManager().set(this, 10);

        return super.use(world, user, hand);
    }
}
