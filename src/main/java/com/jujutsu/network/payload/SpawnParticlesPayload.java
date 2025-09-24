package com.jujutsu.network.payload;

import com.jujutsu.network.ModNetworkConstants;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public record SpawnParticlesPayload(List<SpawnParticleData> particles) implements CustomPayload {
    public static final Id<SpawnParticlesPayload> ID = new Id<>(ModNetworkConstants.SPAWN_PARTICLES_ID);
    public static final PacketCodec<RegistryByteBuf, SpawnParticlesPayload> CODEC = PacketCodec.tuple(
            SpawnParticleData.CODEC.collect(PacketCodecs.toList()), SpawnParticlesPayload::particles,
            SpawnParticlesPayload::new);

    public static void receiveOnClient(SpawnParticlesPayload payload, ClientPlayNetworking.Context context) {
        World world = context.player().getWorld();

        for(SpawnParticleData data: payload.particles()) {
            world.addParticle(data.effect, data.pos().x, data.pos().y, data.pos().z, data.velocity().x, data.velocity().y, data.velocity().z);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Builder {
        private final List<SpawnParticleData> list = new ArrayList<>();

        public Builder addParticleData(ParticleEffect effect, Vec3d pos, Vec3d velocity) {
            list.add(new SpawnParticleData(effect, pos, velocity));
            return this;
        }

        public SpawnParticlesPayload build() {
            return new SpawnParticlesPayload(list);
        }
    }

    private record SpawnParticleData(ParticleEffect effect, Vec3d pos, Vec3d velocity) {
        private static final PacketCodec<ByteBuf, Vec3d> VEC_CODEC = PacketCodecs.DOUBLE.collect(PacketCodecs.toList())
                .xmap((coordinates) -> {
                    return Util.decodeFixedLengthList(coordinates, 3).map((coords) -> {
                        return new Vec3d((Double)coords.get(0), (Double)coords.get(1), (Double)coords.get(2));
                    }).getOrThrow();
                }, (vec) -> {
                    return List.of(vec.getX(), vec.getY(), vec.getZ());
                });

        private static final PacketCodec<RegistryByteBuf, SpawnParticleData> CODEC = PacketCodec.tuple(
                ParticleTypes.PACKET_CODEC, SpawnParticleData::effect,
                VEC_CODEC, SpawnParticleData::pos,
                VEC_CODEC, SpawnParticleData::velocity,
                SpawnParticleData::new
        );
    }
}
