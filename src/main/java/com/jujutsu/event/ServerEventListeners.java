package com.jujutsu.event;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.event.server.AbilityEvents;
import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.registry.BuffTypes;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.holder.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.holder.PlayerJujutsuAbilities;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradesReloadListener;
import com.jujutsu.systems.buff.*;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.systems.buff.type.ConstantBuff;
import com.jujutsu.systems.buff.type.SupersonicBuff;
import com.jujutsu.util.AbilitiesHolderUtils;
import com.jujutsu.util.AttributeUtils;
import com.jujutsu.util.IOldPosHolder;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;

public class ServerEventListeners {
    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((from, to, alive) -> {
            PlayerJujutsuAbilities abilities = ((IPlayerJujutsuAbilitiesHolder) from).getAbilities();
            ((IPlayerJujutsuAbilitiesHolder) to).setAbilities(abilities);

            AbilityAttributesContainer attributes = ((AbilityAttributeContainerHolder) from).getAbilityAttributes();
            ((AbilityAttributeContainerHolder) to).setAbilityAttributes(attributes);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if(entity.isPlayer()) {
                IAbilitiesHolder holder = (IAbilitiesHolder) entity;
                holder.getPassiveAbilities().forEach(passiveAbility -> passiveAbility.onGained((PlayerEntity) entity));

                AbilityUpgradesReloadListener.getInstance().syncUpgrades((ServerPlayerEntity) entity);
            }
        });

        PlayerBonusEvents.GET_DAMAGE_BONUS_EVENT.register((player, entity) -> {
            IAbilitiesHolder holder = (IAbilitiesHolder) player;
            float bonus = 0;
            if(holder.getPassiveAbilities().isEmpty()) return new Pair<>(ActionResult.PASS, bonus);

            SpeedPassiveAbility speedAbility = null;
            for(PassiveAbility ability: holder.getPassiveAbilities()) {
                if(ability instanceof SpeedPassiveAbility speedPassiveAbility) {
                    speedAbility = speedPassiveAbility;
                    break;
                }
            }

            if(speedAbility == null) return new Pair<>(ActionResult.PASS, bonus);

            float playerSpeed = (float) AttributeUtils.getActualSpeed(player);
            float entitySpeed =  (float) AttributeUtils.getActualSpeed(entity);

            float speedDifference = ( playerSpeed - entitySpeed ) * 25f;
            bonus += speedDifference;

            if(!speedAbility.isPunchReady()) return new Pair<>(ActionResult.PASS, bonus);

            bonus += speedAbility.getDistance() * 0.8f;

            return new Pair<>(ActionResult.PASS, bonus);
        });

        PlayerBonusEvents.GET_SPEED_BONUS_EVENT.register((player) -> {
            IAbilitiesHolder holder = (IAbilitiesHolder) player;
            float bonus = 0;
            if(holder.getPassiveAbilities().isEmpty()) return new Pair<>(ActionResult.PASS, bonus);

            SpeedPassiveAbility speedAbility = null;
            for(PassiveAbility ability: holder.getPassiveAbilities()) {
                if(ability instanceof SpeedPassiveAbility speedPassiveAbility) {
                    speedAbility = speedPassiveAbility;
                    break;
                }
            }

            if(speedAbility == null) return new Pair<>(ActionResult.PASS, bonus);

            double range = 10;
            List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), range, range, range), (entity) -> entity != player);
            for(LivingEntity entity: entities) {
                float distance = player.distanceTo(entity);
                if(distance > range) continue;

                double entitySpeed = entity.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.1;
                bonus += (float) entitySpeed;

                ConstantBuff buff = new ConstantBuff(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.25, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

                BuffWrapper.createBuff(entity, buff, ImmutableList.of(new TimeCancellingCondition(20)), BuffWrapper.CancellingPolicy.ONE_OR_MORE,
                        Jujutsu.getId("wither_momentum"));
            }

            return new Pair<>(ActionResult.PASS, bonus);
        });

        PlayerBonusEvents.GET_SPEED_BONUS_EVENT.register((player) -> {
            BuffHolder holder = (BuffHolder) player;
            float bonus = 0;
            if(!holder.getBuffs().isEmpty()) {
                for(BuffWrapper buffWrapper: holder.getBuffs()) {
                    IBuff buff = buffWrapper.buff();
                    if(buff.getType().equals(BuffTypes.SUPERSONIC_BUFF_TYPE)) {
                        double value = ((SupersonicBuff) buff).getValue(player);
                        bonus += (float) value;

                    }
                }
            }

            return new Pair<>(ActionResult.PASS, bonus);
        });

        PlayerBonusEvents.GET_JUMP_VELOCITY_BONUS_EVENT.register(player -> {
            IAbilitiesHolder holder = (IAbilitiesHolder) player;
            float bonus = 1;

            if(player.getAttributes().hasAttribute(ModAttributes.JUMP_VELOCITY_MULTIPLIER)) {
                bonus = (float) player.getAttributeValue(ModAttributes.JUMP_VELOCITY_MULTIPLIER);
            }

            if(!holder.getPassiveAbilities().isEmpty()) {
                Optional<SpeedPassiveAbility> optional = AbilitiesHolderUtils.findPassiveAbility(holder, SpeedPassiveAbility.class);
                if(optional.isPresent()) {
                    double dynamicSpeed = ((PlayerDynamicAttributesAccessor) player).getDynamicSpeed();
                    double x = (player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + dynamicSpeed) /
                            player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) - 1;

                    bonus += (float) Math.max(0, x * 0.325);
                }
            }
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
