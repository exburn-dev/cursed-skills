package com.jujutsu.event.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jujutsu.Jujutsu;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;

public abstract class JujutsuResourceLoader<T> implements SimpleSynchronousResourceReloadListener {
    private final String id;
    private final String resourcesPath;

    protected JujutsuResourceLoader(String id, String resourcesPath) {
        this.id = id;
        this.resourcesPath = resourcesPath;
    }

    @Override
    public Identifier getFabricId() {
        return Jujutsu.id(id);
    }

    protected abstract T read(JsonElement json);
    protected abstract void store(T value);
    public abstract T get(Identifier id);

    @Override
    public void reload(ResourceManager manager) {
        for(var entry: manager.findResources(resourcesPath, path -> path.toString().endsWith(".json")).entrySet()) {
            Identifier id = entry.getKey();
            Resource resource = entry.getValue();

            try {
                JsonReader jsonReader = new JsonReader(new InputStreamReader(resource.getInputStream()));
                JsonElement jsonElement = JsonParser.parseReader(jsonReader);

                T value = read(jsonElement);

                store(value);
            }
            catch (Exception e) {
                Jujutsu.LOGGER.error("Failed to load resource {}", id.toString(), e);
            }
        }
    }
}
