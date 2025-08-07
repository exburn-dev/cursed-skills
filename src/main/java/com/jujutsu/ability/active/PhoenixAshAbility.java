package com.jujutsu.ability.active;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.util.ParticleUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Style;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Supplier;

public class PhoenixAshAbility extends AbilityType {
    public PhoenixAshAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        Vector3f color = new Vector3f(1, 0.306f, 0);
        Supplier<ParticleEffect> particle = () -> new ColoredSparkParticleEffect(4f, 0.94f, new ColoredSparkParticleEffect.ColorTransition(color, new Vector3f(1, 0, 0)),0, 0.1f, 40);
        ParticleUtils.createCyl(particle, player.getPos().add(0, 1, 0), player.getWorld(), 20, 1, 0.1f);

        Buff.createBuff(player, ModAttributes.INVINCIBLE, ImmutableList.of(new TimeCancellingCondition(60)),
                Buff.CancellingPolicy.ONE_OR_MORE, 1, EntityAttributeModifier.Operation.ADD_VALUE, Jujutsu.getId("phoenix_invincible"));
        Buff.createBuff(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, ImmutableList.of(new TimeCancellingCondition(200)),
                Buff.CancellingPolicy.ONE_OR_MORE, 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, Jujutsu.getId("phoenix_speed"));

        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 16, 16, 16), entity -> !entity.getUuid().equals(player.getUuid()));
        for(LivingEntity entity: entities) {
            double distance = entity.distanceTo(player);
            if(distance > 8) continue;

            entity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 60, 0, true, false, false));

            Buff.createBuff(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, ImmutableList.of(new TimeCancellingCondition(200)),
                    Buff.CancellingPolicy.ONE_OR_MORE, -0.25, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, Jujutsu.getId("phoenix_slowness"));
        }
    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 20;
    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0xfc4e03);
    }
}
