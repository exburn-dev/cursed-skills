package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.item.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item TECHNIQUE_SCROLL = register("technique_scroll", TechniqueScrollItem::new);
    public static final Item UPGRADE_RESET_SCROLL = register("upgrade_reset_scroll", TalentResetScrollItem::new);
    public static final Item REARM = register("rearm", (p) ->
            new RearmItem(p.stacksTo(1)));
    
    private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Jujutsu.id(name));
        T item = itemFactory.apply(new Item.Properties().setId(key));

        Registry.register(BuiltInRegistries.ITEM, key, item);

        return item;
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering items for "  + Jujutsu.MODID);
    }
}
