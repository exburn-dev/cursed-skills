package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import net.minecraft.item.Item;

public class SnusMusicDiscItem extends Item implements IBorderTooltipItem {
    public SnusMusicDiscItem(Settings settings) {
        super(settings);
    }

    @Override
    public TooltipBorderData getBorderData() {
        return new TooltipBorderData.Builder()
                .addMainBorder(Jujutsu.getId("tooltip/snus/main"))
                .addUpperCentralElement(Jujutsu.getId("tooltip/snus/upper_central_element"), 16, 16, 2)
                .build();
    }

    @Override
    public int getOffset() {
        return 5;
    }
}
