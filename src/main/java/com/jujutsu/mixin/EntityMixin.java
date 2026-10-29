package com.jujutsu.mixin;

import com.jujutsu.api.ability.client.ClientComponentContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void isInvisibleTo(Player player, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof Player && level().isClientSide()) {
            if(ClientComponentContainer.abilityComponent.isTranslucent()) {
                cir.setReturnValue(false);
            }
        }
    }
}
