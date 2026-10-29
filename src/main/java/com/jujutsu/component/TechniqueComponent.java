package com.jujutsu.component;

import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.passive.PassiveAbility;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record TechniqueComponent(Map<AbilitySlot, AbilityType> abilities, List<PassiveAbility> passiveAbilities, Identifier upgradesId) {
    public static final Codec<TechniqueComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(AbilitySlot.CODEC,
                    JujutsuRegistries.ABILITY_TYPE.byNameCodec()).fieldOf("abilities").forGetter(TechniqueComponent::abilities),
                    PassiveAbility.CODEC.listOf().fieldOf("passiveAbilities").forGetter(TechniqueComponent::passiveAbilities),
                    Identifier.CODEC.fieldOf("tree").forGetter(TechniqueComponent::upgradesId))
                    .apply(instance, TechniqueComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TechniqueComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, AbilitySlot.PACKET_CODEC, ByteBufCodecs.registry(JujutsuRegistries.ABILITY_TYPE_KEY)), TechniqueComponent::abilities,
            PassiveAbility.STREAM_CODEC.apply(ByteBufCodecs.list()), TechniqueComponent::passiveAbilities,
            Identifier.STREAM_CODEC, TechniqueComponent::upgradesId,
            TechniqueComponent::new);
}
