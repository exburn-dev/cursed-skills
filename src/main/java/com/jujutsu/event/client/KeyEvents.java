package com.jujutsu.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public final class KeyEvents {
    public static final Event<KeyPressedCallback> KEY_PRESSED_EVENT = EventFactory.createArrayBacked(KeyPressedCallback.class,
            (listeners) -> (client, key, scanCode) -> {
                for(KeyPressedCallback listener: listeners) {
                    ActionResult result = listener.interact(client, key, scanCode);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    public static final Event<KeyReleasedCallback> KEY_RELEASED_EVENT = EventFactory.createArrayBacked(KeyReleasedCallback.class,
            (listeners) -> (client, key, scanCode) -> {
                for(KeyReleasedCallback listener: listeners) {
                    ActionResult result = listener.interact(client, key, scanCode);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface KeyPressedCallback {
        ActionResult interact(MinecraftClient client, int keyCode, int scanCode);
    }

    @FunctionalInterface
    public interface KeyReleasedCallback {
        ActionResult interact(MinecraftClient client, int keyCode, int scanCode);
    }
}
