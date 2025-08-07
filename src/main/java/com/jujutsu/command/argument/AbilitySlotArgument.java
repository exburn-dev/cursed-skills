package com.jujutsu.command.argument;

import com.jujutsu.Jujutsu;
import com.jujutsu.systems.ability.AbilitySlot;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class AbilitySlotArgument implements ArgumentType<AbilitySlot> {
    @Override
    public AbilitySlot parse(StringReader stringReader) throws CommandSyntaxException {
        try {
            String string = stringReader.getString();
            Jujutsu.LOGGER.info(string);

            Identifier id = Identifier.tryParse(string);
            AbilitySlot slot = AbilitySlot.byId(id);

            if(slot == null) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid AbilitySlot format");
            }
            return slot;
        }
        catch (Exception e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid AbilitySlot format");
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(AbilitySlot.getAllSlots().stream().map(AbilitySlot::toString), builder);
    }
}
