package com.jujutsu.api.talent;

import com.jujutsu.api.ability.upgrade.AbilityUpgradeReward;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public record AbilityTalent(Identifier id, Identifier icon, int cost, List<AbilityUpgradeReward> rewards) {
    public static final Codec<AbilityTalent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AbilityTalent::id),
            Identifier.CODEC.fieldOf("icon").forGetter(AbilityTalent::icon),
            Codec.INT.fieldOf("cost").forGetter(AbilityTalent::cost),
            AbilityUpgradeReward.CODEC.listOf().fieldOf("rewards").forGetter(AbilityTalent::rewards)
    ).apply(instance, AbilityTalent::new));

    public static final StreamCodec<FriendlyByteBuf, AbilityTalent> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AbilityTalent::id,
            Identifier.STREAM_CODEC, AbilityTalent::icon,
            ByteBufCodecs.INT, AbilityTalent::cost,
            AbilityUpgradeReward.STREAM_CODEC.apply(ByteBufCodecs.list()), AbilityTalent::rewards,
            AbilityTalent::new
    );

    public void apply(Player player) {
        for(AbilityUpgradeReward reward: rewards) {
            reward.apply(player);
        }
    }

    public void remove(Player player) {
        for(AbilityUpgradeReward reward: rewards) {
            reward.remove(player);
        }
    }

    public List<Component> getAllDescriptions() {
        List<Component> list = new ArrayList<>();
        for(AbilityUpgradeReward reward: rewards) {
            list.addAll(reward.getDescription());
        }
        return list;
    }
}
