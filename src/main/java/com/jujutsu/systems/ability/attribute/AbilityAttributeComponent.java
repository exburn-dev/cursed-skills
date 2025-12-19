package com.jujutsu.systems.ability.attribute;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

import java.util.HashMap;

public class AbilityAttributeComponent implements EntityComponent {

    private final PlayerEntity player;
    private AbilityAttributesContainer container;

    public AbilityAttributeComponent(PlayerEntity player) {
        this.player = player;
        this.container = new AbilityAttributesContainer(new HashMap<>());
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        NbtCompound compound = new NbtCompound();
        AbilityAttributesContainer.CODEC.encode(container, NbtOps.INSTANCE, compound);

        nbt.put("AbilityAttributes", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound compound = nbt.getCompound("AbilityAttributes");
        var result = AbilityAttributesContainer.CODEC.parse(NbtOps.INSTANCE, compound);

        if(result.isSuccess()) {
            container = result.getOrThrow();
        }
    }

    @Override
    public void sendToClient() {

    }

    public static AbilityAttributeComponent get(PlayerEntity player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.ABILITY_ATTRIBUTES);
    }
}
