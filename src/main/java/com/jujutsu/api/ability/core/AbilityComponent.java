package com.jujutsu.api.ability.core;

import com.jujutsu.mixinterface.EntityComponentsAccessor;
import com.jujutsu.network.payload.abilities.AbilitiesSyncS2CPayload;
import com.jujutsu.registry.ModEffects;
import com.jujutsu.api.ability.attribute.AbilityAttributeComponent;
import com.jujutsu.api.ability.data.InputRequest;
import com.jujutsu.api.component.*;
import com.jujutsu.api.talent.TalentComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbilityComponent implements EntityComponent, EntityTickingComponent, EntityDataComponent {
    public static final Codec<Map<AbilitySlot, AbilityInstance>> CODEC;
    public static final StreamCodec<FriendlyByteBuf, Map<AbilitySlot, AbilityInstanceData>> STREAM_CODEC;

    private final Player player;

    private Map<AbilitySlot, AbilityInstance> abilities = new HashMap<>();
    private Map<AbilitySlot, InputRequest> inputRequests = new HashMap<>();

    public AbilityComponent(Player player) {
        this.player = player;
    }

    @Override
    public void onLoaded() {
        for(AbilityInstance instance : abilities.values()) {
            instance.endCooldown();
        }
    }

    @Override
    public void tick() {
        for(AbilityInstance instance : abilities()) {
            instance.tick(player);
        }
    }

    public void addInstance(AbilitySlot slot, AbilityType type) {
        AbilityInstance instance = new AbilityInstance(type, slot);
        abilities.put(slot, instance);

        AbilityAttributeComponent.get(player).addAbilityDefaultAttributes(instance.type());
    }

    public void clearInstances() {
        for(var it = abilities.values().iterator(); it.hasNext(); ) {
            AbilityInstance instance = it.next();
            if(instance.status().isRunning()) {
                instance.endAbility(player);
            }
            it.remove();
        }

        TalentComponent.get(player).removePurchasedTalents();
    }

    public AbilityInstance getInstance(AbilitySlot slot) {
        return abilities.get(slot);
    }

    public void runAbility(AbilitySlot slot) {
        if(!canStartAbility(slot)) return;
        AbilityInstance instance = abilities.get(slot);
        instance.start(player);
        sendToClient();
    }

    public boolean canStartAbility(AbilitySlot slot) {
        return abilities.containsKey(slot) && abilities.get(slot).status().isNone() && !player.hasEffect(ModEffects.STUN);
    }

    public Collection<AbilitySlot> slots() {
        return abilities.keySet();
    }

    public Collection<AbilityInstance> abilities() {
        return abilities.values();
    }


    public void copyFrom(AbilityComponent component) {
        abilities = component.abilities;
        onLoaded();
    }

    @Override
    public void save(ValueOutput data) {
        NbtList list = new NbtList();

        var result = AbilityInstanceData.MAP_CODEC.encode(abilitiesDataMap(), NbtOps.INSTANCE, list);
        if(result.isSuccess()) {
            list = (NbtList) result.getOrThrow();
        }

        nbt.put("Abilities", list);
    }

    @Override
    public void read(ValueInput data) {
        NbtList compound = nbt.getList("Abilities", 10);

        abilities.clear();
        var result = AbilityInstanceData.MAP_CODEC.parse(NbtOps.INSTANCE, compound);
        if(result.isSuccess()) {
            readDataMap(result.getOrThrow());
        }
    }

    @Override
    public void sendToClient() {
        ServerPlayNetworking.send((ServerPlayerEntity) player, new AbilitiesSyncS2CPayload(
                abilitiesDataMap().values().stream().toList()
        ));
    }

    public static AbilityComponent get(Player player) {
        return ((EntityComponentsAccessor) player).jujutsu$getContainer().get(ComponentKeys.ABILITIES);
    }

    public static AbilityInstance getInstance(Player player, AbilitySlot slot) {
        return get(player).getInstance(slot);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(AbilitySlot.CODEC, AbilityInstance.CODEC)
        ));
        STREAM_CODEC = ByteBufCodecs.map(HashMap::new,
                AbilitySlot.STREAM_CODEC, );
    }
}
