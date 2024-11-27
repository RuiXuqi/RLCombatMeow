package bettercombat.mod.mixin;

import bettercombat.mod.compat.EnchantCompatHandler;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    
    /**
     * Add a fallback catch for if mods send their own attack packets so RLCombat events still get posted
     */
    @Inject(
            method = "attackTargetEntityWithCurrentItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void bettercombat_EntityPlayer_attackTargetEntityWithCurrentItem(Entity targetEntity, CallbackInfo ci) {
        if(targetEntity != null &&
                !((EntityPlayer)(Object)this).world.isRemote &&
                ConfigurationHandler.server.enableMixinCompatFallback) {
            Helpers.attackTargetEntityItem(((EntityPlayer)(Object)this), targetEntity, false, 0, 0, 0);
            ci.cancel();
        }
    }
    
    @Unique
    private EntityLivingBase bettercombat$shieldattacker;
    
    @Inject(
            method = "blockUsingShield",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;disableShield(Z)V", shift = At.Shift.BEFORE)
    )
    public void bettercombat_EntityPlayer_blockUsingShield_inject(EntityLivingBase attacker, CallbackInfo ci) {
        this.bettercombat$shieldattacker = attacker;
    }
    
    /**
     * Fix attacking a player blocking with a shield checking for efficiency on the shield and not the attacker's weapon
     * Also make the chance to disable the shield based on the attacker's cooled strength, not just always disable
     */
    @Redirect(
            method = "blockUsingShield",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;disableShield(Z)V")
    )
    public void bettercombat_EntityPlayer_blockUsingShield_redirect(EntityPlayer instance, boolean strong) {
        EntityLivingBase attacker = this.bettercombat$shieldattacker;
        if(attacker == null) {
            instance.disableShield(strong);
        }
        else {
            float f = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05F;
            
            //Vanilla always passes true which makes these checks pointless, make it actually based on cooled strength
            if(EnchantCompatHandler.attackEntityFromCooledStrength > 0.9F) {
                f += 0.75F;
            }
            
            if(instance.getRNG().nextFloat() < f) {
                instance.getCooldownTracker().setCooldown(instance.getActiveItemStack().getItem(), 100);
                instance.resetActiveHand();
                instance.world.setEntityState(instance, (byte)30);
            }
        }
    }
}