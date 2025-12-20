package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.jujutsu.systems.talent.AbilityTalent;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TalentResourceLoader extends JujutsuResourceLoader<AbilityTalent> {
    private static TalentResourceLoader INSTANCE;

    private final Map<Identifier, AbilityTalent> map = new HashMap<>();

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

    public static void register() {
        INSTANCE = new TalentResourceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static TalentResourceLoader getInstance() {
        return INSTANCE;
    }
}
