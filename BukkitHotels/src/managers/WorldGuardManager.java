package managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class WorldGuardManager {

	public static WorldGuardPlugin getWorldGuard(){
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");


		if (p instanceof WorldGuardPlugin) return (WorldGuardPlugin) p;
		else return null;
	}

	public static void addOwner(Player p, ProtectedCuboidRegion r){
		DefaultDomain owners = new DefaultDomain();
		owners.addPlayer(WorldGuardManager.getWorldGuard().wrapPlayer(p));
		r.setOwners(owners);
	}

	public static void addMember(Player p, ProtectedCuboidRegion r){
		DefaultDomain members = new DefaultDomain();
		members.addPlayer(WorldGuardManager.getWorldGuard().wrapPlayer(p));
		r.setMembers(members);
	}

	public static void removeMember(Player p, ProtectedCuboidRegion r){
		DefaultDomain members = new DefaultDomain();
		members.removePlayer(WorldGuardManager.getWorldGuard().wrapPlayer(p));
		r.setMembers(members);
	}

	public static void addRegion(Player p, ProtectedCuboidRegion r){
		getWorldGuard().getRegionManager(p.getWorld()).addRegion(r);
	}

	public static void saveRegions(World world){
		RegionManager regionManager = getWorldGuard().getRegionManager(world);
		try {
			regionManager.save();
		} catch (ProtectionDatabaseException e) {
			e.printStackTrace();
		}
	}
	public static void hotelFlags(ProtectedCuboidRegion r,String hotelName){

		Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>(66);
		/*Map<String, String> flagsOld = new HashMap<String, String>(66);

		File configFile = new File("plugins//Hotels//flags.yml");
		YamlConfiguration cf = YamlConfiguration.loadConfiguration(configFile);

		flags.putAll("com.sk89q.worldguard.protection.flags.StateFlag {}"+flagsOld+"com.sk89q.worldguard.protection.flags.StateFlag$State");
		flags.putAll(map);
		 */

		flags.put(DefaultFlag.PASSTHROUGH, State.ALLOW);
		flags.put(DefaultFlag.BUILD, State.DENY);
		//flags.put(DefaultFlag.CONSTRUCT, State.DENY);
		flags.put(DefaultFlag.PVP, State.DENY);
		flags.put(DefaultFlag.CHEST_ACCESS, State.DENY);
		flags.put(DefaultFlag.PISTONS, State.DENY);
		flags.put(DefaultFlag.TNT, State.DENY);
		flags.put(DefaultFlag.LIGHTER, State.DENY);
		flags.put(DefaultFlag.USE, State.DENY);
		flags.put(DefaultFlag.PLACE_VEHICLE, State.DENY);
		flags.put(DefaultFlag.DESTROY_VEHICLE, State.DENY);
		flags.put(DefaultFlag.SLEEP, State.DENY);
		flags.put(DefaultFlag.MOB_DAMAGE, State.DENY);
		flags.put(DefaultFlag.MOB_SPAWNING, State.DENY);
		//flags.put(DefaultFlag.DENY_SPAWN, State.DENY);
		flags.put(DefaultFlag.INVINCIBILITY, State.DENY);
		flags.put(DefaultFlag.EXP_DROPS, State.DENY);
		flags.put(DefaultFlag.CREEPER_EXPLOSION, State.DENY);
		flags.put(DefaultFlag.OTHER_EXPLOSION, State.DENY);
		flags.put(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, State.DENY);
		flags.put(DefaultFlag.GHAST_FIREBALL, State.DENY);
		flags.put(DefaultFlag.ENDER_BUILD, State.DENY);
		flags.put(DefaultFlag.GREET_MESSAGE, ("&cWelcome to the "+hotelName+" hotel"));
		flags.put(DefaultFlag.FAREWELL_MESSAGE, ("&gCome back soon to the "+hotelName+" hotel"));
		//flags.put(DefaultFlag.NOTIFY_ENTER, Boolean.FALSE);
		//flags.put(DefaultFlag.NOTIFY_LEAVE, Boolean.FALSE);
		flags.put(DefaultFlag.EXIT, State.ALLOW);
		flags.put(DefaultFlag.ENTRY, State.ALLOW);
		flags.put(DefaultFlag.LIGHTNING, State.DENY);
		flags.put(DefaultFlag.ENTITY_PAINTING_DESTROY, State.DENY);
		flags.put(DefaultFlag.ENDERPEARL, State.DENY);
		flags.put(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, State.DENY);
		flags.put(DefaultFlag.ITEM_DROP, State.ALLOW);
		/*flags.put(DefaultFlag.HEAL_AMOUNT, 0);
		flags.put(DefaultFlag.HEAL_DELAY, 0);
		flags.put(DefaultFlag.MIN_HEAL, 0);
		flags.put(DefaultFlag.MAX_HEAL, 20);
		flags.put(DefaultFlag.FEED_DELAY, 0);
		flags.put(DefaultFlag.FEED_AMOUNT, 20);
		flags.put(DefaultFlag.MIN_FOOD, 0);
		flags.put(DefaultFlag.MAX_FOOD, 20);*/
		flags.put(DefaultFlag.SNOW_FALL, State.DENY);
		flags.put(DefaultFlag.SNOW_MELT, State.DENY);
		flags.put(DefaultFlag.ICE_FORM, State.DENY);
		flags.put(DefaultFlag.ICE_MELT, State.DENY);
		flags.put(DefaultFlag.SOIL_DRY, State.DENY);
		//flags.put(DefaultFlag.GAME_MODE, State.DENY);
		flags.put(DefaultFlag.MUSHROOMS, State.DENY);
		flags.put(DefaultFlag.LEAF_DECAY, State.DENY);
		flags.put(DefaultFlag.GRASS_SPREAD, State.DENY);
		flags.put(DefaultFlag.MYCELIUM_SPREAD, State.DENY);
		flags.put(DefaultFlag.VINE_GROWTH, State.DENY);
		flags.put(DefaultFlag.SEND_CHAT, State.ALLOW);
		flags.put(DefaultFlag.RECEIVE_CHAT, State.ALLOW);
		flags.put(DefaultFlag.FIRE_SPREAD, State.DENY);
		flags.put(DefaultFlag.LAVA_FIRE, State.DENY);
		flags.put(DefaultFlag.LAVA_FLOW, State.DENY);
		flags.put(DefaultFlag.WATER_FLOW, State.DENY);
		//flags.put(DefaultFlag.TELE_LOC, State.DENY);
		//flags.put(DefaultFlag.SPAWN_LOC, State.DENY);
		flags.put(DefaultFlag.POTION_SPLASH, State.DENY);
		//flags.put(DefaultFlag.BLOCKED_CMDS, SetFlag<T>);
		//flags.put(DefaultFlag.ALLOWED_CMDS, State.DENY);
		//Double price = 0.0;
		//flags.put(DefaultFlag.PRICE, price);
		flags.put(DefaultFlag.BUYABLE, Boolean.FALSE);

		r.setFlags(flags);
	}
	public static void roomFlags(ProtectedCuboidRegion r,String hotelName,Player p,int roomNum){

		Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>(66);
		flags.put(DefaultFlag.PASSTHROUGH, State.ALLOW);
		flags.put(DefaultFlag.BUILD, State.DENY);
		//flags.put(DefaultFlag.CONSTRUCT, State.DENY);
		flags.put(DefaultFlag.PVP, State.DENY);
		flags.put(DefaultFlag.CHEST_ACCESS, State.ALLOW);
		flags.put(DefaultFlag.PISTONS, State.DENY);
		flags.put(DefaultFlag.TNT, State.DENY);
		flags.put(DefaultFlag.LIGHTER, State.DENY);
		flags.put(DefaultFlag.USE, State.ALLOW);
		flags.put(DefaultFlag.PLACE_VEHICLE, State.DENY);
		flags.put(DefaultFlag.DESTROY_VEHICLE, State.DENY);
		flags.put(DefaultFlag.SLEEP, State.ALLOW);
		flags.put(DefaultFlag.MOB_DAMAGE, State.DENY);
		flags.put(DefaultFlag.MOB_SPAWNING, State.DENY);
		//flags.put(DefaultFlag.DENY_SPAWN, State.DENY);
		flags.put(DefaultFlag.INVINCIBILITY, State.DENY);
		flags.put(DefaultFlag.EXP_DROPS, State.ALLOW);
		flags.put(DefaultFlag.CREEPER_EXPLOSION, State.DENY);
		flags.put(DefaultFlag.OTHER_EXPLOSION, State.DENY);
		flags.put(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, State.DENY);
		flags.put(DefaultFlag.GHAST_FIREBALL, State.DENY);
		flags.put(DefaultFlag.ENDER_BUILD, State.DENY);
		flags.put(DefaultFlag.GREET_MESSAGE, ("&cWelcome to room "+roomNum+" , "+p.getName()));
		flags.put(DefaultFlag.FAREWELL_MESSAGE, ("&gCome back soon to your room"));
		//flags.put(DefaultFlag.NOTIFY_ENTER, State.DENY);
		//flags.put(DefaultFlag.NOTIFY_LEAVE, State.DENY);
		flags.put(DefaultFlag.EXIT, State.ALLOW);
		flags.put(DefaultFlag.ENTRY, State.ALLOW);
		flags.put(DefaultFlag.LIGHTNING, State.DENY);
		flags.put(DefaultFlag.ENTITY_PAINTING_DESTROY, State.DENY);
		flags.put(DefaultFlag.ENDERPEARL, State.DENY);
		flags.put(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, State.DENY);
		flags.put(DefaultFlag.ITEM_DROP, State.ALLOW);
		/*flags.put(DefaultFlag.HEAL_AMOUNT, State.DENY);
		flags.put(DefaultFlag.HEAL_DELAY, State.DENY);
		flags.put(DefaultFlag.MIN_HEAL, State.DENY);
		flags.put(DefaultFlag.MAX_HEAL, State.DENY);
		flags.put(DefaultFlag.FEED_DELAY, State.DENY);
		flags.put(DefaultFlag.FEED_AMOUNT, State.DENY);
		flags.put(DefaultFlag.MIN_FOOD, State.DENY);
		flags.put(DefaultFlag.MAX_FOOD, State.DENY);*/
		flags.put(DefaultFlag.SNOW_FALL, State.DENY);
		flags.put(DefaultFlag.SNOW_MELT, State.DENY);
		flags.put(DefaultFlag.ICE_FORM, State.DENY);
		flags.put(DefaultFlag.ICE_MELT, State.DENY);
		flags.put(DefaultFlag.SOIL_DRY, State.DENY);
		//flags.put(DefaultFlag.GAME_MODE, State.DENY);
		flags.put(DefaultFlag.MUSHROOMS, State.DENY);
		flags.put(DefaultFlag.LEAF_DECAY, State.DENY);
		flags.put(DefaultFlag.GRASS_SPREAD, State.DENY);
		flags.put(DefaultFlag.MYCELIUM_SPREAD, State.DENY);
		flags.put(DefaultFlag.VINE_GROWTH, State.DENY);
		flags.put(DefaultFlag.SEND_CHAT, State.ALLOW);
		flags.put(DefaultFlag.RECEIVE_CHAT, State.ALLOW);
		flags.put(DefaultFlag.FIRE_SPREAD, State.DENY);
		flags.put(DefaultFlag.LAVA_FIRE, State.DENY);
		flags.put(DefaultFlag.LAVA_FLOW, State.DENY);
		flags.put(DefaultFlag.WATER_FLOW, State.DENY);
		//flags.put(DefaultFlag.TELE_LOC, State.DENY);
		//flags.put(DefaultFlag.SPAWN_LOC, State.DENY);
		flags.put(DefaultFlag.POTION_SPLASH, State.DENY);
		//flags.put(DefaultFlag.BLOCKED_CMDS, State.DENY);
		//flags.put(DefaultFlag.ALLOWED_CMDS, State.DENY);
		//Double price = 0.0;
		//flags.put(DefaultFlag.PRICE, price);
		flags.put(DefaultFlag.BUYABLE, Boolean.FALSE);

		r.setFlags(flags);
	}
}
