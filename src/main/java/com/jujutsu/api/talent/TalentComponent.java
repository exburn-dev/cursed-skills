package com.jujutsu.api.talent;

import com.jujutsu.api.component.EntityDataComponent;
import com.jujutsu.event.resource.TalentResourceLoader;
import com.jujutsu.event.resource.TalentTreeResourceLoader;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.network.payload.talents.TalentDataSyncS2CPayload;
import com.jujutsu.api.ability.upgrade.TalentsData;
import com.jujutsu.api.component.ComponentKeys;
import com.jujutsu.api.component.EntityComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashMap;
import java.util.Map;

public class TalentComponent implements EntityComponent, EntityDataComponent {

    private final Player player;

    private int points;
    private Identifier tree;
    private Identifier lastPurchasedBranch;

    private Map<Identifier, Identifier> purchasedTalents = new HashMap<>();

    public TalentComponent(Player player) {
        this.player = player;
        this.tree = null;
        this.lastPurchasedBranch = null;
    }

    public TalentComponent(Player player, TalentsData data) {
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
        sendToClient();
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
        sendToClient();
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
        this.purchasedTalents = new HashMap<>(data.purchasedUpgrades());
        this.lastPurchasedBranch = data.lastPurchasedBranch();
    }

    public void copyFrom(TalentComponent component) {
        points = component.points;
        tree = component.tree;
        lastPurchasedBranch = component.lastPurchasedBranch;
        purchasedTalents = component.purchasedTalents;
    }

    @Override
    public void save(ValueOutput data) {
        data.store("Talents", TalentsData.CODEC, collectData());
    }

    @Override
    public void read(ValueInput data) {
        var result = data.read("Talents", TalentsData.CODEC);
        if(result.isEmpty()) return;

        readData(result.get());
    }

    @Override
    public void sendToClient() {
        ServerPlayNetworking.send((ServerPlayer) player, new TalentDataSyncS2CPayload(collectData()));
    }

    public static TalentComponent get(Player player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.TALENTS);
    }
}
