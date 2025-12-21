package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.jujutsu.systems.talent.AbilityTalent;
import com.jujutsu.systems.talent.TalentBranch;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TalentBranchesResourceLoader extends JujutsuResourceLoader<TalentBranch> {
    private static TalentBranchesResourceLoader INSTANCE;

    private Map<Identifier, TalentBranch> map = new HashMap<>();

    protected TalentBranchesResourceLoader() {
        super("talents_branches", "talent_tree/branches");
    }

    @Override
    protected TalentBranch read(JsonElement json) {
        var result = TalentBranch.CODEC.parse(JsonOps.INSTANCE, json);

        return result.getOrThrow();
    }

    @Override
    protected void store(TalentBranch value) {
        map.put(value.id(), value);
    }

    @Override
    public TalentBranch get(Identifier id) {
        return map.get(id);
    }

    public Map<Identifier, TalentBranch> getBranches() {
        return map;
    }

    public void setBranches(Map<Identifier, TalentBranch> map) {
        this.map = map;
    }

    public static void register() {
        INSTANCE = new TalentBranchesResourceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static TalentBranchesResourceLoader getInstance() {
        return INSTANCE;
    }
}
