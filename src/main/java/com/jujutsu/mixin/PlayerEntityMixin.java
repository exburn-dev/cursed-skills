package com.jujutsu.mixin;

import com.jujutsu.event.server.AbilityEvents;
import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.systems.ability.core.AbilityComponent;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.buff.PlayerDynamicAttributesAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements AbilityAttributeContainerHolder,
        PlayerDynamicAttributesAccessor {
    @Unique
    private static final TrackedData<Float> DYNAMIC_SPEED_BONUS =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @Unique
    private static final TrackedData<Float> DYNAMIC_JUMP_BONUS =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);


    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {

    }

    @Override
    public float getDynamicSpeed() {
        return this.dataTracker.get(DYNAMIC_SPEED_BONUS);
    }

    @Override
    public void setDynamicSpeed(float value) {
        this.dataTracker.set(DYNAMIC_SPEED_BONUS, value);
    }

    @Override
    public float getDynamicJumpVelocityMultiplier() {
        return this.dataTracker.get(DYNAMIC_JUMP_BONUS);
    }

    @Override
    public void setDynamicJumpVelocityMultiplier(float value) {
        this.dataTracker.set(DYNAMIC_JUMP_BONUS, value);
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(DYNAMIC_SPEED_BONUS, 0f);
        builder.add(DYNAMIC_JUMP_BONUS, 1f);
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                .add(ModAttributes.ROTATION_RESTRICTION, 360)
                .add(ModAttributes.ROTATION_SPEED, 1)
                .add(ModAttributes.JUMP_VELOCITY_MULTIPLIER, 1));
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"))
    private double attack(PlayerEntity player, RegistryEntry registryEntry, @Local(argsOnly = true) Entity target) {
        float value = (float) player.getAttributeValue(registryEntry);
        float bonus = 0;

        if(value <= 0 || !(target instanceof LivingEntity entity)) return value;

        Pair<ActionResult, Float> result = PlayerBonusEvents.GET_DAMAGE_BONUS_EVENT.invoker().interact(player, entity);
        if(result.getLeft() != ActionResult.FAIL) {
            bonus += result.getRight();
        }

        return value + bonus;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", shift = At.Shift.AFTER), cancellable = true)
    private void applyDamage(DamageSource source, float amount, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        AbilityComponent component = AbilityComponent.get(player);

        if(player.getHealth() - amount <= 0f && component.canStartAbility(AbilitySlot.ABILITY_SLOT_ON_DEATH)) {
            Entity attacker = source.getAttacker();
            component.runAbility(AbilitySlot.ABILITY_SLOT_ON_DEATH);
            AbilityEvents.ON_PREVENT_DYING.invoker().interact(player, attacker, amount);

            ci.cancel();
        }
    }

    @Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float value = (float) player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if(player.getWorld().isClient()) {
            cir.setReturnValue(value + getDynamicSpeed());
        }
        else {
            Pair<ActionResult, Float> result = PlayerBonusEvents.GET_SPEED_BONUS_EVENT.invoker().interact(player);

            float finalValue = result.getLeft() == ActionResult.FAIL ? value : value + result.getRight();

            setDynamicSpeed(result.getRight());
            cir.setReturnValue(finalValue);
        }
    }
}
