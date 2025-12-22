package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.systems.ability.attribute.SimpleAbilityAttributeContainer;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.BoolAbilityProperty;
import com.jujutsu.systems.ability.data.DoubleAbilityProperty;
import com.jujutsu.systems.ability.data.IntAbilityProperty;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimerBuffPredicate;
import com.jujutsu.systems.buff.type.AttributeBuff;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class SupersonicAbility extends AbilityType {
    public static final DoubleAbilityProperty DISTANCE_ON_START = DoubleAbilityProperty.of("distanceOnStart");
    public static final DoubleAbilityProperty DISTANCE = DoubleAbilityProperty.of("distance");
    public static final IntAbilityProperty DURATION = IntAbilityProperty.of("duration");
    public static final BoolAbilityProperty CRASHED = BoolAbilityProperty.of("crashed");

    public SupersonicAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        double startDistance = player.distanceTraveled;

        int duration = 20 * (int) getAttributeValue(player, ModAbilityAttributes.SUPERSONIC_DURATION);
        setData(instance, startDistance, 0, duration, false);

        AttributeBuff buff = new AttributeBuff(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Buff.createBuff(player, buff, ImmutableList.of(new TimerBuffPredicate(duration)),
                false, Jujutsu.id("supersonic"));
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        double distance = player.distanceTraveled - instance.get(DISTANCE_ON_START);

        setData(instance, instance.get(DISTANCE_ON_START), distance, instance.get(DURATION), instance.get(CRASHED));

        Vec3d movement = player.getMovement().normalize();
        Vec3d horizontal = new Vec3d(movement.x, 0, movement.z).multiply(1.25);

        if (horizontal.lengthSquared() > 1.0E-6) {
            Vec3d start = player.getEyePos();
            Vec3d end = start.add(horizontal);

            HitResult hit = player.getWorld().raycast(new RaycastContext(
                    start,
                    end,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));

            if (hit.getType() != HitResult.Type.MISS && !instance.get(CRASHED)) {
                player.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 20, 0, true, false, false));
                setData(instance, instance.get(DISTANCE_ON_START), instance.get(DISTANCE),  instance.get(DURATION), true);
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public SimpleAbilityAttributeContainer getDefaultAttributes() {
        return SimpleAbilityAttributeContainer.builder()
                .addBaseModifier(ModAbilityAttributes.SUPERSONIC_SPEED, 0.0625)
                .addBaseModifier(ModAbilityAttributes.SUPERSONIC_DURATION, 10)
                .build();
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.useTime() >= instance.get(DURATION);
    }

    private void setData(AbilityInstance instance, double distanceOnStart, double distance, int duration, boolean crashed) {
        instance.set(DISTANCE_ON_START, distanceOnStart);
        instance.set(DISTANCE, distance);
        instance.set(DURATION, duration);
        instance.set(CRASHED, crashed);
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x3869c9);
    }
}
