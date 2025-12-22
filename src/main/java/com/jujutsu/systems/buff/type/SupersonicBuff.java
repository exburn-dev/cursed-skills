package com.jujutsu.systems.buff.type;

import com.jujutsu.ability.active.SupersonicAbility;
import com.jujutsu.registry.ModAbilityAttributes;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public record SupersonicBuff(AbilitySlot slot)  {
    public static final MapCodec<SupersonicBuff> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilitySlot.CODEC.fieldOf("slot").forGetter(SupersonicBuff::slot)
    ).apply(instance, SupersonicBuff::new));
//
//    @Override
//    public double getValue(LivingEntity entity) {
//        if(!entity.isPlayer()) return 0;
//
//        IAbilitiesHolder holder = (IAbilitiesHolder) entity;
//        AbilityInstance instance = holder.getAbilityInstance(slot);
//        if(instance != null && instance.getType() instanceof SupersonicAbility ability) {
//
//            return Math.sqrt(instance.get(SupersonicAbility.DISTANCE)) *
//                    AbilitiesHolderUtils.getAbilityAttributeValue((PlayerEntity) entity, ModAbilityAttributes.SUPERSONIC_SPEED);
//        }
//
//        return 0;
//    }
//
//    @Override
//    public void apply(LivingEntity entity, Identifier buffId) {
//
//    }
//
//    @Override
//    public void remove(LivingEntity entity, Identifier buffId) {
//
//    }
//
//    @Override
//    public BuffType<?> getType() {
//        return BuffTypes.SUPERSONIC_BUFF_TYPE;
//    }
}
