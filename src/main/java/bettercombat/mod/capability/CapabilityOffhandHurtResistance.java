package bettercombat.mod.capability;

import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.mixin.IEntityLivingBaseMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.concurrent.Callable;

public class CapabilityOffhandHurtResistance implements ICapabilityProvider {
	
	private int hurtResistantTimeOffhand;
	private float lastDamageOffhand;
	
	public CapabilityOffhandHurtResistance() {
		this.hurtResistantTimeOffhand = 0;
		this.lastDamageOffhand = 0.0F;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability != null && capability == EventHandlers.OFFHAND_HURTRESISTANCE;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability != null && capability == EventHandlers.OFFHAND_HURTRESISTANCE ? (T) this : null;
	}
	
	public void tick() {
		if(this.hurtResistantTimeOffhand > 0) this.hurtResistantTimeOffhand -= 1;
		else this.lastDamageOffhand = 0;
	}
	
	public boolean attackEntityFromOffhand(Entity target, DamageSource dmgSrc, float amount) {
		if(target.world.isRemote || !(target instanceof EntityLivingBase || target instanceof MultiPartEntityPart)) {
			return false;
		}
		
		EntityLivingBase targetMain;
		//Use parents values but attack specific part
		if(target instanceof MultiPartEntityPart) targetMain = (EntityLivingBase)(((MultiPartEntityPart)target).parent);
		else targetMain = (EntityLivingBase)target;
		
		if(targetMain == null || targetMain.getHealth() <= 0.0F) return false;
		
		//Theoretically true source should never not be a player for offhand attacks, but handle as EntityLivingBase anyways
		if(!(dmgSrc.getTrueSource() instanceof EntityLivingBase)) return false;
		EntityLivingBase attacker = (EntityLivingBase)dmgSrc.getTrueSource();
		
		//Swap offhand item to mainhand, so entities can properly determine what item hit them
		ItemStack bufferStack = attacker.getHeldItemMainhand();
		attacker.setHeldItem(EnumHand.MAIN_HAND, attacker.getHeldItemOffhand());
		attacker.setHeldItem(EnumHand.OFF_HAND, bufferStack);
		
		//Save mainhand hurt values and set it to use offhand values instead
		//Ignore hurtTime as it is for animation and should be hand-agnostic
		int mainHurtResistance = targetMain.hurtResistantTime;
		float mainLastDamage = ((IEntityLivingBaseMixin)targetMain).getLastDamage();
		targetMain.hurtResistantTime = this.hurtResistantTimeOffhand;
		((IEntityLivingBaseMixin)targetMain).setLastDamage(this.lastDamageOffhand);
		
		//Attack non-cast, incase its multipart like the ender dragon
		boolean successfulAttack = target.attackEntityFrom(dmgSrc, amount);
		
		//Swap the values back and use the new values for offhand set through attackEntityFrom
		this.hurtResistantTimeOffhand = targetMain.hurtResistantTime;
		this.lastDamageOffhand = ((IEntityLivingBaseMixin)targetMain).getLastDamage();
		targetMain.hurtResistantTime = mainHurtResistance;
		((IEntityLivingBaseMixin)targetMain).setLastDamage(mainLastDamage);
		
		//Swap held items back
		bufferStack = attacker.getHeldItemOffhand();
		attacker.setHeldItem(EnumHand.OFF_HAND, attacker.getHeldItemMainhand());
		attacker.setHeldItem(EnumHand.MAIN_HAND, bufferStack);
		
		return successfulAttack;
	}
	
	public int getHurtResistantTimeOffhand() {
		return this.hurtResistantTimeOffhand;
	}
	
	public void setHurtResistantTimeOffhand(int hurtResistantTimeOffhand) {
		this.hurtResistantTimeOffhand = hurtResistantTimeOffhand;
	}
	
	public float getLastDamageOffhand() {
		return this.lastDamageOffhand;
	}
	
	public void setLastDamageOffhand(float lastDamageOffhand) {
		this.lastDamageOffhand = lastDamageOffhand;
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(CapabilityOffhandHurtResistance.class, new Storage(), new Factory());
	}
	
	public static class Storage implements net.minecraftforge.common.capabilities.Capability.IStorage<CapabilityOffhandHurtResistance> {
		
		@Override
		public NBTBase writeNBT(Capability<CapabilityOffhandHurtResistance> capability, CapabilityOffhandHurtResistance instance, EnumFacing side) {
			return null;
		}
		
		@Override
		public void readNBT(Capability<CapabilityOffhandHurtResistance> capability, CapabilityOffhandHurtResistance instance, EnumFacing side, NBTBase nbt) {
		
		}
	}
	
	public static class Factory implements Callable<CapabilityOffhandHurtResistance> {
		
		@Override
		public CapabilityOffhandHurtResistance call() {
			return null;
		}
	}
}