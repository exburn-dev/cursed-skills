package com.jujutsu.client.hud;

import com.jujutsu.api.buff.BuffCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record BuffDisplayData(Holder<Attribute> attribute, BuffCondition condition) {
    public static final Codec<BuffDisplayData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(BuffDisplayData::attribute),
            BuffCondition.CODEC.fieldOf("condition").forGetter(BuffDisplayData::condition))
            .apply(instance, BuffDisplayData::new));

    public static final StreamCodec<ByteBuf, BuffDisplayData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
