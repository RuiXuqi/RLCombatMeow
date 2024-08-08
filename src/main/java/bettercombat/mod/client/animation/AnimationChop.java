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
public class AnimationChop implements IAnimation {

    @Override
    public void animationMainhand(boolean rightHanded, float swingProgress, float partialTick) {
        //Raise chopping animation as equipped shift puts it very low
        if(AnimationHandler.equippedProgressMainhand > 0.5F) {
            GlStateManager.translate(0.0F, (0.5F - AnimationHandler.equippedProgressMainhand) * -0.6F, 0.0F);
        }
        this.animateSwing(rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatMainhand);
    }

    @Override
    public void animationOffhand(boolean rightHanded, float swingProgress, float partialTick) {
        //Raise chopping animation as equipped shift puts it very low
        if(AnimationHandler.equippedProgressOffhand > 0.5F) {
            GlStateManager.translate(0.0F, (0.5F - AnimationHandler.equippedProgressOffhand) * -0.6F, 0.0F);
        }
        this.animateSwing(!rightHanded, swingProgress, partialTick, EventHandlersClient.betterCombatOffhand);
    }

    @Override
    public void positionMainhand(boolean rightHanded, float partialTick) {
        /* If the weapon is an axe, position it upwards */
        GlStateManager.rotate(-11.0F - (EventHandlersClient.betterCombatMainhand.sprintingTimerPrev +
                (partialTick * (EventHandlersClient.betterCombatMainhand.sprintingTimer - EventHandlersClient.betterCombatMainhand.sprintingTimerPrev))
        ), 1.0F, 0.0F, 0.0F);
        /* Position the weapon in default position */
        int i = rightHanded ? 1 : -1;
        GlStateManager.translate(i*0.02F, 0.08F, 0.0F);
        GlStateManager.rotate(i*-16.0F,0.0F,1.0F,0.0F);
        GlStateManager.rotate(i*-8.0F,0.0F,0.0F,1.0F);
    }

    @Override
    public void positionOffhand(boolean rightHanded, float partialTick) {
        /* If the weapon is an axe, position it upwards */
        GlStateManager.rotate(-11.0F - (EventHandlersClient.betterCombatOffhand.sprintingTimerPrev +
                (partialTick * (EventHandlersClient.betterCombatOffhand.sprintingTimer - EventHandlersClient.betterCombatOffhand.sprintingTimerPrev))
        ),1.0F,0.0F,0.0F);
        /* Position the weapon in default position */
        int i = rightHanded ? -1 : 1;
        GlStateManager.translate(i*0.02F, 0.08F, 0.0F);
        GlStateManager.rotate(i*-16.0F,0.0F,1.0F,0.0F);
        GlStateManager.rotate(i*-8.0F,0.0F,0.0F,1.0F);
    }

    @Override
    public void setActive(BetterCombatHand hand) {
        hand.setSwingTimestampSound(0.3F);
        hand.randomizeVariances();
    }
    
    private void animateSwing(boolean rightHanded, float swingProgress, float partialTick, BetterCombatHand hand) {
        int i = rightHanded ? 1 : -1;
        
        float moveRight = 0.0F; /* +right */
        float moveUp = 0.0F; /* +up */
        float moveClose = 0.0F; /* +zoom */
        float rotateUp = 0.0F;
        float rotateCounterClockwise = AnimationHandler.min(swingProgress*300.0F, 15.0F) - swingProgress*15.0F;
        float rotateLeft = AnimationHandler.min(swingProgress*100.0F, 30.0F); /* +left */
        float closeCap = (AnimationHandler.lastTooCloseAmount + (AnimationHandler.tooCloseAmount - AnimationHandler.lastTooCloseAmount) * partialTick) - 0.4F;
        
        if(swingProgress > 0.2F) {
            if(swingProgress > 0.7F) {
                /* HIGHEST | holdDuration -> 1 */
                /* ======= */
                float energy = swingProgress - 0.7F;
                
                moveUp = -energy;
                moveRight = -0.7F + energy * 2.0F;
                moveClose = closeCap + energy * 6.0F;
                rotateUp = -95.0F;
            }
            else {
                /* (HOLD) HIGH | 0.2 -> 0.7 */
                /* ==== */
                moveRight = -0.7F;
                moveClose = closeCap;
                rotateUp = -95.0F;
                
                if(swingProgress < 0.4F) {
                    rotateUp += MathHelper.sin((swingProgress-0.2F)*AnimationHandler.PI*5.0F) * 5.0F;
                }
            }
        }
        else {
            /* (SWING) LOW | 0.0 -> 0.2 */
            /* === */
            float energy = 1.0F - MathHelper.cos(swingProgress*AnimationHandler.PI*2.5F);
            
            moveRight = energy * -0.7F;
            moveClose = energy * closeCap;
            rotateUp = energy * -95.0F;
        }
        
        if(swingProgress <= 0.22F) {
            moveUp = MathHelper.sin(swingProgress*AnimationHandler.PI*4.6F) * 0.18F + 0.01F;
        }
        
        GlStateManager.translate(
                1.1F * i * moveRight * hand.moveRightVariance,
                moveUp * hand.moveUpVariance,
                moveClose * hand.moveCloseVariance);
        //Chop
        GlStateManager.rotate(rotateUp * hand.rotateUpVariance, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(i * rotateCounterClockwise * hand.rotateCounterClockwiseVariance, 0.0F, 1.0F, 0.0F);
        //Swivel back and forth
        GlStateManager.rotate(i * rotateLeft * hand.rotateLeftVariance, 0.0F, 0.0F, 1.0F);
    }
    
    @Override
    public float getCameraPitchMult() {
        return 2.0F;
    }
    
    @Override
    public float getCameraYawMult() {
        return 0.2F;
    }
}