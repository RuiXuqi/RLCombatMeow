package bettercombat.mod.handler;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.capability.CapabilityOffhandHurtResistance;
import bettercombat.mod.util.BetterCombatMod;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import bettercombat.mod.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlers {

    @CapabilityInject(CapabilityOffhandHurtResistance.class)
    public static final Capability<CapabilityOffhandHurtResistance> OFFHAND_HURTRESISTANCE = Helpers.getNull();
    
    @CapabilityInject(CapabilityOffhandCooldown.class)
    public static final Capability<CapabilityOffhandCooldown> OFFHAND_COOLDOWN = Helpers.getNull();

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if(event.getTarget() == null) return;
        if(event.getTarget().hurtResistantTime <= 10 ) {
            if(ConfigurationHandler.client.moreSweepParticles) {
                BetterCombatMod.proxy.spawnSweep((EntityPlayer)event.getEntityLiving());
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        CapabilityOffhandHurtResistance cohr = event.getEntityLiving().getCapability(OFFHAND_HURTRESISTANCE, null);
        if(cohr != null) cohr.tick();

        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            CapabilityOffhandCooldown cof = player.getCapability(OFFHAND_COOLDOWN, null);

            if(cof != null) {
                cof.tick();
                //if(!player.world.isRemote) cof.sync();//Sync in Helpers when its initially set, not here, hopefully works with less packet spam?
            }
        }
    }

    @SubscribeEvent
    public void onEntityConstruct(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "OFFHAND_COOLDOWN"), new CapabilityOffhandCooldown((EntityPlayer)event.getObject()));
        }
        if(event.getObject() instanceof EntityLivingBase) {
            event.addCapability(new ResourceLocation(Reference.MOD_ID, "OFFHAND_HURTRESISTANCE"), new CapabilityOffhandHurtResistance());
        }
    }
}