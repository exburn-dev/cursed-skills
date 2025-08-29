package com.jujutsu.systems.ability.upgrade;

import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AbilityAttributeAbilityUpgrade extends AbilityUpgrade {
    public static final MapCodec<AbilityAttributeAbilityUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
            .and(Codec.unboundedMap(JujutsuRegistries.ABILITY_ATTRIBUTE.getCodec(), ShortAbilityAttributeModifier.CODEC).xmap(HashMap::new, HashMap::new)
                    .fieldOf("modifiers").forGetter(AbilityAttributeAbilityUpgrade::modifiers))
            .apply(instance, AbilityAttributeAbilityUpgrade::new));

    private final HashMap<AbilityAttribute, ShortAbilityAttributeModifier> modifiers;

    public AbilityAttributeAbilityUpgrade(Identifier id, Identifier icon, float cost, AbilityUpgradeType<?> type,
                                          HashMap<AbilityAttribute, ShortAbilityAttributeModifier> modifiers) {
        super(id, icon, cost, type);
        this.modifiers = modifiers;
    }

    public HashMap<AbilityAttribute, ShortAbilityAttributeModifier> modifiers() {
        return this.modifiers;
    }

    @Override
    public List<MutableText> getDescription() {
        List<MutableText> description = new ArrayList<>();
        for(var attributeEntry: modifiers.entrySet()) {
            AbilityAttribute attribute = attributeEntry.getKey();
            ShortAbilityAttributeModifier modifier = attributeEntry.getValue();
            double value = modifier.amount;
            boolean multiplyMode = modifier.type() == AbilityAttributeModifier.Type.MULTIPLY;
            if(multiplyMode) {
                value = 100 * value - 100;
            }
            boolean positiveValue = value >= 0;

            MutableText text = Text.literal(positiveValue ? "+" : "-");
            text.append(Text.literal(String.valueOf(Math.abs(value))));
            text.append(Text.literal(multiplyMode ? "% " : " "));
            text.append(Text.translatable(attribute.getTranslationKey()));
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

            attributeHolder.addModifier(entry.getKey(), shortModifier.id, modifier);
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) player;
        AbilityAttributesContainer playerContainer = holder.getAbilityAttributes();

        for(var entry: modifiers.entrySet()) {
            AbilityAttribute attribute = entry.getKey();
            Identifier id = entry.getValue().id();

            playerContainer.attributes().get(attribute).remove(id);
        }
    }

    public record ShortAbilityAttributeModifier(Identifier id, double amount, AbilityAttributeModifier.Type type) {
        private static final Codec<ShortAbilityAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(ShortAbilityAttributeModifier::id),
                Codec.DOUBLE.fieldOf("amount").forGetter(ShortAbilityAttributeModifier::amount),
                AbilityAttributeModifier.Type.CODEC.fieldOf("operation").forGetter(ShortAbilityAttributeModifier::type)
        ).apply(instance, ShortAbilityAttributeModifier::new));
    }
}
