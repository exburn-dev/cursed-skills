package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.data.AbilityData;
import com.jujutsu.systems.buff.BuffWrapper;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.systems.buff.type.SupersonicBuff;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class SupersonicAbility extends AbilityType {
    public static final Codec<SupersonicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("distanceOnStart").forGetter(SupersonicData::distanceOnStart),
            Codec.DOUBLE.fieldOf("distance").forGetter(SupersonicData::distance),
            Codec.INT.fieldOf("duration").forGetter(SupersonicData::duration),
            Codec.BOOL.fieldOf("crashed").forGetter(SupersonicData::crashed)
    ).apply(instance, SupersonicData::new));

    public SupersonicAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        double startDistance = player.distanceTraveled;

        int duration = 20 * (int) getAbilityAttributeValue(player, ModAbilityAttributes.SUPERSONIC_DURATION);
        instance.setAbilityData(new SupersonicData(startDistance, 0, duration, false));

        SupersonicBuff buff = new SupersonicBuff(instance.getSlot());
        BuffWrapper.createBuff(player, buff, ImmutableList.of(new TimeCancellingCondition(duration)),
                BuffWrapper.CancellingPolicy.ONE_OR_MORE, Jujutsu.getId("supersonic"));
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {
        SupersonicData data = getData(instance);
        double distance = player.distanceTraveled - data.distanceOnStart();

        data = new SupersonicData(data.distanceOnStart(), distance, data.duration(), data.crashed());
        instance.setAbilityData(data);

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

            if (hit.getType() != HitResult.Type.MISS && !data.crashed()) {
                player.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 20, 0, true, false, false));
                instance.setAbilityData(new SupersonicData(data.distanceOnStart(), data.distance(),  data.duration, true));
            }
        }
    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public AbilityAttributesContainer getDefaultAttributes() {
        return new AbilityAttributesContainer.Builder()
                .addBaseModifier(ModAbilityAttributes.SUPERSONIC_SPEED, 0.0625)
                .addBaseModifier(ModAbilityAttributes.SUPERSONIC_DURATION, 10)
                .build();
    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        SupersonicData data = getData(instance);
        return instance.getUseTime() >= data.duration();
    }

    @Override
    public Codec<? extends AbilityData> getCodec() {
        return CODEC;
    }

    public SupersonicData getData(AbilityInstance instance) {
        return instance.getAbilityData(SupersonicData.class, () -> (SupersonicData) getInitialData());
    }

    @Override
    public AbilityData getInitialData() {
        return new SupersonicData(0, 0, 0, false);
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x3869c9);
    }

    public record SupersonicData(double distanceOnStart, double distance, int duration, boolean crashed) implements AbilityData {}
}
