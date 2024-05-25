package bettercombat.mod.compat;

import net.minecraftforge.fml.common.Loader;

public abstract class ModLoadedUtil {

    private static Boolean betterSurvivalLoaded = null;
    private static Boolean qualityToolsLoaded = null;
    private static Boolean reskillableLoaded = null;
    private static Boolean spartanWeaponryLoaded = null;
    private static Boolean iceandfireLoaded = null;

    public static boolean isBetterSurvivalLoaded() {
        if(betterSurvivalLoaded == null) betterSurvivalLoaded = Loader.isModLoaded("mujmajnkraftsbettersurvival");
        return betterSurvivalLoaded;
    }

    public static boolean isQualityToolsLoaded() {
        if(qualityToolsLoaded == null) qualityToolsLoaded = Loader.isModLoaded("qualitytools");
        return qualityToolsLoaded;
    }

    public static boolean isReskillableLoaded() {
        if(reskillableLoaded == null) reskillableLoaded = Loader.isModLoaded("reskillable");
        return reskillableLoaded;
    }

    public static boolean isSpartanWeaponryLoaded() {
        if(spartanWeaponryLoaded == null) spartanWeaponryLoaded = Loader.isModLoaded("spartanweaponry");
        return spartanWeaponryLoaded;
    }

    public static boolean isIceAndFireLoaded() {
        if(iceandfireLoaded == null) iceandfireLoaded = Loader.isModLoaded("iceandfire");
        return iceandfireLoaded;
    }
}