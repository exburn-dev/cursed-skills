package com.jujutsu.api.talent.client;

import com.jujutsu.api.ability.upgrade.TalentsData;

public class TalentClientComponent {
    public TalentsData talentsData;

    public void apply(TalentsData data) {
        talentsData = data;
    }
}
