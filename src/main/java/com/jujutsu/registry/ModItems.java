package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.item.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item TECHNIQUE_SCROLL = registerItem("technique_scroll", new TechniqueScrollItem(new Item.Settings()));
    public static final Item UPGRADE_RESET_SCROLL = registerItem("upgrade_reset_scroll", new UpgradeResetScrollItem(new Item.Settings()));
    public static final Item REARM = registerItem("rearm", new RearmItem(new Item.Settings().maxCount(1)));

    public static final Item ALLAH_MUSIC_DISC = registerItem("allah_music_disc", new AllahMusicDiscItem(new Item.Settings().jukeboxPlayable(ModSounds.ALLAH_MUSIC_DISC_KEY).maxCount(1)));
    public static final Item SNUS_MUSIC_DISC = registerItem("snus_music_disc", new SnusMusicDiscItem(new Item.Settings().jukeboxPlayable(ModSounds.SNUS_MUSIC_DISC_KEY).maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Jujutsu.getId(name), item);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering items for "  + Jujutsu.MODID);
    }
}
