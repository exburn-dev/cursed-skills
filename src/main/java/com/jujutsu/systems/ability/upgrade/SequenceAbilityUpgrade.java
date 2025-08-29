package com.jujutsu.systems.ability.upgrade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SequenceAbilityUpgrade extends AbilityUpgrade{
    public static final MapCodec<SequenceAbilityUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
            .and(AbilityUpgrade.CODEC.listOf().fieldOf("upgrades").forGetter(SequenceAbilityUpgrade::upgrades))
            .apply(instance, SequenceAbilityUpgrade::new));

    private final List<AbilityUpgrade> upgrades;

    public SequenceAbilityUpgrade(Identifier id, Identifier icon, float cost, AbilityUpgradeType<?> type, List<AbilityUpgrade> upgrades) {
        super(id, icon, cost, type);
        this.upgrades = upgrades;
    }

    public List<AbilityUpgrade> upgrades() {
        return this.upgrades;
    }

    @Override
    public void apply(PlayerEntity player) {
        for(AbilityUpgrade upgrade: upgrades) {
            upgrade.apply(player);
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        for(AbilityUpgrade upgrade: upgrades) {
            upgrade.remove(player);
        }
    }

    @Override
    public List<MutableText> getDescription() {
        List<MutableText> description = new ArrayList<>();

        for(AbilityUpgrade upgrade: upgrades) {
            description.addAll(upgrade.getDescription());
        }

        return description;
    }
}
