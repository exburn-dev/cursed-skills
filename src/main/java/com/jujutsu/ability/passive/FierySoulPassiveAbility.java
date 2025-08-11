package com.jujutsu.ability.passive;

import com.jujutsu.Jujutsu;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.passive.PassiveAbilityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.util.List;

public class FierySoulPassiveAbility extends PassiveAbility {
    public static final MapCodec<FierySoulPassiveAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("enabled").forGetter(FierySoulPassiveAbility::isEnabled)).apply(instance, FierySoulPassiveAbility::new));

    private boolean enabled;

    public FierySoulPassiveAbility() {
        this(false);
    }

    public FierySoulPassiveAbility(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onGained(PlayerEntity player) {
        addAttributes(
                player,
                new Pair<>(
                        ModAttributes.FIRE_RESISTANCE,
                        new EntityAttributeModifier(Jujutsu.getId("fiery_soul"), 1, EntityAttributeModifier.Operation.ADD_VALUE)
                ),
                new Pair<>(
                        ModAttributes.BLAST_RESISTANCE,
                        new EntityAttributeModifier(Jujutsu.getId("fiery_soul"), 1, EntityAttributeModifier.Operation.ADD_VALUE)
                )
        );
    }

    @Override
    public void tick(PlayerEntity player) {
        super.tick(player);
        if(!enabled) return;

        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 3, 3, 3), entity -> !entity.getUuid().equals(player.getUuid()));
        for(LivingEntity entity: entities) {
            if(entity.distanceTo(player) > 3) continue;

            entity.setOnFireFor(3);
        }
    }

    @Override
    public void onRemoved(PlayerEntity player) {
        EntityAttributeInstance instance = player.getAttributes().getCustomInstance(ModAttributes.FIRE_RESISTANCE);
        instance.removeModifier(Jujutsu.getId("fiery_soul"));
        EntityAttributeInstance instance1 = player.getAttributes().getCustomInstance(ModAttributes.BLAST_RESISTANCE);
        instance1.removeModifier(Jujutsu.getId("fiery_soul"));
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public PassiveAbilityType<?> getType() {
        return ModAbilities.FIERY_SOUL;
    }
}
