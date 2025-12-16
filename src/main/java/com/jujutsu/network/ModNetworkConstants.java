package com.jujutsu.network;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.animation.IAnimatedPlayer;
import com.jujutsu.client.hud.BuffIconsRenderer;
import com.jujutsu.client.hud.ColorModifierHudRenderer;
import com.jujutsu.client.hud.CrosshairMarkRenderer;
import com.jujutsu.client.hud.FlashSystemHudRenderer;
import com.jujutsu.client.toast.AbilitiesAcquiredToast;
import com.jujutsu.network.payload.*;
import com.jujutsu.network.payload.abilities.AbilitiesSyncS2CPayload;
import com.jujutsu.network.payload.abilities.RunAbilityC2SPayload;
import com.jujutsu.network.payload.input_requests.ClearInputRequestS2CPayload;
import com.jujutsu.network.payload.input_requests.RequestInputS2CPayload;
import com.jujutsu.network.payload.input_requests.RequestedInputPressedC2SPayload;
import com.jujutsu.systems.ability.attribute.AbilityAttributeContainerHolder;
import com.jujutsu.screen.HandTransformSettingScreen;
import com.jujutsu.systems.ability.upgrade.*;
import com.jujutsu.systems.animation.AnimationData;
import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class ModNetworkConstants {
    public static final Identifier ABILITY_UPGRADE_PURCHASED_ID = Jujutsu.id("ability_upgrade_purchased");

    public static final Identifier OPEN_HAND_SETTING_SCREEN_ID = Jujutsu.id("open_hand_setting_screen");
    public static final Identifier ABILITIES_ACQUIRED_ID = Jujutsu.id("abilities_acquired");
    public static final Identifier PLAY_CLIENT_SOUND_ID = Jujutsu.id("play_client_sound");
    public static final Identifier PLAY_ANIMATION_ID = Jujutsu.id("play_animation");
    public static final Identifier SYNC_BUFFS_FOR_DISPLAYING_ID = Jujutsu.id("sync_buffs");
    public static final Identifier SHOW_SCREEN_FLASH_ID = Jujutsu.id("show_screen_flash");
    public static final Identifier SHOW_CROSSHAIR_MARK_ID = Jujutsu.id("show_crosshair_mark");
    public static final Identifier SHOW_SCREEN_COLOR_MODIFIER_ID = Jujutsu.id("show_screen_color_modifier");
    public static final Identifier SYNC_ABILITY_ATTRIBUTES_ID = Jujutsu.id("sync_ability_attributes");
    public static final Identifier SYNC_ABILITY_UPGRADES_ID = Jujutsu.id("sync_ability_upgrades");
    public static final Identifier SPAWN_PARTICLES_ID = Jujutsu.id("spawn_particles");

    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(RunAbilityC2SPayload.ID, RunAbilityC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestedInputPressedC2SPayload.ID, RequestedInputPressedC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AbilityUpgradePurchasedPayload.ID, AbilityUpgradePurchasedPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(AbilitiesSyncS2CPayload.ID, AbilitiesSyncS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenHandSettingScreenPayload.ID, OpenHandSettingScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AbilitiesAcquiredPayload.ID, AbilitiesAcquiredPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayClientSoundPayload.ID, PlayClientSoundPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayAnimationPayload.ID, PlayAnimationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBuffsForDisplaying.ID, SyncBuffsForDisplaying.CODEC);
        PayloadTypeRegistry.playS2C().register(ShowScreenFlashPayload.ID, ShowScreenFlashPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ShowCrosshairMarkPayload.ID, ShowCrosshairMarkPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ShowScreenColorModifierPayload.ID, ShowScreenColorModifierPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RequestInputS2CPayload.ID, RequestInputS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAbilityAttributesPayload.ID, SyncAbilityAttributesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAbilityUpgradesPayload.ID, SyncAbilityUpgradesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SpawnParticlesPayload.ID, SpawnParticlesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AbilityRuntimeDataSyncS2CPayload.ID, AbilityRuntimeDataSyncS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClearInputRequestS2CPayload.ID, ClearInputRequestS2CPayload.CODEC);
    }

    public static void registerServerReceivers() {
        RunAbilityC2SPayload.registerServerReceiver();

        RequestedInputPressedC2SPayload.registerServerReceiver();

        ServerPlayNetworking.registerGlobalReceiver(AbilityUpgradePurchasedPayload.ID, (payload, context) -> {
            IAbilitiesHolder holder = (IAbilitiesHolder) context.player();
            UpgradesData data = holder.getUpgradesData();

            List<AbilityUpgradeBranch> branches = AbilityUpgradesReloadListener.getInstance().getBranches(holder.getUpgradesId());
            if(branches == null || branches.isEmpty()) return;

            int lastBranchIndex = AbilityUpgradeBranch.findPlayerLastPurchasedBranchIndex(branches, data);
            AbilityUpgradeBranch branch = null;
            int branchIndex = 0;
            for(int i = 0; i < branches.size(); i++) {
                AbilityUpgradeBranch branch1 = branches.get(i);
                if(branch1.id().equals(payload.branchId())) {
                    branch = branch1;
                    branchIndex = i;
                    break;
                }
            }

            if(branch == null || data.purchasedUpgrades().containsKey(payload.branchId())) return;
            if(lastBranchIndex != -1 && lastBranchIndex + 1 != branchIndex) return;

            AbilityUpgrade upgrade = null;
            for(AbilityUpgrade upgrade1: branch.upgrades()) {
                if(upgrade1.id().equals(payload.upgradeId())) {
                    upgrade = upgrade1;
                    break;
                }
            }

            if(upgrade == null) return;

            if(data.points() < upgrade.cost()) return;

            //branch exists; upgrade exists; has enough points; dont bought another upgrade from branch; branch ordinal is correct - so we can add upgrade
            
            upgrade.apply(context.player());

            HashMap<Identifier, Identifier> purchasedUpgrades = data.purchasedUpgrades();
            purchasedUpgrades.put(branch.id(), upgrade.id());

            holder.setUpgradesData(new UpgradesData(data.upgradesId(), data.points() - upgrade.cost(), purchasedUpgrades));

            //TODO: sync upgrades w client
            //ServerPlayNetworking.send(context.player(), new AbilitiesSyncS2CPayload(((IPlayerJujutsuAbilitiesHolder) context.player()).getAbilities(), holder.getUpgradesData()));
        });
    }

    public static void registerClientReceivers() {
        AbilitiesSyncS2CPayload.registerClientReceiver();

        ClientPlayNetworking.registerGlobalReceiver(OpenHandSettingScreenPayload.ID, (payload, context) -> {
            context.client().setScreen(new HandTransformSettingScreen(payload.before()));
        });

        ClientPlayNetworking.registerGlobalReceiver(AbilitiesAcquiredPayload.ID, (payload, context) -> {
            context.client().getToastManager().add(new AbilitiesAcquiredToast(payload.acquiredAbilities()));
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayClientSoundPayload.ID, (payload, context) -> {
            World world = context.client().world;
            world.playSound(null, context.player().getBlockPos(), payload.sound(), SoundCategory.MASTER);
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayAnimationPayload.ID, (payload, context) -> {
            AnimationData data = payload.animationData();
            PlayerEntity player = context.player().getWorld().getPlayerByUuid(data.playerUUID());

            if(player == null) return;

            IAnimatedPlayer animatedPlayer = (IAnimatedPlayer) player;
            IPlayable playable = PlayerAnimationRegistry.getAnimation(payload.animationData().animation());
            if(!(playable instanceof KeyframeAnimation keyframeAnimation)) return;

            animatedPlayer.jujutsu_getModAnimation().setAnimation(new KeyframeAnimationPlayer(keyframeAnimation));
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncBuffsForDisplaying.ID, (payload, context) -> {
            BuffIconsRenderer.setBuffs(payload.buffs());
        });

        ClientPlayNetworking.registerGlobalReceiver(ShowScreenFlashPayload.ID, (payload, context) -> {
            FlashSystemHudRenderer.addFlashData(payload.flashData());
        });

        ClientPlayNetworking.registerGlobalReceiver(ShowCrosshairMarkPayload.ID, (payload, context) -> {
            CrosshairMarkRenderer.addMarkData(payload.markData());
        });

        ClientPlayNetworking.registerGlobalReceiver(ShowScreenColorModifierPayload.ID, (payload, context) -> {
            ColorModifierHudRenderer.addColorModifier(payload.colorModifier());
        });

        RequestInputS2CPayload.registerClientReceiver();

        ClientPlayNetworking.registerGlobalReceiver(SyncAbilityAttributesPayload.ID, (payload, context) -> {
            AbilityAttributeContainerHolder holder = (AbilityAttributeContainerHolder) context.player();
            holder.setAbilityAttributes(payload.container());
        });

        ClientPlayNetworking.registerGlobalReceiver(SpawnParticlesPayload.ID, SpawnParticlesPayload::receiveOnClient);

        AbilityUpgradesReloadListener.registerClientReceiver();

        AbilityRuntimeDataSyncS2CPayload.registerClientReceiver();

        ClearInputRequestS2CPayload.registerClientReceiver();
    }
}
