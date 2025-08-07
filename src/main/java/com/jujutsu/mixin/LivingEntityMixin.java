package com.jujutsu.mixin;

import com.jujutsu.client.hud.BuffDisplayData;
import com.jujutsu.network.payload.SyncBuffsForDisplaying;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.registry.tag.ModDamageTypeTags;
import com.jujutsu.systems.buff.Buff;
import com.jujutsu.systems.buff.BuffHashMapStorage;
import com.jujutsu.systems.buff.IBuffHolder;
import com.jujutsu.util.IOldPosHolder;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IBuffHolder, IOldPosHolder {
    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow protected boolean jumping;
    @Unique
    private BuffHashMapStorage buffStorage = new BuffHashMapStorage(new HashMap<>());
    @Unique
    private Vec3d oldPos = Vec3d.ZERO;
    @Unique
    private Vec3d posTracker = Vec3d.ZERO;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Vec3d pos = entity.getPos();
        if(!posTracker.equals(pos)) {
            oldPos = posTracker;
        }
        posTracker = pos;

        List<Identifier> toRemove = new ArrayList<>();

        for(Map.Entry<Identifier, Buff> entry: buffStorage.buffs().entrySet()) {
            Identifier id = entry.getKey();
            Buff buff = entry.getValue();

            if(buff.checkConditions(entity)) {
                toRemove.add(id);
            }
        }

        for(int i = 0; i < toRemove.size(); i++) {
            Identifier id = toRemove.getFirst();
            removeBuff(id);
        }
    }

//    @Inject(method = "applyMovementInput", at = @At("HEAD"), cancellable = true)
//    private void applyMovementInput(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
//        if(hasStatusEffect(ModStatusEffects.STUN_EFFECT)) {
//            cir.setReturnValue(Vec3d.ZERO);
//        }
//    }

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

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtElement serializedBuffs = buffStorage.serialize(NbtOps.INSTANCE);

        nbt.put("JujutsuBuffs", serializedBuffs);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound nbtCompound = nbt.getCompound("JujutsuBuffs");

        this.buffStorage = BuffHashMapStorage.deserialize(new Dynamic<>(NbtOps.INSTANCE, nbtCompound));
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
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue()
                .add(ModAttributes.INVINCIBLE, 0)
                .add(ModAttributes.FIRE_RESISTANCE, 0)
                .add(ModAttributes.BLAST_RESISTANCE, 0));
    }

    @Override
    public Buff getBuff(Identifier id) {
        return buffStorage.buffs().get(id);
    }

    @Override
    public void addBuff(Identifier id, Buff buff, EntityAttributeModifier modifier) {
        LivingEntity entity = (LivingEntity) (Object) this;

        EntityAttributeInstance instance = entity.getAttributes().getCustomInstance(buff.getAttribute());
        instance.addTemporaryModifier(modifier);

        buffStorage.buffs().put(id, buff);

        syncBuffs(entity);
    }

    @Override
    public void removeBuff(Identifier id) {
        if(!buffStorage.buffs().containsKey(id)) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        Buff buff = buffStorage.buffs().get(id);

        EntityAttributeInstance instance = entity.getAttributes().getCustomInstance(buff.getAttribute());
        instance.removeModifier(id);

        buffStorage.buffs().remove(id);

        syncBuffs(entity);
    }

    @Unique
    private void syncBuffs(LivingEntity entity) {
        if(entity.isPlayer() && !entity.getWorld().isClient()) {
            List<BuffDisplayData> list = new ArrayList<>();
            for(Buff buff: buffStorage.buffs().values()) {
                if(buff.getCancellingPolicy() != Buff.CancellingPolicy.ONE_OR_MORE) continue;

                list.add(new BuffDisplayData(buff.getAttribute(), buff.getConditions().getFirst()));
            }

            ServerPlayNetworking.send((ServerPlayerEntity) entity, new SyncBuffsForDisplaying(list));
        }
    }

    public List<Buff> getBuffs() {
        return buffStorage.buffs().values().stream().toList();
    }

    @Override
    public Vec3d getOldPos() {
        return oldPos;
    }
}
