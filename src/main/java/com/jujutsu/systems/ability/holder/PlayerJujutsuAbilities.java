package com.jujutsu.systems.ability.holder;


import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.core.AbilityInstanceOld;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.*;

public record PlayerJujutsuAbilities(HashMap<AbilitySlot, AbilityInstanceOld> abilities, ArrayList<AbilitySlot> runningAbilities, ArrayList<PassiveAbility> passiveAbilities) {
    public static final Codec<PlayerJujutsuAbilities> CODEC;
    public static final PacketCodec<RegistryByteBuf, PlayerJujutsuAbilities> PACKET_CODEC;

    public <T> T serialize(DynamicOps<T> ops) {
        Optional<T> optional = CODEC.encodeStart(ops, this).resultOrPartial((error) -> {
            Jujutsu.LOGGER.warn("Failed to serialize PlayerJujutsuAbilities: {}", error);
        });
        return optional.orElseGet(ops::emptyList);
    }

    public static PlayerJujutsuAbilities deserialize(Dynamic<?> dynamic) {
        var optional = CODEC.decode(dynamic).resultOrPartial((error) -> {
            Jujutsu.LOGGER.warn("Failed to deserialize PlayerJujutsuAbilities: {}", error);
        });
        if(optional.isEmpty()) {
            return new PlayerJujutsuAbilities(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
        }
        return optional.get().getFirst();
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(Codec.unboundedMap(AbilitySlot.CODEC, AbilityInstanceOld.CODEC).xmap(HashMap::new, HashMap::new).fieldOf("abilities").forGetter(PlayerJujutsuAbilities::abilities),
                            AbilitySlot.CODEC.listOf().xmap(ArrayList::new, ImmutableList::copyOf).fieldOf("runningAbilities").forGetter(PlayerJujutsuAbilities::runningAbilities),
                            PassiveAbility.CODEC.listOf().xmap(ArrayList::new, ImmutableList::copyOf).fieldOf("passiveAbilities").forGetter(PlayerJujutsuAbilities::passiveAbilities))
                    .apply(instance, PlayerJujutsuAbilities::new);
        });

        PACKET_CODEC = PacketCodec.tuple(PacketCodecs.map(HashMap::new, AbilitySlot.PACKET_CODEC, AbilityInstanceOld.PACKET_CODEC_RUNTIME_DATA), PlayerJujutsuAbilities::abilities,
                AbilitySlot.PACKET_CODEC.collect(PacketCodecs.toCollection(ArrayList::new)), PlayerJujutsuAbilities::runningAbilities,
                PassiveAbility.PACKET_CODEC.collect(PacketCodecs.toCollection(ArrayList::new)), PlayerJujutsuAbilities::passiveAbilities,
                PlayerJujutsuAbilities::new);
    }
}