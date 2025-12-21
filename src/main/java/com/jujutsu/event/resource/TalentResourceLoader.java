package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.jujutsu.network.payload.talents.TalentResourcesSyncS2CPayload;
import com.jujutsu.systems.talent.AbilityTalent;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TalentResourceLoader extends JujutsuResourceLoader<AbilityTalent> {
    private static TalentResourceLoader INSTANCE;

    private Map<Identifier, AbilityTalent> map = new HashMap<>();

    protected TalentResourceLoader() {
        super("talents", "talent_tree/talents");
    }

    @Override
    protected AbilityTalent read(JsonElement json) {
        var result = AbilityTalent.CODEC.parse(JsonOps.INSTANCE, json);

        return result.getOrThrow();
    }

    @Override
    protected void store(AbilityTalent value) {
        map.put(value.id(), value);
    }

    @Override
    public AbilityTalent get(Identifier id) {
        return map.get(id);
    }

    public Map<Identifier, AbilityTalent> getTalents() {
        return map;
    }

    public void setTalents(Map<Identifier, AbilityTalent> map) {
        this.map = map;
    }

    public static void register() {
        INSTANCE = new TalentResourceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(((server, resourceManager, success) -> {
            if(!success) return;

            PlayerLookup.all(server).forEach(player ->
                    ServerPlayNetworking.send(player, new TalentResourcesSyncS2CPayload(
                            TalentResourceLoader.getInstance().getTalents(),
                            TalentBranchesResourceLoader.getInstance().getBranches(),
                            TalentTreeResourceLoader.getInstance().getTrees()
                    ))
            );
        }));

        ServerPlayConnectionEvents.JOIN.register((networkHandler, sender, server) -> {
            ServerPlayerEntity player = networkHandler.getPlayer();

            ServerPlayNetworking.send(player, new TalentResourcesSyncS2CPayload(
                    TalentResourceLoader.getInstance().getTalents(),
                    TalentBranchesResourceLoader.getInstance().getBranches(),
                    TalentTreeResourceLoader.getInstance().getTrees()
            ));
        });
    }

    public static TalentResourceLoader getInstance() {
        return INSTANCE;
    }
}
