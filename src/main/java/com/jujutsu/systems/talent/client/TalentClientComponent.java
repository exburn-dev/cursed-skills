package com.jujutsu.systems.talent.client;

import com.jujutsu.systems.ability.upgrade.TalentsData;

public class TalentClientComponent {
    public TalentsData talentsData;

    public void apply(TalentsData data) {
        talentsData = data;
    }
}
