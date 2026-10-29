package com.jujutsu.api.ability.attribute;

import com.jujutsu.api.component.EntityDataComponent;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.component.ComponentKeys;
import com.jujutsu.api.component.EntityComponent;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;

public class AbilityAttributeComponent implements EntityComponent, EntityDataComponent {

    private final Player player;
    private AbilityAttributesContainer container;

    public AbilityAttributeComponent(Player player) {
        this.player = player;
        this.container = AbilityAttributesContainer.emptyMutable();
    }

    public AbilityAttributeComponent(Player player, AbilityAttributesContainer container) {
        this.player = player;
        this.container = container;
    }

    public double getAttributeValue(Holder<AbilityAttribute> attribute) {
        double value = 0;
        double multiplier = 1;

        for(AbilityAttributeModifier modifier : container.getModifiers(attribute).values()) {
            if(modifier.type() == AbilityAttributeModifier.Type.ADD) {
                value += modifier.value();
            }
            else {
                multiplier += modifier.value();
            }
        }
        return value * multiplier;
    }

    public Map<Identifier, AbilityAttributeModifier> getModifiers(Holder<AbilityAttribute> attribute) {
        return container.getModifiers(attribute);
    }

    public void addModifier(Holder<AbilityAttribute> attribute, Identifier id, AbilityAttributeModifier modifier) {
        container.addModifier(attribute, id, modifier);
    }

    public void removeModifier(Holder<AbilityAttribute> attribute, Identifier id) {
        container.getModifiers(attribute).remove(id);
    }

    public void addAbilityDefaultAttributes(AbilityType type) {
        AbilityAttributesContainer typeAttributes = type.getDefaultAttributes();

        for(var mapEntry : typeAttributes.base().entrySet()) {
            Holder<AbilityAttribute> attribute = mapEntry.getKey();
            container.addBase(attribute, mapEntry.getValue().value());
        }
    }

    public void copyFrom(AbilityAttributeComponent component) {
        container = component.container;
        onLoaded();
    }

    @Override
    public void save(ValueOutput data) {
        data.store("AbilityAttributes", AbilityAttributesContainer.CODEC, container);
    }

    @Override
    public void read(ValueInput data) {
        var optional = data.read("AbilityAttributes", AbilityAttributesContainer.CODEC);
        if(optional.isEmpty()) return;

        container = AbilityAttributesContainer.copyFrom(optional.get());
    }

    @Override
    public void sendToClient() {

    }

    public static AbilityAttributeComponent get(Player player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.ABILITY_ATTRIBUTES);
    }
}
