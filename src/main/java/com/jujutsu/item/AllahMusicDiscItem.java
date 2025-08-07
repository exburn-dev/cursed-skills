package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class AllahMusicDiscItem extends Item implements IBorderTooltipItem {
    public AllahMusicDiscItem(Settings settings) {
        super(settings);
    }

    @Override
    public TooltipBorderData getBorderData() {
        return new TooltipBorderData.Builder().addUpperCentralElement(Jujutsu.getId("tooltip/allah/upper_central_element"), 16, 16, 2).build();
    }

    @Override
    public int getOffset() {
        return 5;
    }

    @Override
    public void render(DrawContext context, int x, int y, int width, int height) {
        Identifier topTile = Jujutsu.getId("tooltip/allah/upper_tile");
        Identifier bottomTile = Jujutsu.getId("tooltip/allah/bottom_tile");

        context.drawGuiTexture(topTile, x, y, 500, width, height / 2);
        context.drawGuiTexture(bottomTile, x, y + height / 2, 500, width, height / 2);
    }
}
