package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.FierySoulPassiveAbility;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.ability.passive.WitherMomentumPassiveAbility;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.component.TechniqueComponent.ItemStackBuilder;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class ModItemGroups {
    public static final RegistryKey<ItemGroup> MAIN_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, Jujutsu.getId("jujutsu_main"));
    public static final ItemGroup MAIN_GROUP = Registry.register(Registries.ITEM_GROUP, Jujutsu.getId("jujutsu_main"), FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.jujutsu.main"))
            .icon(ModItems.TECHNIQUE_SCROLL::getDefaultStack)
            .build());

    public static void register() {
        Jujutsu.LOGGER.info("Registering item groups for " + Jujutsu.MODID);
        ItemGroupEvents.modifyEntriesEvent(MAIN_GROUP_KEY).register((itemGroup) -> {
            itemGroup.add(ModItems.REARM);

            ItemStack godjoScroll = new ItemStackBuilder(Jujutsu.getId("gojo"))
                    .addAbility(AbilitySlot.ABILITY_SLOT_1, ModAbilities.HOLLOW_PURPLE)
                    .addAbility(AbilitySlot.ABILITY_SLOT_2, ModAbilities.INFINITY)
                    .addAbility(AbilitySlot.ABILITY_SLOT_3, ModAbilities.LAPSE_BLUE)
                    .addAbility(AbilitySlot.ABILITY_SLOT_4, ModAbilities.REVERSAL_RED)
                    .build();

            itemGroup.add(godjoScroll);


            ItemStack speedScroll = new ItemStackBuilder(Jujutsu.getId("speedster"))
                    .addAbility(AbilitySlot.ABILITY_SLOT_1, ModAbilities.SHADOW_STEP)
                    .addPassiveAbility(new SpeedPassiveAbility())
                    .addPassiveAbility(new WitherMomentumPassiveAbility())
                    .build();

            itemGroup.add(speedScroll);


            ItemStack fireScroll = new ItemStackBuilder(Jujutsu.getId("phoenix"))
                    .addAbility(AbilitySlot.ABILITY_SLOT_1, ModAbilities.PHOENIX_FIREBALL)
                    .addAbility(AbilitySlot.ABILITY_SLOT_2, ModAbilities.FIERY_SOUL_SWITCH)
                    .addAbility(AbilitySlot.ABILITY_SLOT_ON_DEATH, ModAbilities.PHOENIX_ASH)
                    .addPassiveAbility(new FierySoulPassiveAbility())
                    .build();

            itemGroup.add(fireScroll);

            itemGroup.add(ModItems.UPGRADE_RESET_SCROLL);
            itemGroup.add(ModItems.ALLAH_MUSIC_DISC);
            itemGroup.add(ModItems.SNUS_MUSIC_DISC);
        });
    }
}
