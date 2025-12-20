package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.jujutsu.systems.talent.TalentsTree;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TalentTreeResourceLoader extends JujutsuResourceLoader<TalentsTree> {
    private static TalentTreeResourceLoader INSTANCE;

    private Map<Identifier, TalentsTree> map = new HashMap<>();

    protected TalentTreeResourceLoader() {
        super("talent_tree", "talent_tree/trees");
    }

    @Override
    protected TalentsTree read(JsonElement json) {
        var result = TalentsTree.CODEC.parse(JsonOps.INSTANCE, json);

        return result.getOrThrow();
    }

    @Override
    protected void store(TalentsTree value) {
        map.put(value.id(), value);
    }

    @Override
    public TalentsTree get(Identifier id) {
        return map.get(id);
    }

    public static void register() {
        INSTANCE = new TalentTreeResourceLoader();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
    }
}
