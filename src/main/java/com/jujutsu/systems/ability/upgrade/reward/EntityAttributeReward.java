package com.jujutsu.systems.ability.upgrade.reward;

import com.jujutsu.registry.AbilityUpgradeRewardTypes;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeReward;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeRewardType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityAttributeReward extends AbilityUpgradeReward {
    public static final MapCodec<EntityAttributeReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(EntityAttribute.CODEC, EntityAttributeModifier.CODEC).xmap(HashMap::new, HashMap::new)
                    .fieldOf("modifiers").forGetter(EntityAttributeReward::modifiers))
                    .apply(instance, EntityAttributeReward::new)
    );

    private final HashMap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers;

    public EntityAttributeReward(HashMap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public HashMap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers() {
        return this.modifiers;
    }

    @Override
    public void apply(PlayerEntity player) {
        for(var entry: modifiers.entrySet()) {
            EntityAttributeInstance instance = player.getAttributes().getCustomInstance(entry.getKey());
            if(instance != null) {
                instance.addTemporaryModifier(entry.getValue());
            }
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        for(var entry: modifiers.entrySet()) {
            EntityAttributeInstance instance = player.getAttributes().getCustomInstance(entry.getKey());
            if(instance != null) {
                instance.removeModifier(entry.getValue());
            }
        }
    }

    @Override
    public List<MutableText> getDescription() {
        List<MutableText> description = new ArrayList<>();
        for(var entry: modifiers.entrySet()) {
            EntityAttribute attribute = entry.getKey().value();
            EntityAttributeModifier modifier = entry.getValue();
            boolean multiplyMode = !modifier.operation().equals(EntityAttributeModifier.Operation.ADD_VALUE);
            double value = multiplyMode ? 100 * modifier.value() : modifier.value();
            boolean positiveValue = value >= 0;

            MutableText text = Text.literal(positiveValue ? "+" : "-").append(Text.literal(String.valueOf(Math.abs(value))));
            text.append(multiplyMode ? "% " : " ");
            text.append(Text.translatable(attribute.getTranslationKey()));
            text.formatted(positiveValue ? Formatting.GREEN : Formatting.RED);

            description.add(text);
        }

        return description;
    }

    @Override
    public AbilityUpgradeRewardType<?> getType() {
        return AbilityUpgradeRewardTypes.ENTITY_ATTRIBUTE;
    }
}
