package com.jujutsu.mixin;

import com.jujutsu.event.client.CameraEvents;
import com.jujutsu.event.client.KeyEvents;
import com.jujutsu.event.server.PlayerBonusEvents;
import com.jujutsu.registry.ModAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Smoother;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHandle()J", shift = At.Shift.AFTER))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != 1 && action != 2) {
            if (action == 0) {
                KeyEvents.MOUSE_BUTTON_RELEASED_EVENT.invoker().interact(client, button);
            }
        } else {
            KeyEvents.MOUSE_BUTTON_PRESSED_EVENT.invoker().interact(client, button);
        }
    }

    @ModifyArgs(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void updateMouse(Args args, @Local(argsOnly = true) double timeDelta, @Local(ordinal = 2) double f) {
        double x = args.get(0);
        double y = args.get(1);

        double maxAngle = client.player.getAttributeValue(ModAttributes.ROTATION_RESTRICTION);
        double speed = client.player.getAttributeValue(ModAttributes.ROTATION_SPEED);

        CameraEvents.CameraBonus bonus = CameraEvents.GET_CAMERA_BONUS_EVENT.invoker().interact(client.player).getRight();
        maxAngle += bonus.cameraRestriction();
        speed += bonus.cameraSpeed();

        x *= speed;
        y *= speed;

        if(Math.abs(x) > maxAngle) {
            x = Math.clamp(x, -maxAngle, maxAngle);
        }
        if(Math.abs(y) > maxAngle) {
            y = Math.clamp(y, -maxAngle, maxAngle);
        }

        args.set(0, x);
        args.set(1, y);
    }
}
