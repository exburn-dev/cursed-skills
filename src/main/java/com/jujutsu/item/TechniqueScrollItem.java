package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import com.jujutsu.api.ability.core.AbilitySlot;
import com.jujutsu.component.TechniqueComponent;
import com.jujutsu.network.payload.AbilitiesAcquiredPayload;
import com.jujutsu.registry.ModDataComponents;
import com.jujutsu.registry.ModItems;
import com.jujutsu.registry.ModSounds;
import com.jujutsu.api.ability.core.AbilityComponent;
import com.jujutsu.api.ability.core.AbilityType;
import com.jujutsu.api.ability.passive.PassiveAbility;
import com.jujutsu.api.ability.passive.PassiveAbilityComponent;
import com.jujutsu.api.talent.TalentComponent;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TechniqueScrollItem extends Item implements IBorderTooltipItem, ModelWithIcon{
    public TechniqueScrollItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if(level.isClientSide()) return InteractionResult.PASS;

        TechniqueComponent component = stack.get(ModDataComponents.TECHNIQUE_COMPONENT);
        AbilityComponent abilityComponent = AbilityComponent.get(user);
        PassiveAbilityComponent passiveAbilityComponent = PassiveAbilityComponent.get(user);
        TalentComponent talentComponent = TalentComponent.get(user);

        if(component == null) return InteractionResult.PASS;

        abilityComponent.clearInstances();
        passiveAbilityComponent.removePassiveAbilities();

        for(var mapEntry: component.abilities().entrySet()) {
            abilityComponent.addInstance(mapEntry.getKey(), mapEntry.getValue());
        }

        talentComponent.setTree(component.upgradesId());

        talentComponent.applyPurchasedTalents();

        for(PassiveAbility ability : component.passiveAbilities()) {
            passiveAbilityComponent.addPassiveAbility(ability);
        }

        ServerPlayNetworking.send(
                (ServerPlayer) user,
                new AbilitiesAcquiredPayload(component.abilities().values().stream().toList()));

        level.playSound(null, user.blockPosition(), ModSounds.TECHNIQUE_SCROLL_USED, SoundSource.MASTER, 1.0f, 1.0f);

        stack.shrink(1);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay,
                                Consumer<Component> builder, TooltipFlag tooltipFlag) {
        TechniqueComponent component = stack.get(ModDataComponents.TECHNIQUE_COMPONENT);
        if(component != null) {
            if(!Minecraft.getInstance().hasShiftDown()) {
                builder.accept(Component.translatable("item.jujutsu.technique_scroll.press_for_info",
                                Component.literal("<SHIFT>").withStyle(ChatFormatting.YELLOW))
                                .withStyle(ChatFormatting.GRAY));
                return;
            }
            builder.accept(Component.translatable("item.jujutsu.technique_scroll.stored_abilities")
                    .setStyle(Style.EMPTY.withColor(0xfcaf68).withUnderlined(true)));
            for(AbilityType ability: component.abilities().values()) {
                builder.accept(Component.literal("- ").append(ability.getName().copy().setStyle(ability.getStyle())));
            }

            if(component.passiveAbilities().isEmpty()) return;

            builder.accept(Component.literal(""));
            builder.accept(Component.translatable("item.jujutsu.technique_scroll.stored_passive_abilities")
                    .setStyle(Style.EMPTY.withColor(0xfcaf68).withUnderlined(true)));

            for(PassiveAbility passiveAbility: component.passiveAbilities()) {
                builder.accept(Component.literal("- ").append(passiveAbility.getName().copy().withStyle(passiveAbility.getStyle())));
            }
        }

        super.appendHoverText(stack, tooltipContext, tooltipDisplay, builder, tooltipFlag);
    }

    @Override
    public TooltipBorderData getBorderData() {
        return new TooltipBorderData.Builder(Jujutsu.id("tooltip/technique_scroll/main"))
                .addUpperTile(Jujutsu.id("tooltip/technique_scroll/upper_tile"))
                .addUpperDecorTile(Jujutsu.id("tooltip/technique_scroll/upper_decor_tile"))
                .addBottomTile(Jujutsu.id("tooltip/technique_scroll/bottom_tile"))
                .addBottomDecorTile(Jujutsu.id("tooltip/technique_scroll/bottom_decor_tile"))
                .build();
    }

    @Override
    public int getOffset() {
        return 3;
    }

    @Override
    public Identifier getModel() {
        return Jujutsu.id("technique_scroll_3d");
    }

    public static Builder createStack(Identifier talentTreeId) {
        return new Builder(talentTreeId);
    }

    public static class Builder {
        private final List<Pair<AbilitySlot, AbilityType>> abilities = new ArrayList<>();
        private final List<PassiveAbility> passiveAbilities = new ArrayList<>();
        private final Identifier talentTreeId;

        public Builder(Identifier talentTreeId) {
            this.talentTreeId = talentTreeId;
        }

        public Builder addAbility(AbilitySlot slot, AbilityType type) {
            abilities.add(new Pair<>(slot, type));
            return this;
        }

        public Builder addPassiveAbility(PassiveAbility ability) {
            passiveAbilities.add(ability);
            return this;
        }

        public ItemStack build() {
            ItemStack stack = ModItems.TECHNIQUE_SCROLL.getDefaultInstance();
            Map<AbilitySlot, AbilityType> map = new HashMap<>();
            for(Pair<AbilitySlot, AbilityType> pair: abilities) {
                map.put(pair.getFirst(), pair.getSecond());
            }
            stack.set(ModDataComponents.TECHNIQUE_COMPONENT, new TechniqueComponent(map, passiveAbilities, talentTreeId));
            return stack;
        }
    }
}
