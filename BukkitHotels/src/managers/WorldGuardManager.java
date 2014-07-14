package managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.Flag;
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
	public static void loadFlags(ProtectedCuboidRegion r){

		Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>(66);
		Map<String, String> flagsOld = new HashMap<String, String>(66);

		File configFile = new File("plugins//Hotels//flags.yml");
		YamlConfiguration cf = YamlConfiguration.loadConfiguration(configFile);
		
		//flags.putAll("com.sk89q.worldguard.protection.flags.StateFlag {}"+flagsOld+"com.sk89q.worldguard.protection.flags.StateFlag$State");
		//flags.putAll(map);
		
		
		r.setFlags(flags);
}
}
