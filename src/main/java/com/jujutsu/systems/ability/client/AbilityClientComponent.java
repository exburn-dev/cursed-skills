package com.jujutsu.systems.ability.client;

import com.jujutsu.client.hud.AbilityCooldownRenderer;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.AbilityPropertiesContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityClientComponent {

    private Map<AbilitySlot, AbilityInstanceData> abilities = new HashMap<>();
    private Map<AbilitySlot, AbilityPropertiesContainer> abilitiesData = new HashMap<>();

    private boolean isTranslucent;

    public void apply(List<AbilityInstanceData> data) {
        abilities.clear();
        for(AbilityInstanceData instanceData : data) {
            abilities.put(instanceData.slot(), instanceData);

            if(instanceData.type().equals(ModAbilities.SHADOW_STEP)) {
                isTranslucent = instanceData.status().isRunning();
            }
        }

        AbilityCooldownRenderer.onAbilitiesUpdated();
    }

    public void applyRuntimeData(AbilitySlot slot, AbilityPropertiesContainer container) {
        abilitiesData.put(slot, container);
    }

    public Collection<AbilityInstanceData> all() {
        return abilities.values();
    }

    public AbilityInstanceData get(AbilitySlot slot) {
        return abilities.get(slot);
    }

    public AbilityPropertiesContainer getRuntimeData(AbilitySlot slot) {
        return abilitiesData.get(slot);
    }

    public Collection<AbilitySlot> getSlots() {
        return abilities.keySet();
    }

    public boolean isRunning(AbilityType type) {
        for(AbilityInstanceData instance : all()) {
            if(instance.type().equals(type) && instance.status().isRunning()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTranslucent() {
        return isTranslucent;
    }
}
