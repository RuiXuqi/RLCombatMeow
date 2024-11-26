package bettercombat.mod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class RLCombatAttackEntityEvent extends AttackEntityEvent {
	
	private final boolean offhand;
	
	/**
	 * Extend AttackEntityEvent and add offhand boolean for compatibility with things such as enchantments
	 */
	public RLCombatAttackEntityEvent(EntityPlayer player, Entity target, boolean offhand) {
		super(player, target);
		this.offhand = offhand;
	}
	
	public boolean getOffhand() {
		return this.offhand;
	}
}
