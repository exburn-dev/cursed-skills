package com.jujutsu.systems.ability.upgrade;

import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public abstract class AbilityUpgradeReward {
    public abstract void apply(PlayerEntity player);
    public abstract void remove(PlayerEntity player);

    public abstract AbilityUpgradeRewardType<?> getType();

    public List<MutableText> getDescription() {
        return List.of(Text.translatable(JujutsuRegistries.ABILITY_UPGRADE_REWARD_TYPE.getId(getType()).toTranslationKey("ability_upgrade")));
    }

    public static final Codec<AbilityUpgradeReward> CODEC = JujutsuRegistries.ABILITY_UPGRADE_REWARD_TYPE.getCodec().dispatch(
            AbilityUpgradeReward::getType,
            AbilityUpgradeRewardType::codec
    );

    public static final PacketCodec<RegistryByteBuf, AbilityUpgradeReward> PACKET_CODEC = new NbtPacketCodec<>(CODEC);
}
