package com.jujutsu.systems.ability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class PassiveAbility {
    //public static final Codec<RegistryEntry<PassiveAbility>> ENTRY_CODEC = JujutsuRegistries.PASSIVE_ABILITY_TYPE.getEntryCodec();
    public static final Codec<PassiveAbility> CODEC = JujutsuRegistries.PASSIVE_ABILITY_TYPE.getCodec()
            .dispatch("type", PassiveAbility::getType, PassiveAbilityType::codec);
    public static final PacketCodec<RegistryByteBuf, PassiveAbility> PACKET_CODEC = new NbtPacketCodec<>(CODEC);


    public PassiveAbility() {
    }

    public void tick(PlayerEntity player) {
//        if(!conditions.isEmpty()) {
//            boolean conditionsCompleted = true;
//            for(Condition condition: conditions) {
//                if (condition.test(player, instance)) continue;
//
//                conditionsCompleted = false;
//                break;
//            }
//            if(conditionsCompleted) {
//                onConditionsCompleted(player, instance);
//            }
//        }
    }

    public abstract void onGained(PlayerEntity player);
    public abstract void onRemoved(PlayerEntity player);

    @SafeVarargs
    protected final void addAttributes(PlayerEntity player, Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>... attributes) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ArrayListMultimap.create();
        for(int i = 0; i < attributes.length; i++) {
            Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier> pair = attributes[i];

            map.put(pair.getLeft(), pair.getRight());
        }
        player.getAttributes().addTemporaryModifiers(map);
    }

    protected final void addAttributes(PlayerEntity player, Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributes) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ArrayListMultimap.create();
        map.put(attributes.getLeft(), attributes.getRight());
        player.getAttributes().addTemporaryModifiers(map);
    }

    public abstract PassiveAbilityType<?> getType();
}
