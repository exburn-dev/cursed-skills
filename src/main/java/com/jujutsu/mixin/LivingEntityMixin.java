package com.jujutsu.mixin;

import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.registry.tag.ModDamageTypeTags;
import com.jujutsu.systems.buff.PlayerDynamicAttributesAccessor;
import com.jujutsu.systems.entitydata.EntityComponentContainer;
import com.jujutsu.systems.entitydata.EntityComponentRegistry;
import com.jujutsu.util.IOldPosHolder;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityComponentsAccessor, Attackable, IOldPosHolder {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow public abstract AttributeContainer getAttributes();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Unique
    private Vec3d oldPos = Vec3d.ZERO;
    @Unique
    private Vec3d posTracker = Vec3d.ZERO;

    private EntityComponentContainer jujutsu$components = new EntityComponentContainer();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(EntityType<?> entityType, World world, CallbackInfo ci) {
        if(!getWorld().isClient()) {
            LivingEntity self = (LivingEntity) (Object) this;

            EntityComponentRegistry.attach(self);
        }
    }

    @Override
    public EntityComponentContainer jujutsu$getContainer() {
        return jujutsu$components;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Vec3d pos = entity.getPos();
        if(!posTracker.equals(pos)) {
            oldPos = posTracker;
        }
        posTracker = pos;

        jujutsu$components.tick();
    }

    @ModifyArg(method = "applyMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"), index = 1)
    private Vec3d modifyMovementInput(Vec3d par2) {
        if(hasStatusEffect(ModEffects.STUN)) {
            return new Vec3d(0, par2.y, 0);
        }
        return par2;
    }

    @Inject(method = "getJumpVelocity()F", at = @At("HEAD"), cancellable = true)
    private void getJumpVelocity(CallbackInfoReturnable<Float> cir) {
        if(hasStatusEffect(ModEffects.STUN)) {
            cir.setReturnValue(0f);
        }
    }

    @ModifyArgs(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    private void modifyJumpVelocity(Args args) {
        if(!this.isPlayer() || !getAttributes().hasAttribute(ModAttributes.JUMP_VELOCITY_MULTIPLIER)) return;
        Pair<ActionResult, Float> result = PlayerBonusEvents.GET_JUMP_VELOCITY_BONUS_EVENT.invoker().interact((PlayerEntity) (Object) this);
        double multiplier = 1;

        if(result.getLeft() != ActionResult.FAIL) {
            multiplier = ((PlayerDynamicAttributesAccessor) this).getDynamicJumpVelocityMultiplier();
        }

        double baseX = args.get(0);
        double baseZ = args.get(2);

        args.set(0, baseX * multiplier);
        args.set(2, baseZ * multiplier);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound componentsNbt = jujutsu$components.writeNbt();
        nbt.put("Components", componentsNbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readNBT(NbtCompound nbt, CallbackInfo ci) {
        jujutsu$components.readNbt(nbt.getCompound("Components"));
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity.getAttributes().hasAttribute(ModAttributes.INVINCIBLE) && entity.getAttributes().getValue(ModAttributes.INVINCIBLE) > 0) {
            double value = entity.getAttributeValue(ModAttributes.INVINCIBLE);
            if(value == 1 || !source.isIn(ModDamageTypeTags.BYPASSES_INVINCIBILITY)) {
                cir.setReturnValue(false);
            }
        }
        if((source.isIn(DamageTypeTags.IS_FIRE) && entity.getAttributes().hasAttribute(ModAttributes.FIRE_RESISTANCE) && entity.getAttributes().getValue(ModAttributes.FIRE_RESISTANCE) >= 1)
                || (source.isIn(DamageTypeTags.IS_EXPLOSION) && entity.getAttributes().hasAttribute(ModAttributes.BLAST_RESISTANCE) && entity.getAttributes().getValue(ModAttributes.BLAST_RESISTANCE) > 1)) {
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    private void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(source.isIn(DamageTypeTags.IS_FIRE) && entity.getAttributes().hasAttribute(ModAttributes.FIRE_RESISTANCE)) {
            cir.setReturnValue((float) (amount - amount * entity.getAttributes().getValue(ModAttributes.FIRE_RESISTANCE)));
        }
        else if(source.isIn(DamageTypeTags.IS_EXPLOSION) && entity.getAttributes().hasAttribute(ModAttributes.BLAST_RESISTANCE)) {
            cir.setReturnValue((float) (amount - amount * entity.getAttributes().getValue(ModAttributes.BLAST_RESISTANCE)));
        }

        if(source.isIn(DamageTypeTags.IS_FIRE) && entity.hasStatusEffect(ModEffects.INCINERATION)) {
            cir.setReturnValue(amount * (1.5f + 0.5f * entity.getStatusEffect(ModEffects.INCINERATION).getAmplifier()));
        }
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                .add(ModAttributes.INVINCIBLE, 0)
                .add(ModAttributes.FIRE_RESISTANCE, 0)
                .add(ModAttributes.BLAST_RESISTANCE, 0));
    }

    @Override
    public Vec3d getOldPos() {
        return oldPos;
    }
}
