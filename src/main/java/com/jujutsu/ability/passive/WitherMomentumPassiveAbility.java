package com.jujutsu.ability.passive;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.passive.PassiveAbilityType;
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
import net.minecraft.text.Style;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Supplier;

public class WitherMomentumPassiveAbility extends PassiveAbility {
    public static final MapCodec<WitherMomentumPassiveAbility> CODEC = MapCodec.unit(new WitherMomentumPassiveAbility());

    //Marker-ability. Realisation in ServerEventListeners GET_SPEED_BONUS event listener

    public WitherMomentumPassiveAbility() {}

    @Override
    public void tick(PlayerEntity player) {}

    @Override
    public void onGained(PlayerEntity player) {

    }

    @Override
    public void onRemoved(PlayerEntity player) {

    }

    @Override
    public Style getStyle() {
        return Style.EMPTY.withColor(0x4a385c);
    }

    @Override
    public PassiveAbilityType<?> getType() {
        return ModAbilities.WITHER_MOMENTUM;
    }
}
