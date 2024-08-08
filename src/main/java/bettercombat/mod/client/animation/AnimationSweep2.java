package bettercombat.mod.client.animation;

import bettercombat.mod.client.animation.util.BetterCombatHand;
import bettercombat.mod.client.handler.AnimationHandler;
import bettercombat.mod.client.handler.EventHandlersClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public class AnimationSweep2 extends AnimationSweep {
    
    @Override
    public void animationMainhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateSwing(rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatMainhand);
    }
    
    @Override
    public void animationOffhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateSwing(!rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatOffhand);
    }
    
    @Override
    public void setActive(BetterCombatHand hand) {
        hand.setSwingTimestampSound(0.4F);

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
        float closeCap = 1.0F - (AnimationHandler.lastTooCloseAmount + (AnimationHandler.tooCloseAmount - AnimationHandler.lastTooCloseAmount) * partialTick) * 2.5F;
        
        if(swingProgress > 0.2F) {
            if(swingProgress > 0.6F) {
                /* (SWEEP) HIGHEST | 0.6F -> 1 */
                /* ======= */
                float energy = (swingProgress - 0.6F);
                
                rotateCounterClockwise = energy * -400.0F;
                moveUp = 0.2F - energy * 0.5F;
                moveRight = -1.55F + energy * 30.0F;
                rotateLeft = 83.0F + energy * 12.5F;
                moveClose = (-energy * 3.0F) * closeCap;
            }
            else {
                if(swingProgress > 0.4F) {
                    /* (HOLD) HIGH | 0.4F -> 0.6F */
                    /* ==== */
                    float energy = (swingProgress - 0.4F);
                    
                    moveUp = 0.2F;
                    moveRight = -1.5F - energy * 0.25F;
                    rotateLeft = 75.0F + energy * 40.0F;
                    moveClose = (0.2F - energy) * closeCap;
                }
                else {
                    /* (FAST READY) LOW | 0.2F -> 0.4F */
                    /* ==== */
                    float energy = (swingProgress - 0.2F);
                    
                    /* Fast > Slow */
                    moveUp = -0.2F + MathHelper.sin(energy* AnimationHandler.PI*2.5F) * 0.4F;
                    moveRight = -1.2F - MathHelper.sin(energy*AnimationHandler.PI*2.5F) * 0.3F;
                    rotateLeft = 25.0F + energy * 250.0F;
                    moveClose = swingProgress * 0.5F * closeCap;
                }
            }
        }
        else {
            /* (FAST READY) LOWEST | 0.0F -> 0.2F */
            /* ==== */
            /* Slow > Fast | (1.0F - MathHelper.cos(f*PI*2.5F)) */
            moveUp = -swingProgress;
            moveRight = swingProgress * -6.0F;
            rotateLeft = swingProgress * 125.0F;
            moveClose = swingProgress * 0.5F * closeCap;
        }
        
        GlStateManager.translate(
                /* X */ 0.75F * i * moveRight * hand.moveRightVariance,
                /* Y */ 1.15F * moveUp * hand.moveUpVariance,
                /* Z */ moveClose * hand.moveCloseVariance);
        
        GlStateManager.rotate(rotateUp * hand.rotateUpVariance, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(i * rotateCounterClockwise * hand.rotateCounterClockwiseVariance, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(i * rotateLeft * hand.rotateLeftVariance, 0.0F, 0.0F, 1.0F);
    }
    
    @Override
    public float getCameraPitchMult() {
        return -0.5F;
    }
    
    @Override
    public float getCameraYawMult() {
        return 1.0F;
    }
}