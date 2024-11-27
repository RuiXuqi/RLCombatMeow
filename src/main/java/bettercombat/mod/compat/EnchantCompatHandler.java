package bettercombat.mod.compat;

/**
 * Compatibility util for other mods to get additional combat information from player attacks
 * Primarily meant for Enchantment mods like SoManyEnchantments to handle specific enchants without using RLCombat Events
 */
public abstract class EnchantCompatHandler {
	
	/**
	 * If getKnockbackModifier is being triggered from Offhand
	 */
	public static boolean knockbackFromOffhand = false;
	
	/**
	 * The cooldown strength of the attack when triggering getKnockbackModifier
	 */
	public static float knockbackCooledStrength = 1.0F;
	
	/**
	 * If getFireAspectModifier is being triggered from Offhand
	 */
	public static boolean fireAspectFromOffhand = false;
	
	/**
	 * The cooldown strength of the attack when triggering getFireAspectModifier
	 */
	public static float fireAspectCooledStrength = 1.0F;
	
	/**
	 * If getSweepingDamageRatio is being triggered from Offhand
	 */
	public static boolean sweepingFromOffhand = false;
	
	/**
	 * The cooldown strength of the attack when triggering getSweepingDamageRatio
	 */
	public static float sweepingCooledStrength = 1.0F;
	
	/**
	 * If applyThornEnchantments is being triggered from Offhand
	 */
	public static boolean thornsFromOffhand = false;
	
	/**
	 * The cooldown strength of the attack when triggering applyThornEnchantments
	 */
	public static float thornsCooledStrength = 1.0F;
	
	/**
	 * If applyArthropodEnchantments is being triggered from Offhand
	 */
	public static boolean arthropodFromOffhand = false;
	
	/**
	 * The cooldown strength of the attack when triggering applyArthropodEnchantments
	 */
	public static float arthropodCooledStrength = 1.0F;
	
	/**
	 * The cooldown strength of the attack when triggering attackEntityFrom
	 * Offhand check not needed as RLCombat internally swaps offhand to mainhand during attackEntityFrom handling
	 */
	public static float attackEntityFromCooledStrength = 1.0F;
}