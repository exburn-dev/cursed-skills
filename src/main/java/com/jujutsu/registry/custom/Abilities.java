package com.jujutsu.registry.custom;

import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.FierySoulPassiveAbility;
import com.jujutsu.ability.passive.WitherMomentumPassiveAbility;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.passive.PassiveAbility;
import com.jujutsu.ability.active.*;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.api.ability.passive.PassiveAbilityType;
import com.jujutsu.registry.JujutsuRegistries;
import net.minecraft.core.Registry;

public class Abilities {
    public static final AbilityType HOLLOW_PURPLE = registerAbilityType("hollow_purple", new HollowPurpleAbility(1000));
    public static final AbilityType INFINITY = registerAbilityType("infinity", new InfinityAbility(1000));
    public static final AbilityType LAPSE_BLUE = registerAbilityType("lapse_blue", new LapseBlueAbility(600));
    public static final AbilityType REVERSAL_RED = registerAbilityType("reversal_red", new ReversalRedAbility(600));

    public static final AbilityType SHADOW_STEP = registerAbilityType("shadow_step", new ShadowStepAbility(140));
    public static final AbilityType SONIC_RIFT = registerAbilityType("sonic_rift", new SonicRiftAbility(320));
    public static final AbilityType SUPERSONIC = registerAbilityType("supersonic", new SupersonicAbility(400));

    public static final AbilityType BLINK = registerAbilityType("blink", new BlinkAbility(600));

    public static final AbilityType PHOENIX_ASH = registerAbilityType("phoenix_ash", new PhoenixAshAbility(4000));
    public static final AbilityType PHOENIX_FIREBALL = registerAbilityType("phoenix_fireball", new PhoenixFireballAbility(300));
    public static final AbilityType FIERY_SOUL_SWITCH = registerAbilityType("fiery_soul_switch", new FierySoulSwitchAbility(20));

    public static final PassiveAbilityType<SpeedPassiveAbility> SPEED_PASSIVE_ABILITY = registerPassiveAbilityType("speed", new PassiveAbilityType<>(SpeedPassiveAbility.CODEC));
    public static final PassiveAbilityType<WitherMomentumPassiveAbility> WITHER_MOMENTUM = registerPassiveAbilityType("wither_momentum", new PassiveAbilityType<>(WitherMomentumPassiveAbility.CODEC));
    public static final PassiveAbilityType<FierySoulPassiveAbility> FIERY_SOUL = registerPassiveAbilityType("fiery_soul", new PassiveAbilityType<>(FierySoulPassiveAbility.CODEC));

    private static AbilityType registerAbilityType(String name, AbilityType abilityType) {
        return Registry.register(JujutsuRegistries.ABILITY_TYPE, Jujutsu.id(name), abilityType);
    }

    private static <T extends PassiveAbility> PassiveAbilityType<T> registerPassiveAbilityType(String name, PassiveAbilityType<T> type) {
        return Registry.register(JujutsuRegistries.PASSIVE_ABILITY_TYPE, Jujutsu.id(name), type);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering abilities for " + Jujutsu.MODID);
    }
}
