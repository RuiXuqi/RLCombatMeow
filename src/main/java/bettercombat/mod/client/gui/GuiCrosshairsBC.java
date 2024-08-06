package bettercombat.mod.client.gui;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.util.ConfigurationHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class GuiCrosshairsBC extends Gui {

    public void renderAttackIndicator(float partTicks, ScaledResolution scaledRes) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.enableBlend();
        
        GameSettings gamesettings = mc.gameSettings;
        if(gamesettings.thirdPersonView == 0) {
            if(mc.playerController.isSpectator() && mc.pointedEntity == null) {
                RayTraceResult rtRes = mc.objectMouseOver;
                if(rtRes == null || rtRes.typeOfHit != net.minecraft.util.math.RayTraceResult.Type.BLOCK) {
                    return;
                }

                BlockPos blockpos = rtRes.getBlockPos();
                IBlockState state = mc.world.getBlockState(blockpos);
                if(!state.getBlock().hasTileEntity(state) || !(mc.world.getTileEntity(blockpos) instanceof IInventory)) {
                    return;
                }
            }

            int sw = scaledRes.getScaledWidth();
            int sh = scaledRes.getScaledHeight();
            if(gamesettings.showDebugInfo && !gamesettings.hideGUI && !mc.player.hasReducedDebug() && !gamesettings.reducedDebugInfo) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(sw / 2.0F, sh / 2.0F, this.zLevel);
                Entity entity = mc.getRenderViewEntity();
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partTicks, -1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partTicks, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(-1.0F, -1.0F, -1.0F);
                OpenGlHelper.renderDirections(10);
                GlStateManager.popMatrix();
            }
            else {
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableAlpha();
                drawTexturedModalRect(sw / 2 - 7, sh / 2 - 7, 0, 0, 16, 16);
                if(mc.gameSettings.attackIndicator == 1) {
                    float cooledStr = mc.player.getCooledAttackStrength(0.0F);
                    
                    if(ConfigurationHandler.isItemAttackUsableOffhand(mc.player.getHeldItemOffhand().getItem())) {
                        CapabilityOffhandCooldown capability = mc.player.getCapability(EventHandlers.TUTO_CAP, null);
                        float ohCooldown = 0;
                        if(capability != null) {
                            int ohCooldownBeginning = capability.getOffhandBeginningCooldown();
                            if(ohCooldownBeginning > 0) ohCooldown = capability.getOffhandCooldown()/(float)ohCooldownBeginning;
                        }
                        ohCooldown = Math.abs(1.0F - ohCooldown);
                        
                        boolean fullyCooledIcon = false;
                        if(mc.pointedEntity instanceof EntityLivingBase && cooledStr >= 1.0F && ohCooldown >= 1.0F) {
                            fullyCooledIcon = mc.player.getCooldownPeriod() > 5.0F;
                            fullyCooledIcon = fullyCooledIcon & mc.pointedEntity.isEntityAlive();
                        }
                        
                        if(fullyCooledIcon && ConfigurationHandler.client.renderAttackReadyIcon) {
                            int i = sh / 2 - 7 + 16;
                            int j = sw / 2 - 8;
                            drawTexturedModalRect(j, i, 68, 94, 16, 16);
                        }
                        else {
                            int i = sh / 2 - 7 + 16;
                            int j = sw / 2 - 7;
                            
                            if(cooledStr < 1.0F) {
                                int k = (int)(cooledStr * 17.0F);
                                drawTexturedModalRect(j + 15, i, 36, 94, 16, 4);
                                drawTexturedModalRect(j + 15, i, 52, 94, k, 4);
                            }
                            
                            if(ohCooldown < 1.0F) {
                                int k = (int)(ohCooldown * 17.0F);
                                drawTexturedModalRect(j - 15, i, 36, 94, 16, 4);
                                drawTexturedModalRect(j - 15, i, 52, 94, k, 4);
                            }
                        }
                    }
                    else {
                        boolean fullyCooledIcon = false;
                        if(mc.pointedEntity instanceof EntityLivingBase && cooledStr >= 1.0F) {
                            fullyCooledIcon = mc.player.getCooldownPeriod() > 5.0F;
                            fullyCooledIcon = fullyCooledIcon & mc.pointedEntity.isEntityAlive();
                        }
                        
                        int i = sh / 2 - 7 + 16;
                        int j = sw / 2 - 8;
                        
                        if(fullyCooledIcon && ConfigurationHandler.client.renderAttackReadyIcon) {
                            this.drawTexturedModalRect(j, i, 68, 94, 16, 16);
                        }
                        else if(cooledStr < 1.0F) {
                            int k = (int)(cooledStr * 17.0F);
                            this.drawTexturedModalRect(j, i, 36, 94, 16, 4);
                            this.drawTexturedModalRect(j, i, 52, 94, k, 4);
                        }
                    }
                }
            }
        }
    }
}