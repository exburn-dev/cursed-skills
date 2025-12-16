package com.jujutsu.systems.ability.passive;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import com.jujutsu.systems.entitydata.EntityTickingComponent;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

import java.util.ArrayList;
import java.util.List;

public class PassiveAbilityComponent implements EntityComponent, EntityTickingComponent {
    public static final Codec<List<PassiveAbility>> CODEC;

    private final PlayerEntity player;
    private List<PassiveAbility> abilities = new ArrayList<>();

    public PassiveAbilityComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void onLoaded() {
        for(PassiveAbility ability : abilities) {
            ability.onGained(player);
        }
    }

    @Override
    public void tick() {
        for(PassiveAbility ability : abilities) {
            ability.tick(player);
        }
    }

    public void addPassiveAbility(PassiveAbility ability) {
        abilities.add(ability);
        ability.onGained(player);
    }

    public void removePassiveAbilities() {
        for(PassiveAbility ability : abilities) {
            ability.onRemoved(player);
        }
        abilities.clear();
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        NbtCompound compound = new NbtCompound();
        CODEC.encode(abilities, NbtOps.INSTANCE, compound);

        nbt.put("PassiveAbilities", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound compound = nbt.getCompound("PassiveAbilities");
        var result = CODEC.parse(NbtOps.INSTANCE, compound);

        abilities.clear();
        if(result.isSuccess()) {
            abilities = result.getOrThrow();
        }
    }

    @Override
    public void sendToClient() { }

    public static PassiveAbilityComponent get(PlayerEntity player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.PASSIVE_ABILITIES);
    }

    static {
        CODEC = PassiveAbility.CODEC.listOf();
    }
}
