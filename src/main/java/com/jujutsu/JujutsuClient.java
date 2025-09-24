package com.jujutsu;

import com.jujutsu.client.hud.*;
import com.jujutsu.client.keybind.AdditionalInputSystem;
import com.jujutsu.client.particle.*;
import com.jujutsu.entity.model.BlinkMarkerModel;
import com.jujutsu.entity.model.PhoenixFireballModel;
import com.jujutsu.entity.renderer.*;
import com.jujutsu.event.ClientEventListeners;
import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.registry.ModEntityTypes;
import com.jujutsu.client.keybind.ModKeybindings;
import com.jujutsu.registry.ModParticleTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class JujutsuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntityTypes.HOLLOW_PURPLE, HollowPurpleEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.LAPSE_BLUE, LapseBlueEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.REVERSAL_RED, ReversalRedEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.BLINK_MARKER, BlinkMarkerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityTypes.PHOENIX_FIREBALL, PhoenixFireballRenderer::new);

        ModKeybindings.register();
        AdditionalInputSystem.register();
        ClientEventListeners.register();
        ModNetworkConstants.registerClientReceivers();

        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.HOLLOW_PURPLE_PARTICLE, HollowPurpleParticle.ExplosionFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.LAPSE_BLUE_LIGHTNING, LapseBlueLightningParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.LAPSE_BLUE_CORE, LapseBlueCoreParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.COLORED_SPARK, ColoredSparkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.BIG_COLORED_SPARK, BigColoredSparkParticleEffect.Factory::new);

        HudRenderCallback.EVENT.register(AbilitiesHudRenderer::render);
        HudRenderCallback.EVENT.register(AbilityCooldownRenderer::render);
        HudRenderCallback.EVENT.register(BuffIconsRenderer::render);
        HudRenderCallback.EVENT.register(FlashSystemHudRenderer::render);
        HudRenderCallback.EVENT.register(CrosshairMarkRenderer::render);
        HudRenderCallback.EVENT.register(ColorModifierHudRenderer::render);

        EntityModelLayerRegistry.registerModelLayer(BlinkMarkerModel.MODEL_LAYER, BlinkMarkerModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(PhoenixFireballModel.MODEL_LAYER, PhoenixFireballModel::getTexturedModelData);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public void reload(ResourceManager manager) {
                        try {
                            ShaderUtils.reload(manager);
                        } catch (IOException e) {
                            Jujutsu.LOGGER.error("Couldn't load stun shader", e);
                        }
                    }

                    @Override
                    public Identifier getFabricId() {
                        return Jujutsu.getId("stun_shader_loader");
                    }
                }
        );

        HudRenderCallback.EVENT.register((drawContext, counter) -> {
            ShaderUtils.renderStunEffect(counter.getTickDelta(false));
        });
    }
}
