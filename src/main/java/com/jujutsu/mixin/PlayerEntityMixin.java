package com.jujutsu.mixin;

import com.jujutsu.Jujutsu;
import com.jujutsu.event.server.AbilityEvents;
import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.network.payload.SyncAbilityAttributesPayload;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.systems.ability.*;
import com.jujutsu.network.payload.SyncPlayerAbilitiesPayload;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributeModifier;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.holder.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.ability.holder.PlayerJujutsuAbilities;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IAbilitiesHolder, IPlayerJujutsuAbilitiesHolder, AbilityAttributeContainerHolder {
    @Unique
    private PlayerJujutsuAbilities abilities = new PlayerJujutsuAbilities(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
    @Unique
    private AbilityAttributesContainer abilityAttributes = new AbilityAttributesContainer(new HashMap<>());
    @Unique
    private Identifier abilityUpgradesId = Jujutsu.getId("");

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        List<AbilitySlot> toRemove = new ArrayList<>();
        for(AbilitySlot slot: abilities.runningAbilities()) {
            AbilityInstance instance = abilities.abilities().get(slot);

            instance.tick(player);

            if((instance.getStatus().isRunning() && instance.isFinished(player)) || instance.getStatus().isCancelled()) {
                instance.endAbility(player);
            }
            else if(instance.getStatus().onCooldown() && instance.getCooldownTime() <= 0) {
                toRemove.add(slot);
                instance.endCooldown();
                jujutsu$syncAbilitiesToClient();
            }
        }
        abilities.runningAbilities().removeAll(toRemove);
        for(PassiveAbility passiveAbility: abilities.passiveAbilities()) {
            passiveAbility.tick(player);
        }
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue().add(ModAttributes.ROTATION_RESTRICTION, 360).add(ModAttributes.ROTATION_SPEED, 1));
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
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity.isPlayer()) {
            IAbilitiesHolder abilitiesHolder = (IAbilitiesHolder) entity;
            if (abilitiesHolder.getAbilityInstance(AbilitySlot.ABILITY_SLOT_ON_DEATH) != null && !onCooldown(AbilitySlot.ABILITY_SLOT_ON_DEATH)) {
                if (entity.getHealth() - amount <= 0f) {
                    Entity attacker = source.getAttacker();

                    AbilitySlot.ABILITY_SLOT_ON_DEATH.activate(abilitiesHolder);
                    AbilityEvents.ON_PREVENT_DYING.invoker().interact((PlayerEntity) entity, attacker, amount);

                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float value = (float) player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        Pair<ActionResult, Float> result = PlayerBonusEvents.GET_SPEED_BONUS_EVENT.invoker().interact(player);

        cir.setReturnValue(result.getLeft() == ActionResult.FAIL ? value : value + result.getRight());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound main = new NbtCompound();
        NbtElement serializedAbilities = abilities.serialize(NbtOps.INSTANCE);
        NbtElement serializedAttributes = abilityAttributes.serialize(NbtOps.INSTANCE);

        main.putString("UpgradesId", abilityUpgradesId.toString());
        main.put("Abilities", serializedAbilities);
        main.put("Attributes", serializedAttributes);

        nbt.put("Jujutsu", main);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound main = nbt.getCompound("Jujutsu");

        NbtCompound abilitiesCompound = main.getCompound("Abilities");
        NbtCompound attributesCompound = main.getCompound("Attributes");
        Identifier upgradesId = Identifier.tryParse(main.getString("UpgradesId"));

        this.abilities = PlayerJujutsuAbilities.deserialize(new Dynamic<>(NbtOps.INSTANCE, abilitiesCompound));
        this.abilityAttributes = AbilityAttributesContainer.deserialize(new Dynamic<>(NbtOps.INSTANCE, attributesCompound));
        this.abilityUpgradesId = upgradesId;
    }

    @Override
    public AbilityInstance getAbilityInstance(AbilitySlot slot) {
        return abilities.abilities().get(slot);
    }

    @Override
    public void addAbilityInstance(AbilityInstance instance, AbilitySlot slot) {
        abilities.abilities().put(slot, instance);
        instance.addDefaultAttributes((PlayerEntity) (Object) this);
        jujutsu$syncAbilitiesToClient();
    }

    @Override
    public void removeAbilityInstance(AbilitySlot slot) {
        abilities.abilities().remove(slot);
        abilities.runningAbilities().remove(slot);
    }

    @Override
    public void runAbility(AbilitySlot slot) {
        if(!abilities.abilities().containsKey(slot) || abilities.runningAbilities().contains(slot) || this.hasStatusEffect(ModEffects.STUN)) return;
        PlayerEntity player = (PlayerEntity) (Object) this;

        abilities.runningAbilities().add(slot);
        getAbilityInstance(slot).start(player);

        jujutsu$syncAbilitiesToClient();
    }

    @Override
    public void tryCancelAbility(AbilitySlot slot) {
        if(!abilities.abilities().containsKey(slot) || !abilities.runningAbilities().contains(slot)) return;

        AbilityInstance instance = abilities.abilities().get(slot);
        if(instance.getType().isCancelable()) {
            instance.cancel();
            jujutsu$syncAbilitiesToClient();
        }
    }

    @Override
    public List<AbilitySlot> getSlots() {
        return abilities.abilities().keySet().stream().toList();
    }

    @Override
    public List<AbilitySlot> getRunningSlots() {
        return abilities.runningAbilities();
    }

    @Override
    public PlayerJujutsuAbilities getAbilities() {
        return abilities;
    }

    @Override
    public void setAbilities(PlayerJujutsuAbilities abilities) {
        this.abilities = abilities;
    }

    @Override
    public boolean isRunning(AbilityType type) {
        for(int i = 0; i < abilities.runningAbilities().size(); i++) {
            AbilityInstance instance = abilities.abilities().get(abilities.runningAbilities().get(i));
            if(instance.getType() == type && instance.getStatus().isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCooldown(AbilitySlot slot) {
        AbilityInstance instance = abilities.abilities().get(slot);
        return instance.getStatus().onCooldown();
    }

    @Override
    public void addPassiveAbility(PassiveAbility instance) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        abilities.passiveAbilities().add(instance);
        instance.onGained(player);
    }

    @Override
    public void removePassiveAbility(PassiveAbility instance) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        abilities.passiveAbilities().remove(instance);
        instance.onRemoved(player);
    }

    @Override
    public List<PassiveAbility> getPassiveAbilities() {
        return abilities.passiveAbilities();
    }

    @Override
    public HashMap<Identifier, AbilityAttributeModifier> getModifiers(AbilityAttribute attribute) {
        return abilityAttributes.attributes().get(attribute);
    }

    @Override
    public void addModifier(AbilityAttribute attribute, Identifier id, AbilityAttributeModifier modifier) {
        HashMap<Identifier, AbilityAttributeModifier> modifiers = getModifiers(attribute);
        if(modifiers != null) {
            modifiers.put(id, modifier);
            abilityAttributes.attributes().put(attribute, modifiers);
        }
        else {
            HashMap<Identifier, AbilityAttributeModifier> map = new HashMap<>();
            map.put(id, modifier);
            abilityAttributes.attributes().put(attribute, map);
        }
        jujutsu$syncAbilityAttributesToClient();
    }

    @Override
    public void setAbilityAttributes(AbilityAttributesContainer container) {
        this.abilityAttributes = container;
    }

    @Override
    public AbilityAttributesContainer getAbilityAttributes() {
        return abilityAttributes;
    }

    @Override
    public void setUpgradesId(Identifier id) {
        this.abilityUpgradesId = id;
    }

    @Override
    public Identifier getUpgradesId() {
        return this.abilityUpgradesId;
    }

    @Unique
    private void jujutsu$syncAbilitiesToClient() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(!player.getWorld().isClient()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncPlayerAbilitiesPayload(abilities, abilityUpgradesId));
        }
    }

    @Unique
    private void jujutsu$syncAbilityAttributesToClient() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(!player.getWorld().isClient()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncAbilityAttributesPayload(abilityAttributes));
        }
    }
}
