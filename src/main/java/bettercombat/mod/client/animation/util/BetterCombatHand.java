package bettercombat.mod.client.animation.util;

import java.util.Random;

import bettercombat.mod.client.animation.util.CustomWeapon.WeaponProperty;
import bettercombat.mod.client.animation.util.CustomWeapon.SoundType;
import bettercombat.mod.mixin.IEntityLivingBaseMixin;
import net.minecraft.client.Minecraft;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public class BetterCombatHand {

    //Current custom weapon
    CustomWeapon customWeapon = null;
    
    //Current active attack animation
    public IAnimation activeAttackAnimation;
    
    //Current active mining animation
    public IAnimation activeMiningAnimation;
    
    //How long the equip sound timer is in ticks after equipping a weapon, counting down to 0, This is only used for determining equip/sheathe sounds
    public int equipSoundTimer = 0;
    //When the swingProgress reaches past this number, make a swing sound then reset
    public float swingTimestampSound = 0.0F;
    
    //Is the weapon currently swinging
    private boolean isSwingInProgress = false;
    //Swing progress int timer ticked up
    private int swingProgressInt = 0;
    //Swing progress float from 0.0 to 1.0
    private float swingProgressFloat = 0.0F;
    //Previous tick swing progress float
    private float prevSwingProgressFloat = 0.0F;
    
    //Minimum cooldown ticks used for sound effects
    public static int minimumCooldownTicks = 4;
    //Maximum cooldown ticks used for sound effects
    public static int maximumCooldownTicks = 15;
    //Cooldown used for sound effects
    public int attackCooldown = minimumCooldownTicks;
    
    public float moveRightVariance = 1.0F;
    public float moveUpVariance = 1.0F;
    public float moveCloseVariance = 1.0F;
    public float rotateUpVariance = 1.0F;
    public float rotateCounterClockwiseVariance = 1.0F;
    public float rotateLeftVariance = 1.0F;
    
    //Weapon is currently in equip animation, but hasn't been swung yet
    public boolean firstRaise = false;
    
    private final Random rand = new Random();

    public BetterCombatHand() {
        this.resetBetterCombatWeapon();
    }

    public void setBetterCombatWeapon(CustomWeapon customWeapon, int cooldownTicks) {
        this.customWeapon = customWeapon;

        this.equipSoundTimer = cooldownTicks / 2;

        this.activeAttackAnimation = this.getAttackAnimationEnum().getAnimation();
        this.activeMiningAnimation = this.getMiningAnimationEnum().getAnimation();
        
        this.firstRaise = true;
    }
    
    public void tick() {
        int i = Math.max(((IEntityLivingBaseMixin)Minecraft.getMinecraft().player).invokeGetArmSwingAnimationEnd(), 1);
        
        this.prevSwingProgressFloat = this.swingProgressFloat;
        
        if(this.isSwingInProgress) {
            ++this.swingProgressInt;
            
            if(this.swingProgressInt >= i) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        }
        else {
            this.swingProgressInt = 0;
        }
        
        this.swingProgressFloat = (float)this.swingProgressInt / (float)i;
    }
    
    public void swingHand() {
        if(!this.isSwingInProgress || this.swingProgressInt >= ((IEntityLivingBaseMixin)Minecraft.getMinecraft().player).invokeGetArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
        }
    }
    
    public float getSwingProgress(float partialTick) {
        float f = this.swingProgressFloat - this.prevSwingProgressFloat;
        
        if(f < 0.0F) {
            ++f;
        }
        
        return this.prevSwingProgressFloat + f * partialTick;
    }
    
    private CustomWeapon getCustomWeapon() {
        return this.customWeapon;
    }
    
    public boolean hasCustomWeapon() {
        return this.customWeapon != null;
    }
    
    public WeaponProperty getWeaponProperty() {
        if(this.hasCustomWeapon()) {
            return this.getCustomWeapon().property;
        }
        return WeaponProperty.ONEHAND;
    }

    public AnimationEnum getAttackAnimationEnum() {
        if(this.hasCustomWeapon()) {
            return this.getCustomWeapon().attackAnimation;
        }
        return AnimationEnum.PUNCH;
    }
    
    public AnimationEnum getMiningAnimationEnum() {
        if(this.hasCustomWeapon()) {
            return this.getCustomWeapon().miningAnimation;
        }
        return AnimationEnum.PUNCH;
    }

    public IAnimation getActiveAttackAnimation() {
        return this.activeAttackAnimation != null ? this.activeAttackAnimation : AnimationEnum.PUNCH.getAnimation();
    }
    
    public IAnimation getActiveMiningAnimation() {
        return this.activeMiningAnimation != null ? this.activeMiningAnimation : AnimationEnum.PUNCH.getAnimation();
    }

    public SoundType getSoundType() {
        if(this.hasCustomWeapon()) {
            return this.getCustomWeapon().soundType;
        }
        return SoundType.DEFAULT;
    }
    
    public void resetBetterCombatWeapon() {
        this.customWeapon = null;

        this.isSwingInProgress = false;
        this.swingProgressInt = 0;
        this.swingProgressFloat = 0.0F;
        this.prevSwingProgressFloat = 0.0F;

        this.equipSoundTimer = 0;

        this.swingTimestampSound = 0;

        this.activeAttackAnimation = AnimationEnum.PUNCH.getAnimation();
        this.activeMiningAnimation = AnimationEnum.PUNCH.getAnimation();
        
        this.firstRaise = false;
    }

    public void randomizeVariances() {
        this.moveRightVariance = this.randomMoveVariance();
        this.moveUpVariance = this.randomMoveVariance();
        this.moveCloseVariance = this.randomMoveVariance();

        this.rotateUpVariance = this.randomRotationVariance();
        this.rotateCounterClockwiseVariance = this.randomRotationVariance();
        this.rotateLeftVariance = this.randomRotationVariance();
    }

    public float randomMoveVariance() {
        return 1.06F - this.rand.nextFloat() * 0.12F;
    }

    public float randomRotationVariance() {
        return 1.03F - this.rand.nextFloat() * 0.06F;
    }

    public float randomPreciseRotationVariance() {
        return 1.015F - this.rand.nextFloat() * 0.03F;
    }

    public boolean soundReady() {
        if(this.swingTimestampSound < this.getSwingProgress(1.0F) && this.swingTimestampSound != 0.0F) {
            this.swingTimestampSound = 0;
            return true;
        }
        return false;
    }
    
    public void setSwingTimestampSound(float timeStamp) {
        this.swingTimestampSound = timeStamp;
    }

    public void initiateAnimation() {
        if(this.hasCustomWeapon()) {
            this.activeAttackAnimation = this.getAttackAnimationEnum().getAnimation();
            this.activeMiningAnimation = this.getMiningAnimationEnum().getAnimation();
            this.getActiveAttackAnimation().setActive(this);
        }
        else {
            this.activeAttackAnimation = AnimationEnum.PUNCH.getAnimation();
            this.activeMiningAnimation = AnimationEnum.PUNCH.getAnimation();
            this.getActiveAttackAnimation().setActive(this);
        }
        this.swingHand();
        
        this.firstRaise = false;
    }
}