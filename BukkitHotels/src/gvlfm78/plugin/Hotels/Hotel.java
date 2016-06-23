package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.managers.WorldGuardManager;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Hotel {
	
	private WorldGuardManager WGM = new WorldGuardManager();
	
	private World world;
	private String name;

	public Hotel(World world, String name){
		this.world = world;
		this.name = name;
	}
	public World getWorld(){
		return world;
	}
	public String getName(){
		return name;
	}
	public ProtectedRegion getRegion(){
		return WGM.getHotelRegion(world, name);
	}
}
