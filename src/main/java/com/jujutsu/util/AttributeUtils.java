package com.jujutsu.util;

import net.minecraft.entity.LivingEntity;

public class AttributeUtils {
    public static double getActualSpeed(LivingEntity entity) {
        double speed = entity.getPos().subtract(((IOldPosHolder) entity).getOldPos()).length();
        return speed < 0.001f ? 0 : speed;
    }
}
