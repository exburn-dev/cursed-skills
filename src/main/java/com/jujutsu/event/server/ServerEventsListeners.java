package com.jujutsu.event.server;

import com.google.common.collect.ImmutableList;
import com.jujutsu.Jujutsu;
import com.jujutsu.ability.passive.SpeedPassiveAbility;
import com.jujutsu.systems.ability.IAbilitiesHolder;
import com.jujutsu.systems.ability.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.ability.PassiveAbility;
import com.jujutsu.systems.ability.PlayerJujutsuAbilities;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.conditions.TimeCancellingCondition;
import com.jujutsu.util.IOldPosHolder;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;

public class ServerEventsListeners {
    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((from, to, alive) -> {
            PlayerJujutsuAbilities abilities = ((IPlayerJujutsuAbilitiesHolder) from).getAbilities();
            ((IPlayerJujutsuAbilitiesHolder) to).setAbilities(abilities);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if(entity.isPlayer()) {
                IAbilitiesHolder holder = (IAbilitiesHolder) entity;
                holder.getPassiveAbilities().forEach(passiveAbility -> passiveAbility.onGained((PlayerEntity) entity));
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

            float playerSpeed = (float) player.getPos().subtract(((IOldPosHolder) player).getOldPos()).length();
            playerSpeed = playerSpeed < 0.01f ? 0 : playerSpeed;
            float entitySpeed =  (float) entity.getPos().subtract(((IOldPosHolder) entity).getOldPos()).length();
            entitySpeed = entitySpeed < 0.01f ? 0 : entitySpeed;

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

                Buff.createBuff(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, ImmutableList.of(new TimeCancellingCondition(20)), Buff.CancellingPolicy.ONE_OR_MORE, -0.25,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, Jujutsu.getId("wither_momentum"));
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
