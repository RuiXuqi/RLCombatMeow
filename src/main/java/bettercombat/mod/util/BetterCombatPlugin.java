package bettercombat.mod.util;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("BetterCombat")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class BetterCombatPlugin implements IFMLLoadingPlugin {

    public BetterCombatPlugin() {
        FermiumRegistryAPI.enqueueMixin(false, "mixins.bettercombatmod.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}