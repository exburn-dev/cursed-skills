package com.jujutsu.api.ability.client;

import com.jujutsu.api.ability.client.render.AbilityClientRenderers;
import com.jujutsu.api.ability.core.AbilityInstance;
import com.jujutsu.registry.custom.Abilities;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.data.AbilityPropertiesContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityClientComponent {
    private static final Map<AbilityType, AbilityClientRenderers> renderers = new HashMap<>();

    private Map<AbilitySlot, AbilityInstance> abilities = new HashMap<>();
    private Map<AbilitySlot, AbilityPropertiesContainer> abilitiesData = new HashMap<>();

    private boolean isTranslucent;

    public void apply(List<AbilityInstance> data) {
        abilities.clear();
        for(AbilityInstance instanceData : data) {
            abilities.put(instanceData.slot(), instanceData);

            if(instanceData.type().equals(Abilities.SHADOW_STEP)) {
                isTranslucent = instanceData.status().isRunning();
            }
        }

        AbilityCooldownRenderer.onAbilitiesUpdated();
    }

    public void applyRuntimeData(AbilitySlot slot, AbilityPropertiesContainer container) {
        abilitiesData.put(slot, container);
    }

    public Collection<AbilityInstance> all() {
        return abilities.values();
    }

    public AbilityInstance get(AbilitySlot slot) {
        return abilities.get(slot);
    }

    public AbilityPropertiesContainer getRuntimeData(AbilitySlot slot) {
        return abilitiesData.get(slot);
    }

    public Collection<AbilitySlot> getSlots() {
        return abilities.keySet();
    }

    public boolean isRunning(AbilityType type) {
        for(AbilityInstance instance : all()) {
            if(instance.type().equals(type) && instance.status().isRunning()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTranslucent() {
        return isTranslucent;
    }

    public static AbilityClientRenderers getRenderers(AbilityType type) {
        return renderers.get(type);
    }

    public static void registerRenderers(AbilityType type, AbilityClientRenderers abilityRenderers) {
        renderers.put(type, abilityRenderers);
    }
}
