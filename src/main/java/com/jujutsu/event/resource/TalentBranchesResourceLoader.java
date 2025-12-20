package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.jujutsu.systems.talent.TalentsBranch;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalentBranchesResourceLoader extends JujutsuResourceLoader<TalentsBranch> {
    private static TalentBranchesResourceLoader INSTANCE;

    private Map<Identifier, TalentsBranch> map = new HashMap<>();

    protected TalentBranchesResourceLoader() {
        super("talents_branches", "talent_tree/branches");
    }

    @Override
    protected TalentsBranch read(JsonElement json) {
        var result = TalentsBranch.CODEC.parse(JsonOps.INSTANCE, json);

        return result.getOrThrow();
    }

    @Override
    protected void store(TalentsBranch value) {
        map.put(value.id(), value);
    }

    @Override
    public TalentsBranch get(Identifier id) {
        return map.get(id);
    }

    public static void register() {
        INSTANCE = new TalentBranchesResourceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static TalentBranchesResourceLoader getInstance() {
        return INSTANCE;
    }
}
