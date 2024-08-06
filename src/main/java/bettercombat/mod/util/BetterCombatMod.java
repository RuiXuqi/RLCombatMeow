package bettercombat.mod.util;

import bettercombat.mod.capability.CapabilityOffhandHurtResistance;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.capability.CapabilityOffhandCooldown;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid=Reference.MOD_ID,
        name=Reference.MOD_NAME,
        version=Reference.VERSION,
        acceptedMinecraftVersions="[1.12.2]",
        dependencies = "required-after:reachfix;" +
                "required-after:fermiumbooter;" +
                "after:qualitytools;" +
                "after:iceandfire")
public class BetterCombatMod {

    @SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static BetterCombatMod modInstance;

    public static Logger LOG = LogManager.getLogger(Reference.MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandlers());
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CapabilityOffhandCooldown.register();
        CapabilityOffhandHurtResistance.register();
        proxy.initConfigCache();
    }
}