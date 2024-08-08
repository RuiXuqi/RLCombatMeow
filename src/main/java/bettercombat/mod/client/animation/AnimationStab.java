package bettercombat.mod.client.animation;

import bettercombat.mod.client.animation.util.BetterCombatHand;
import bettercombat.mod.client.animation.util.IAnimation;
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
public class AnimationStab implements IAnimation {

    @Override
    public void animationMainhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateSwing(rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatMainhand);
    }

    @Override
    public void animationOffhand(boolean rightHanded, float swingProgress, float partialTick) {
        this.animateSwing(!rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatOffhand);
    }

    @Override
    public void positionMainhand(boolean rightHanded, float partialTick) {
        /* If the weapon is a spear, rotate it accordingly */
        GlStateManager.rotate(-44.0F - (EventHandlersClient.betterCombatMainhand.sprintingTimerPrev +
                (partialTick * (EventHandlersClient.betterCombatMainhand.sprintingTimer - EventHandlersClient.betterCombatMainhand.sprintingTimerPrev))
        ), 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -(AnimationHandler.lastTooCloseAmount + (AnimationHandler.tooCloseAmount - AnimationHandler.lastTooCloseAmount) * partialTick), 0.0F);
    }

    @Override
    public void positionOffhand(boolean rightHanded, float partialTick) {
        /* If the weapon is a spear, rotate it accordingly */
        GlStateManager.rotate(-44.0F - (EventHandlersClient.betterCombatOffhand.sprintingTimerPrev +
                (partialTick * (EventHandlersClient.betterCombatOffhand.sprintingTimer - EventHandlersClient.betterCombatOffhand.sprintingTimerPrev))
        ),1.0F,0.0F,0.0F);
        GlStateManager.translate(0.0F, -(AnimationHandler.lastTooCloseAmount + (AnimationHandler.tooCloseAmount - AnimationHandler.lastTooCloseAmount) * partialTick), 0.0F);
    }

    @Override
    public void setActive(BetterCombatHand hand) {
        hand.setSwingTimestampSound(0.4F);
        hand.randomizeVariances();
    }
    
    private void animateSwing(boolean rightHanded, float swingProgress, float partialTick, BetterCombatHand hand) {
        int i = rightHanded ? 1 : -1;
        
        float moveRight = 0.0F; /* +right */
        float moveUp = 0.0F; /* +up */
        float moveClose = 0.0F; /* +zoom */
        float rotateUp = 0.0F; /* +up */
        float rotateCounterClockwise = 0.0F; /* +counter-clockwise */
        float rotateLeft = 0.0F; /* +left */
        float closeCap = 0.6F - (AnimationHandler.lastTooCloseAmount + (AnimationHandler.tooCloseAmount - AnimationHandler.lastTooCloseAmount) * partialTick);
        
        rotateUp = -AnimationHandler.min(swingProgress * 240.0F, 60.0F);
        rotateCounterClockwise = AnimationHandler.min(swingProgress * 125.0F, 50.0F) - swingProgress * 10.0F;
        rotateLeft = AnimationHandler.min(swingProgress * 75.0F, 30.0F) - swingProgress * 10.0F;
        
        if(swingProgress > 0.2F) {
            if(swingProgress > 0.4F) {
                if(swingProgress > 0.8F) {
                    /* (RETURN) HIGHEST | 0.8F -> 1 */
                    /* ======= */
                    float energy = (swingProgress - 0.8F);
                    
                    /* Move +Close */
                    moveClose = -closeCap + energy * 2.5F;
                    /* Move +Right */
                    moveRight = moveClose;
                    /* Move -Down */
                    moveUp = -moveClose;
                    
                    energy *= energy;
                    rotateUp += AnimationHandler.minMult(energy, 25.0F, 70.0F);
                    rotateCounterClockwise -= AnimationHandler.minMult(energy, 25.0F, 30.0F);
                    rotateLeft -= AnimationHandler.minMult(energy, 25.0F, 10.0F);
                }
                else {
                    /* (HOLD) HIGH | 0.4F -> 0.8F */
                    /* ==== */
                    
                    /* Stay -Away */
                    moveClose = -closeCap * (swingProgress * 0.25F + 0.9F);
                    /* Stay -Left */
                    moveRight = moveClose;
                    /* Stay +Up */
                    moveUp = -moveClose;
                }
            }
            else {
                /* (THRUST) LOW | 0.2F -> 0.4F */
                /* === */
                
                /* Move -Away */
                moveClose = (0.2F - swingProgress) * closeCap * 5.0F;
                /* Move -Left */
                moveRight = moveClose;
                /* Move +Up */
                moveUp = -moveClose -0.25F * closeCap;
            }
        }
        else {
            /* (READY) LOWEST | 0.0F -> 0.2F */
            moveClose = MathHelper.sin(swingProgress *AnimationHandler.PI*5.0F) * 0.2F;
            
            moveUp = -swingProgress * closeCap * 2.5F;
        }
        
        GlStateManager.translate(
                /* X */ i * moveRight * hand.moveRightVariance,
                /* Y */ 1.3F * moveUp * hand.moveUpVariance,
                /* Z */ moveClose * hand.moveCloseVariance);
        GlStateManager.rotate(rotateUp * hand.rotateUpVariance, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(i * rotateCounterClockwise * hand.rotateCounterClockwiseVariance, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(i * rotateLeft * hand.rotateLeftVariance, 0.0F, 0.0F, 1.0F);
    }
    
    @Override
    public float getCameraPitchMult() {
        return 0.6F;
    }
    
    @Override
    public float getCameraYawMult() {
        return 0.2F;
    }
}