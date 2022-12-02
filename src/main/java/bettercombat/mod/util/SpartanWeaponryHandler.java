package bettercombat.mod.util;

import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public abstract class SpartanWeaponryHandler {
    public static void handleSpartanQuickStrike(ItemStack stack, Entity targetEntity) {
        if(stack.getItem() instanceof ItemSwordBase && ((ItemSwordBase)stack.getItem()).hasWeaponProperty(WeaponProperties.QUICK_STRIKE)) {
            targetEntity.hurtResistantTime = ConfigHandler.quickStrikeHurtResistTicks;
        }
    }
}