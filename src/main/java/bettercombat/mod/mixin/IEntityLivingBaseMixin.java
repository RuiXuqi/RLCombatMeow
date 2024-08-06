package bettercombat.mod.mixin;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface IEntityLivingBaseMixin {
	
	@Invoker("getArmSwingAnimationEnd")
	int invokeGetArmSwingAnimationEnd();
	
	@Accessor("lastDamage")
	float getLastDamage();
	
	@Accessor("lastDamage")
	void setLastDamage(float val);
}