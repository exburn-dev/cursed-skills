package com.jujutsu.systems.ability.attribute;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AbilityAttributeComponent implements EntityComponent {

    private final PlayerEntity player;
    private AbilityAttributesContainer container;

    public AbilityAttributeComponent(PlayerEntity player) {
        this.player = player;
        this.container = new AbilityAttributesContainer(new HashMap<>());
    }

    public AbilityAttributeComponent(PlayerEntity player, AbilityAttributesContainer container) {
        this.player = player;
        this.container = container;
    }

    public double getAttributeValue(RegistryEntry<AbilityAttribute> attribute) {
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

    public Map<Identifier, AbilityAttributeModifier> getModifiers(RegistryEntry<AbilityAttribute> attribute) {
        return container.getModifiers(attribute);
    }

    public void addModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id, AbilityAttributeModifier modifier) {
        container.addModifier(attribute, id, modifier);
    }

    public void removeModifier(RegistryEntry<AbilityAttribute> attribute, Identifier id) {
        container.getModifiers(attribute).remove(id);
    }

    public void addAbilityDefaultAttributes(AbilityType type) {
        SimpleAbilityAttributeContainer typeAttributes = type.getDefaultAttributes();

        for(var mapEntry : typeAttributes.map().entrySet()) {
            RegistryEntry<AbilityAttribute> attribute = mapEntry.getKey();
            Identifier id = mapEntry.getValue().id();
            AbilityAttributeModifier modifier = new AbilityAttributeModifier(mapEntry.getValue().amount(), mapEntry.getValue().type());

            container.addModifier(attribute, id, modifier);
        }
    }

    public void copyFrom(AbilityAttributeComponent component) {
        container = component.container;
        onLoaded();
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        NbtCompound compound = new NbtCompound();
        var result = AbilityAttributesContainer.CODEC.encode(container, NbtOps.INSTANCE, compound);
        if(result.isSuccess()) {
            compound = (NbtCompound) result.getOrThrow();
        }

        nbt.put("AbilityAttributes", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound compound = nbt.getCompound("AbilityAttributes");
        var result = AbilityAttributesContainer.CODEC.parse(NbtOps.INSTANCE, compound);

        if(result.isSuccess()) {
            container = AbilityAttributesContainer.copyFrom(result.getOrThrow());
        }
    }

    @Override
    public void sendToClient() {

    }

    public static AbilityAttributeComponent get(PlayerEntity player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.ABILITY_ATTRIBUTES);
    }
}
