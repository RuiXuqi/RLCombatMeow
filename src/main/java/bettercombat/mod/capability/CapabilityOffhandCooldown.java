package bettercombat.mod.capability;

import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.network.PacketOffhandCooldown;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;

public class CapabilityOffhandCooldown implements ICapabilityProvider {

    private int ticksSinceLastSwing;
    private final EntityPlayer player;

    public CapabilityOffhandCooldown(@Nonnull EntityPlayer player) {
        this.ticksSinceLastSwing = 0;
        this.player = player;
    }

    public void sync() {
        PacketOffhandCooldown packet = new PacketOffhandCooldown();
        if(!this.player.world.isRemote) {
            EntityPlayerMP playerMP = (EntityPlayerMP) this.player;
            PacketHandler.instance.sendTo(packet, playerMP);
        }
        else {
            PacketHandler.instance.sendToServer(packet);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability != null && capability == EventHandlers.OFFHAND_COOLDOWN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability != null && capability == EventHandlers.OFFHAND_COOLDOWN ? (T) this : null;
    }

    public void tick() {
        this.ticksSinceLastSwing++;
    }
    
    public void resetTicksSinceLastSwing() {
        this.ticksSinceLastSwing = 0;
    }
    
    public int getTicksSinceLastSwing() {
        return this.ticksSinceLastSwing;
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(CapabilityOffhandCooldown.class, new Storage(), new Factory());
    }

    public static class Storage implements net.minecraftforge.common.capabilities.Capability.IStorage<CapabilityOffhandCooldown> {

        @Override
        public NBTBase writeNBT(Capability<CapabilityOffhandCooldown> capability, CapabilityOffhandCooldown instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<CapabilityOffhandCooldown> capability, CapabilityOffhandCooldown instance, EnumFacing side, NBTBase nbt) {
        
        }
    }

    public static class Factory implements Callable<CapabilityOffhandCooldown> {

        @Override
        public CapabilityOffhandCooldown call() {
            return null;
        }
    }
}