package com.jujutsu.registry;

import com.jujutsu.Jujutsu;
import com.jujutsu.client.particle.BigColoredSparkParticleEffect;
import com.jujutsu.client.particle.ColoredSparkParticleEffect;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ModParticleTypes {
    public static final SimpleParticleType HOLLOW_PURPLE_PARTICLE = registerSimple("hollow_purple");
    public static final SimpleParticleType LAPSE_BLUE_LIGHTNING = registerSimple("lapse_blue_lightning");
    public static final SimpleParticleType LAPSE_BLUE_CORE = registerSimple("lapse_blue_core");
    public static final ParticleType<ColoredSparkParticleEffect> COLORED_SPARK = registerParticle(
            "colored_spark", false,
            ColoredSparkParticleEffect.CODEC,
            ColoredSparkParticleEffect.STREAM_CODEC);
    public static final ParticleType<BigColoredSparkParticleEffect> BIG_COLORED_SPARK = registerParticle(
            "big_colored_spark", false,
            BigColoredSparkParticleEffect.CODEC,
            BigColoredSparkParticleEffect.PACKET_CODEC);

    private static SimpleParticleType registerSimple(String name) {
        SimpleParticleType type = FabricParticleTypes.simple();
        register(name, type);
        return type;
    }

    private static <T extends ParticleOptions> ParticleType<T> registerParticle(
            String name, boolean alwaysShow,
            MapCodec<T> codec,
            StreamCodec<FriendlyByteBuf, T> streamCodec) {
        ParticleType<T> type = new ParticleType<>(alwaysShow) {
            @Override
            public MapCodec<T> codec() {
                return codec;
            }

            @Override
            public StreamCodec<? super FriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
        return register(name, type);
    }

    private static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Jujutsu.id(name), type);
    }
 public static void register() {
        Jujutsu.LOGGER.info("Registering particles for " + Jujutsu.MODID);
    }
}
