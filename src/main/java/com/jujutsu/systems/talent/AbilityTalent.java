package com.jujutsu.systems.talent;

import com.jujutsu.systems.ability.upgrade.AbilityUpgradeReward;
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

public record AbilityTalent(Identifier id, Identifier icon, int cost, List<AbilityUpgradeReward> rewards) {
    public static final Codec<AbilityTalent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityTalent::id),
            Identifier.CODEC.fieldOf("icon").forGetter(AbilityTalent::icon),
            Codec.INT.fieldOf("cost").forGetter(AbilityTalent::cost),
            AbilityUpgradeReward.CODEC.listOf().fieldOf("rewards").forGetter(AbilityTalent::rewards)
    ).apply(instance, AbilityTalent::new));

    public static final PacketCodec<RegistryByteBuf, AbilityTalent> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AbilityTalent::id,
            Identifier.PACKET_CODEC, AbilityTalent::icon,
            PacketCodecs.INTEGER, AbilityTalent::cost,
            AbilityUpgradeReward.PACKET_CODEC.collect(PacketCodecs.toList()), AbilityTalent::rewards,
            AbilityTalent::new
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
