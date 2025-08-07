package com.jujutsu.ability.passive;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jujutsu.Jujutsu;
import com.jujutsu.network.payload.PlayClientSoundPayload;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.systems.ability.PassiveAbility;
import com.jujutsu.systems.ability.PassiveAbilityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;

public class SpeedPassiveAbility extends PassiveAbility {
    public static final MapCodec<SpeedPassiveAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("punchCooldown").forGetter(SpeedPassiveAbility::getPunchCooldown),
            Codec.BOOL.fieldOf("punchReady").forGetter(SpeedPassiveAbility::isPunchReady),
            Codec.INT.fieldOf("tick").forGetter(SpeedPassiveAbility::getTick),
            Codec.INT.fieldOf("lastAttackTime").forGetter(SpeedPassiveAbility::getLastAttackTime),
            Codec.FLOAT.fieldOf("distanceOld").forGetter(SpeedPassiveAbility::getDistanceOld),
            Codec.FLOAT.fieldOf("distance").forGetter(SpeedPassiveAbility::getDistance))
            .apply(instance, SpeedPassiveAbility::new)
    );

    private int punchCooldown;
    private boolean punchReady;
    private int tick;
    private int lastAttackTime;
    private float distanceOld;
    private float distance;

    public SpeedPassiveAbility() {}

    public SpeedPassiveAbility(int punchCooldown, boolean punchReady, int tick, int lastAttackTime, float distanceOld, float distance) {
        this.punchCooldown = punchCooldown;
        this.punchReady = punchReady;
        this.tick = tick;
        this.lastAttackTime = lastAttackTime;
        this.distanceOld = distanceOld;
        this.distance = distance;
    }

    @Override
    public void onGained(PlayerEntity player) {
        addAttributes(
                player,
                new Pair<>(
                        EntityAttributes.GENERIC_MOVEMENT_SPEED,
                        new EntityAttributeModifier(Jujutsu.getId("passive_speed"), 0.25, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                ),
                new Pair<>(
                        EntityAttributes.GENERIC_STEP_HEIGHT,
                        new EntityAttributeModifier(Jujutsu.getId("passive_step"), 0.4, EntityAttributeModifier.Operation.ADD_VALUE)
                )
        );
    }

    @Override
    public void tick(PlayerEntity player) {
        if(punchCooldown <= 0 && !punchReady) {
            punchReady = true;
            lastAttackTime = player.getLastAttackTime();

            if(!player.getWorld().isClient()) {
                ServerPlayNetworking.send((ServerPlayerEntity) player, new PlayClientSoundPayload(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP));
            }
        }
        else if (punchReady && lastAttackTime != player.getLastAttackTime()) {
            punchReady = false;
            punchCooldown = 160;
        }

        if(tick >= 40) {
            tick = 0;
            distance = player.distanceTraveled - distanceOld;
            distanceOld = player.distanceTraveled;
        }

        if (punchCooldown > 0) {
            punchCooldown--;
        }

        tick++;
    }

    @Override
    public void onRemoved(PlayerEntity player) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        instance.removeModifier(Jujutsu.getId("passive_speed"));
        EntityAttributeInstance instance1 = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
        instance1.removeModifier(Jujutsu.getId("passive_step"));
    }

    @Override
    public PassiveAbilityType<?> getType() {
        return ModAbilities.SPEED_PASSIVE_ABILITY;
    }

    public int getPunchCooldown() {
        return punchCooldown;
    }

    public boolean isPunchReady() {
        return punchReady;
    }

    public int getTick() {
        return tick;
    }

    public int getLastAttackTime() {
        return lastAttackTime;
    }

    public float getDistanceOld() {
        return distanceOld;
    }

    public float getDistance() {
        return distance;
    }

}
