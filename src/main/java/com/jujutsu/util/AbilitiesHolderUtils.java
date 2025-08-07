package com.jujutsu.util;

import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.IAbilitiesHolder;
import com.jujutsu.systems.ability.PassiveAbility;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Iterator;

public class AbilitiesHolderUtils {
    public static void removeAbilities(IAbilitiesHolder holder) {
        if (!holder.getSlots().isEmpty()) {
            for(AbilitySlot slot: holder.getSlots()) {
                holder.tryCancelAbility(slot);
                holder.removeAbilityInstance(slot);
            }
        }

        if (!holder.getPassiveAbilities().isEmpty()) {
            for(Iterator<PassiveAbility> iterator = holder.getPassiveAbilities().iterator(); iterator.hasNext(); ) {
                PassiveAbility instance = iterator.next();
                instance.onRemoved((PlayerEntity) holder);
                iterator.remove();
            }
        }
    }
}
