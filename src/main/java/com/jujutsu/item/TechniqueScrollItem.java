package com.jujutsu.item;

import com.jujutsu.Jujutsu;
import com.jujutsu.component.TechniqueComponent;
import com.jujutsu.network.payload.AbilitiesAcquiredPayload;
import com.jujutsu.registry.ModDataComponents;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.core.AbilityType;
import com.jujutsu.systems.ability.holder.IAbilitiesHolder;
import com.jujutsu.systems.ability.passive.PassiveAbility;
import com.jujutsu.systems.ability.upgrade.AbilityUpgrade;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradeBranch;
import com.jujutsu.systems.ability.upgrade.AbilityUpgradesReloadListener;
import com.jujutsu.systems.ability.upgrade.UpgradesData;
import com.jujutsu.util.AbilitiesHolderUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class TechniqueScrollItem extends Item implements IBorderTooltipItem, ModelWithIcon{
    public TechniqueScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(world.isClient()) return TypedActionResult.pass(stack);

        TechniqueComponent component = stack.get(ModDataComponents.TECHNIQUE_COMPONENT);
        if(component == null) return TypedActionResult.pass(stack);

        IAbilitiesHolder holder = (IAbilitiesHolder) user;

        AbilitiesHolderUtils.removeAbilities((ServerPlayerEntity) user);
        holder.setUpgradesId(component.upgradesId());

        List<AbilityUpgradeBranch> branches = AbilityUpgradesReloadListener.getInstance().getBranches(component.upgradesId());
        UpgradesData data = holder.getUpgradesData();
        if(branches != null && !branches.isEmpty()) {
            for (AbilityUpgradeBranch branch : branches) {
                if (data.purchasedUpgrades().containsKey(branch.id())) {
                    AbilityUpgrade upgrade = branch.findUpgrade(data.purchasedUpgrades().get(branch.id()));

                    if (upgrade != null) {
                        upgrade.apply(user);
                    }
                }
            }
        }

        for(Map.Entry<AbilitySlot, AbilityType> entry: component.abilities().entrySet()) {
            holder.addAbilityInstance(entry.getValue().getDefaultInstance(), entry.getKey());
        }
        for(PassiveAbility passiveAbility: component.passiveAbilities()) {
            holder.addPassiveAbility(passiveAbility);
        }

        ServerPlayNetworking.send((ServerPlayerEntity) user, new AbilitiesAcquiredPayload(component.abilities().values().stream().toList()));

        stack.decrement(1);
        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        TechniqueComponent component = stack.get(ModDataComponents.TECHNIQUE_COMPONENT);
        if(component != null) {
            if(!Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("item.jujutsu.technique_scroll.press_shift").formatted(Formatting.YELLOW));
                return;
            }
            tooltip.add(Text.translatable("item.jujutsu.technique_scroll.stored_abilities").setStyle(Style.EMPTY.withColor(0xfcaf68).withUnderline(true)));
            for(AbilityType ability: component.abilities().values()) {
                tooltip.add(Text.literal("- ").append(ability.getName().copy().setStyle(ability.getStyle())));
            }

            if(component.passiveAbilities().isEmpty()) return;

            tooltip.add(Text.literal(""));
            tooltip.add(Text.translatable("item.jujutsu.technique_scroll.stored_passive_abilities").setStyle(Style.EMPTY.withColor(0xfcaf68).withUnderline(true)));
            for(PassiveAbility passiveAbility: component.passiveAbilities()) {
                tooltip.add(Text.literal("- ").append(passiveAbility.getName().copy().setStyle(passiveAbility.getStyle())));
            }
        }

        super.appendTooltip(stack, context, tooltip, type);
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
}
