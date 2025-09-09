package com.jujutsu.systems.ability.upgrade.reward;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.AbilityUpgradeRewardTypes;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
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
import java.util.HashMap;
import java.util.List;

public class AbilityAttributeReward extends AbilityUpgradeReward {
    public static final MapCodec<AbilityAttributeReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getEntryCodec(), ShortAbilityAttributeModifier.CODEC).xmap(HashMap::new, HashMap::new)
                    .fieldOf("modifiers").forGetter(AbilityAttributeReward::modifiers))
            .apply(instance, AbilityAttributeReward::new));

    private final HashMap<RegistryEntry<AbilityAttribute>, ShortAbilityAttributeModifier> modifiers;

    public AbilityAttributeReward(HashMap<RegistryEntry<AbilityAttribute>, ShortAbilityAttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public HashMap<RegistryEntry<AbilityAttribute>, ShortAbilityAttributeModifier> modifiers() {
        return this.modifiers;
    }

    @Override
    public List<MutableText> getDescription() {
        List<MutableText> description = new ArrayList<>();
        for(var attributeEntry: modifiers.entrySet()) {
            RegistryEntry<AbilityAttribute> attribute = attributeEntry.getKey();
            ShortAbilityAttributeModifier modifier = attributeEntry.getValue();
            double value = modifier.amount;
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
        AbilityAttributeContainerHolder attributeHolder = (AbilityAttributeContainerHolder) player;

        for(var entry: modifiers.entrySet()) {
            ShortAbilityAttributeModifier shortModifier = entry.getValue();
            AbilityAttributeModifier modifier = new AbilityAttributeModifier(shortModifier.amount, shortModifier.type);

            attributeHolder.getAbilityAttributes().addModifier(entry.getKey(), shortModifier.id, modifier);
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) player;
        AbilityAttributesContainer playerContainer = holder.getAbilityAttributes();

        for(var entry: modifiers.entrySet()) {
            RegistryEntry<AbilityAttribute> attribute = entry.getKey();
            Identifier id = entry.getValue().id();
            Jujutsu.LOGGER.info("Removing {} {} upgrade", attribute, entry.getValue().amount);

            playerContainer.attributes().get(attribute).remove(id);
        }
    }

    @Override
    public AbilityUpgradeRewardType<?> getType() {
        return AbilityUpgradeRewardTypes.ABILITY_ATTRIBUTE;
    }

    public record ShortAbilityAttributeModifier(Identifier id, double amount, AbilityAttributeModifier.Type type) {
        public ShortAbilityAttributeModifier(Identifier id, double amount, AbilityAttributeModifier.Type type) {
            this.id = id;
            this.amount = BigDecimal.valueOf(amount)
                    .setScale(5, RoundingMode.HALF_UP)
                    .doubleValue();
            this.type = type;
        }

        private static final Codec<ShortAbilityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(ShortAbilityAttributeModifier::id),
                Codec.DOUBLE.fieldOf("amount").forGetter(ShortAbilityAttributeModifier::amount),
                AbilityAttributeModifier.Type.CODEC.fieldOf("operation").forGetter(ShortAbilityAttributeModifier::type)
        ).apply(instance, ShortAbilityAttributeModifier::new));
    }
}
