package com.jujutsu.systems.talent;

import com.jujutsu.event.resource.TalentResourceLoader;
import com.jujutsu.event.resource.TalentTreeResourceLoader;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.network.payload.talents.TalentDataSyncS2CPayload;
import com.jujutsu.systems.ability.upgrade.TalentsData;
import com.jujutsu.systems.entitydata.ComponentKeys;
import com.jujutsu.systems.entitydata.EntityComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TalentComponent implements EntityComponent {

    private final PlayerEntity player;

    private int points;
    private Identifier tree;
    private Identifier lastPurchasedBranch;

    private Map<Identifier, Identifier> purchasedTalents = new HashMap<>();

    public TalentComponent(PlayerEntity player) {
        this.player = player;
        this.tree = Identifier.of("", "");
        this.lastPurchasedBranch = Identifier.of("", "");
    }

    public TalentComponent(PlayerEntity player, TalentsData data) {
        this.player = player;
        this.points = data.points();
        this.tree = data.tree();
        this.lastPurchasedBranch = data.lastPurchasedBranch();
        this.purchasedTalents = data.purchasedUpgrades();
    }

    public void talentPurchased(Identifier branchId, AbilityTalent talent) {
        purchasedTalents.put(branchId,talent.id());
        lastPurchasedBranch = branchId;
        points -= talent.cost();
    }

    public void addPoints(int value) {
        points += value;
    }

    public void removePurchasedTalents() {
        for(Identifier purchasedTalent : purchasedTalents.values()) {
            AbilityTalent talent = TalentResourceLoader.getInstance().get(purchasedTalent);
            talent.remove(player);
        }
    }

    public void applyPurchasedTalents() {
        TalentTreeValidator validator = new TalentTreeValidator(currentTree());

        for(Identifier branchId : purchasedTalents.keySet()) {
            if(!validator.containsBranch(branchId)) continue;

            AbilityTalent talent = TalentResourceLoader.getInstance().get(purchasedTalents.get(branchId));
            talent.apply(player);
        }
    }

    public void setTree(Identifier treeId) {
        this.tree = treeId;
    }

    public Identifier currentTreeId() {
        return tree;
    }

    public int countSpentPoints() {
        int points = 0;

        for(Identifier purchasedTalent : purchasedTalents().values()) {
            AbilityTalent talent = TalentResourceLoader.getInstance().get(purchasedTalent);
            points += talent.cost();
        }

        return points;
    }

    public TalentTree currentTree() {
        return TalentTreeResourceLoader.getInstance().get(tree);
    }

    public Identifier lastPurchasedBranch() {
        return lastPurchasedBranch;
    }

    public int points() {
        return points;
    }

    public Map<Identifier, Identifier> purchasedTalents() {
        return purchasedTalents;
    }

    public TalentsData collectData() {
        return new TalentsData(tree, points, purchasedTalents, lastPurchasedBranch);
    }

    public void readData(TalentsData data) {
        this.tree = data.tree();
        this.points = data.points();
        this.purchasedTalents = data.purchasedUpgrades();
        this.lastPurchasedBranch = data.lastPurchasedBranch();
    }

    public void copyFrom(TalentComponent component) {
        points = component.points;
        tree = component.tree;
        lastPurchasedBranch = component.lastPurchasedBranch;
        purchasedTalents = component.purchasedTalents;
    }

    @Override
    public void saveToNbt(NbtCompound nbt) {
        NbtCompound compound = new NbtCompound();
        TalentsData.CODEC.encode(collectData(), NbtOps.INSTANCE, compound);

        nbt.put("Talents", compound);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound compound = nbt.getCompound("Talents");
        var result = TalentsData.CODEC.parse(NbtOps.INSTANCE, compound);

        if(result.isSuccess()) {
             readData(result.getOrThrow());
        }
    }

    @Override
    public void sendToClient() {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new TalentDataSyncS2CPayload(collectData()));
    }

    public static TalentComponent get(PlayerEntity player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.TALENTS);
    }
}
