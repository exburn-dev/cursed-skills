package com.jujutsu.api.ability.passive;

import com.jujutsu.api.component.EntityDataComponent;
import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.api.component.ComponentKeys;
import com.jujutsu.api.component.EntityComponent;
import com.jujutsu.api.component.EntityTickingComponent;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PassiveAbilityComponent implements EntityComponent, EntityTickingComponent, EntityDataComponent {
    public static final Codec<List<PassiveAbility>> CODEC;

    private final Player player;
    private List<PassiveAbility> abilities = new ArrayList<>();

    public PassiveAbilityComponent(Player player) {
        this.player = player;
    }

    @Override
    public void onLoaded() {
        for(PassiveAbility ability : abilities) {
            ability.onGained(player);
        }
    }

    @Override
    public void tick() {
        for(PassiveAbility ability : abilities) {
            ability.tick(player);
        }
    }

    public void addPassiveAbility(PassiveAbility ability) {
        abilities.add(ability);
        ability.onGained(player);
    }

    public void removePassiveAbilities() {
        for(PassiveAbility ability : abilities) {
            ability.onRemoved(player);
        }
        abilities.clear();
    }

    public List<PassiveAbility> all() {
        return abilities;
    }

    public boolean hasAbilities() {
        return !abilities.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public <T extends PassiveAbility> Optional<T> find(PassiveAbilityType<T> type) {
        for(PassiveAbility ability : abilities) {
            if(ability.getType().equals(type)) return (Optional<T>) Optional.of(ability);
        }
        return Optional.empty();
    }

    public void copyFrom(PassiveAbilityComponent component) {
        abilities = component.abilities;
        onLoaded();
    }

    @Override
    public void save(ValueOutput data) {
        data.store("PassiveAbilities", CODEC, abilities);
    }

    @Override
    public void read(ValueInput data) {
        var result = data.read("PassiveAbilities", CODEC);
        if(result.isEmpty()) return;

        abilities = new ArrayList<>(result.get());
    }

    @Override
    public void sendToClient() { }

    public static PassiveAbilityComponent get(Player player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.PASSIVE_ABILITIES);
    }

    static {
        CODEC = PassiveAbility.CODEC.listOf();
    }
}
