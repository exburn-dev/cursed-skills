package com.jujutsu.systems.talent;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class TalentsComponent implements EntityComponent {

    private final PlayerEntity player;

    private int points;
    private Identifier tree;
    private Identifier lastPurchasedBranch;

    private Set<Identifier> purchasedUpgrades = new HashSet<>();

    public TalentsComponent(PlayerEntity player) {
        this.player = player;
    }

    public TalentsComponent(PlayerEntity player, UpgradesData data) {
        this.player = player;
        this.points = data.points();
        this.tree = data.upgradesId();
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

    public static TalentsComponent get(PlayerEntity player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.TALENTS);
    }
}
