package bettercombat.mod.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin {
	
	@Unique
	private EnumHand rlcombat$hand = null;
	
	@Inject(
			method = "swingArm",
			at = @At("HEAD")
	)
	public void rlcombat_vanillaEntityPlayerMP_swingArm_inject(EnumHand hand, CallbackInfo ci) {
		this.rlcombat$hand = hand;
	}
	
	/**
	 * Don't reset cooldown for mainhand when offhand is swung
	 */
	@Redirect(
			method = "swingArm",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;resetCooldown()V")
	)
	public void rlcombat_vanillaEntityPlayerMP_swingArm_redirect(EntityPlayerMP instance) {
		if(this.rlcombat$hand != EnumHand.OFF_HAND) instance.resetCooldown();
	}
}