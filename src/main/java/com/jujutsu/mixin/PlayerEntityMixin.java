package com.jujutsu.mixin;

import com.jujutsu.Jujutsu;
import com.jujutsu.event.server.AbilityEvents;
import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.network.payload.SyncAbilityAttributesPayload;
import com.jujutsu.registry.ModAttributes;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.network.payload.SyncPlayerAbilitiesPayload;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.systems.ability.attribute.AbilityAttributesContainer;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.holder.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.systems.ability.holder.PlayerJujutsuAbilities;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.task.AbilityTask;
import com.jujutsu.systems.ability.task.TickAbilitiesTask;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import com.jujutsu.systems.buff.PlayerDynamicAttributesAccessor;
import com.jujutsu.util.CodecUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.nbt.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
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
public abstract class PlayerEntityMixin extends LivingEntity implements IAbilitiesHolder, IPlayerJujutsuAbilitiesHolder, AbilityAttributeContainerHolder,
        PlayerDynamicAttributesAccessor {
    @Unique
    private static final TrackedData<Float> DYNAMIC_SPEED_BONUS =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @Unique
    private static final TrackedData<Float> DYNAMIC_JUMP_BONUS =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);


    @Unique
    private PlayerJujutsuAbilities abilities = new PlayerJujutsuAbilities(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
    @Unique
    private AbilityAttributesContainer abilityAttributes = new AbilityAttributesContainer(new HashMap<>());
    @Unique
    private UpgradesData upgradesData = new UpgradesData(Jujutsu.id(""), 0, new HashMap<>());
    @Unique
    private List<AbilityTask> abilityTasks = new ArrayList<>();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        if(!world.isClient()) {
            abilityTasks.add(new TickAbilitiesTask());
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if(getWorld().isClient()) {
            for(AbilitySlot slot: abilities.runningAbilities()) {
                AbilityInstance instance = abilities.abilities().get(slot);
                instance.tickClient();
            }
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        for(Iterator<AbilityTask> iterator = abilityTasks.iterator(); iterator.hasNext(); ) {
            AbilityTask task = iterator.next();
            ActionResult result = task.execute(player);

            if(result == ActionResult.FAIL || result == ActionResult.SUCCESS) {
                iterator.remove();
            }
        }
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

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound main = new NbtCompound();
        NbtElement serializedAbilities = abilities.serialize(NbtOps.INSTANCE);
        NbtElement serializedAttributes = abilityAttributes.serialize(NbtOps.INSTANCE);
        NbtElement serializedUpgrades = CodecUtils.serialize(UpgradesData.CODEC, NbtOps.INSTANCE, upgradesData,
                (e) -> Jujutsu.LOGGER.error("Failed to serialize rewards data {}", e));

        main.put("Abilities", serializedAbilities);
        main.put("Attributes", serializedAttributes);
        main.put("Upgrades", serializedUpgrades);

        nbt.put("Jujutsu", main);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readNBT(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound main = nbt.getCompound("Jujutsu");

        NbtCompound abilitiesCompound = main.getCompound("Abilities");
        NbtCompound attributesCompound = main.getCompound("Attributes");
        NbtCompound upgradesCompound = main.getCompound("Upgrades");

        this.abilities = PlayerJujutsuAbilities.deserialize(new Dynamic<>(NbtOps.INSTANCE, abilitiesCompound));
        this.abilityAttributes = AbilityAttributesContainer.deserialize(new Dynamic<>(NbtOps.INSTANCE, attributesCompound));
        this.upgradesData = CodecUtils.deserialize(UpgradesData.CODEC, new Dynamic<>(NbtOps.INSTANCE, upgradesCompound),
                () -> new UpgradesData(Jujutsu.id(""), 0, new HashMap<>()), (e) -> Jujutsu.LOGGER.error("Failed to deserialize rewards data {}", e));
    }

    @Override
    public void addAbilityTask(AbilityTask task) {
        this.abilityTasks.add(task);
    }

    @Override
    public AbilityInstance getAbilityInstance(AbilitySlot slot) {
        return abilities.abilities().get(slot);
    }

    @Override
    public void addAbilityInstance(AbilityInstance instance, AbilitySlot slot) {
        abilities.abilities().put(slot, instance);
        instance.initializeSlot(slot);
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
    public void setAbilityAttributes(AbilityAttributesContainer container) {
        this.abilityAttributes = container;
    }

    @Override
    public AbilityAttributesContainer getAbilityAttributes() {
        return abilityAttributes;
    }

    @Override
    public void setUpgradesId(Identifier id) {
        this.upgradesData = new UpgradesData(id, upgradesData.points(), upgradesData.purchasedUpgrades());
    }

    @Override
    public UpgradesData getUpgradesData() {
        return upgradesData;
    }

    @Override
    public Identifier getUpgradesId() {
        return this.upgradesData.upgradesId();
    }

    @Override
    public void setUpgradesData(UpgradesData upgradesData) {
        this.upgradesData = upgradesData;
    }

    @Unique
    private void jujutsu$syncAbilitiesToClient() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(!player.getWorld().isClient()) {
            ServerPlayNetworking.send((ServerPlayerEntity) player, new SyncPlayerAbilitiesPayload(new PlayerJujutsuAbilities(
                    new HashMap<>(abilities.abilities()),
                    abilities.runningAbilities(),
                    abilities.passiveAbilities()
            ), upgradesData));
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
