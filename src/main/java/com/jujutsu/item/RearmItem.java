package com.jujutsu.item;

import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
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

        AbilityComponent component = AbilityComponent.get(user);

        if (component.slots().isEmpty()) return TypedActionResult.pass(stack);
        for(AbilitySlot slot: component.slots()) {
            AbilityInstance instance = component.getInstance(slot);
            instance.endCooldown();
        }

        user.getItemCooldownManager().set(this, 10);

        return super.use(world, user, hand);
    }
}
