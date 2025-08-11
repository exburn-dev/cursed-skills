package com.jujutsu.network;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.animation.IAnimatedPlayer;
import com.jujutsu.client.hud.BuffIconsRenderer;
import com.jujutsu.client.hud.CrosshairMarkRenderer;
import com.jujutsu.client.hud.FlashSystemHudRenderer;
import com.jujutsu.client.toast.AbilitiesAcquiredToast;
import com.jujutsu.network.payload.*;
import com.jujutsu.systems.ability.IAbilitiesHolder;
import com.jujutsu.systems.ability.IPlayerJujutsuAbilitiesHolder;
import com.jujutsu.screen.HandTransformSettingScreen;
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

public class ModNetworkConstants {
    public static final Identifier ABILITY_KEY_PRESSED_ID = Jujutsu.getId("ability_key_pressed");
    public static final Identifier SYNC_PLAYER_ABILITIES_ID = Jujutsu.getId("sync_player_abilities");
    public static final Identifier OPEN_HAND_SETTING_SCREEN_ID = Jujutsu.getId("open_hand_setting_screen");
    public static final Identifier ABILITIES_ACQUIRED_ID = Jujutsu.getId("abilities_acquired");
    public static final Identifier PLAY_CLIENT_SOUND_ID = Jujutsu.getId("play_client_sound");
    public static final Identifier PLAY_ANIMATION_ID = Jujutsu.getId("play_animation");
    public static final Identifier SYNC_BUFFS_FOR_DISPLAYING_ID = Jujutsu.getId("sync_buffs");
    public static final Identifier SHOW_SCREEN_FLASH_ID = Jujutsu.getId("show_screen_flash");
    public static final Identifier SHOW_CROSSHAIR_MARK_ID = Jujutsu.getId("show_crosshair_mark");

    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(AbilityKeyPressedPayload.ID, AbilityKeyPressedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPlayerAbilitiesPayload.ID, SyncPlayerAbilitiesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenHandSettingScreenPayload.ID, OpenHandSettingScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AbilitiesAcquiredPayload.ID, AbilitiesAcquiredPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayClientSoundPayload.ID, PlayClientSoundPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayAnimationPayload.ID, PlayAnimationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncBuffsForDisplaying.ID, SyncBuffsForDisplaying.CODEC);
        PayloadTypeRegistry.playS2C().register(ShowScreenFlashPayload.ID, ShowScreenFlashPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ShowCrosshairMarkPayload.ID, ShowCrosshairMarkPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(AbilityKeyPressedPayload.ID, (payload, context) -> {
            IAbilitiesHolder holder = (IAbilitiesHolder) context.player();

            if(payload.cancel()) {
                payload.abilitySlot().cancel(holder);
            }
            else {
                payload.abilitySlot().activate(holder);
            }
        });
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerAbilitiesPayload.ID, (payload, context) -> {
            IPlayerJujutsuAbilitiesHolder holder = (IPlayerJujutsuAbilitiesHolder) context.player();
            holder.setAbilities(payload.abilities());
        });

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
    }
}
