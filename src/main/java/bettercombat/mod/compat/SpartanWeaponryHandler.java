package bettercombat.mod.compat;

import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public abstract class SpartanWeaponryHandler {

    public static void handleSpartanQuickStrike(ItemStack stack, Entity targetEntity) {
        if(stack.getItem() instanceof ItemSwordBase && ((ItemSwordBase)stack.getItem()).hasWeaponProperty(WeaponProperties.QUICK_STRIKE)) {
            if(targetEntity instanceof EntityLivingBase) {
                targetEntity.hurtResistantTime = Math.min(targetEntity.hurtResistantTime, (int)(((double)((EntityLivingBase)targetEntity).maxHurtResistantTime / 20.0D) * (double)ConfigHandler.quickStrikeHurtResistTicks));
            }
            else {
                targetEntity.hurtResistantTime = Math.min(targetEntity.hurtResistantTime, ConfigHandler.quickStrikeHurtResistTicks);
            }
        }
    }
}