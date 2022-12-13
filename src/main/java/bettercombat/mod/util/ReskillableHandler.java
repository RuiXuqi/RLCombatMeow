package bettercombat.mod.util;

import codersafterdark.reskillable.base.ConfigHandler;
import codersafterdark.reskillable.base.LevelLockHandler;
import codersafterdark.reskillable.network.MessageLockedItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class ReskillableHandler {

    public static boolean shouldLockAttack(EntityPlayer player, ItemStack stack) {
        if(player == null || stack.isEmpty() || (!ConfigHandler.enforceOnCreative && player.isCreative())) return false;

        if(!LevelLockHandler.canPlayerUseItem(player, stack)) {
            if(!LevelLockHandler.isFake(player)) {
                LevelLockHandler.tellPlayer(player, stack, MessageLockedItem.MSG_ITEM_LOCKED);
                return true;
            }
            return ConfigHandler.enforceFakePlayers;
        }

        return false;
    }
}