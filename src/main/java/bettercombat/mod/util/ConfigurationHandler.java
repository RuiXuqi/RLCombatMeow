package bettercombat.mod.util;

import bettercombat.mod.client.animation.util.AnimationEnum;
import bettercombat.mod.client.animation.util.CustomWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Config(modid = Reference.MOD_ID)
public class ConfigurationHandler {

    @Config.Comment("Server-Side Config")
    @Config.Name("Server-Side Config")
    public static final ServerConfig server = new ServerConfig();

    @Config.Comment("Client-Side Config")
    @Config.Name("Client-Side Config")
    public static final ClientConfig client = new ClientConfig();

    public static class ServerConfig {
        
        @Config.Comment("Allows you to attack with your offhand")
        @Config.Name("Enable Offhand Attack")
        public boolean enableOffhandAttack = true;
        
        @Config.Comment("Attacking with the offhand does less damage")
        @Config.Name("Weaker Offhand")
        public boolean weakerOffhand = true;
        
        @Config.Comment("The efficiency of offhand attacks if Weaker Offhand is enabled")
        @Config.Name("Offhand Efficiency")
        @Config.RangeDouble(min=0.0F, max=1.0F)
        public float offhandEfficiency = 0.5F;

        @Config.Comment("Requires your energy to be full in order to attack")
        @Config.Name("Attack Requires Full Energy")
        public boolean requireFullEnergy = false;
        
        @Config.Comment("Requires your energy to be full in order to randomly crit")
        @Config.Name("Random Crit Requires Full Energy")
        public boolean requireEnergyToRandomCrit = true;
        
        @Config.Comment("Requires your energy to be full in order to jump crit")
        @Config.Name("Jump Crit Requires Full Energy")
        public boolean requireEnergyToJumpCrit = true;
        
        @Config.Comment("Allows crits to happen based on a random chance")
        @Config.Name("Random Crit")
        public boolean randomCrits = true;
        
        @Config.Comment("Chance of a crit if Random Crit is enabled")
        @Config.Name("Random Crit Chance")
        @Config.RangeDouble(min=0.0F, max=1.0F)
        public float critChance = 0.2F;
        
        @Config.Comment("Maximum distance from a target to allow jump crits")
        @Config.Name("Jump Crit Max Distance")
        @Config.RangeDouble(min=0.0F, max=10.0F)
        public float distanceToJumpCrit = 2.0F;

        @Config.Comment("Attacking an enemy will not interrupt your sprint")
        @Config.Name("Attack Does Not Interrupt Sprint")
        public boolean dontInterruptSprint = false;
        
        @Config.Comment("If RLCombat should attempt to swing through blocks that have no collision when attacking enemies")
        @Config.Name("Swing Through Passable Blocks")
        public boolean swingThroughPassableBlocks = true;

        @Config.Comment("Add an additional sound when striking a target")
        @Config.Name("Additional Hit Sound")
        public boolean additionalHitSound = true;

        @Config.Comment("Add an additional sound when critical striking a target")
        @Config.Name("Additional Crit Sound")
        public boolean additionalCritSound = true;

        @Config.Comment("Whitelisted item classes used for attacking from offhand")
        @Config.Name("Offhand Item Class Whitelist")
        public String[] offhandItemClassWhitelist = new String[] {
                "net.minecraft.item.ItemSword",
                "net.minecraft.item.ItemAxe",
                "net.minecraft.item.ItemSpade",
                "net.minecraft.item.ItemPickaxe",
                "net.minecraft.item.ItemHoe",
                "com.mujmajnkraft.bettersurvival.items.ItemBattleAxe",
                "com.mujmajnkraft.bettersurvival.items.ItemDagger",
                "com.mujmajnkraft.bettersurvival.items.ItemHammer"
        };

        @Config.Comment("Whitelisted item ids in the format \"domain:itemname\" used for attacking from offhand")
        @Config.Name("Offhand Item ID Whitelist")
        public String[] offhandItemIDWhitelist = new String[] {
        };
        
        @Config.Comment("Blacklisted item ids in the format \"domain:itemname\" used for attacking from offhand")
        @Config.Name("Offhand Item ID Blacklist")
        public String[] offhandItemIDBlacklist = new String[] {
        };

        @Config.Comment("Blacklisted entity classes for attacking with offhand, you will not be able to attack any entity with offhand that extends these classes")
        @Config.Name("Entity Offhand Blacklist")
        public String[] entityBlacklist = new String[] {
                "net.minecraft.entity.passive.EntityHorse",
                "net.minecraft.entity.item.EntityArmorStand",
                "net.minecraft.entity.passive.EntityVillager",
                "net.minecraft.entity.item.EntityItemFrame"
        };
        
        @Config.Comment("Enables a fallback check if modded attacks bypass RLCombats's packets")
        @Config.Name("Enable Mixin Compat Fallback")
        public boolean enableMixinCompatFallback = true;
        
        @Config.Comment("Should warnings from parsing config entry lists be logged")
        @Config.Name("Log Config List Warnings")
        public boolean logConfigListWarnings = true;
    }

    public static class ClientConfig {
        
        @Config.Comment("If RLCombat should display custom weapon animations while swinging (Credit to and modified from ImmersiveCombat)")
        @Config.Name("Custom Weapon Attack Animations")
        public boolean customWeaponAttackAnimations = true;
        
        @Config.Comment("If RLCombat should also display custom weapon animations while mining (Credit to and modified from ImmersiveCombat)")
        @Config.Name("Custom Weapon Mining Animations")
        public boolean customWeaponMiningAnimations = true;
        
        @Config.Comment("If RLCombat should play custom weapon sounds for swinging (Credit to and modified from ImmersiveCombat)")
        @Config.Name("Custom Weapon Swing Sounds")
        public boolean customWeaponSwingSounds = true;
        
        @Config.Comment("If RLCombat should play custom weapon sounds for equipping (Credit to and modified from ImmersiveCombat)")
        @Config.Name("Custom Weapon Equip Sounds")
        public boolean customWeaponEquipSounds = true;
        
        @Config.Comment("If RLCombat should play custom weapon sounds for sheathing (Credit to and modified from ImmersiveCombat)")
        @Config.Name("Custom Weapon Sheathe Sounds")
        public boolean customWeaponSheatheSounds = true;
        
        @Config.Comment("If Custom Weapon Swing Sounds is enabled, should swings with undefined items/empty hands also make a sound")
        @Config.Name("Custom Punch Swing Sounds")
        public boolean customPunchSwingSounds = true;
        
        @Config.Comment("If Custom Weapon Sounds are enabled, makes them all only play in mono, instead of using stereo")
        @Config.Name("Custom Weapon Sounds Mono")
        public boolean customWeaponSoundsMono = false;

        @Config.Comment("If all attacks should spawn the sweep particles")
        @Config.Name("More Sweep Particles")
        public boolean moreSweepParticles = false;
        
        @Config.Comment("If the vanilla attack ready icon should be rendered when both hands are fully cooled down and an entity is pointed at")
        @Config.Name("Render Attack Ready Icon")
        public boolean renderAttackReadyIcon = true;
        
        @Config.Comment("How fast held items move during the breathing animation")
        @Config.Name("Breathing Animation Speed")
        @Config.RangeDouble(min = 0.0F)
        public float breathingAnimationSpeed = 0.08F;
        
        @Config.Comment("How far held items move during the breathing animation")
        @Config.Name("Breathing Animation Intensity")
        @Config.RangeDouble(min = 0.0F)
        public float breathingAnimationIntensity = 0.02F;
        
        @Config.Comment("The distance under which the too close animation will play")
        @Config.Name("Too Close Animation Distance")
        @Config.RangeDouble(min = 0.2F, max = 2.0F)
        public float tooCloseAnimationDistance = 0.7F;
        
        @Config.Comment("How far held items move during the too close animation")
        @Config.Name("Too Close Animation Intensity")
        @Config.RangeDouble(min = 0.0F)
        public float tooCloseAnimationIntensity = 0.4F;
        
        @Config.Comment("If the too close animation should play when too close to entities")
        @Config.Name("Too Close Animation Entities")
        public boolean tooCloseAnimationEntities = true;
        
        @Config.Comment("If the too close animation should play when too close to blocks")
        @Config.Name("Too Close Animation Blocks")
        public boolean tooCloseAnimationBlocks = true;
        
        @Config.Comment("If the breathing and too close animations should play for all items or only defined custom weapons")
        @Config.Name("Breathing/Too Close Animation on All Items")
        public boolean breathingTooCloseAnimationAllItems = true;
        
        @Config.Comment("If weapons should tilt forward when the player sprints")
        @Config.Name("Sprinting Weapon Tilt Animation")
        public boolean sprintingWeaponTilt = true;

        @Config.Comment("How much your camera pitch moves when swinging a weapon")
        @Config.Name("Swing Animation Camera Pitch")
        @Config.RangeDouble(min = 0.0F)
        public float cameraPitchSwing = 1.0F;

        @Config.Comment("How much your camera yaw moves when swinging a weapon")
        @Config.Name("Swing Animation Camera Yaw")
        @Config.RangeDouble(min = 0.0F)
        public float cameraYawSwing = 1.0F;

        @Config.Comment(
                "Item classes with custom weapon entries to be used for animations and sounds" + "\n" +
                "Format: ItemClass, AttackAnimation, MiningAnimation, SoundType, HandType, Priority" + "\n" +
                " " + "\n" +
                "ItemClass: The class or parent class of the item you want to define" + "\n" +
                "AttackAnimation: The animation to be used during attack swing" + "\n" +
                "MiningAnimation: The animation to be used during mining swing" + "\n" +
                "SoundType: The sound type to be used to determine what sounds are played" + "\n" +
                "HandType: The hand type to be used to also determine what sounds are played" + "\n" +
                "Priority: The class priority, for if an item is an instance of multiple defined classes" + "\n" +
                " " + "\n" +
                "Valid Animations: SWEEP_COMBO, SWEEP_1, SWEEP_2, CHOP, DIG, STAB, STAB_CAESTUS, PUNCH" + "\n" +
                "Valid SoundTypes: BLADE, AXE, BLUNT, DEFAULT" + "\n" +
                "Valid HandTypes: ONEHAND, TWOHAND" + "\n"
        )
        @Config.Name("Item Class Custom Weapon Entries")
        public String[] weaponClassCustomWeapons = new String[] {
                "net.minecraft.item.ItemSword, SWEEP_1, PUNCH, BLADE, ONEHAND, 1",
                "net.minecraft.item.ItemAxe, CHOP, CHOP, AXE, ONEHAND, 1",
                "net.minecraft.item.ItemSpade, CHOP, DIG, AXE, ONEHAND, 1",
                "net.minecraft.item.ItemPickaxe, CHOP, CHOP, AXE, ONEHAND, 1",
                "net.minecraft.item.ItemHoe, CHOP, DIG, AXE, ONEHAND, 1",
                "com.oblivioussp.spartanweaponry.item.ItemDagger, STAB, PUNCH, BLADE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemParryingDagger, STAB, PUNCH, BLADE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemLongsword, SWEEP_1, PUNCH, BLADE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemKatana, SWEEP_1, PUNCH, BLADE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemSaber, SWEEP_1, PUNCH, BLADE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemRapier, STAB, PUNCH, BLADE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemGreatsword, SWEEP_1, PUNCH, AXE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemClub, CHOP, CHOP, BLUNT, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemHammer, CHOP, CHOP, BLUNT, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemWarhammer, SWEEP_1, PUNCH, BLUNT, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemSpear, STAB, PUNCH, BLADE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemHalberd, SWEEP_1, PUNCH, AXE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemPike, STAB, PUNCH, BLUNT, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemLance, STAB, PUNCH, BLUNT, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemThrowingKnife, STAB, PUNCH, BLADE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemThrowingAxe, CHOP, PUNCH, AXE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemJavelin, STAB, PUNCH, BLUNT, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemBoomerang, PUNCH, PUNCH, BLUNT, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemBattleaxe, SWEEP_1, PUNCH, AXE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemMace, SWEEP_1, PUNCH, AXE, ONEHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemGlaive, SWEEP_1, PUNCH, AXE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemQuarterstaff, SWEEP_1, PUNCH, BLUNT, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemScythe, SWEEP_1, PUNCH, BLADE, TWOHAND, 2",
                "com.oblivioussp.spartanweaponry.item.ItemCaestus, STAB_CAESTUS, STAB_CAESTUS, DEFAULT, ONEHAND, 2",
                "com.dhanantry.scapeandrunparasites.item.tool.WeaponMeleeAxe, CHOP, CHOP, AXE, TWOHAND, 2",
                "com.dhanantry.scapeandrunparasites.item.tool.WeaponMeleeCleaver, SWEEP_1, PUNCH, AXE, TWOHAND, 2",
                "com.dhanantry.scapeandrunparasites.item.tool.WeaponMeleeLance, STAB, PUNCH, BLUNT, TWOHAND, 2",
                "com.dhanantry.scapeandrunparasites.item.tool.WeaponMeleeMaul, SWEEP_1, PUNCH, AXE, TWOHAND, 2",
                "com.dhanantry.scapeandrunparasites.item.tool.WeaponMeleeScythe, SWEEP_1, PUNCH, BLADE, TWOHAND, 2",
                "com.dhanantry.scapeandrunparasites.item.tool.WeaponMeleeSword, SWEEP_1, PUNCH, BLADE, TWOHAND, 2",
                "com.mujmajnkraft.bettersurvival.items.ItemHammer, CHOP, CHOP, BLUNT, ONEHAND, 2",
                "com.mujmajnkraft.bettersurvival.items.ItemSpear, STAB, PUNCH, BLUNT, ONEHAND, 2",
                "com.mujmajnkraft.bettersurvival.items.ItemDagger, STAB, PUNCH, BLADE, ONEHAND, 2",
                "com.mujmajnkraft.bettersurvival.items.ItemBattleAxe, SWEEP_1, PUNCH, AXE, ONEHAND, 2",
                "com.github.alexthe666.iceandfire.item.ItemTideTrident, PUNCH, PUNCH, BLADE, ONEHAND, 2",
                "com.github.alexthe666.iceandfire.item.ItemTrollWeapon, PUNCH, PUNCH, BLUNT, TWOHAND, 2"
        };

        @Config.Comment("Item ids in the format \"domain:itemname\" with custom weapon entries to be used for animations and sounds" + "\n" +
                "(Takes priority over Item Class Entries)" + "\n" +
                "Format: ItemID, AttackAnimation, MiningAnimation, SoundType, HandType" + "\n" +
                " " + "\n" +
                "ItemClass: The class or parent class of the item you want to define" + "\n" +
                "AttackAnimation: The animation to be used during attack swing" + "\n" +
                "MiningAnimation: The animation to be used during mining swing" + "\n" +
                "SoundType: The sound type to be used to determine what sounds are played" + "\n" +
                "HandType: The hand type to be used to also determine what sounds are played" + "\n" +
                " " + "\n" +
                "Valid Animations: SWEEP_COMBO, SWEEP_1, SWEEP_2, CHOP, DIG, STAB, STAB_CAESTUS, PUNCH" + "\n" +
                "Valid SoundTypes: BLADE, AXE, BLUNT, DEFAULT" + "\n" +
                "Valid HandTypes: ONEHAND, TWOHAND" + "\n")
        @Config.Name("Item ID Custom Weapon Entries")
        public String[] weaponIDCustomWeapons = new String[] {
        };
        
        @Config.Comment("Multiplier to the volume for custom weapon swing sounds")
        @Config.Name("Custom Weapon Swing Volume Multiplier")
        public float weaponSwingVolumeMult = 1.0F;
        
        @Config.Comment("Multiplier to the volume for custom weapon equip and sheathe sounds")
        @Config.Name("Custom Weapon Equip/Sheathe Volume Multiplier")
        public float weaponEquipSheatheVolumeMult = 1.0F;
        
        @Config.Comment("List of words or item names to be compared against to give items non-metallic sounds when equipped/sheathed/swung")
        @Config.Name("Non-Metallic Sound Word List")
        public String[] nonMetallicSoundWordList = {"flint", "wood", "stone"};
    }

    private static Class<?>[] itemClassWhiteArray;
    private static Item[] itemInstWhiteArray;
    private static Item[] itemInstBlackArray;
    private static Class<?>[] entityBlackArray;

    public static void initItemListCache() {
        itemClassWhiteArray = null;
        List<Class<?>> classList = new ArrayList<>();
        for(String className : server.offhandItemClassWhitelist) {
            try {
                classList.add(Class.forName(className.trim()));
            }
            catch(ClassNotFoundException ex) {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Item Class not found for entry: " + className + ", ignoring");
            }
        }
        itemClassWhiteArray = classList.toArray(new Class<?>[0]);

        itemInstWhiteArray = null;
        List<Item> itemList = new ArrayList<>();
        for(String itemName : server.offhandItemIDWhitelist) {
            Item itm = Item.REGISTRY.getObject(new ResourceLocation(itemName.trim()));
            if(itm != null) itemList.add(itm);
            else {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Item ID not found for entry: " + itemName + ", ignoring");
            }
        }
        itemInstWhiteArray = itemList.toArray(new Item[0]);
        
        itemInstBlackArray = null;
        List<Item> itemList1 = new ArrayList<>();
        for(String itemName : server.offhandItemIDBlacklist) {
            Item itm = Item.REGISTRY.getObject(new ResourceLocation(itemName.trim()));
            if(itm != null) itemList1.add(itm);
            else {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Item ID not found for entry: " + itemName + ", ignoring");
            }
        }
        itemInstBlackArray = itemList1.toArray(new Item[0]);
    }

    public static void initEntityListCache() {
        entityBlackArray = null;
        List<Class<?>> classList = new ArrayList<>();
        for(String className : server.entityBlacklist) {
            try {
                classList.add(Class.forName(className.trim()));
            }
            catch(ClassNotFoundException ex) {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Entity Class not found for entry: " + className + ", ignoring");
            }
        }
        entityBlackArray = classList.toArray(new Class<?>[0]);
    }

    public static boolean isItemAttackUsableOffhand(final Item item) {
        for(Item blItem : itemInstBlackArray) {
            if(blItem == item) return false;
        }
        for(Item wlItem : itemInstWhiteArray) {
            if(wlItem == item) return true;
        }
        for(Class<?> clazz : itemClassWhiteArray) {
            if(clazz.isInstance(item)) return true;
        }
        return false;
    }

    public static boolean isEntityAttackableOffhand(final Entity entity) {
        for(Class<?> clazz : entityBlackArray) {
            if(clazz.isInstance(entity)) return false;
        }
        return true;
    }

    private static Map<Class<?>, CustomWeapon> weaponClassMap;
    private static Map<Item, CustomWeapon> weaponInstMap;

    public static void initRenderCache() {
        weaponClassMap = new HashMap<>();
        for(String classEntry : client.weaponClassCustomWeapons) {
            try {
                String[] array = classEntry.split(",");
                Class<?> clazz = Class.forName(array[0].trim());
                CustomWeapon newWeapon = new CustomWeapon(
                        AnimationEnum.valueOf(array[1].trim()),
                        AnimationEnum.valueOf(array[2].trim()),
                        CustomWeapon.SoundType.valueOf(array[3].trim()),
                        CustomWeapon.WeaponProperty.valueOf(array[4].trim()),
                        Integer.parseInt(array[5].trim()));
                weaponClassMap.put(clazz, newWeapon);
            }
            catch(ClassNotFoundException ex) {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Weapon Class not found for entry: " + classEntry + ", ignoring");
            }
            catch(Exception ex) {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Weapon Class Entry failed to parse entry: " + classEntry + ", exception: " + ex.getMessage() + ", ignoring");
            }
        }

        weaponInstMap = new HashMap<>();
        for(String itemEntry : client.weaponIDCustomWeapons) {
            try {
                String[] array = itemEntry.split(",");
                Item item = Item.REGISTRY.getObject(new ResourceLocation(array[0].trim()));
                CustomWeapon newWeapon = new CustomWeapon(
                        AnimationEnum.valueOf(array[1].trim()),
                        AnimationEnum.valueOf(array[2].trim()),
                        CustomWeapon.SoundType.valueOf(array[3].trim()),
                        CustomWeapon.WeaponProperty.valueOf(array[4].trim()),
                        1);
                if(item != null) weaponInstMap.put(item, newWeapon);
                else {
                    if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Weapon ID not found for entry: " + itemEntry + ", ignoring");
                }
            }
            catch(Exception ex) {
                if(server.logConfigListWarnings) BetterCombatMod.LOG.log(Level.WARN, "Weapon ID Entry failed to parse entry: " + itemEntry + ", exception: " + ex.getMessage() + ", ignoring");
            }
        }
    }

    public static CustomWeapon getCustomWeapon(Item item) {
        for(Item mapInst : weaponInstMap.keySet()) {
            if(mapInst == item) return weaponInstMap.get(mapInst);
        }
        CustomWeapon weapon = null;
        for(Class<?> clazz : weaponClassMap.keySet()) {
            if(clazz.isInstance(item)) {
                CustomWeapon weapon1 = weaponClassMap.get(clazz);
                if(weapon == null || weapon1.priority > weapon.priority) weapon = weapon1;
            }
        }
        return weapon;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(Reference.MOD_ID)) {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
                BetterCombatMod.proxy.initConfigCache();
            }
        }
    }
}