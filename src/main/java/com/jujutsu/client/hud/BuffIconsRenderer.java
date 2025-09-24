package com.jujutsu.client.hud;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.BuffTypes;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BuffIconsRenderer {
    private static List<BuffDisplayData> buffs = new ArrayList<>();

    private static final HashMap<RegistryEntry<EntityAttribute>, Identifier> icons = new HashMap<>();
    private static final int iconSize = 20;
    private static final int iconGap = 2;

    private static float prevDelta = 0;

    public static void setBuffs(List<BuffDisplayData> buffsList) {
        buffs = buffsList.stream().filter(buff -> icons.containsKey(buff.attribute())).collect(Collectors.toList());
    }

    public static void render(DrawContext context, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null || client.player == null) return;
        float tickDelta = counter.getTickDelta(false);

        int panelHeight = buffs.size() * iconSize + (buffs.size() - 1) * iconGap;
        int x = context.getScaledWindowWidth() - 30;
        int y = context.getScaledWindowHeight() / 2 - panelHeight / 2;

        List<BuffDisplayData> toRemove = new ArrayList<>();

        for (BuffDisplayData displayData : buffs) {
            TimeCancellingCondition condition = (TimeCancellingCondition) displayData.condition();

            Identifier icon = icons.get(displayData.attribute());

            if (condition.getType() == BuffTypes.TIME_CANCELLING_CONDITION) {
                float progress = 1 - condition.getProgress(client.player);

                ShaderUtils.renderReloadCircle(
                        context, context.getMatrices(), x - 1.5f, y - 1.5f, iconSize + 3, 1, 0.6f,
                        Identifier.of("jujutsu", "textures/gui/square.png"),
                        new Vector3f(0f, 0f, 0f)
                );

                ShaderUtils.renderReloadCircle(
                        context, context.getMatrices(), x, y, iconSize, progress, 0.8f,
                        Identifier.of("jujutsu", "textures/gui/square.png"),
                        new Vector3f(1, 0.86f, 0.373f)
                );

                context.drawGuiTexture(icon, x + 4, y + 5, 0, 12, 12);
                y += iconSize + iconGap;
            }
        }

        if(prevDelta > tickDelta) {
            for(BuffDisplayData buff: buffs) {
                TimeCancellingCondition condition = (TimeCancellingCondition) buff.condition();
                boolean ended = condition.test(client.player);

                if (ended) {
                    toRemove.add(buff);
                }
            }
        }
        prevDelta = tickDelta;

        for(BuffDisplayData displayData: toRemove) {
            buffs.remove(displayData);
        }
    }

    static {
        icons.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, Jujutsu.getId("buff/movement_speed"));
        icons.put(ModAttributes.INVINCIBLE, Jujutsu.getId("buff/invincible"));
    }
}
