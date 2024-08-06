package bettercombat.mod.client.animation.util;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public class CustomWeapon {

    public AnimationEnum attackAnimation;
    public AnimationEnum miningAnimation;
    public SoundType soundType;
    public WeaponProperty property;
    public int priority;

    public CustomWeapon(AnimationEnum attackAnimation, AnimationEnum miningAnimation, SoundType soundType, WeaponProperty property, int priority) {
        this.attackAnimation = attackAnimation;
        this.miningAnimation = miningAnimation;
        this.soundType = soundType;
        this.property = property;
        this.priority = priority;
    }

    public enum WeaponProperty {
        ONEHAND,
        TWOHAND
    }

    public enum SoundType {
        DEFAULT,
        BLADE,
        BLUNT,
        AXE
    }
}