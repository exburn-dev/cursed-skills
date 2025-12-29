package com.jujutsu.mixin;

import com.jujutsu.systems.ability.client.ClientComponentContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract World getWorld();

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void isInvisibleTo(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(isPlayer() && getWorld().isClient()) {
            if(ClientComponentContainer.abilityComponent.isTranslucent()) {
                cir.setReturnValue(false);
            }
        }
    }
}
