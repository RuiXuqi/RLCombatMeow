package bettercombat.mod.client.animation.util;

import bettercombat.mod.client.handler.AnimationHandler;
import bettercombat.mod.util.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class CameraTransformHandler {
	
	private float prevProgress = 0.0F;
	private float prevPartial = 0.0F;
	private float totalDeltaPitch = 0.0F;
	private float totalDeltaYaw = 0.0F;
	
	public void animateCamera(BetterCombatHand hand, boolean rightHand, float swingProgress, float partialTick) {
		int i = rightHand ? 1 : -1;
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		//When swing ends or is reset, compensate for leftover movement
		if(prevProgress > swingProgress) {
			player.rotationPitch = player.rotationPitch - totalDeltaPitch;
			player.rotationYaw = player.rotationYaw - totalDeltaYaw;
			totalDeltaPitch = 0.0F;
			totalDeltaYaw = 0.0F;
		}
		prevProgress = swingProgress;
		
		//Only attempt moving once per real tick, to avoid fps affecting distance
		if(prevPartial <= partialTick) {
			prevPartial = partialTick;
			return;
		}
		prevPartial = partialTick;
		
		//Don't bother calculating further if swing progress is 0 or 1
		if(swingProgress == 0.0F || swingProgress == 1.0F) return;
		float rotationMult = MathHelper.sin(AnimationHandler.PI + swingProgress * 2.0F * AnimationHandler.PI);
		
		//More movement for slower cooldown, faster cooldown less movement
		float f = (float)hand.attackCooldown / 12.0F;
		
		//Calculate and use delta, store for compensation if swing is interrupted
		float deltaPitch = -1.0F * rotationMult * ConfigurationHandler.client.cameraPitchSwing * hand.getActiveAttackAnimation().getCameraPitchMult() * f;
		float deltaYaw = i * rotationMult * ConfigurationHandler.client.cameraYawSwing * hand.getActiveAttackAnimation().getCameraYawMult() * f;
		totalDeltaPitch += deltaPitch;
		totalDeltaYaw += deltaYaw;
		
		player.rotationPitch = player.rotationPitch + deltaPitch;
		player.rotationYaw = player.rotationYaw + deltaYaw;
	}
}