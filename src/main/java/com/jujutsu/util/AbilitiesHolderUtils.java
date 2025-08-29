package com.jujutsu.util;

import com.jujutsu.Jujutsu;
import com.jujutsu.network.payload.SyncPlayerAbilitiesPayload;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.holder.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.upgrade.AbilityUpgrade;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeBranch;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradesReloadListener;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

    public static void removeAbilityUpgrades(ServerPlayerEntity player) {
        IAbilitiesHolder holder = (IAbilitiesHolder) player;
        UpgradesData data = holder.getUpgradesData();

        List<AbilityUpgradeBranch> branches = AbilityUpgradesReloadListener.getInstance().getBranches(data.upgradesId());
        for(AbilityUpgradeBranch branch: branches) {
            if (data.purchasedUpgrades().containsKey(branch.id())) {
                Identifier upgradeId = data.purchasedUpgrades().get(branch.id());
                for(AbilityUpgrade upgrade: branch.upgrades()) {
                    if(upgrade.id().equals(upgradeId)) {
                        upgrade.remove(player);
                    }
                }
            }
        }

        AbilityAttributeContainerHolder attributesHolder = (AbilityAttributeContainerHolder) player;
        AbilityAttributesContainer container = attributesHolder.getAbilityAttributes();
        AbilityAttributesContainer newContainer = new AbilityAttributesContainer(new HashMap<>());

        for(var entry: container.attributes().entrySet()) {
            AbilityAttribute attribute = entry.getKey();
            for(var entry1: entry.getValue().entrySet()) {
                if(entry1.getKey().equals(Jujutsu.getId("base"))) {
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
