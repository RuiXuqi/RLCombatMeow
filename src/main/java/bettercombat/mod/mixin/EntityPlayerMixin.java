package bettercombat.mod.mixin;

import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Inject(
            method = "attackTargetEntityWithCurrentItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void bettercombat_EntityPlayer_attackTargetEntityWithCurrentItem(Entity targetEntity, CallbackInfo ci) {
        if(targetEntity != null &&
                !((EntityPlayer)(Object)this).world.isRemote &&
                ConfigurationHandler.enableMixinCompatFallback) {
            //Catch attacks bypassing clientside checks (Typically mods that send their own packets for changing reach)
            Helpers.attackTargetEntityItem(((EntityPlayer)(Object)this), targetEntity, false, 0, 0, 0);
            ci.cancel();
        }
    }
}