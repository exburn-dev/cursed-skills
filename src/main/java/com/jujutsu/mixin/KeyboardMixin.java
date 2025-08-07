package com.jujutsu.mixin;

import com.jujutsu.event.client.KeyEvents;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHandle()J", shift = At.Shift.AFTER))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action != 1 && action != 2) {
            if (action == 0) {
                KeyEvents.KEY_RELEASED_EVENT.invoker().interact(client, key, scancode);
            }
        } else {
            KeyEvents.KEY_PRESSED_EVENT.invoker().interact(client, key, scancode);
        }
    }
}
