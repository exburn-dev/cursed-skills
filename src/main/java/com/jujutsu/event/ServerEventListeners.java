package com.jujutsu.event;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.event.server.AbilityEvents;
import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.registry.ModAbilities;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.systems.ability.attribute.AbilityAttributeComponent;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.passive.PassiveAbilityComponent;
import com.jujutsu.systems.buff.*;
import com.jujutsu.systems.buff.conditions.TimerBuffPredicate;
import com.jujutsu.systems.buff.type.AttributeBuff;
import com.jujutsu.systems.buff.type.SupersonicBuff;
import com.jujutsu.systems.entitydata.EntityComponent;
import com.jujutsu.systems.entitydata.EntityComponentContainer;
import com.jujutsu.systems.talent.TalentComponent;
import com.jujutsu.util.AttributeUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;

public class ServerEventListeners {
    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((from, to, alive) -> {
            AbilityComponent abilityComponent = AbilityComponent.get(from);
            PassiveAbilityComponent passiveAbilityComponent = PassiveAbilityComponent.get(from);
            AbilityAttributeComponent attributeComponent = AbilityAttributeComponent.get(from);
            TalentComponent talentComponent = TalentComponent.get(from);

            AbilityComponent.get(to).copyFrom(abilityComponent);
            PassiveAbilityComponent.get(to).copyFrom(passiveAbilityComponent);
            AbilityAttributeComponent.get(to).copyFrom(attributeComponent);
            TalentComponent.get(to).copyFrom(talentComponent);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if(entity.isLiving()) {
                EntityComponentContainer container = ((EntityComponentsAccessor) entity).jujutsu$getContainer();
                for (EntityComponent component : container.all()) {
                    component.onLoaded();
                }
            }
        });

        PlayerBonusEvents.GET_DAMAGE_BONUS_EVENT.register((player, entity) -> {
            float bonus = 0;
            if(player.getWorld().isClient()) return new Pair<>(ActionResult.PASS, bonus);

            PassiveAbilityComponent component = PassiveAbilityComponent.get(player);
            if(!component.hasAbilities()) return new Pair<>(ActionResult.PASS, bonus);

            Optional<SpeedPassiveAbility> optional = component.find(ModAbilities.SPEED_PASSIVE_ABILITY);
            if(optional.isEmpty()) return new Pair<>(ActionResult.PASS, bonus);

            SpeedPassiveAbility speedAbility = optional.get();

            float playerSpeed = (float) AttributeUtils.getActualSpeed(player);
            float entitySpeed =  (float) AttributeUtils.getActualSpeed(entity);

            float speedDifference = ( playerSpeed - entitySpeed ) * 25f;
            bonus += speedDifference;

            if(!speedAbility.isPunchReady()) return new Pair<>(ActionResult.PASS, bonus);

            bonus += speedAbility.getDistance() * 0.8f;

            return new Pair<>(ActionResult.PASS, bonus);
        });

        PlayerBonusEvents.GET_SPEED_BONUS_EVENT.register((player) -> {
            float bonus = 0;
            if(player.getWorld().isClient()) return new Pair<>(ActionResult.PASS, bonus);

            PassiveAbilityComponent component = PassiveAbilityComponent.get(player);
            if(!component.hasAbilities()) return new Pair<>(ActionResult.PASS, bonus);

            Optional<SpeedPassiveAbility> optional = component.find(ModAbilities.SPEED_PASSIVE_ABILITY);
            if(optional.isEmpty()) return new Pair<>(ActionResult.PASS, bonus);

            SpeedPassiveAbility speedAbility = optional.get();

            double range = 10;
            List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), range, range, range), (entity) -> entity != player);
            for(LivingEntity entity: entities) {
                float distance = player.distanceTo(entity);
                if(distance > range) continue;

                double entitySpeed = entity.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.1;
                bonus += (float) entitySpeed;

                AttributeBuff buff = new AttributeBuff(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.25, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

                Buff.createBuff(entity, buff, ImmutableList.of(new TimerBuffPredicate(20)),false,
                        Jujutsu.id("wither_momentum"));
            }

            return new Pair<>(ActionResult.PASS, bonus);
        });
        //TODO supersonic buff
//        PlayerBonusEvents.GET_SPEED_BONUS_EVENT.register((player) -> {
//            BuffHolder holder = (BuffHolder) player;
//            float bonus = 0;
//            if(!holder.getBuffs().isEmpty()) {
//                for(Buff buffWrapper: holder.getBuffs()) {
//                    IBuff buff = buffWrapper.buff();
//                    if(buff.getType().equals(BuffTypes.SUPERSONIC_BUFF_TYPE)) {
//                        double value = ((SupersonicBuff) buff).getValue(player);
//                        bonus += (float) value;
//
//                    }
//                }
//            }
//
//            return new Pair<>(ActionResult.PASS, bonus);
//        });

        PlayerBonusEvents.GET_JUMP_VELOCITY_BONUS_EVENT.register(player -> {
            float bonus = 1;
            if(player.getWorld().isClient()) return new Pair<>(ActionResult.PASS, bonus);
            PassiveAbilityComponent component = PassiveAbilityComponent.get(player);

            if(player.getAttributes().hasAttribute(ModAttributes.JUMP_VELOCITY_MULTIPLIER)) {
                bonus = (float) player.getAttributeValue(ModAttributes.JUMP_VELOCITY_MULTIPLIER);
            }

            if(!component.hasAbilities()) return new Pair<>(ActionResult.PASS, bonus);

            Optional<SpeedPassiveAbility> optional = component.find(ModAbilities.SPEED_PASSIVE_ABILITY);
            if(optional.isEmpty()) return new Pair<>(ActionResult.PASS, bonus);

            double dynamicSpeed = ((PlayerDynamicAttributesAccessor) player).getDynamicSpeed();
            double x = (player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + dynamicSpeed) /
                    player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) - 1;

            bonus += (float) Math.max(0, x * 0.325);

            return new Pair<>(ActionResult.PASS, bonus);
        });

        AbilityEvents.ON_PREVENT_DYING.register((player, attacker, amount) -> {
            if(attacker instanceof LivingEntity entity) {
                float damage = (amount - player.getHealth()) * 2;
                entity.damage(player.getDamageSources().mobAttack(player), damage);
            }

            return ActionResult.PASS;
        });
    }
}
