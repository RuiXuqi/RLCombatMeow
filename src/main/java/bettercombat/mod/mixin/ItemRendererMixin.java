package bettercombat.mod.mixin;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.animation.util.AnimationEnum;
import bettercombat.mod.client.animation.util.CameraTransformHandler;
import bettercombat.mod.client.handler.AnimationHandler;
import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.util.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	
	@Shadow @Final private Minecraft mc;
	
	@Shadow private float equippedProgressOffHand;
	
	@Unique
	private final CameraTransformHandler rlcombat$mainhandCameraHandler = new CameraTransformHandler();
	
	@Unique
	private final CameraTransformHandler rlcombat$offhandCameraHandler = new CameraTransformHandler();
	
	/**
	 * Handle custom rendering for the mainhand
	 */
	@Redirect(
			method = "renderItemInFirstPerson(F)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", ordinal = 0)
	)
	public void rlcombat_vanillaItemRenderer_renderItemInFirstPerson_mainHand(ItemRenderer instance, AbstractClientPlayer player, float partialTicks, float interpPitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {
		boolean doCustomAnimations = ConfigurationHandler.client.customWeaponAttackAnimations;
		if(AnimationHandler.shouldSpecialRenderMainhand(player)) {
			GlStateManager.pushMatrix();
			
			//Handle idle breathing animation
			if(ConfigurationHandler.client.breathingTooCloseAnimationAllItems || doCustomAnimations) AnimationHandler.positionBreathingTooClose(partialTicks);
			
			GlStateManager.pushMatrix();
			
			//Handle players being able to switch mainhand
			EnumHandSide enumhandside = player.getPrimaryHand();
			boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
			int i = rightHanded ? 1 : -1;
			
			//Default weapon placement
			GlStateManager.translate((float)i * 0.56F, -0.52F, -0.72F);
			
			float swingProgressNew = EventHandlersClient.betterCombatMainhand.getSwingProgress(partialTicks);
			
			//Sync equipped progress
			AnimationHandler.equippedProgressMainhand = equipProgress;
			//TODO: handle this better
			if(!EventHandlersClient.betterCombatMainhand.firstRaise && doCustomAnimations) {
				if(swingProgressNew > 0) {
					if(EventHandlersClient.betterCombatMainhand.getAttackAnimationEnum() == AnimationEnum.STAB || EventHandlersClient.betterCombatMainhand.getAttackAnimationEnum() == AnimationEnum.STAB_CAESTUS) {
						//Don't do re-equip animation for stabs during attack swing
						AnimationHandler.equippedProgressMainhand = 0.0F;
					}
				}
				else if(swingProgress > 0) {
					if(EventHandlersClient.betterCombatMainhand.getMiningAnimationEnum() == AnimationEnum.STAB || EventHandlersClient.betterCombatMainhand.getMiningAnimationEnum() == AnimationEnum.STAB_CAESTUS) {
						//Don't do re-equip animation for stabs during mining swing
						AnimationHandler.equippedProgressMainhand = 0.0F;
					}
				}
				else {
					if(EventHandlersClient.betterCombatMainhand.getAttackAnimationEnum() == AnimationEnum.STAB || EventHandlersClient.betterCombatMainhand.getAttackAnimationEnum() == AnimationEnum.STAB_CAESTUS) {
						//Don't do re-equip animation for stabs after attack swing (Doesn't function properly for handling after mining swing is done but thats niche for now
						AnimationHandler.equippedProgressMainhand = 0.0F;
					}
				}
			}
			
			//Camera movement
			if(doCustomAnimations) rlcombat$mainhandCameraHandler.animateCamera(EventHandlersClient.betterCombatMainhand, rightHanded, swingProgressNew, partialTicks);
			
			//Weapon raise up after swing
			GlStateManager.translate(0.0F, AnimationHandler.equippedProgressMainhand * -0.6F, 0.0F);
			
			//Hand swinging through custom timing
			if(swingProgressNew > 0) {
				if(doCustomAnimations) {
					//Animation-specific weapon placement
					AnimationHandler.positionMainWeaponAttacking(rightHanded, partialTicks);
					//Move weapon away during other hand attack
					AnimationHandler.positionMainhandAwayIfOffhandAttacking(rightHanded, partialTicks);
					
					//Swing animation
					EventHandlersClient.betterCombatMainhand.getActiveAttackAnimation().animationMainhand(rightHanded, swingProgressNew, partialTicks);
				}
				else {
					//Default punching animation
					float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgressNew) * (float)Math.PI);
					float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgressNew) * ((float)Math.PI * 2F));
					float f2 = -0.2F * MathHelper.sin(swingProgressNew * (float)Math.PI);
					float f3 = MathHelper.sin(swingProgressNew * swingProgressNew * (float)Math.PI);
					float f4 = MathHelper.sin(MathHelper.sqrt(swingProgressNew) * (float)Math.PI);
					GlStateManager.translate((float)i * f, f1, f2);
					GlStateManager.rotate((float)i * (45.0F + f3 * -20.0F), 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate((float)i * f4 * -20.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(f4 * -80.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
				}
			}
			//Hand swinging through default timing, likely mining
			else if(swingProgress > 0) {
				if(ConfigurationHandler.client.customWeaponMiningAnimations && doCustomAnimations) {
					//Lower upper limit of dig animation while mining
					if(EventHandlersClient.betterCombatMainhand.getMiningAnimationEnum() == AnimationEnum.DIG && AnimationHandler.equippedProgressMainhand < 0.75) {
						GlStateManager.translate(0.0F, (0.75F - AnimationHandler.equippedProgressMainhand) * -0.6F, 0.0F);
					}
					//Increase lower limit of chop animation while mining
					if(EventHandlersClient.betterCombatMainhand.getMiningAnimationEnum() == AnimationEnum.CHOP && AnimationHandler.equippedProgressMainhand > 0.5F) {
						GlStateManager.translate(0.0F, (0.5F - AnimationHandler.equippedProgressMainhand) * -0.6F, 0.0F);
					}
					//Lower upper limit of chop animation while mining
					if(EventHandlersClient.betterCombatMainhand.getMiningAnimationEnum() == AnimationEnum.CHOP && AnimationHandler.equippedProgressMainhand < 0.25F) {
						GlStateManager.translate(0.0F, (0.25F - AnimationHandler.equippedProgressMainhand) * -0.6F, 0.0F);
					}
					
					//Use placement to match animation starting point, but don't move away when other hand is swinging
					AnimationHandler.positionMainWeaponMining(rightHanded, partialTicks);
					
					//Run swing animation but not camera
					EventHandlersClient.betterCombatMainhand.getActiveMiningAnimation().animationMainhand(rightHanded, swingProgress, partialTicks);
				}
				else {
					//Default punching animation
					float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
					float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
					float f2 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
					float f3 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
					float f4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
					GlStateManager.translate((float)i * f, f1, f2);
					GlStateManager.rotate((float)i * (45.0F + f3 * -20.0F), 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate((float)i * f4 * -20.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(f4 * -80.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
				}
			}
			//Hand not swinging, do default placement animations
			else if(doCustomAnimations) {
				//Animation-specific weapon placement
				AnimationHandler.positionMainWeaponAttacking(rightHanded, partialTicks);
				//Move weapon away during other hand attack
				AnimationHandler.positionMainhandAwayIfOffhandAttacking(rightHanded, partialTicks);
			}
			
			instance.renderItemSide(player, stack, rightHanded ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
		else {
			GlStateManager.pushMatrix();
			//Handle breathing/too close for all items if enabled
			if(ConfigurationHandler.client.breathingTooCloseAnimationAllItems) AnimationHandler.positionBreathingTooClose(partialTicks);
			instance.renderItemInFirstPerson(player, partialTicks, interpPitch, hand, swingProgress, stack, equipProgress);
			GlStateManager.popMatrix();
		}
		
	}
	
	/**
	 * Handle custom rendering for the offhand
	 */
	@Redirect(
			method = "renderItemInFirstPerson(F)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", ordinal = 1)
	)
	public void rlcombat_vanillaItemRenderer_renderItemInFirstPerson_offHand(ItemRenderer instance, AbstractClientPlayer player, float partialTicks, float interpPitch, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {
		boolean doCustomAnimations = ConfigurationHandler.client.customWeaponAttackAnimations;
		if(AnimationHandler.shouldSpecialRenderOffhand(player)) {
			GlStateManager.pushMatrix();
			
			//Handle idle breathing animation
			if(ConfigurationHandler.client.breathingTooCloseAnimationAllItems && doCustomAnimations) AnimationHandler.positionBreathingTooClose(partialTicks);
			
			GlStateManager.pushMatrix();
			
			//Handle players being able to switch mainhand
			EnumHandSide enumhandside = player.getPrimaryHand();
			boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
			int i = rightHanded ? -1 : 1;
			
			//Default weapon placement
			GlStateManager.translate((float)i * 0.56F, -0.52F, -0.72F);
			
			float swingProgressNew = EventHandlersClient.betterCombatOffhand.getSwingProgress(partialTicks);
			
			//Sync equipped progress
			AnimationHandler.equippedProgressOffhand = equipProgress;
			//TODO: handle this better
			if(!EventHandlersClient.betterCombatOffhand.firstRaise && doCustomAnimations) {
				if(swingProgressNew > 0) {
					if(EventHandlersClient.betterCombatOffhand.getAttackAnimationEnum() == AnimationEnum.STAB || EventHandlersClient.betterCombatOffhand.getAttackAnimationEnum() == AnimationEnum.STAB_CAESTUS) {
						//Don't do re-equip animation for stabs during attack swing
						AnimationHandler.equippedProgressOffhand = 0.0F;
					}
				}
				else if(swingProgress > 0) {
					if(EventHandlersClient.betterCombatOffhand.getMiningAnimationEnum() == AnimationEnum.STAB || EventHandlersClient.betterCombatOffhand.getMiningAnimationEnum() == AnimationEnum.STAB_CAESTUS) {
						//Don't do re-equip animation for stabs during mining swing
						AnimationHandler.equippedProgressOffhand = 0.0F;
					}
				}
				else {
					if(EventHandlersClient.betterCombatOffhand.getAttackAnimationEnum() == AnimationEnum.STAB || EventHandlersClient.betterCombatOffhand.getAttackAnimationEnum() == AnimationEnum.STAB_CAESTUS) {
						//Don't do re-equip animation for stabs after attack swing (Doesn't function properly for handling after mining swing is done but thats niche for now
						AnimationHandler.equippedProgressOffhand = 0.0F;
					}
				}
			}
			
			//Camera movement
			if(doCustomAnimations) rlcombat$offhandCameraHandler.animateCamera(EventHandlersClient.betterCombatOffhand, !rightHanded, swingProgressNew, partialTicks);
			
			//Weapon raise up after swing
			GlStateManager.translate(0.0F, AnimationHandler.equippedProgressOffhand * -0.6F, 0.0F);
			
			//Hand swinging through custom timing
			if(swingProgressNew > 0) {
				if(doCustomAnimations) {
					//Animation-specific weapon placement
					AnimationHandler.positionOffhandWeaponAttacking(rightHanded, partialTicks);
					//Move weapon away during other hand attack
					AnimationHandler.positionOffhandAwayIfMainhandAttacking(rightHanded, partialTicks);
					
					//Swing animation
					EventHandlersClient.betterCombatOffhand.getActiveAttackAnimation().animationOffhand(rightHanded, swingProgressNew, partialTicks);
				}
				else {
					//Default punching animation
					float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgressNew) * (float)Math.PI);
					float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgressNew) * ((float)Math.PI * 2F));
					float f2 = -0.2F * MathHelper.sin(swingProgressNew * (float)Math.PI);
					float f3 = MathHelper.sin(swingProgressNew * swingProgressNew * (float)Math.PI);
					float f4 = MathHelper.sin(MathHelper.sqrt(swingProgressNew) * (float)Math.PI);
					GlStateManager.translate((float)i * f, f1, f2);
					GlStateManager.rotate((float)i * (45.0F + f3 * -20.0F), 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate((float)i * f4 * -20.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(f4 * -80.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
				}
			}
			//Hand swinging through default timing, likely mining
			else if(swingProgress > 0) {
				if(ConfigurationHandler.client.customWeaponMiningAnimations && doCustomAnimations) {
					//Lower upper limit of dig animation while mining
					if(EventHandlersClient.betterCombatOffhand.getMiningAnimationEnum() == AnimationEnum.DIG && AnimationHandler.equippedProgressOffhand < 0.75) {
						GlStateManager.translate(0.0F, (0.75F - AnimationHandler.equippedProgressOffhand) * -0.6F, 0.0F);
					}
					//Increase lower limit of chop animation while mining
					if(EventHandlersClient.betterCombatOffhand.getMiningAnimationEnum() == AnimationEnum.CHOP && AnimationHandler.equippedProgressOffhand > 0.5F) {
						GlStateManager.translate(0.0F, (0.5F - AnimationHandler.equippedProgressOffhand) * -0.6F, 0.0F);
					}
					//Lower upper limit of chop animation while mining
					if(EventHandlersClient.betterCombatOffhand.getMiningAnimationEnum() == AnimationEnum.CHOP && AnimationHandler.equippedProgressOffhand < 0.25F) {
						GlStateManager.translate(0.0F, (0.25F - AnimationHandler.equippedProgressOffhand) * -0.6F, 0.0F);
					}
					
					//Use placement to match animation starting point, but don't move away when other hand is swinging
					AnimationHandler.positionOffhandWeaponMining(rightHanded, partialTicks);
					
					//Run swing animation but not camera
					EventHandlersClient.betterCombatOffhand.getActiveMiningAnimation().animationOffhand(rightHanded, swingProgress, partialTicks);
				}
				else {
					//Default punching animation
					float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
					float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
					float f2 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
					float f3 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
					float f4 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
					GlStateManager.translate((float)i * f, f1, f2);
					GlStateManager.rotate((float)i * (45.0F + f3 * -20.0F), 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate((float)i * f4 * -20.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(f4 * -80.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
				}
			}
			//Hand not swinging, do default placement animations
			else if(doCustomAnimations) {
				//Animation-specific weapon placement
				AnimationHandler.positionOffhandWeaponAttacking(rightHanded, partialTicks);
				//Move weapon away during other hand attack
				AnimationHandler.positionOffhandAwayIfMainhandAttacking(rightHanded, partialTicks);
			}
			
			instance.renderItemSide(player, stack, rightHanded ? ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, rightHanded);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
		else {
			GlStateManager.pushMatrix();
			//Handle breathing/too close for all items if enabled
			if(ConfigurationHandler.client.breathingTooCloseAnimationAllItems) AnimationHandler.positionBreathingTooClose(partialTicks);
			instance.renderItemInFirstPerson(player, partialTicks, interpPitch, hand, swingProgress, stack, equipProgress);
			GlStateManager.popMatrix();
		}
	}
	
	/**
	 * Make offhand cooldown rendering use actual cooldown values
	 */
	@Redirect(
			method = "updateEquippedItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 3)
	)
	public float rlcombat_vanillaItemRenderer_updateEquippedItem(float num, float min, float max) {
		float val = num + this.equippedProgressOffHand;
		if(val == 1) {
			CapabilityOffhandCooldown capability = this.mc.player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null);
			if(capability != null) {
				float ohCooldown = 0;
				int ohCooldownBeginning = capability.getOffhandBeginningCooldown();
				if(ohCooldownBeginning > 0) ohCooldown = capability.getOffhandCooldown()/(float)ohCooldownBeginning;
				val = Math.abs(1.0F - ohCooldown);
				return MathHelper.clamp((val * val * val) - this.equippedProgressOffHand, -0.4F, 0.4F);
			}
		}
		return MathHelper.clamp(val - this.equippedProgressOffHand, -0.4F, 0.4F);
	}
}