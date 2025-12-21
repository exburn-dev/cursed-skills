package com.jujutsu.systems.talent.client;

import com.jujutsu.systems.ability.upgrade.TalentsData;

public class ClientTalentContainer {
    public static TalentsData talentsData;

    public static void apply(TalentsData data) {
        talentsData = data;
    }
}
