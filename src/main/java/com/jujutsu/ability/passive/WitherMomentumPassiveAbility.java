package com.jujutsu.ability.passive;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.systems.ability.PassiveAbility;
import com.jujutsu.systems.ability.PassiveAbilityType;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.util.ParticleUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Supplier;

public class WitherMomentumPassiveAbility extends PassiveAbility {
    public static final MapCodec<WitherMomentumPassiveAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.INT.fieldOf("tick").forGetter(WitherMomentumPassiveAbility::getTick)).apply(instance, WitherMomentumPassiveAbility::new));

    private int tick = 0;

    public WitherMomentumPassiveAbility() {}

    public WitherMomentumPassiveAbility(int tick) {
        this.tick = tick;
    }


    @Override
    public void tick(PlayerEntity player) {
        if(tick % 20 != 0) return;

        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, getBox(player.getPos(), 7), (entity) -> entity != player);
        double totalSpeed = 0;
        for(LivingEntity entity: entities) {
            float distance = player.distanceTo(entity);
            if(distance > 7) continue;

            double entitySpeed = entity.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.1;
            totalSpeed += entitySpeed;

            Buff.createBuff(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, ImmutableList.of(new TimeCancellingCondition(20)), Buff.CancellingPolicy.ONE_OR_MORE, -0.25,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, Jujutsu.getId("wither_momentum"));

            Supplier<ParticleEffect> particle = () -> new ColoredSparkParticleEffect(5, 0.95f,
                    new ColoredSparkParticleEffect.ColorTransition(new Vector3f(0.9f, 0.9f, 0.9f), new Vector3f(0.9f, 0.9f, 0.9f)), 0, 0.1f, 50);
            ParticleUtils.createBall(particle, entity.getPos().add(0, entity.getHeight() / 2, 0), entity.getWorld(), 15, 0.75f, 0.1f);

            player.getWorld().playSound(entity, entity.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.MASTER, 1, 1.75f);
        }

        Buff.createBuff(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, ImmutableList.of(new TimeCancellingCondition(20)), Buff.CancellingPolicy.ONE_OR_MORE,
                totalSpeed, EntityAttributeModifier.Operation.ADD_VALUE, Jujutsu.getId("wither_momentum_owner"));
    }

    private Box getBox(Vec3d pos, float range) {
        return Box.of(pos, range, range, range);
    }

    @Override
    public void onGained(PlayerEntity player) {

    }

    @Override
    public void onRemoved(PlayerEntity player) {

    }

    public int getTick() {
        return tick;
    }

    @Override
    public PassiveAbilityType<?> getType() {
        return ModAbilities.WITHER_MOMENTUM;
    }
}
