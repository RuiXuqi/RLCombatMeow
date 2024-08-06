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
public class AnimationPunch implements IAnimation {
    
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
        GlStateManager.rotate(-13.0F-AnimationHandler.mainhandSprintingTimer, 1.0F, 0.0F, 0.0F);
    }
    
    @Override
    public void positionOffhand(boolean rightHanded, float partialTick) {
        GlStateManager.rotate(-13.0F-AnimationHandler.offhandSprintingTimer,1.0F,0.0F,0.0F);
    }
    
    @Override
    public void setActive(BetterCombatHand hand) {
        hand.setSwingTimestampSound(0.3F);
    }
    
    private void animateSwing(boolean rightHanded, float swingProgress, float partialTick, BetterCombatHand hand) {
        int i = rightHanded ? 1 : -1;
        
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