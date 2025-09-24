package com.jujutsu.command;

import com.jujutsu.Jujutsu;
import com.jujutsu.command.argument.AbilitySlotArgument;
import com.jujutsu.registry.JujutsuRegistries;
import com.jujutsu.systems.ability.core.AbilityInstance;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.attribute.AbilityAttribute;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.network.payload.OpenHandSettingScreenPayload;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import com.jujutsu.systems.animation.PlayerAnimations;
import com.jujutsu.util.AbilitiesHolderUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class JujutsuCommand {
    public static void register() {
        ArgumentTypeRegistry.registerArgumentType(Jujutsu.getId("ability_slot"), AbilitySlotArgument.class, ConstantArgumentSerializer.of(AbilitySlotArgument::new));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("jujutsu")
                    .then(CommandManager.literal("ability")
                            .then(CommandManager.literal("info").executes(JujutsuCommand::getAbilitiesInfo))
                            .then(CommandManager.literal("remove")
                                    .then(CommandManager.literal("all").executes(JujutsuCommand::removeAllAbilities))
                                    .then(CommandManager.argument("ability_slot", new AbilitySlotArgument()).executes(JujutsuCommand::removeAbility)) )

                            .then(CommandManager.literal("set")
                                    .then(CommandManager.argument("ability_slot", new AbilitySlotArgument() )
                                            .then(CommandManager.argument("ability",
                                                    RegistryEntryReferenceArgumentType.registryEntry(registryAccess, JujutsuRegistries.ABILITY_TYPE_REGISTRY_KEY) )
                                                    .executes(JujutsuCommand::setAbility) )))

                            .then(CommandManager.literal("attribute")
                                    .then(CommandManager.literal("set")
                                            .then(CommandManager.argument( "attribute", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, JujutsuRegistries.ABILITY_ATTRIBUTE_REGISTRY_KEY))
                                                    .then(CommandManager.argument("id", IdentifierArgumentType.identifier())
                                                            .then(CommandManager.argument("value", DoubleArgumentType.doubleArg())
                                                                    .executes(JujutsuCommand::setAttribute))))) )

                    )

                    .then(CommandManager.literal("points")
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.argument("points", FloatArgumentType.floatArg()).executes(JujutsuCommand::setPoints)))
                    )

                    .then(CommandManager.literal("attribute")
                            .then(CommandManager.literal("reset").executes(JujutsuCommand::resetAttributesAndUpgrades)))

                    .then(CommandManager.literal("rearm").executes(JujutsuCommand::reloadAbilities))

                    .then(CommandManager.literal("animation").then(CommandManager.argument("player", EntityArgumentType.player()).executes(JujutsuCommand::playAnimation)) )

                    .then(CommandManager.literal("hand")
                            .then(CommandManager.literal("right").executes(context -> openHandSettingMenu(context, true)))
                            .then(CommandManager.literal("left").executes(context -> openHandSettingMenu(context, false))))
            );
        });
    }

    private static int resetAttributesAndUpgrades(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        AbilitiesHolderUtils.removeAbilityUpgrades(context.getSource().getPlayer());

        return 1;
    }

    private static int setPoints(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        float points = FloatArgumentType.getFloat(context, "points");
        IAbilitiesHolder holder = (IAbilitiesHolder) context.getSource().getPlayer();

        UpgradesData data = holder.getUpgradesData();

        holder.setUpgradesData(new UpgradesData(data.upgradesId(), points, data.purchasedUpgrades()));

        return 1;
    }

    private static int setAttribute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        AbilityAttribute attribute = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "attribute", JujutsuRegistries.ABILITY_ATTRIBUTE_REGISTRY_KEY).value();
        Identifier id = IdentifierArgumentType.getIdentifier(context, "id");
        double value = DoubleArgumentType.getDouble(context, "value");



        return 1;
    }

    private static int playAnimation(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PlayerAnimations.playAnimation(player, Jujutsu.getId("test"), 1000, 50);

        return 1;
    }

    private static int openHandSettingMenu(CommandContext<ServerCommandSource> context, boolean rightHand) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayNetworking.send(player, new OpenHandSettingScreenPayload(rightHand));
        return 1;
    }

    private static int reloadAbilities(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        IAbilitiesHolder holder = (IAbilitiesHolder) player;

        if (holder.getSlots().size() <= 0) return 1;
        for(AbilitySlot slot: holder.getSlots()) {
            AbilityInstance instance = holder.getAbilityInstance(slot);
            instance.setCooldownTime(0);
        }
        return 1;
    }

    private static int removeAllAbilities(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();

        try {
            AbilitiesHolderUtils.removeAbilities(player);
        }
        catch (Exception e) {
            Jujutsu.LOGGER.warn("exception: {}", e);
        }
        return 1;
    }

    private static int removeAbility(CommandContext<ServerCommandSource> context) {
        AbilitySlot slot = context.getArgument("ability_slot", AbilitySlot.class);
        ServerPlayerEntity player = context.getSource().getPlayer();
        IAbilitiesHolder holder = (IAbilitiesHolder) player;

        holder.removeAbilityInstance(slot);

        return 1;
    }

    private static int getAbilitiesInfo(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        IAbilitiesHolder holder = (IAbilitiesHolder) player;

        StringBuilder builder = new StringBuilder();
        for(AbilitySlot slot: holder.getSlots()) {
            AbilityInstance instance = holder.getAbilityInstance(slot);
            builder.append(String.format("Instance: {%s} \n", instance.toString()));
        }
        player.sendMessage(Text.literal(builder.toString()), true);
        return 1;
    }

    private static int setAbility(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        AbilitySlot slot = context.getArgument("ability_slot", AbilitySlot.class);
        RegistryEntry.Reference<AbilityType> ability = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "ability", JujutsuRegistries.ABILITY_TYPE_REGISTRY_KEY);

        ServerPlayerEntity player = context.getSource().getPlayer();
        IAbilitiesHolder holder = (IAbilitiesHolder) player;


        return 1;
    }
}
