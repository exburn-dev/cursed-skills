package com.jujutsu;

import com.jujutsu.command.JujutsuCommand;
import com.jujutsu.event.server.ServerEventsListeners;
import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.registry.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jujutsu implements ModInitializer {
	public static final String MODID = "jujutsu";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModItemGroups.register();
		ModDataComponents.register();
		ModAttributes.register();

		ModEffects.register();
		ModSounds.register();

		ModEntityTypes.register();
		ModParticleTypes.register();

		ModNetworkConstants.registerPackets();
		ModNetworkConstants.registerServerReceivers();

		JujutsuCommand.register();
		ServerEventsListeners.register();

		ModAbilities.register();
		BuffCancellingConditionTypes.register();
		ModAbilityAttributes.register();

	}

	public static Identifier getId(String id) {
		return Identifier.of(MODID, id);
	}
}