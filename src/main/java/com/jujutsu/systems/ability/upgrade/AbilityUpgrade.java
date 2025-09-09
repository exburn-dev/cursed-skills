package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record AbilityUpgrade(Identifier id, Identifier icon, float cost, List<AbilityUpgradeReward> rewards) {
    public static final Codec<AbilityUpgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityUpgrade::id),
            Identifier.CODEC.fieldOf("icon").forGetter(AbilityUpgrade::icon),
            Codec.FLOAT.fieldOf("cost").forGetter(AbilityUpgrade::cost),
            AbilityUpgradeReward.CODEC.listOf().fieldOf("rewards").forGetter(AbilityUpgrade::rewards)
    ).apply(instance, AbilityUpgrade::new));

    public static final PacketCodec<RegistryByteBuf, AbilityUpgrade> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityUpgrade::id,
            Identifier.PACKET_CODEC, AbilityUpgrade::icon,
            PacketCodecs.FLOAT, AbilityUpgrade::cost,
            AbilityUpgradeReward.PACKET_CODEC.collect(PacketCodecs.toList()), AbilityUpgrade::rewards,
            AbilityUpgrade::new
    );

    public void apply(PlayerEntity player) {
        for(AbilityUpgradeReward reward: rewards) {
            reward.apply(player);
        }
    }

    public void remove(PlayerEntity player) {
        for(AbilityUpgradeReward reward: rewards) {
            reward.remove(player);
        }
    }

    public List<MutableText> getAllDescriptions() {
        List<MutableText> list = new ArrayList<>();
        for(AbilityUpgradeReward reward: rewards) {
            list.addAll(reward.getDescription());
        }
        return list;
    }
}
