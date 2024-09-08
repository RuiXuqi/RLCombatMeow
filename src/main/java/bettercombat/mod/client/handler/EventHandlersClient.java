package bettercombat.mod.client.handler;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.client.animation.util.BetterCombatHand;
import bettercombat.mod.client.animation.util.CustomWeapon;
import bettercombat.mod.client.gui.GuiCrosshairsBC;
import bettercombat.mod.compat.BetterSurvivalHandler;
import bettercombat.mod.compat.ModLoadedUtil;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.handler.SoundHandler;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.network.PacketMainhandAttack;
import bettercombat.mod.network.PacketOffhandAttack;
import bettercombat.mod.util.*;
import bettercombat.mod.util.inf.InFHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public class EventHandlersClient {

    private static final GuiCrosshairsBC BCCROSSHAIR = new GuiCrosshairsBC();

    public static ItemStack itemStackMainhand = ItemStack.EMPTY;
    public static ItemStack itemStackOffhand = ItemStack.EMPTY;
    
    public final static BetterCombatHand betterCombatMainhand = new BetterCombatHand();
    public final static BetterCombatHand betterCombatOffhand = new BetterCombatHand();
    
    public static float cachedOffhandCooldownPeriod = 1.0F;

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onMouseEvent(MouseEvent event) {
        KeyBinding attack = Minecraft.getMinecraft().gameSettings.keyBindAttack;
        KeyBinding useItem = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
        if(attack.getKeyCode() < 0 && event.getButton() == attack.getKeyCode() + 100 && event.isButtonstate()) {
            onMouseLeftClick(event);
        }
        if(ConfigurationHandler.server.enableOffhandAttack && useItem.getKeyCode() < 0 && event.getButton() == useItem.getKeyCode() + 100 && event.isButtonstate()) {
            onMouseRightClick();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if(!ConfigurationHandler.server.enableOffhandAttack) return;

        if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true);
            BCCROSSHAIR.renderAttackIndicator(event.getPartialTicks(), new ScaledResolution(Minecraft.getMinecraft()));
            MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(event, event.getType()));
        }
    }

    public static void onMouseLeftClick(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity rvEntity = mc.getRenderViewEntity();
        EntityPlayer player = mc.player;
        if(player == null || rvEntity == null || player.isSpectator()) return;
        
        checkItemstacksChanged();

        if(!player.getActiveItemStack().isEmpty()) return;

        //Defer to BetterSurvival's handling of Nunchaku since it then defers back to our handling once its spinning
        if(ModLoadedUtil.isBetterSurvivalLoaded() && BetterSurvivalHandler.isNunchaku(player.getHeldItemMainhand().getItem())) return;

        RayTraceResult mov = null;
        if(ConfigurationHandler.server.swingThroughPassableBlocks) {
            RayTraceResult mov1 = ReachFixUtil.pointedObjectIgnorePassable(rvEntity, player, EnumHand.MAIN_HAND, mc.world, mc.getRenderPartialTicks());
            if(mov1 != null && mov1.entityHit != null && mov1.entityHit != player) mov = mov1;
        }
        //If swing through check finds an entity, use that, otherwise use the normal check to not count a passable block hit as a miss
        if(mov == null) mov = ReachFixUtil.pointedObject(rvEntity, player, EnumHand.MAIN_HAND, mc.world, mc.getRenderPartialTicks());
        
        if(mov != null && mov.entityHit != null && mov.entityHit != player) {
            event.setCanceled(true);
            
            if(ConfigurationHandler.server.requireFullEnergy && player.getCooledAttackStrength(0.5F) < 0.95F) return;
            
            if(ModLoadedUtil.isIceAndFireLoaded() && InFHandler.isMultipart(mov.entityHit)) {
                mov.entityHit = InFHandler.getMultipartParent(mov.entityHit);
            }
            
            Entity mount = player.getRidingEntity();
            if(player.isRiding() && mount != null) PacketHandler.instance.sendToServer(new PacketMainhandAttack(mov.entityHit.getEntityId(), mount.motionX, mount.motionY, mount.motionZ));
            else PacketHandler.instance.sendToServer(new PacketMainhandAttack(mov.entityHit.getEntityId(), player.motionX, player.motionY, player.motionZ));
            player.attackTargetEntityWithCurrentItem(mov.entityHit);
            player.resetCooldown();
            
            player.swingArm(EnumHand.MAIN_HAND);
            resetMainhandAnimationCooldown(player.getCooldownPeriod());
            betterCombatMainhand.initiateAnimation();
        }
        else if(mov == null || mov.typeOfHit == RayTraceResult.Type.MISS) {
            if(ConfigurationHandler.server.requireFullEnergy && player.getCooledAttackStrength(0.5F) < 0.95F) return;
            
            resetMainhandAnimationCooldown(player.getCooldownPeriod());
            betterCombatMainhand.initiateAnimation();
        }
    }

    public static void onMouseRightClick() {
        Minecraft mc = Minecraft.getMinecraft();
        Entity rvEntity = mc.getRenderViewEntity();
        EntityPlayer player = mc.player;
        if(player == null || rvEntity == null || player.isSpectator()) return;
        
        checkItemstacksChanged();
        
        if(!player.getActiveItemStack().isEmpty()) return;
        //Don't allow shield spamming with an offhand weapon
        if(player.getHeldItemMainhand().getItem() instanceof ItemShield) return;
        
        ItemStack stackOffHand = player.getHeldItemOffhand();
        if(stackOffHand.isEmpty() || !ConfigurationHandler.isItemAttackUsableOffhand(stackOffHand.getItem())) return;
        
        Helpers.clearOldModifiers(player, player.getHeldItemMainhand(), false, true, true);
        Helpers.addNewModifiers(player, player.getHeldItemOffhand(), false, true, true);
        
        RayTraceResult mov = null;
        if(ConfigurationHandler.server.swingThroughPassableBlocks) {
            RayTraceResult mov1 = ReachFixUtil.pointedObjectIgnorePassable(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());
            if(mov1 != null && mov1.entityHit != null && mov1.entityHit != player) mov = mov1;
        }
        //If swing through check finds an entity, use that, otherwise use the normal check to not count a passable block hit as a miss
        if(mov == null) mov = ReachFixUtil.pointedObject(rvEntity, player, EnumHand.OFF_HAND, mc.world, mc.getRenderPartialTicks());
        
        float cooldown = player.getCooldownPeriod();
        
        Helpers.clearOldModifiers(player, player.getHeldItemOffhand(), false, true, true);
        Helpers.addNewModifiers(player, player.getHeldItemMainhand(), false, true, true);
        
        if(ConfigurationHandler.server.requireFullEnergy) {
            CapabilityOffhandCooldown capability = player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null);
            if(capability != null) {
                float val = MathHelper.clamp(((float)capability.getTicksSinceLastSwing() + 0.5F) / cooldown, 0.0F, 1.0F);
                if(val < 0.95F) return;
            }
        }
        
        if(mov != null && mov.entityHit != null && mov.entityHit != player) {
            if(ModLoadedUtil.isIceAndFireLoaded() && InFHandler.isMultipart(mov.entityHit)) {
                mov.entityHit = InFHandler.getMultipartParent(mov.entityHit);
            }
            
            if(shouldAttack(mov.entityHit, player)) {
                Entity mount = player.getRidingEntity();
                if(player.isRiding() && mount != null) PacketHandler.instance.sendToServer(new PacketOffhandAttack(mov.entityHit.getEntityId(), mount.motionX, mount.motionY, mount.motionZ));
                else PacketHandler.instance.sendToServer(new PacketOffhandAttack(mov.entityHit.getEntityId(), player.motionX, player.motionY, player.motionZ));
                
                player.swingArm(EnumHand.OFF_HAND);
                resetOffhandAnimationCooldown(cooldown);
                betterCombatOffhand.initiateAnimation();
            }
        }
        //Only do an offhand miss swing if the mainhand isn't a usable item (shield, food, bow,etc)
        else if((mov == null || mov.typeOfHit == RayTraceResult.Type.MISS) && player.getHeldItemMainhand().getItem().getItemUseAction(player.getHeldItemMainhand()) == EnumAction.NONE) {
            CapabilityOffhandCooldown coh = player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null);
            if(coh != null) {
                coh.resetTicksSinceLastSwing();
                coh.sync();
                
                player.swingArm(EnumHand.OFF_HAND);
                resetOffhandAnimationCooldown(cooldown);
                betterCombatOffhand.initiateAnimation();
            }
        }
    }

    private static boolean shouldAttack(Entity entHit, EntityPlayer player) {
        if(entHit == null) return false;

        if(entHit instanceof EntityPlayerMP) {
            return Helpers.execNullable(entHit.getServer(), MinecraftServer::isPVPEnabled, false);
        }

        return ConfigurationHandler.isEntityAttackableOffhand(entHit) && !(entHit instanceof IEntityOwnable && ((IEntityOwnable)entHit).getOwner() == player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void tickEventLow(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.END && mc.player != null) {
            //Don't do breathing animation while paused
            AnimationHandler.lastBreatheTicks = AnimationHandler.breatheTicks;
            if(!mc.isGamePaused()) {
                AnimationHandler.breatheTicks += ConfigurationHandler.client.breathingAnimationSpeed;
            }

            checkItemstacksChanged();
            
            Helpers.clearOldModifiers(mc.player, mc.player.getHeldItemMainhand(), false, true, false);
            Helpers.addNewModifiers(mc.player, mc.player.getHeldItemOffhand(), false, true, false);
            
            //Don't like having to do this every tick but otherwise cooldown animation and bar will not be correct if values are changed after a swing
            cachedOffhandCooldownPeriod = mc.player.getCooldownPeriod();
            
            Helpers.clearOldModifiers(mc.player, mc.player.getHeldItemOffhand(), false, true, false);
            Helpers.addNewModifiers(mc.player, mc.player.getHeldItemMainhand(), false, true, false);

            //Wall-aware positioning, only bother raytracing if enabled
            boolean close = false;
            if(mc.getRenderViewEntity() != null && (ConfigurationHandler.client.tooCloseAnimationBlocks || ConfigurationHandler.client.tooCloseAnimationEntities) && (ConfigurationHandler.client.tooCloseAnimationIntensity > 0.0F)) {
                RayTraceResult rtr = ReachFixUtil.pointedObjectIgnorePassable(mc.getRenderViewEntity(), mc.player, EnumHand.MAIN_HAND, mc.world, mc.getRenderPartialTicks());
                if(rtr != null) {
                    double hX;
                    double hY;
                    double hZ;
                    AnimationHandler.lastTooCloseAmount = AnimationHandler.tooCloseAmount;
                    
                    if(ConfigurationHandler.client.tooCloseAnimationBlocks && rtr.hitVec != null) {
                        hX = mc.player.posX - rtr.hitVec.x;
                        hY = (mc.player.getEyeHeight() + mc.player.posY) - rtr.hitVec.y;
                        hZ = mc.player.posZ - rtr.hitVec.z;
                        
                        if((hX = hX * hX) < ConfigurationHandler.client.tooCloseAnimationDistance &&
                                (hY = hY * hY) < ConfigurationHandler.client.tooCloseAnimationDistance &&
                                (hZ = hZ * hZ) < ConfigurationHandler.client.tooCloseAnimationDistance) {
                            AnimationHandler.tooCloseAmount = (float)MathHelper.clamp(0.4D - ((hX + hY + hZ) / (3*ConfigurationHandler.client.tooCloseAnimationDistance)), 0.0D, 0.4D);
                            AnimationHandler.tooClose = true;
                            close = true;
                        }
                    }
                    else if(ConfigurationHandler.client.tooCloseAnimationEntities && rtr.entityHit != null) {
                        hX = mc.player.posX - (rtr.entityHit.posX + rtr.entityHit.width * 0.5D);
                        hY = (mc.player.getEyeHeight() + mc.player.posY) - (rtr.entityHit.posY + rtr.entityHit.height * 0.5D);
                        hZ = mc.player.posZ - (rtr.entityHit.posZ + rtr.entityHit.width * 0.5D);
                        
                        if((hX = hX * hX) < ConfigurationHandler.client.tooCloseAnimationDistance &&
                                (hY = hY * hY) < ConfigurationHandler.client.tooCloseAnimationDistance &&
                                (hZ = hZ * hZ) < ConfigurationHandler.client.tooCloseAnimationDistance) {
                            AnimationHandler.tooCloseAmount = (float)MathHelper.clamp(0.4D - ((hX + hY + hZ) / (3*ConfigurationHandler.client.tooCloseAnimationDistance)), 0.0D, 0.4D);
                            AnimationHandler.tooClose = true;
                            close = true;
                        }
                    }
                }
            }
            if(!close) {
                AnimationHandler.tooCloseAmount = 0.0F;
                AnimationHandler.tooClose = false;
            }
            
            betterCombatMainhand.tick();
            if(betterCombatMainhand.getSwingProgress(1.0F) > 0) {
                if(betterCombatMainhand.soundReady()) {
                    mainhandSwingSound();
                }
            }
            else if(betterCombatMainhand.equipSoundTimer > 0 && --betterCombatMainhand.equipSoundTimer == 0) {
                mainhandEquipSound();
                //Don't play two equip sounds right after each other if sounds are set to mono
                if(ConfigurationHandler.client.customWeaponSoundsMono && betterCombatOffhand.equipSoundTimer > 0) {
                    betterCombatOffhand.equipSoundTimer = 0;
                }
            }
            
            betterCombatOffhand.tick();
            if(betterCombatOffhand.getSwingProgress(1.0F) > 0) {
                if(betterCombatOffhand.soundReady()) {
                    offhandSwingSound();
                }
            }
            else if(betterCombatOffhand.equipSoundTimer > 0 && --betterCombatOffhand.equipSoundTimer == 0) {
                offhandEquipSound();
                //Don't play two equip sounds right after each other if sounds are set to mono
                if(ConfigurationHandler.client.customWeaponSoundsMono && betterCombatMainhand.equipSoundTimer > 0) {
                    betterCombatMainhand.equipSoundTimer = 0;
                }
            }
        }
    }

    public static void checkItemstacksChanged() {
        checkItemstackChangedMainhand();
        checkItemstackChangedOffhand();
	}

    public static void checkItemstackChangedMainhand() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        ItemStack heldStack = player.getHeldItemMainhand();
        
        boolean nonequal = !ItemStack.areItemsEqualIgnoreDurability(itemStackMainhand, heldStack);
        if(nonequal) {
            if(betterCombatMainhand.equipSoundTimer == 0 && betterCombatMainhand.hasCustomWeapon()) {
                mainhandSheatheSound();
                //Don't play two mono sheathe sounds right after each other
                if(ConfigurationHandler.client.customWeaponSoundsMono && betterCombatOffhand.equipSoundTimer == 0 && betterCombatOffhand.hasCustomWeapon()) {
                    betterCombatOffhand.equipSoundTimer = -1;
                }
            }
            
            itemStackMainhand = heldStack;
            resetMainhandAnimationCooldown(player.getCooldownPeriod());
            betterCombatMainhand.resetBetterCombatWeapon();
            
            CustomWeapon weapon = ConfigurationHandler.getCustomWeapon(itemStackMainhand.getItem());
            if(weapon != null) {
                betterCombatMainhand.setBetterCombatWeapon(weapon, betterCombatMainhand.attackCooldown);
            }
            else {
                //Add an equip sound to the shield
                if(itemStackMainhand.getItem() instanceof ItemShield) {
                    betterCombatMainhand.equipSoundTimer = 5;
                }
            }
        }
        //Undo mono sheathe delay after next tick
        else if(betterCombatOffhand.equipSoundTimer == -1) betterCombatOffhand.equipSoundTimer = 0;
    }

    public static void checkItemstackChangedOffhand() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        ItemStack heldStack = player.getHeldItemOffhand();
        
        boolean nonequal = !ItemStack.areItemsEqualIgnoreDurability(itemStackOffhand, heldStack);
        if(nonequal) {
            if(betterCombatOffhand.equipSoundTimer == 0 && betterCombatOffhand.hasCustomWeapon()) {
                offhandSheatheSound();
                //Don't play two mono sheathe sounds right after each other
                if(ConfigurationHandler.client.customWeaponSoundsMono && betterCombatMainhand.equipSoundTimer == 0 && betterCombatMainhand.hasCustomWeapon()) {
                    betterCombatMainhand.equipSoundTimer = -1;
                }
            }
            
            itemStackOffhand = heldStack;
            
            Helpers.clearOldModifiers(player, player.getHeldItemMainhand(), false, true, false);
            Helpers.addNewModifiers(player, player.getHeldItemOffhand(), false, true, false);
            
            resetOffhandAnimationCooldown(player.getCooldownPeriod());
            
            Helpers.clearOldModifiers(player, player.getHeldItemOffhand(), false, true, false);
            Helpers.addNewModifiers(player, player.getHeldItemMainhand(), false, true, false);
            
            betterCombatOffhand.resetBetterCombatWeapon();
            
            CustomWeapon weapon = ConfigurationHandler.getCustomWeapon(itemStackOffhand.getItem());
            if(weapon != null) {
                betterCombatOffhand.setBetterCombatWeapon(weapon, betterCombatOffhand.attackCooldown);
            }
            else {
                //Add an equip sound to the shield
                if(itemStackOffhand.getItem() instanceof ItemShield) {
                    betterCombatOffhand.equipSoundTimer = 5;
                }
            }
        }
        //Undo mono sheathe delay after next tick
        else if(betterCombatMainhand.equipSoundTimer == -1) betterCombatMainhand.equipSoundTimer = 0;
    }

    public static void resetMainhandAnimationCooldown(float cooldown) {
        betterCombatMainhand.attackCooldown = (int)MathHelper.clamp(cooldown, BetterCombatHand.minimumCooldownTicks, BetterCombatHand.maximumCooldownTicks);
    }

    public static void resetOffhandAnimationCooldown(float cooldown) {
        betterCombatOffhand.attackCooldown = (int)MathHelper.clamp(cooldown, BetterCombatHand.minimumCooldownTicks, BetterCombatHand.maximumCooldownTicks);
    }

    public static void mainhandSwingSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHandSide enumhandside = player.getPrimaryHand();
        boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
        SoundHandler.playSwingSound(player, betterCombatMainhand, itemStackMainhand, betterCombatMainhand.attackCooldown, !rightHanded);
    }

    public static void offhandSwingSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHandSide enumhandside = player.getPrimaryHand();
        boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
        SoundHandler.playSwingSound(player, betterCombatOffhand, itemStackOffhand, betterCombatOffhand.attackCooldown, rightHanded);
    }

    private static void mainhandEquipSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHandSide enumhandside = player.getPrimaryHand();
        boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
        SoundHandler.playEquipSound(player, betterCombatMainhand, itemStackMainhand, betterCombatMainhand.attackCooldown, !rightHanded);
    }

    private static void offhandEquipSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHandSide enumhandside = player.getPrimaryHand();
        boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
        SoundHandler.playEquipSound(player, betterCombatOffhand, itemStackOffhand, betterCombatOffhand.attackCooldown, rightHanded);
    }
    
    private static void mainhandSheatheSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHandSide enumhandside = player.getPrimaryHand();
        boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
        SoundHandler.playSheatheSound(player, betterCombatMainhand, itemStackMainhand, betterCombatMainhand.attackCooldown, !rightHanded);
    }
    
    private static void offhandSheatheSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHandSide enumhandside = player.getPrimaryHand();
        boolean rightHanded = enumhandside == EnumHandSide.RIGHT;
        SoundHandler.playSheatheSound(player, betterCombatOffhand, itemStackOffhand, betterCombatOffhand.attackCooldown, rightHanded);
    }
}