package com.jujutsu.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

public interface IAnimatedPlayer {
    ModifierLayer<IAnimation> jujutsu_getModAnimation();
}
