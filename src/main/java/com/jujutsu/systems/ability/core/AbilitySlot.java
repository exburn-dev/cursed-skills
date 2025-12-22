package com.jujutsu.systems.ability.core;

import com.jujutsu.Jujutsu;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.List;

public final class AbilitySlot {
    public static final Codec<AbilitySlot> CODEC = Identifier.CODEC.xmap(AbilitySlot::byId, abilitySlot -> abilitySlot.id);
    public static final PacketCodec<RegistryByteBuf, AbilitySlot> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public AbilitySlot decode(RegistryByteBuf buf) {
            return AbilitySlot.byId(Identifier.PACKET_CODEC.decode(buf));
        }

        @Override
        public void encode(RegistryByteBuf buf, AbilitySlot value) {
            Identifier.PACKET_CODEC.encode(buf, value.id);
        }
    };

    public static final AbilitySlot ABILITY_SLOT_1 = new AbilitySlot(Jujutsu.id("slot_1"));
    public static final AbilitySlot ABILITY_SLOT_2 = new AbilitySlot(Jujutsu.id("slot_2"));
    public static final AbilitySlot ABILITY_SLOT_3 = new AbilitySlot(Jujutsu.id("slot_3"));
    public static final AbilitySlot ABILITY_SLOT_4 = new AbilitySlot(Jujutsu.id("slot_4"));
    public static final AbilitySlot ABILITY_SLOT_ON_DEATH = new AbilitySlot(Jujutsu.id("slot_on_death"));

    private final Identifier id;

    private AbilitySlot(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public static List<AbilitySlot> getAllSlots() {
        return List.of(ABILITY_SLOT_1, ABILITY_SLOT_2, ABILITY_SLOT_3, ABILITY_SLOT_4, ABILITY_SLOT_ON_DEATH);
    }

    public static AbilitySlot byId(Identifier id) {
        for (AbilitySlot abilitySlot : getAllSlots()) {
            if (id.equals(abilitySlot.id)) {
                return abilitySlot;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((AbilitySlot) obj).id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}