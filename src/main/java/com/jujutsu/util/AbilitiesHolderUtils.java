package com.jujutsu.util;

import com.jujutsu.Jujutsu;
import com.jujutsu.network.payload.SyncPlayerAbilitiesPayload;
import com.jujutsu.systems.ability.core.AbilityInstanceOld;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.holder.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.upgrade.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class AbilitiesHolderUtils {
    public static void removeAbilities(ServerPlayerEntity player) {
        IAbilitiesHolder holder = (IAbilitiesHolder) player;
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

        cancelAbilityUpgrades(player);
    }

    public static double getAbilityAttributeValue(PlayerEntity player, RegistryEntry<AbilityAttribute> attribute) {
        AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) player;
        List<AbilityAttributeModifier> holderModifiers = new ArrayList<>(holder.getAbilityAttributes().attributes().get(attribute).values());

        double totalValue = 0;
        double totalMultiplier = 1;
        for(int i = 0; i < holderModifiers.size(); i++) {
            AbilityAttributeModifier modifier = holderModifiers.get(i);
            if(modifier.type() == AbilityAttributeModifier.Type.ADD) {
                totalValue += modifier.value();
            }
            else {
                totalMultiplier += modifier.value();
            }
        }
        return totalValue * totalMultiplier;
    }

    public static <T extends AbilityType> Optional<AbilityInstanceOld> findAbility(IAbilitiesHolder holder, Class<T> ability) {
        if(holder.getSlots().isEmpty()) return Optional.empty();

        for(AbilitySlot slot: holder.getSlots()) {
            AbilityInstanceOld instance = holder.getAbilityInstance(slot);

            if(ability.equals(instance.getType().getClass())) {
                return Optional.of(instance);
            }
        }
        return Optional.empty();
    }

    public static <T extends PassiveAbility> Optional<T> findPassiveAbility(IAbilitiesHolder holder, Class<T> ability) {
        if(holder.getPassiveAbilities().isEmpty()) return Optional.empty();

        for(PassiveAbility passiveAbility: holder.getPassiveAbilities()) {
            if(ability.equals(passiveAbility.getClass())) {
                return Optional.of(ability.cast(passiveAbility));
            }
        }
        return Optional.empty();
    }

    public static void cancelAbilityUpgrades(ServerPlayerEntity player) {
        IAbilitiesHolder holder = (IAbilitiesHolder) player;
        UpgradesData data = holder.getUpgradesData();

        List<AbilityUpgradeBranch> branches = AbilityUpgradesReloadListener.getInstance().getBranches(data.upgradesId());
        if(branches == null || branches.isEmpty()) return;
        for(AbilityUpgradeBranch branch: branches) {
            if (data.purchasedUpgrades().containsKey(branch.id())) {
                AbilityUpgrade upgrade = branch.findUpgrade(data.purchasedUpgrades().get(branch.id()));

                if(upgrade != null) {
                    upgrade.remove(player);
                }
            }
        }
    }

    public static void removeAbilityUpgrades(ServerPlayerEntity player) {
        IAbilitiesHolder holder = (IAbilitiesHolder) player;
        UpgradesData data = holder.getUpgradesData();

        List<AbilityUpgradeBranch> branches = AbilityUpgradesReloadListener.getInstance().getBranches(data.upgradesId());
        if(branches == null || branches.isEmpty()) return;
        for(AbilityUpgradeBranch branch: branches) {
            if (data.purchasedUpgrades().containsKey(branch.id())) {
                AbilityUpgrade upgrade = branch.findUpgrade(data.purchasedUpgrades().get(branch.id()));

                if(upgrade != null) {
                    upgrade.remove(player);
                }
            }
        }

        AbilityAttributeContainerHolder attributesHolder = (AbilityAttributeContainerHolder) player;
        AbilityAttributesContainer container = attributesHolder.getAbilityAttributes();
        AbilityAttributesContainer newContainer = new AbilityAttributesContainer(new HashMap<>());

        for(var entry: container.attributes().entrySet()) {
            RegistryEntry<AbilityAttribute> attribute = entry.getKey();
            for(var entry1: entry.getValue().entrySet()) {
                if(entry1.getKey().equals(Jujutsu.id("base"))) {
                    HashMap<Identifier, AbilityAttributeModifier> map = newContainer.attributes().getOrDefault(attribute, new HashMap<>());
                    map.put(entry1.getKey(), entry1.getValue());
                    newContainer.attributes().put(attribute, map);
                }
            }
        }

        attributesHolder.setAbilityAttributes(newContainer);

        holder.setUpgradesData(new UpgradesData(data.upgradesId(), data.points(), new HashMap<>()));

        ServerPlayNetworking.send(player, new SyncPlayerAbilitiesPayload(
                ((IPlayerJujutsuAbilitiesHolder) player).getAbilities(),
                holder.getUpgradesData()
        ));
    }
}
