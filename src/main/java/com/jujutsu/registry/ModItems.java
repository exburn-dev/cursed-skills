package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.item.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item TECHNIQUE_SCROLL = registerItem("technique_scroll", new TechniqueScrollItem(new Item.Settings()));
    public static final Item UPGRADE_RESET_SCROLL = registerItem("upgrade_reset_scroll", new TalentResetScrollItem(new Item.Settings()));
    public static final Item REARM = registerItem("rearm", new RearmItem(new Item.Settings().maxCount(1)));
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Jujutsu.id(name), item);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering items for "  + Jujutsu.MODID);
    }
}
