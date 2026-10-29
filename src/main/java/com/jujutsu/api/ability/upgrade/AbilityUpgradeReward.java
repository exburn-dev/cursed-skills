package com.jujutsu.api.ability.upgrade;

import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class AbilityUpgradeReward {
    public static final Codec<AbilityUpgradeReward> CODEC = JujutsuRegistries.ABILITY_UPGRADE_REWARD_TYPE.byNameCodec().dispatch(
            AbilityUpgradeReward::getType,
            AbilityUpgradeRewardType::codec
    );

    public static final StreamCodec<ByteBuf, AbilityUpgradeReward> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
    public abstract void apply(Player player);
    public abstract void remove(Player player);

    public abstract AbilityUpgradeRewardType<?> getType();

    public List<Component> getDescription() {
        return List.of(Component.translatable(JujutsuRegistries.ABILITY_UPGRADE_REWARD_TYPE.getKey(getType()).toLanguageKey("ability_upgrade")));
    }
}
