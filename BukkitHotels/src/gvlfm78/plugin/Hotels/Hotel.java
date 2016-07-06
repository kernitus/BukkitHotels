package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.managers.WorldGuardManager;

import java.util.ArrayList;

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
	public ArrayList<Room> getRooms(){
		ArrayList<Room> rooms = new ArrayList<Room>();
		for(ProtectedRegion r : WGM.getRegions(world)){
			String id = r.getId();
			if(id.matches("hotel-"+name+"-"+"\\d+")){
				String num = id.replaceFirst("hotel-"+name+"-", "");
				Room room = new Room(this, num);
				rooms.add(room);
			}
		}
		return rooms;
	}
}
