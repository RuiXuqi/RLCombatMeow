package bettercombat.mod.client.handler;

import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShield;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public class AnimationHandler {

    //equippedProgressMainhand goes from 1.0 to 0.0, used for raising the weapon up to normal position
    public static float equippedProgressMainhand = 0.0F;
    //equippedProgressOffhand goes from 1.0 to 0.0, used for raising the weapon up to normal position
    public static float equippedProgressOffhand = 0.0F;
    //Raises the weapon up and down to simulate breathing
    public static float breatheTicks = 0.0F;
    public static float lastBreatheTicks = 0.0F;
    public static boolean tooClose = false;
    public static float tooCloseAmount = 0.0F;
    public static float lastTooCloseAmount = 0.0F;
    //How long the player has been blocking for, up to 10 frames (3.33 ticks)
    //-1 if there is no shield in hand
    public static int blockingTimerOffhand = -1;
    //Can't swing offhand when shielding in mainhand, but can still render the movement
    public static int blockingTimerMainhand = -1;
    
    public static final float PI = (float)Math.PI;
    
    public static boolean shouldSpecialRenderMainhand(EntityPlayer player) {
        if(EventHandlersClient.betterCombatMainhand.hasCustomWeapon()) {
            if(Helpers.isHandActive(player, EnumHand.MAIN_HAND)) {
                return false;
            }
            return true;
        }
        else if(EventHandlersClient.itemStackMainhand.getItem() instanceof ItemShield) {
            if(Helpers.isHandActive(player, EnumHand.MAIN_HAND)) {
                /* Block animation takes 10 frames (3.33 ticks) */
                if(blockingTimerMainhand < 10) {
                    blockingTimerMainhand++;
                }
            }
            else if(blockingTimerMainhand > 0) {
                blockingTimerMainhand--;
            }
            
            return false;
        }
        
        blockingTimerMainhand = -1;
        return false;
    }
    
    public static boolean shouldSpecialRenderOffhand(EntityPlayer player) {
        if(EventHandlersClient.betterCombatOffhand.hasCustomWeapon()) {
            if(Helpers.isHandActive(player, EnumHand.OFF_HAND)) {
                return false;
            }
            return true;
        }
        else if(EventHandlersClient.itemStackOffhand.getItem() instanceof ItemShield) {
            if(Helpers.isHandActive(player, EnumHand.OFF_HAND)) {
                /* Block animation takes 10 frames (3.33 ticks) */
                if(blockingTimerOffhand < 10) {
                    blockingTimerOffhand++;
                }
            }
            else if(blockingTimerOffhand > 0) {
                blockingTimerOffhand--;
            }
            
            return false;
        }
        
        blockingTimerOffhand = -1;
        return false;
    }

    public static void positionMainWeaponAttacking(boolean rightHanded, float partialTick) {
        EventHandlersClient.betterCombatMainhand.getActiveAttackAnimation().positionMainhand(rightHanded, partialTick);

        //Position this weapon away when the player is blocking in offhand
        if(blockingTimerOffhand > 0) {
            GlStateManager.translate((rightHanded ? 1 : -1)*blockingTimerOffhand*0.016F, -blockingTimerOffhand*0.012F, 0.0F);
        }
    }
    
    public static void positionMainWeaponMining(boolean rightHanded, float partialTick) {
        EventHandlersClient.betterCombatMainhand.getActiveMiningAnimation().positionMainhand(rightHanded, partialTick);
    }
    
    public static void positionMainhandAwayIfOffhandAttacking(boolean rightHanded, float partialTick) {
        float swingProgress = EventHandlersClient.betterCombatOffhand.getSwingProgress(partialTick);
        if(swingProgress > 0) {
            float f = MathHelper.sin(swingProgress*PI);
            GlStateManager.translate((rightHanded ? 1 : -1)*f*0.0625F, -f*0.025F, 0.0F);
        }
    }
    
    public static void positionOffhandWeaponAttacking(boolean rightHanded, float partialTick) {
        EventHandlersClient.betterCombatOffhand.getActiveAttackAnimation().positionOffhand(rightHanded, partialTick);
        
        //Position this weapon away when the player is blocking in mainhand
        if(blockingTimerMainhand > 0) {
            GlStateManager.translate((rightHanded ? -1 : 1)*blockingTimerMainhand*0.016F, -blockingTimerMainhand*0.012F, 0.0F);
        }
    }
    
    public static void positionOffhandWeaponMining(boolean rightHanded, float partialTick) {
        EventHandlersClient.betterCombatOffhand.getActiveMiningAnimation().positionOffhand(rightHanded, partialTick);
    }
    
    public static void positionOffhandAwayIfMainhandAttacking(boolean rightHanded, float partialTick) {
        float swingProgress = EventHandlersClient.betterCombatMainhand.getSwingProgress(partialTick);
        if(swingProgress > 0) {
            float f = MathHelper.sin(swingProgress*PI);
            GlStateManager.translate((rightHanded ? -1 : 1)*f*0.125F, -f*0.05F, 0.0F);
        }
    }
    
    public static void positionBreathingTooClose(float partialTick) {
        float t = lastTooCloseAmount + (tooCloseAmount - lastTooCloseAmount) * partialTick;
        float b = lastBreatheTicks + (breatheTicks - lastBreatheTicks) * partialTick;
        GlStateManager.translate(0.0F, MathHelper.sin(b)*ConfigurationHandler.client.breathingAnimationIntensity, t*ConfigurationHandler.client.tooCloseAnimationIntensity);
    }
    
    public static float min(float f0, float f1) {
        return Math.min(f0, f1);
    }
    
    public static float max(float f0, float f1) {
        return Math.max(f0, f1);
    }
    
    public static float minMult(float base, float multiplier, float cap) {
        float f = base * multiplier * cap;
        return Math.min(f, cap);
    }
}