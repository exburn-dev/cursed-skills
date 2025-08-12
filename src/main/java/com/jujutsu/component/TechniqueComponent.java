package com.jujutsu.component;

import com.jujutsu.registry.ModDataComponents;
import com.jujutsu.registry.ModItems;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record TechniqueComponent(Map<AbilitySlot, AbilityType> abilities, List<PassiveAbility> passiveAbilities) {
    public static final Codec<TechniqueComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.unboundedMap(AbilitySlot.CODEC,
                    JujutsuRegistries.ABILITY_TYPE.getCodec()).fieldOf("abilities").forGetter(TechniqueComponent::abilities),
                    PassiveAbility.CODEC.listOf().fieldOf("passiveAbilities").forGetter(TechniqueComponent::passiveAbilities))
                    .apply(instance, TechniqueComponent::new));

    public static final PacketCodec<RegistryByteBuf, TechniqueComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, AbilitySlot.PACKET_CODEC, PacketCodecs.registryCodec(JujutsuRegistries.ABILITY_TYPE.getCodec())), TechniqueComponent::abilities,
            PassiveAbility.PACKET_CODEC.collect(PacketCodecs.toList()), TechniqueComponent::passiveAbilities,
            TechniqueComponent::new);

    public static class ItemStackBuilder {
        private final List<Pair<AbilitySlot, AbilityType>> abilities = new ArrayList<>();
        private final List<PassiveAbility> passiveAbilities = new ArrayList<>();

        public ItemStackBuilder addAbility(AbilitySlot slot, AbilityType type) {
            abilities.add(new Pair<>(slot, type));
            return this;
        }

        public ItemStackBuilder addPassiveAbility(PassiveAbility ability) {
            passiveAbilities.add(ability);
            return this;
        }

        public ItemStack build() {
            ItemStack stack = ModItems.TECHNIQUE_SCROLL.getDefaultStack();
            Map<AbilitySlot, AbilityType> map = new HashMap<>();
            for(Pair<AbilitySlot, AbilityType> pair: abilities) {
                map.put(pair.getLeft(), pair.getRight());
            }
            stack.set(ModDataComponents.TECHNIQUE_COMPONENT, new TechniqueComponent(map, passiveAbilities));
            return stack;
        }
    }
}
