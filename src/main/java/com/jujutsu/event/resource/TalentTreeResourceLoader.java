package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.jujutsu.systems.talent.AbilityTalent;
import com.jujutsu.systems.talent.TalentTree;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TalentTreeResourceLoader extends JujutsuResourceLoader<TalentTree> {
    private static TalentTreeResourceLoader INSTANCE;

    private Map<Identifier, TalentTree> map = new HashMap<>();

    protected TalentTreeResourceLoader() {
        super("talent_tree", "talent_tree/trees");
    }

    @Override
    protected TalentTree read(JsonElement json) {
        var result = TalentTree.CODEC.parse(JsonOps.INSTANCE, json);

        return result.getOrThrow();
    }

    @Override
    protected void store(TalentTree value) {
        map.put(value.id(), value);
    }

    @Override
    public TalentTree get(Identifier id) {
        return map.get(id);
    }

    public Map<Identifier, TalentTree> getTrees() {
        return map;
    }

    public void setTrees(Map<Identifier, TalentTree> map) {
        this.map = map;
    }

    public static void register() {
        INSTANCE = new TalentTreeResourceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static TalentTreeResourceLoader getInstance() {
        return INSTANCE;
    }
}
