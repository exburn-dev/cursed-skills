package com.jujutsu.ability.active;

import com.jujutsu.systems.ability.AbilityData;
import com.jujutsu.systems.ability.AbilityInstance;
import com.jujutsu.systems.ability.AbilityType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;

public class TestAbility extends AbilityType {
    private static final Codec<TestAbilityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(TestAbilityData::radius)
    ).apply(instance, TestAbilityData::new));

    public TestAbility(int cooldownTime) {
        super(cooldownTime, false);
    }

    @Override
    public void start(PlayerEntity player, AbilityInstance instance) {
        TestAbilityData data = instance.getAbilityData(TestAbilityData.class, () -> (TestAbilityData) getInitialData());

        data = new TestAbilityData(data.radius() + 1);
        instance.setAbilityData(data);

    }

    @Override
    public void tick(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public void end(PlayerEntity player, AbilityInstance instance) {

    }

    @Override
    public boolean isFinished(PlayerEntity player, AbilityInstance instance) {
        return instance.getUseTime() >= 40;
    }

    @Override
    public AbilityData getInitialData() {
        return new TestAbilityData(0);
    }

    @Override
    public Codec<? extends AbilityData> getCodec() {
        return CODEC;
    }

    public record TestAbilityData(int radius) implements AbilityData { }
}
