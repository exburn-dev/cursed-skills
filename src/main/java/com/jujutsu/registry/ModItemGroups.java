package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.FierySoulPassiveAbility;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.systems.ability.AbilitySlot;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.component.TechniqueComponent;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;

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

            ItemStack godjoScroll = ModItems.TECHNIQUE_SCROLL.getDefaultStack();
            HashMap<AbilitySlot, AbilityType> map = new HashMap<>();
            map.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.HOLLOW_PURPLE);
            map.put(AbilitySlot.ABILITY_SLOT_2, ModAbilities.INFINITY);
            map.put(AbilitySlot.ABILITY_SLOT_3, ModAbilities.LAPSE_BLUE);
            map.put(AbilitySlot.ABILITY_SLOT_4, ModAbilities.REVERSAL_RED);
            godjoScroll.set(ModDataComponents.TECHNIQUE_COMPONENT, new TechniqueComponent(map, List.of()));

            itemGroup.add(godjoScroll);

            ItemStack speedScroll = ModItems.TECHNIQUE_SCROLL.getDefaultStack();
            HashMap<AbilitySlot, AbilityType> map1 = new HashMap<>();
            //map1.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.FLASH_PUNCH);
            //map1.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.WITHER_MOMENTUM);
            map1.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.SHADOW_STEP);

            speedScroll.set(ModDataComponents.TECHNIQUE_COMPONENT, new TechniqueComponent(map1, List.of(new SpeedPassiveAbility())));
            itemGroup.add(speedScroll);

//            ItemStack blinkScroll = ModItems.TECHNIQUE_SCROLL.getDefaultStack();
//            HashMap<AbilitySlot, AbilityType> map2 = new HashMap<>();
//            //map1.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.FLASH_PUNCH);
//            //map1.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.WITHER_MOMENTUM);
//            map2.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.BLINK);
//
//            blinkScroll.set(ModDataComponents.TECHNIQUE_COMPONENT, new TechniqueComponent(map2, List.of()));
//            itemGroup.add(blinkScroll);

            ItemStack fireScroll = ModItems.TECHNIQUE_SCROLL.getDefaultStack();
            HashMap<AbilitySlot, AbilityType> map3 = new HashMap<>();
            map3.put(AbilitySlot.ABILITY_SLOT_1, ModAbilities.PHOENIX_FIREBALL);
            map3.put(AbilitySlot.ABILITY_SLOT_2, ModAbilities.FIERY_SOUL_SWITCH);
            map3.put(AbilitySlot.ABILITY_SLOT_ON_DEATH, ModAbilities.PHOENIX_ASH);

            fireScroll.set(ModDataComponents.TECHNIQUE_COMPONENT, new TechniqueComponent(map3, List.of(new FierySoulPassiveAbility())));
            itemGroup.add(fireScroll);

            //itemGroup.add(ModItems.ALLAH_MUSIC_DISC);
            //itemGroup.add(ModItems.SNUS_MUSIC_DISC);
        });
    }
}
