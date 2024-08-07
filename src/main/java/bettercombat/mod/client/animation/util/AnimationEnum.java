package bettercombat.mod.client.animation.util;

import bettercombat.mod.client.animation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Custom Animation and Sound Systems based on and modified from ImmersiveCombat
 * Credit to
 * https://github.com/bglandolt/bettercombat
 * https://www.curseforge.com/minecraft/mc-mods/immersive-combat
 */
public enum AnimationEnum {

    SWEEP_COMBO(new AnimationSweep(), new AnimationSweep2()),
    SWEEP_1(new AnimationSweep()),
    SWEEP_2(new AnimationSweep2()),
    CHOP(new AnimationChop()),
    DIG(new AnimationDig()),
    STAB(new AnimationStab()),
    STAB_CAESTUS(new AnimationStabCaestus()),
    PUNCH(new AnimationPunch());

    private final static Random rand = new Random();
    private final List<IAnimation> animations = new ArrayList<>();

    AnimationEnum(IAnimation... entries) {
        animations.addAll(Arrays.asList(entries));
    }

    public IAnimation getAnimation() {
        int index = 0;
        if(animations.size() > 1) {
            index = rand.nextInt(animations.size());
        }
        return animations.get(index);
    }
}