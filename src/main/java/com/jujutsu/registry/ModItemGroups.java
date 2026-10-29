package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.FierySoulPassiveAbility;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.item.TechniqueScrollItem;
import com.jujutsu.registry.custom.Abilities;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final ResourceKey<CreativeModeTab> MAIN_GROUP_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Jujutsu.id("jujutsu_main"));
    public static final CreativeModeTab MAIN_GROUP = register(MAIN_GROUP_KEY, FabricItemGroup.builder()
            .icon(ModItems.TECHNIQUE_SCROLL::getDefaultInstance)
            .title(Component.translatable("itemGroup.jujutsu.main"))
    );

    private static CreativeModeTab register(ResourceKey<CreativeModeTab> key, CreativeModeTab.Builder builder) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, builder.build());
    }

    private static void subscribeMainGroup() {
        ItemGroupEvents.modifyEntriesEvent(MAIN_GROUP_KEY).register((builder) -> {
            builder.accept(ModItems.REARM);

            ItemStack godjoScroll = TechniqueScrollItem.createStack(Jujutsu.id("gojo"))
                    .addAbility(AbilitySlot.ABILITY_SLOT_1, Abilities.HOLLOW_PURPLE)
                    .addAbility(AbilitySlot.ABILITY_SLOT_2, Abilities.INFINITY)
                    .addAbility(AbilitySlot.ABILITY_SLOT_3, Abilities.LAPSE_BLUE)
                    .addAbility(AbilitySlot.ABILITY_SLOT_4, Abilities.REVERSAL_RED)
                    .build();

            builder.accept(godjoScroll);


            ItemStack speedScroll = TechniqueScrollItem.createStack(Jujutsu.id("speedster"))
                    .addAbility(AbilitySlot.ABILITY_SLOT_1, Abilities.SHADOW_STEP)
                    .addAbility(AbilitySlot.ABILITY_SLOT_2, Abilities.SONIC_RIFT)
                    .addAbility(AbilitySlot.ABILITY_SLOT_3, Abilities.SUPERSONIC)
                    .addPassiveAbility(new SpeedPassiveAbility())
                    .build();

            builder.accept(speedScroll);


            ItemStack fireScroll = TechniqueScrollItem.createStack(Jujutsu.id("phoenix"))
                    .addAbility(AbilitySlot.ABILITY_SLOT_1, Abilities.PHOENIX_FIREBALL)
                    .addAbility(AbilitySlot.ABILITY_SLOT_2, Abilities.FIERY_SOUL_SWITCH)
                    .addAbility(AbilitySlot.ABILITY_SLOT_ON_DEATH, Abilities.PHOENIX_ASH)
                    .addPassiveAbility(new FierySoulPassiveAbility())
                    .build();

            builder.accept(fireScroll);

            builder.accept(ModItems.UPGRADE_RESET_SCROLL);
        });
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering item groups for " + Jujutsu.MODID);

        subscribeMainGroup();
    }
}
