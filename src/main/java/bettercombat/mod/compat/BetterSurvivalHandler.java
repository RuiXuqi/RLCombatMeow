package bettercombat.mod.compat;

import com.mujmajnkraft.bettersurvival.items.ItemNunchaku;
import net.minecraft.item.Item;

public abstract class BetterSurvivalHandler {

    public static boolean isNunchaku(Item item) {
        return item instanceof ItemNunchaku;
    }
}