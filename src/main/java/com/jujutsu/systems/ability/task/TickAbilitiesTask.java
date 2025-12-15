package com.jujutsu.systems.ability.task;

import com.jujutsu.systems.ability.core.AbilityInstanceOld;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public record TickAbilitiesTask() implements AbilityTask {
    @Override
    public ActionResult execute(PlayerEntity player) {
        IAbilitiesHolder holder = (IAbilitiesHolder) player;

        List<AbilitySlot> toRemove = new ArrayList<>();
        for(AbilitySlot slot: holder.getRunningSlots()) {
            AbilityInstanceOld instance = holder.getAbilityInstance(slot);

            if(!instance.slotInitialized()) {
                instance.initializeSlot(slot);
            }

            instance.tick(player);

            if((instance.getStatus().isRunning() && instance.isFinished(player)) || instance.getStatus().isCancelled()) {
                instance.endAbility(player);
                syncAbilitiesToClient((ServerPlayerEntity) player);
            }
            else if(instance.getStatus().onCooldown() && instance.getCooldownTime() <= 0) {
                toRemove.add(slot);
                instance.endCooldown();
                syncAbilitiesToClient((ServerPlayerEntity) player);
            }
        }
        holder.getRunningSlots().removeAll(toRemove);

        for(PassiveAbility passiveAbility: holder.getPassiveAbilities()) {
            passiveAbility.tick(player);
        }

        return ActionResult.PASS;
    }
}
