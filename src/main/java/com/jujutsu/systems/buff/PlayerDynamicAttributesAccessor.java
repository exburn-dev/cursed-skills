package com.jujutsu.systems.buff;

public interface PlayerDynamicAttributesAccessor {
    float getDynamicSpeed();
    void setDynamicSpeed(float value);

    float getDynamicJumpVelocityMultiplier();
    void setDynamicJumpVelocityMultiplier(float value);
}
