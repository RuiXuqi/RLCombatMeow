package bettercombat.mod.compat;

public abstract class EnchantCompatHandler {
	
	//SME patches into applyArthropodEnchantments and checks the stacktrace to see if its from an offhand packet
	//Easier to set these fields instead and check it for if its from offhand
	
	public static boolean thornsFromOffhand = false;
	
	public static boolean arthropodFromOffhand = false;
	
	public static boolean knockbackFromOffhand = false;
	
	public static boolean fireAspectFromOffhand = false;
	
	public static boolean efficiencyFromOffhand = false;
	
	public static boolean lootingFromOffhand = false;
	
}