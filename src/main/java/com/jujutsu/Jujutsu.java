package com.jujutsu;

import com.jujutsu.command.JujutsuCommand;
import com.jujutsu.event.ServerEventListeners;
import com.jujutsu.event.resource.TalentBranchesResourceLoader;
import com.jujutsu.event.resource.TalentResourceLoader;
import com.jujutsu.event.resource.TalentTreeResourceLoader;
import com.jujutsu.network.ModNetworkConstants;
import com.jujutsu.network.ModPackets;
import com.jujutsu.registry.*;
import com.jujutsu.event.server.DelayedTasks;
import com.jujutsu.registry.custom.BuffConditions;
import com.jujutsu.registry.custom.Abilities;
import com.jujutsu.registry.custom.AbilityAttributes;
import com.jujutsu.registry.custom.AbilityUpgradeRewardTypes;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;
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

		ModPackets.registerPackets();
		ModNetworkConstants.registerPackets();
		ModNetworkConstants.registerServerReceivers();

		JujutsuCommand.register();
		ServerEventListeners.register();

		Abilities.register();
		BuffConditions.register();
		AbilityAttributes.register();
		AbilityUpgradeRewardTypes.register();

		TalentResourceLoader.register();
		TalentBranchesResourceLoader.register();
		TalentTreeResourceLoader.register();

		DelayedTasks.init();
	}

	public static Identifier id(String id) {
		return Identifier.fromNamespaceAndPath(MODID, id);
	}
}