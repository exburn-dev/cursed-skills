package com.jujutsu.systems.ability.client;

import com.jujutsu.systems.ability.core.AbilityInstanceData;
import com.jujutsu.systems.ability.core.AbilitySlot;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AbilityClientComponent {

    private Map<AbilitySlot, AbilityInstanceData> abilities;

    public void apply(List<AbilityInstanceData> data) {
        abilities.clear();
        for(AbilityInstanceData instanceData : data) {
            abilities.put(instanceData.slot(), instanceData);
        }
    }

    public Collection<AbilityInstanceData> all() {
        return abilities.values();
    }

    public AbilityInstanceData get(AbilitySlot slot) {
        return abilities.get(slot);
    }
}
