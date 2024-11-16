package bettercombat.mod.mixin;

import bettercombat.mod.compat.EnchantCompatHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Make enchantment checks hand-sensitive
 */
@Mixin(value = EnchantmentHelper.class, priority = 2000)
public abstract class EnchantmentHelperMixin {
	
	@Redirect(
			method = "applyThornEnchantments",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHeldItemMainhand()Lnet/minecraft/item/ItemStack;")
	)
	private static ItemStack rlcombat_vanillaEnchantmentHelper_applyThornEnchantments(EntityLivingBase instance) {
		if(EnchantCompatHandler.thornsFromOffhand) return instance.getHeldItemOffhand();
		return instance.getHeldItemMainhand();
	}
	
	@Redirect(
			method = "applyArthropodEnchantments",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHeldItemMainhand()Lnet/minecraft/item/ItemStack;")
	)
	private static ItemStack rlcombat_vanillaEnchantmentHelper_applyArthropodEnchantments(EntityLivingBase instance) {
		if(EnchantCompatHandler.arthropodFromOffhand) return instance.getHeldItemOffhand();
		return instance.getHeldItemMainhand();
	}
	
	@Redirect(
			method = "getKnockbackModifier",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getMaxEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)I")
	)
	private static int rlcombat_vanillaEnchantmentHelper_getKnockbackModifier(Enchantment j, EntityLivingBase entity) {
		if(EnchantCompatHandler.knockbackFromOffhand) return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemOffhand());
		return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemMainhand());
	}
	
	@Redirect(
			method = "getFireAspectModifier",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getMaxEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)I")
	)
	private static int rlcombat_vanillaEnchantmentHelper_getFireAspectModifier(Enchantment j, EntityLivingBase entity) {
		if(EnchantCompatHandler.fireAspectFromOffhand) return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemOffhand());
		return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemMainhand());
	}
	
	@Redirect(
			method = "getEfficiencyModifier",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getMaxEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)I")
	)
	private static int rlcombat_vanillaEnchantmentHelper_getEfficiencyModifier(Enchantment j, EntityLivingBase entity) {
		if(EnchantCompatHandler.efficiencyFromOffhand) return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemOffhand());
		return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemMainhand());
	}
	
	@Redirect(
			method = "getLootingModifier",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getMaxEnchantmentLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)I")
	)
	private static int rlcombat_vanillaEnchantmentHelper_getLootingModifier(Enchantment j, EntityLivingBase entity) {
		if(EnchantCompatHandler.lootingFromOffhand) return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemOffhand());
		return EnchantmentHelper.getEnchantmentLevel(j, entity.getHeldItemMainhand());
	}
}