package com.jujutsu.systems.ability.upgrade;

import com.jujutsu.systems.entitydata.EntityComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class AbilityUpgradesComponent implements EntityComponent {

    private final PlayerEntity player;

    private int points;
    private Identifier upgradesId;

    private Set<Identifier> purchasedUpgrades = new HashSet<>();

    public AbilityUpgradesComponent(PlayerEntity player) {
        this.player = player;
    }

    public AbilityUpgradesComponent(PlayerEntity player, UpgradesData data) {
        this.player = player;
        this.points = data.points();
        this.upgradesId = data.upgradesId();

    }

    @Override
    public void saveToNbt(NbtCompound nbt) {

    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public void sendToClient() {

    }
}
