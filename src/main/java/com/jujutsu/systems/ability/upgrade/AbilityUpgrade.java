package com.jujutsu.systems.ability.upgrade;

import com.jujutsu.network.NbtPacketCodec;
import com.jujutsu.registry.JujutsuRegistries;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class AbilityUpgrade {
    private final Identifier id;
    private final Identifier icon;
    private final float cost;
    private final AbilityUpgradeType<?> type;

    public AbilityUpgrade(Identifier id, Identifier icon, float cost, AbilityUpgradeType<?> type) {
        this.id = id;
        this.icon = icon;
        this.cost = cost;
        this.type = type;
    }

    public Identifier id() {
        return this.id;
    }
    public Identifier icon() {
        return this.icon;
    }
    public float cost() {
        return this.cost;
    }
    public AbilityUpgradeType<?> type() {
        return this.type;
    }

    public abstract void apply(PlayerEntity player);
    public abstract void remove(PlayerEntity player);

    public List<MutableText> getDescription() {
        return List.of(Text.translatable(JujutsuRegistries.ABILITY_UPGRADE_TYPE.getId(type).toTranslationKey("ability_upgrade")));
    }

    public static final Codec<AbilityUpgrade> CODEC = JujutsuRegistries.ABILITY_UPGRADE_TYPE.getCodec().dispatch(
            AbilityUpgrade::type,
            AbilityUpgradeType::codec
    );

    public static final PacketCodec<RegistryByteBuf, AbilityUpgrade> PACKET_CODEC = new NbtPacketCodec<>(CODEC);

    protected static <T extends AbilityUpgrade> Products.P4<RecordCodecBuilder.Mu<T>, Identifier, Identifier, Float, AbilityUpgradeType<?>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(AbilityUpgrade::id),
                Identifier.CODEC.fieldOf("icon").forGetter(AbilityUpgrade::icon),
                Codec.FLOAT.fieldOf("cost").forGetter(AbilityUpgrade::cost),
                JujutsuRegistries.ABILITY_UPGRADE_TYPE.getCodec().fieldOf("type").forGetter(AbilityUpgrade::type)
        );
    }
}
