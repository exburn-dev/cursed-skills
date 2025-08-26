package com.jujutsu.systems.ability.upgrade;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jujutsu.Jujutsu;
import com.jujutsu.network.payload.SyncAbilityUpgradesPayload;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class AbilityUpgradesReloadListener implements SimpleSynchronousResourceReloadListener {
    public static AbilityUpgradesReloadListener INSTANCE;

    private static final Codec<List<AbilityUpgradeBranch>> codec = AbilityUpgradeBranch.CODEC.listOf();
    private static final HashMap<Identifier, List<AbilityUpgradeBranch>> upgrades = new HashMap<>();

    private AbilityUpgradesReloadListener() {}

    public static void register() {
        INSTANCE = new AbilityUpgradesReloadListener();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SyncAbilityUpgradesPayload.ID, (payload, context) -> {
            upgrades.clear();
            upgrades.putAll(payload.upgrades());
        });
    }

    public static AbilityUpgradesReloadListener getInstance() {
        if(INSTANCE == null) {
            Jujutsu.LOGGER.error("Ability upgrades reload listener is not registered");
        }
        return INSTANCE;
    }

    public List<AbilityUpgradeBranch> getBranches(Identifier id) {
        return upgrades.get(id);
    }

    public List<Identifier> getBranchesIds() {
        return upgrades.keySet().stream().toList();
    }

    public void syncUpgrades(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SyncAbilityUpgradesPayload(upgrades));
    }

    @Override
    public Identifier getFabricId() {
        return Jujutsu.getId("ability_upgrades");
    }

    @Override
    public void reload(ResourceManager manager) {
        upgrades.clear();
        for(var entry: manager.findResources("ability_upgrade", path -> path.toString().endsWith(".json")).entrySet()) {
            Identifier id = entry.getKey();
            Resource resource = entry.getValue();

            try {
                JsonReader jsonReader = new JsonReader(new InputStreamReader(resource.getInputStream()));
                JsonElement jsonElement = JsonParser.parseReader(jsonReader);

                var result =  codec.decode(new Dynamic<>(JsonOps.INSTANCE, jsonElement));
                List<AbilityUpgradeBranch> branches = result.result().get().getFirst();

                Identifier key = Jujutsu.getId(id.toString().replace("jujutsu:ability_upgrade/", "").replace(".json", ""));

                upgrades.put(key, branches);
            }
            catch (Exception e) {
                Jujutsu.LOGGER.error("Failed to load ability upgrade {}", id.toString(), e);
            }
        }
        Jujutsu.LOGGER.info(upgrades.toString());
    }
}
