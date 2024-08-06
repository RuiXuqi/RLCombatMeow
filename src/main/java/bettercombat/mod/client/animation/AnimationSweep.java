package bettercombat.mod.client.animation;

import bettercombat.mod.client.animation.util.BetterCombatHand;
import bettercombat.mod.client.animation.util.IAnimation;
import bettercombat.mod.client.handler.AnimationHandler;
import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.util.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public class AnimationSweep implements IAnimation {

    @Override
    public void animationMainhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateSwing(rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatMainhand);
    }

    @Override
    public void animationCameraMainhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateCamera(rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatMainhand);
    }

    @Override
    public void animationOffhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateSwing(!rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatOffhand);
    }

    @Override
    public void animationCameraOffhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateCamera(!rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatOffhand);
    }

    @Override
    public void setActive(BetterCombatHand hand) {
        hand.setSwingTimestampSound(0.6F);

        hand.moveRightVariance = hand.randomMoveVariance();
        hand.moveUpVariance = hand.randomMoveVariance();
        hand.moveCloseVariance = hand.randomMoveVariance();

        hand.rotateUpVariance = hand.randomPreciseRotationVariance();
        hand.rotateCounterClockwiseVariance = hand.randomRotationVariance();
        hand.rotateLeftVariance = hand.randomRotationVariance();
    }
    
    private void animateSwing(boolean rightHanded, float swingProgress, float partialTick, BetterCombatHand hand) {
        int i = rightHanded ? 1 : -1;
        
        float moveRight = 0.0F;
        float moveUp = 0.0F;
        float moveClose = 0.0F;
        float rotateUp = 0.0F;
        float rotateCounterClockwise = 0.0F;
        float rotateLeft = 0.0F;
        float closeCap = 0.4F - (AnimationHandler.lastTooCloseAmount + (AnimationHandler.tooCloseAmount - AnimationHandler.lastTooCloseAmount) * partialTick);
        
        rotateUp = -AnimationHandler.minMult(swingProgress, 6.0F, 140.0F + closeCap * 50.0F); /* Sweep = Up */
        rotateCounterClockwise = AnimationHandler.minMult(swingProgress, 12.0F, 150.0F) - AnimationHandler.minMult(swingProgress, 3.0F, 50.0F + closeCap * 100.0F) - swingProgress * 15.0F; /* Sweep = Left and To */
        rotateLeft = AnimationHandler.minMult(swingProgress, 6.0F, 85.0F); /* Sweep = Twist Clockwise -- * */
        
        /* Move right very fast at the start */
        moveRight = AnimationHandler.minMult(swingProgress, 12.0F, 3.5F) + 0.5F;
        moveUp = AnimationHandler.min(swingProgress*10.0F, 0.47F);
        moveClose = -AnimationHandler.min(swingProgress*10.0F, closeCap);
        
        if(swingProgress > 0.6F) {
            /* Move left slowly as the animation has reached the center */
            moveRight -= 4.5F + closeCap - (1.0F - MathHelper.sin(swingProgress * AnimationHandler.PI)) * 0.3F;
            
            if(swingProgress > 0.85F) {
                moveUp -= MathHelper.sin(swingProgress - 0.85F) * 6.0F;
                moveClose += MathHelper.sin(swingProgress - 0.85F) * 6.0F;
                moveRight += (swingProgress - 0.85F) * 5.5F;
            }
        }
        else {
            /* Move left fast until 0.6 energy */
            moveRight -= AnimationHandler.min(MathHelper.sin(swingProgress * AnimationHandler.PI) * 5.5F, 4.5F + closeCap);
        }
        
        GlStateManager.translate(
                /* X */ 1.2F * i * moveRight * hand.moveRightVariance,
                /* Y */ 1.1F * moveUp * hand.moveUpVariance,
                /* Z */ moveClose * hand.moveCloseVariance);
        
        GlStateManager.rotate(rotateUp * hand.rotateUpVariance, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(i * rotateCounterClockwise * hand.rotateCounterClockwiseVariance, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(i * rotateLeft * hand.rotateLeftVariance, 0.0F, 0.0F, 1.0F);
    }
    
    private void animateCamera(boolean rightHanded, float swingProgress, float partialTick, BetterCombatHand hand) {
        //Reduce movement with less cooldown
        float f = (float)hand.attackCooldown / 12.0F;
        
        /* Camera */
        EntityPlayer player = Minecraft.getMinecraft().player;
        float rotation = MathHelper.cos(0.5F + swingProgress * 2.5F * AnimationHandler.PI);
        player.cameraPitch += rotation * ConfigurationHandler.client.cameraPitchSwing * f;
        if(rightHanded) {
            player.rotationYaw -= rotation * ConfigurationHandler.client.cameraYawSwing * f;
        }
        else {
            player.rotationYaw += rotation * ConfigurationHandler.client.cameraYawSwing * f;
        }
    }
}