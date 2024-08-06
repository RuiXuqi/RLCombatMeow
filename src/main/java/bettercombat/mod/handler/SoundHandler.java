package bettercombat.mod.handler;

import bettercombat.mod.client.animation.util.AnimationEnum;
import bettercombat.mod.client.animation.util.BetterCombatHand;
import bettercombat.mod.client.animation.util.CustomWeapon;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class SoundHandler {
    
    private static final Random rand = new Random();
    
    public static final SoundEvent SWORD_SLASH = registerSound("player.swordslash");
    public static final SoundEvent CRITICAL_STRIKE = registerSound("player.criticalstrike");
    
    public static final SoundEvent SWING_2H = registerSound("player.swing_2h");
    public static final SoundEvent SWING_2H_LEFT = registerSound("player.swing_2h_left");
    public static final SoundEvent SWING_2H_RIGHT = registerSound("player.swing_2h_right");
    
    public static final SoundEvent SWING_METAL_AXE = registerSound("player.swing_metal_axe");
    public static final SoundEvent SWING_METAL_AXE_LEFT = registerSound("player.swing_metal_axe_left");
    public static final SoundEvent SWING_METAL_AXE_RIGHT = registerSound("player.swing_metal_axe_right");
    
    public static final SoundEvent SWING_METAL_BLADE = registerSound("player.swing_metal_blade");
    public static final SoundEvent SWING_METAL_BLADE_LEFT = registerSound("player.swing_metal_blade_left");
    public static final SoundEvent SWING_METAL_BLADE_RIGHT = registerSound("player.swing_metal_blade_right");
    
    public static final SoundEvent SWING_METAL_BLUNT = registerSound("player.swing_metal_blunt");
    public static final SoundEvent SWING_METAL_BLUNT_LEFT = registerSound("player.swing_metal_blunt_left");
    public static final SoundEvent SWING_METAL_BLUNT_RIGHT = registerSound("player.swing_metal_blunt_right");
    
    public static final SoundEvent SWING_SLOW = registerSound("player.swing_slow");
    public static final SoundEvent SWING_SLOW_LEFT = registerSound("player.swing_slow_left");
    public static final SoundEvent SWING_SLOW_RIGHT = registerSound("player.swing_slow_right");
    
    public static final SoundEvent SWING_NORMAL = registerSound("player.swing_normal");
    public static final SoundEvent SWING_NORMAL_LEFT = registerSound("player.swing_normal_left");
    public static final SoundEvent SWING_NORMAL_RIGHT = registerSound("player.swing_normal_right");
    
    public static final SoundEvent SWING_QUICK = registerSound("player.swing_quick");
    public static final SoundEvent SWING_QUICK_LEFT = registerSound("player.swing_quick_left");
    public static final SoundEvent SWING_QUICK_RIGHT = registerSound("player.swing_quick_right");
    
    public static final SoundEvent EQUIP_BLADE = registerSound("player.equip_blade");
    public static final SoundEvent EQUIP_BLADE_LEFT = registerSound("player.equip_blade_left");
    public static final SoundEvent EQUIP_BLADE_RIGHT = registerSound("player.equip_blade_right");
    
    public static final SoundEvent EQUIP_AXE = registerSound("player.equip_axe");
    public static final SoundEvent EQUIP_AXE_LEFT = registerSound("player.equip_axe_left");
    public static final SoundEvent EQUIP_AXE_RIGHT = registerSound("player.equip_axe_right");
    
    public static final SoundEvent EQUIP_OTHER = registerSound("player.equip_other");
    public static final SoundEvent EQUIP_OTHER_LEFT = registerSound("player.equip_other_left");
    public static final SoundEvent EQUIP_OTHER_RIGHT = registerSound("player.equip_other_right");
    
    public static final SoundEvent SHEATHE_BLADE = registerSound("player.sheathe_blade");
    public static final SoundEvent SHEATHE_BLADE_LEFT = registerSound("player.sheathe_blade_left");
    public static final SoundEvent SHEATHE_BLADE_RIGHT = registerSound("player.sheathe_blade_right");
    
    public static final SoundEvent SHEATHE_AXE = registerSound("player.sheathe_axe");
    public static final SoundEvent SHEATHE_AXE_LEFT = registerSound("player.sheathe_axe_left");
    public static final SoundEvent SHEATHE_AXE_RIGHT = registerSound("player.sheathe_axe_right");
    
    public static final SoundEvent SHEATHE_OTHER = registerSound("player.sheathe_other");
    public static final SoundEvent SHEATHE_OTHER_LEFT = registerSound("player.sheathe_other_left");
    public static final SoundEvent SHEATHE_OTHER_RIGHT = registerSound("player.sheathe_other_right");
    
    private static SoundEvent registerSound(String name) {
        ResourceLocation soundID = new ResourceLocation(Reference.MOD_ID, name);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(SWORD_SLASH);
        event.getRegistry().register(CRITICAL_STRIKE);
        
        event.getRegistry().register(SWING_2H);
        event.getRegistry().register(SWING_2H_LEFT);
        event.getRegistry().register(SWING_2H_RIGHT);
        
        event.getRegistry().register(SWING_METAL_AXE);
        event.getRegistry().register(SWING_METAL_AXE_LEFT);
        event.getRegistry().register(SWING_METAL_AXE_RIGHT);
        
        event.getRegistry().register(SWING_METAL_BLADE);
        event.getRegistry().register(SWING_METAL_BLADE_LEFT);
        event.getRegistry().register(SWING_METAL_BLADE_RIGHT);
        
        event.getRegistry().register(SWING_METAL_BLUNT);
        event.getRegistry().register(SWING_METAL_BLUNT_LEFT);
        event.getRegistry().register(SWING_METAL_BLUNT_RIGHT);
        
        event.getRegistry().register(SWING_SLOW);
        event.getRegistry().register(SWING_SLOW_LEFT);
        event.getRegistry().register(SWING_SLOW_RIGHT);
        
        event.getRegistry().register(SWING_NORMAL);
        event.getRegistry().register(SWING_NORMAL_LEFT);
        event.getRegistry().register(SWING_NORMAL_RIGHT);
        
        event.getRegistry().register(SWING_QUICK);
        event.getRegistry().register(SWING_QUICK_LEFT);
        event.getRegistry().register(SWING_QUICK_RIGHT);
        
        event.getRegistry().register(EQUIP_BLADE);
        event.getRegistry().register(EQUIP_BLADE_LEFT);
        event.getRegistry().register(EQUIP_BLADE_RIGHT);
        
        event.getRegistry().register(EQUIP_AXE);
        event.getRegistry().register(EQUIP_AXE_LEFT);
        event.getRegistry().register(EQUIP_AXE_RIGHT);
        
        event.getRegistry().register(EQUIP_OTHER);
        event.getRegistry().register(EQUIP_OTHER_LEFT);
        event.getRegistry().register(EQUIP_OTHER_RIGHT);
        
        event.getRegistry().register(SHEATHE_BLADE);
        event.getRegistry().register(SHEATHE_BLADE_LEFT);
        event.getRegistry().register(SHEATHE_BLADE_RIGHT);
        
        event.getRegistry().register(SHEATHE_AXE);
        event.getRegistry().register(SHEATHE_AXE_LEFT);
        event.getRegistry().register(SHEATHE_AXE_RIGHT);
        
        event.getRegistry().register(SHEATHE_OTHER);
        event.getRegistry().register(SHEATHE_OTHER_LEFT);
        event.getRegistry().register(SHEATHE_OTHER_RIGHT);
    }

    public static void playSwingSound(EntityPlayer player, BetterCombatHand betterCombatHand, ItemStack itemStack, int cooldown, boolean offhand) {
        if(!ConfigurationHandler.client.customWeaponSwingSounds) return;
        float volume = getRandomSwingVolume();
        float pitch = getSwingPitch(cooldown);

        if(!betterCombatHand.hasCustomWeapon()) {
            if(ConfigurationHandler.client.customPunchSwingSounds) playSwingPunch(player, volume, pitch, offhand);
            return;
        }

        if(betterCombatHand.getAttackAnimationEnum() == AnimationEnum.STAB) {
            playSwingNonMetal(player, volume, pitch, offhand);
            return;
        }

        if(betterCombatHand.getWeaponProperty() == CustomWeapon.WeaponProperty.TWOHAND) {
            playSwing2H(player, volume, pitch, offhand);
            return;
        }

        if(isMetallic(itemStack)) {
            switch(betterCombatHand.getSoundType()) {
                case BLADE: {
                    playSwingMetalBlade(player, volume, pitch, offhand);
                    return;
                }
                case AXE: {
                    playSwingMetalAxe(player, volume, pitch, offhand);
                    return;
                }
                case BLUNT: {
                    playSwingMetalBlunt(player, volume, pitch, offhand);
                    return;
                }
                default: {
                    playSwingNonMetal(player, volume, pitch, offhand);
                    return;
                }
            }
        }
        else {
            playSwingNonMetal(player, volume, pitch, offhand);
            return;
        }
    }

    public static void playEquipSound(EntityPlayer player, BetterCombatHand betterCombatHand, ItemStack itemStack, int cooldown, boolean offhand) {
        if(!ConfigurationHandler.client.customWeaponEquipSounds) return;
        //Add equip sound to shield
        if(!betterCombatHand.hasCustomWeapon() && !(itemStack.getItem() instanceof ItemShield)) return;

        float volume = getRandomEquipAndSheatheVolume();
        float pitch = getSwingPitch(cooldown);

        if(!isMetallic(itemStack)) {
            playEquipOtherSound(player, volume, pitch, offhand);
            return;
        }
        else {
            switch(betterCombatHand.getSoundType()) {
                case BLADE: {
                    playEquipBladeSound(player, volume, pitch, offhand);
                    return;
                }
                case AXE: {
                    playEquipAxeSound(player, volume, pitch, offhand);
                    return;
                }
                default: {
                    playEquipOtherSound(player, volume, pitch, offhand);
                    return;
                }
            }
        }
    }

    public static void playSheatheSound(EntityPlayer player, BetterCombatHand betterCombatHand, ItemStack itemStack, int cooldown, boolean offhand) {
        if(!ConfigurationHandler.client.customWeaponSheatheSounds) return;
        if(!betterCombatHand.hasCustomWeapon()) return;
        
        float volume = getRandomEquipAndSheatheVolume();
        float pitch = getEquipAndSheathePitch(cooldown);
        
        if(!isMetallic(itemStack)) {
            playSheatheOtherSound(player, volume, pitch, offhand);
            return;
        }
        else {
            switch(betterCombatHand.getSoundType()) {
                case BLADE: {
                    playSheatheBladeSound(player, volume, pitch, offhand);
                    return;
                }
                case AXE: {
                    playSheatheAxeSound(player, volume, pitch, offhand);
                    return;
                }
                default: {
                    playSheatheOtherSound(player, volume, pitch, offhand);
                    return;
                }
            }
        }
    }
    
    public static void playSwingMetalBlade(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SWING_METAL_BLADE, SWING_METAL_BLADE_LEFT, volume, pitch);
        else playSound(player, SWING_METAL_BLADE, SWING_METAL_BLADE_RIGHT, volume, pitch);

    }

    public static void playSwingMetalAxe(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SWING_METAL_AXE, SWING_METAL_AXE_LEFT, volume, pitch);
        else playSound(player, SWING_METAL_AXE, SWING_METAL_AXE_RIGHT, volume, pitch);
    }

    public static void playSwingMetalBlunt(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SWING_METAL_BLUNT, SWING_METAL_BLUNT_LEFT, volume, pitch);
        else playSound(player, SWING_METAL_BLUNT, SWING_METAL_BLUNT_RIGHT, volume, pitch);
    }

    public static void playSwing2H(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SWING_2H, SWING_2H_LEFT, volume, pitch);
        else playSound(player, SWING_2H, SWING_2H_RIGHT, volume, pitch);
    }

    public static void playSwingPunch(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SWING_NORMAL, SWING_NORMAL_LEFT, volume, pitch);
        else playSound(player, SWING_NORMAL, SWING_NORMAL_RIGHT, volume, pitch);
    }
    
    public static void playSwingNonMetal(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(pitch <= 0.9F) {
            pitch += 0.1F;
            if(offhand) playSound(player, SWING_SLOW, SWING_SLOW_LEFT, volume, pitch);
            else playSound(player, SWING_SLOW, SWING_SLOW_RIGHT, volume, pitch);
        }
        else if(pitch >= 1.1F) {
            pitch -= 0.1F;
            if(offhand) playSound(player, SWING_QUICK, SWING_QUICK_LEFT, volume*0.6F, pitch);
            else playSound(player, SWING_QUICK, SWING_QUICK_RIGHT, volume*0.6F, pitch);
        }
        else {
            if(offhand) playSound(player, SWING_NORMAL, SWING_NORMAL_LEFT, volume, pitch);
            else playSound(player, SWING_NORMAL, SWING_NORMAL_RIGHT, volume, pitch);
        }
    }

    private static void playEquipAxeSound(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, EQUIP_AXE, EQUIP_AXE_LEFT, volume, pitch);
        else playSound(player, EQUIP_AXE, EQUIP_AXE_RIGHT, volume, pitch);
    }

    private static void playEquipBladeSound(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, EQUIP_BLADE, EQUIP_BLADE_LEFT, volume, pitch);
        else playSound(player, EQUIP_BLADE, EQUIP_BLADE_RIGHT, volume, pitch);
    }

    private static void playEquipOtherSound(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, EQUIP_OTHER, EQUIP_OTHER_LEFT, volume, pitch);
        else playSound(player, EQUIP_OTHER, EQUIP_OTHER_RIGHT, volume, pitch);
    }

    private static void playSheatheBladeSound(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SHEATHE_BLADE, SHEATHE_BLADE_LEFT, volume, pitch);
        else playSound(player, SHEATHE_BLADE, SHEATHE_BLADE_RIGHT, volume, pitch);
    }

    private static void playSheatheAxeSound(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SHEATHE_AXE, SHEATHE_AXE_LEFT, volume, pitch);
        else playSound(player, SHEATHE_AXE, SHEATHE_AXE_RIGHT, volume, pitch);
    }

    private static void playSheatheOtherSound(EntityPlayer player, float volume, float pitch, boolean offhand) {
        if(offhand) playSound(player, SHEATHE_OTHER, SHEATHE_OTHER_LEFT, volume, pitch);
        else playSound(player, SHEATHE_OTHER, SHEATHE_OTHER_RIGHT, volume, pitch);
    }
    
    public static void playSound(EntityPlayer player, SoundEvent mono, SoundEvent stereo, float volume, float pitch) {
        try {
            if(player.world.isRemote) {
                //Play stereo sound if it exists, otherwise mono, only play it to client player if they are the source of the sound
                player.world.playSound(player, player.posX, player.posY, player.posZ, stereo == null ? mono : stereo, player.getSoundCategory(), volume, pitch);
            }
            //TODO: handle sound packet so others hear new custom sounds from players
            //TODO: config option to only play mono sounds
            /*
            else {
                //Otherwise, play mono sound to everyone else from server
                //player.world.playSound(player, player.posX, player.posY, player.posZ, mono, player.getSoundCategory(), volume, pitch);
            }
            */
        }
        catch(Exception ignored) { }
    }

    public static float getSwingPitch(float f) {
        return 0.75F + (12.5F / f) * 0.25F;
    }

    public static float getEquipAndSheathePitch(float f) {
        return 0.8F + (12.5F / f) * 0.2F;
    }

    public static float getRandomSwingVolume() {
        return ConfigurationHandler.client.weaponSwingVolumeMult * (0.7F + rand.nextFloat() * 0.1F);
    }

    public static float getRandomEquipAndSheatheVolume() {
        return ConfigurationHandler.client.weaponEquipSheatheVolumeMult * (0.7F + rand.nextFloat() * 0.1F);
    }
    
    //TODO: Better way of handling this
    private static boolean isMetallic(ItemStack stack) {
        String name = String.valueOf(stack.getItem().getRegistryName());
        for(String s : ConfigurationHandler.client.nonMetallicSoundWordList) {
            if(name.contains(s)) {
                return false;
            }
        }
        return true;
    }
}