package bettercombat.mod.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
	
	@Shadow public float prevSwingProgress;
	
	@Shadow public float swingProgress;
	
	@Shadow public EnumHand swingingHand;
	
	/**
	 * Properly reset vanilla swinging tracking when hand is switched, so the new hand doesn't try to finish the swing of the previous
	 */
	@Inject(
			method = "swingArm",
			at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;swingingHand:Lnet/minecraft/util/EnumHand;")
	)
	public void rlcombat_vanillaEntityLivingBase_swingArm(EnumHand hand, CallbackInfo ci) {
		if(this.swingingHand != hand) {
			this.swingProgress = 0;
			this.prevSwingProgress = 0;
		}
	}
}