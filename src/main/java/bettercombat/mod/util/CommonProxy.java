package bettercombat.mod.util;

import bettercombat.mod.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.registerMessages(Reference.MOD_ID);
    }

    public void spawnSweep(EntityPlayer player) { }

    public void initConfigCache() {
        ConfigurationHandler.initItemListCache();
        ConfigurationHandler.initEntityListCache();
    }
}