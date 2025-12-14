package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.BigColoredSparkParticleEffect;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

public class ModParticleTypes {
    public static final SimpleParticleType HOLLOW_PURPLE_PARTICLE = registerParticle("hollow_purple", FabricParticleTypes.simple());
    public static final SimpleParticleType LAPSE_BLUE_LIGHTNING = registerParticle("lapse_blue_lightning", FabricParticleTypes.simple());
    public static final SimpleParticleType LAPSE_BLUE_CORE = registerParticle("lapse_blue_core", FabricParticleTypes.simple());
    public static final ParticleType<ColoredSparkParticleEffect> COLORED_SPARK = registerParticle("colored_spark", false,
            (type) -> ColoredSparkParticleEffect.CODEC,
            (type) -> ColoredSparkParticleEffect.PACKET_CODEC);
    public static final ParticleType<BigColoredSparkParticleEffect> BIG_COLORED_SPARK = registerParticle("big_colored_spark", false,
            (type) -> BigColoredSparkParticleEffect.CODEC,
            (type) -> BigColoredSparkParticleEffect.PACKET_CODEC);

    private static <T extends ParticleType<?>> T registerParticle(String name, T type) {
        return Registry.register(Registries.PARTICLE_TYPE, Jujutsu.id(name), type);
    }

    private static <T extends ParticleEffect> ParticleType<T> registerParticle(String name, boolean alwaysShow, Function<ParticleType<T>, MapCodec<T>> codec, Function<ParticleType<T>, PacketCodec<RegistryByteBuf, T>> packetCodec) {
        ParticleType<T> type = new ParticleType<T>(alwaysShow) {
            @Override
            public MapCodec<T> getCodec() {
                return codec.apply(this);
            }

            @Override
            public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
                return packetCodec.apply(this);
            }
        };
        return Registry.register(Registries.PARTICLE_TYPE, Jujutsu.id(name), type);
    }

    public static void register() {
        Jujutsu.LOGGER.info("Registering particles for " + Jujutsu.MODID);
    }
}
