package com.jujutsu.systems.ability.upgrade.reward;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.AbilityUpgradeRewardTypes;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.attribute.*;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeReward;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeRewardType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbilityAttributeReward extends AbilityUpgradeReward {
    public static final MapCodec<AbilityAttributeReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getEntryCodec(), IdentifiedAbilityAttributeModifier.CODEC)
                    .fieldOf("modifiers").forGetter(AbilityAttributeReward::modifiers))
            .apply(instance, AbilityAttributeReward::new));

    private final Map<RegistryEntry<AbilityAttribute>, IdentifiedAbilityAttributeModifier> modifiers;

    public AbilityAttributeReward(Map<RegistryEntry<AbilityAttribute>, IdentifiedAbilityAttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public Map<RegistryEntry<AbilityAttribute>, IdentifiedAbilityAttributeModifier> modifiers() {
        return this.modifiers;
    }

    @Override
    public List<MutableText> getDescription() {
        List<MutableText> description = new ArrayList<>();
        for(var attributeEntry: modifiers.entrySet()) {
            RegistryEntry<AbilityAttribute> attribute = attributeEntry.getKey();
            IdentifiedAbilityAttributeModifier modifier = attributeEntry.getValue();
            double value = modifier.amount();
            boolean multiplyMode = modifier.type() == AbilityAttributeModifier.Type.MULTIPLY;
            if(multiplyMode) {
                value = 100 * value;
            }
            boolean positiveValue = value >= 0;

            DecimalFormat decimalFormat = new DecimalFormat("#.####");

            MutableText text = Text.literal(positiveValue ? "+" : "-");
            text.append(Text.literal(decimalFormat.format(Math.abs(value))));
            text.append(Text.literal(multiplyMode ? "% " : attribute.value().getMeasureUnitSymbol() + " "));
            text.append(Text.translatable(attribute.value().getTranslationKey()));
            text.setStyle(Style.EMPTY.withColor(positiveValue ? Formatting.GREEN : Formatting.RED));

            description.add(text);
        }
        return description;
    }

    @Override
    public void apply(PlayerEntity player) {
        AbilityAttributeComponent component = AbilityAttributeComponent.get(player);

        for(var entry: modifiers.entrySet()) {
            IdentifiedAbilityAttributeModifier identifiedModifier = entry.getValue();
            AbilityAttributeModifier modifier = new AbilityAttributeModifier(identifiedModifier.amount(), identifiedModifier.type());

            component.addModifier(entry.getKey(), identifiedModifier.id(), modifier);
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        AbilityAttributeComponent component = AbilityAttributeComponent.get(player);

        for(var entry: modifiers.entrySet()) {
            RegistryEntry<AbilityAttribute> attribute = entry.getKey();
            Identifier id = entry.getValue().id();

            component.removeModifier(attribute, id);
        }
    }

    @Override
    public AbilityUpgradeRewardType<?> getType() {
        return AbilityUpgradeRewardTypes.ABILITY_ATTRIBUTE;
    }
}
