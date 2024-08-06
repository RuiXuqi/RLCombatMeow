package bettercombat.mod.client.animation.util;

import bettercombat.mod.client.handler.AnimationHandler;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public interface IAnimation {

    default void animationMainhand(boolean rightHanded, float swingProgress, float partialTick) { }

    default void animationCameraMainhand(boolean rightHanded, float swingProgress, float partialTick) { }

    default void animationOffhand(boolean rightHanded, float swingProgress, float partialTick) { }

    default void animationCameraOffhand(boolean rightHanded, float swingProgress, float partialTick) { }

    default void positionMainhand(boolean rightHanded, float partialTick) {
        GlStateManager.rotate(-13.0F-AnimationHandler.mainhandSprintingTimer,1.0F,0.0F,0.0F);
        if(rightHanded) {
            GlStateManager.rotate(-13.0F,0.0F,1.0F,0.0F);
            GlStateManager.rotate(-13.0F,0.0F,0.0F,1.0F);
        }
        else {
            GlStateManager.rotate(13.0F,0.0F,1.0F,0.0F);
            GlStateManager.rotate(13.0F,0.0F,0.0F,1.0F);
        }
    }

    default void positionOffhand(boolean rightHanded, float partialTick) {
        GlStateManager.rotate(-13.0F-AnimationHandler.offhandSprintingTimer,1.0F,0.0F,0.0F);
        if(rightHanded) {
            GlStateManager.rotate(13.0F,0.0F,1.0F,0.0F);
            GlStateManager.rotate(13.0F,0.0F,0.0F,1.0F);
        }
        else {
            GlStateManager.rotate(-13.0F,0.0F,1.0F,0.0F);
            GlStateManager.rotate(-13.0F,0.0F,0.0F,1.0F);
        }
    }

    default void setActive(BetterCombatHand hand) { }
}