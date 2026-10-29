package com.jujutsu.item;

import com.jujutsu.api.ability.core.AbilityComponent;
import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.api.ability.core.AbilitySlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RearmItem extends Item {
    public RearmItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if(level.isClientSide()) return InteractionResult.PASS;

        AbilityComponent component = AbilityComponent.get(user);

        if (component.slots().isEmpty()) return InteractionResult.PASS;
        for(AbilitySlot slot: component.slots()) {
            AbilityInstance instance = component.getInstance(slot);
            instance.endCooldown();
        }

        user.getCooldowns().addCooldown(stack, 10);

        return InteractionResult.SUCCESS;
    }
}
