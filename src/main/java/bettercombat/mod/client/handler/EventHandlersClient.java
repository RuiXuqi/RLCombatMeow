package bettercombat.mod.client.handler;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.gui.GuiCrosshairsBC;
import bettercombat.mod.combat.IOffHandAttack;
import bettercombat.mod.combat.ISecondHurtTimer;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.network.PacketMainhandAttack;
import bettercombat.mod.network.PacketOffhandAttack;
import bettercombat.mod.util.*;
import meldexun.reachfix.hook.client.EntityRendererHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EventHandlersClient
{
    public static final EventHandlersClient INSTANCE = new EventHandlersClient();

    private final GuiCrosshairsBC gc = new GuiCrosshairsBC();

    private EventHandlersClient() {}

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onMouseEvent(MouseEvent event) {
        KeyBinding attack = Minecraft.getMinecraft().gameSettings.keyBindAttack;
        KeyBinding useItem = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
        if( attack.getKeyCode() < 0 && event.getButton() == attack.getKeyCode() + 100 && event.isButtonstate() ) {
            onMouseLeftClick(event);
        }
        if( ConfigurationHandler.enableOffHandAttack && useItem.getKeyCode() < 0 && event.getButton() == useItem.getKeyCode() + 100 && event.isButtonstate() ) {
            onMouseRightClick();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if( !ConfigurationHandler.enableOffHandAttack ) {
            return;
        }

        if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            boolean cancelled = event.isCanceled();
            event.setCanceled(true);
            if (!cancelled) {
                this.gc.renderAttackIndicator(0.5F, new ScaledResolution(Minecraft.getMinecraft()));
                MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, event.getType()));
            }
        }
    }

    public static void onMouseLeftClick(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity rvEntity = mc.getRenderViewEntity();
        EntityPlayer player = mc.player;
        if(player == null || rvEntity == null) return;

        if(!player.getActiveItemStack().isEmpty()) return;
        //Defer to BetterSurvival's handling of Nunchaku since it then defers back to our handling once its spinning
        if(Loader.isModLoaded("mujmajnkraftsbettersurvival") && BetterSurvivalHandler.isNunchaku(player.getHeldItemMainhand().getItem())) return;

        RayTraceResult mov = EntityRendererHook.pointedObject(rvEntity, player, EnumHand.MAIN_HAND, mc.world, mc.getRenderPartialTicks());
        //RayTraceResult mov = ReachFixFuzzyUtil.pointedObject(rvEntity, player, EnumHand.MAIN_HAND, mc.world, mc.getRenderPartialTicks());

        if( mov != null && mov.entityHit != null ) {
            if( mov.entityHit != player ) {
                event.setCanceled(true);
                if( ConfigurationHandler.requireFullEnergy && player.getCooledAttackStrength(0.5F) < 1.0F ) {
                    return;
                }

                player.isSwingInProgress = true;
                player.swingingHand = EnumHand.MAIN_HAND;

                /*
                if(mov.entityHit instanceof MultiPartEntityPart) {
                    mov.entityHit = ((Entity)((MultiPartEntityPart)mov.entityHit).parent);
                }
                else if(Loader.isModLoaded("iceandfire") && InFHandler.isMultipart(mov.entityHit)) {
                    mov.entityHit = InFHandler.getMultipartParent(mov.entityHit);
                }
                */
                if(Loader.isModLoaded("iceandfire") && InFHandler.isMultipart(mov.entityHit)) {
                    mov.entityHit = InFHandler.getMultipartParent(mov.entityHit);
                }

                player.attackTargetEntityWithCurrentItem(mov.entityHit);
                Entity mount = player.getRidingEntity();
                if(player.isRiding() && mount != null) PacketHandler.instance.sendToServer(new PacketMainhandAttack(mov.entityHit.getEntityId(), mount.motionX, mount.motionY, mount.motionZ));
                else PacketHandler.instance.sendToServer(new PacketMainhandAttack(mov.entityHit.getEntityId(), player.motionX, player.motionY, player.motionZ));
            }
        }
    }

    public static void onMouseRightClick() {
        Minecraft mc = Minecraft.getMinecraft();
        Entity rvEntity = mc.getRenderViewEntity();
        EntityPlayer player = mc.player;
        if(player == null || rvEntity == null) return;

        if(!player.isSpectator()) {
            if(!player.getActiveItemStack().isEmpty() ) return;
            if(ConfigurationHandler.requireFullEnergy && Helpers.execNullable(player.getCapability(EventHandlers.TUTO_CAP, null), CapabilityOffhandCooldown::getOffhandCooldown, 1) > 0) return;

            ItemStack stackOffHand = player.getHeldItemOffhand();
            if(stackOffHand.isEmpty() || !ConfigurationHandler.isItemAttackUsable(stackOffHand.getItem())) return;

            IOffHandAttack oha = player.getCapability(EventHandlers.OFFHAND_CAP, null);

            Helpers.clearOldModifiers(player, player.getHeldItemMainhand());
            Helpers.addNewModifiers(player, player.getHeldItemOffhand());

            RayTraceResult mov = EntityRendererHook.pointedObject(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());
            //RayTraceResult mov = ReachFixFuzzyUtil.pointedObject(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());
            int cooldown = Helpers.getOffhandCooldown(player);

            Helpers.clearOldModifiers(player, player.getHeldItemOffhand());
            Helpers.addNewModifiers(player, player.getHeldItemMainhand());

            if( oha != null && (mov == null || mov.typeOfHit == RayTraceResult.Type.MISS || shouldAttack(mov.entityHit, player) || mov.typeOfHit == RayTraceResult.Type.BLOCK) ) {
                oha.swingOffHand(player);
            }

            if( mov != null && mov.entityHit != null ) {
                /*
                if(mov.entityHit instanceof MultiPartEntityPart) {
                    mov.entityHit = ((Entity)((MultiPartEntityPart)mov.entityHit).parent);
                }
                else if(Loader.isModLoaded("iceandfire") && InFHandler.isMultipart(mov.entityHit)) {
                    mov.entityHit = InFHandler.getMultipartParent(mov.entityHit);
                }
                */
                if(Loader.isModLoaded("iceandfire") && InFHandler.isMultipart(mov.entityHit)) {
                    mov.entityHit = InFHandler.getMultipartParent(mov.entityHit);
                }

                ISecondHurtTimer sht;
                if(mov.entityHit instanceof MultiPartEntityPart) sht = ((Entity)(((MultiPartEntityPart)mov.entityHit).parent)).getCapability(EventHandlers.SECONDHURTTIMER_CAP, null);
                else sht = mov.entityHit.getCapability(EventHandlers.SECONDHURTTIMER_CAP, null);

                if( sht != null && sht.getHurtTimerBCM() <= 0 ) {
                    if( shouldAttack(mov.entityHit, player) ) {
                        Entity mount = player.getRidingEntity();
                        if(player.isRiding() && mount != null) PacketHandler.instance.sendToServer(new PacketOffhandAttack(mov.entityHit.getEntityId(), mount.motionX, mount.motionY, mount.motionZ));
                        else PacketHandler.instance.sendToServer(new PacketOffhandAttack(mov.entityHit.getEntityId(), player.motionX, player.motionY, player.motionZ));
                    }
                }
            }
            else if(mov == null || mov.typeOfHit != RayTraceResult.Type.BLOCK){
                CapabilityOffhandCooldown coh = player.getCapability(EventHandlers.TUTO_CAP, null);
                if(coh != null) {
                    coh.setOffhandCooldown(cooldown);
                    coh.setOffhandBeginningCooldown(cooldown);
                    coh.sync();
                }
            }
        }
    }

    private static boolean shouldAttack(Entity entHit, EntityPlayer player) {
        if( entHit == null ) {
            return false;
        }

        if( entHit instanceof EntityPlayerMP ) {
            return Helpers.execNullable(entHit.getServer(), MinecraftServer::isPVPEnabled, false);
        }

        return ConfigurationHandler.isEntityAttackable(entHit) && !(entHit instanceof IEntityOwnable && ((IEntityOwnable) entHit).getOwner() == player);
    }
}