package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Hotel {

	private WorldGuardManager WGM = new WorldGuardManager();

	private World world;
	private String name;

	public Hotel(World world, String name){
		this.world = world;
		this.name = name;
	}
	///////////////////////
	////////Getters////////
	///////////////////////
	public boolean exists(){
		return WGM.hasRegion(world, "hotel-"+name);
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
	public String getRegionId(){
		return getRegion().getId();
	}
	public ArrayList<Room> getRooms(){
		ArrayList<Room> rooms = new ArrayList<Room>();
		for(ProtectedRegion r : WorldGuardManager.getRegions(world)){
			String id = r.getId();
			if(id.matches("hotel-"+name+"-"+"\\d+")){
				String num = id.replaceFirst("hotel-"+name+"-", "");
				Room room = new Room(this, num);
				rooms.add(room);
			}
		}
		return rooms;
	}
	public int getTotalRoomCount(){
		//Finds total amount of rooms in given hotel
		return getRooms().size();
	}
	public ArrayList<Room> getFreeRooms(){
		ArrayList<Room> rooms = getRooms();
		ArrayList<Room> freeRooms = new ArrayList<Room>();
		for(Room room : rooms){
			if(room.isFree())
				freeRooms.add(room);

		}
		return freeRooms;
	}
	public int getFreeRoomCount(){
		//Finds amount of free rooms in given hotel
		return getFreeRooms().size();
	}
	public boolean hasRentedRooms(){
		if(exists()){
			ArrayList<Room> rooms = getRooms();
			for(Room room : rooms){
				if(room.isRented())
					return true;
			}
		}
		return false;
	}
	public DefaultDomain getOwners(){
		return getRegion().getOwners();
	}
	public boolean isOwner(UUID uuid){
		return getOwners().contains(uuid);
	}
	public boolean isOwner(String name){
		return getOwners().contains(name);
	}
	//////////////////////////
	////////Setters///////////
	//////////////////////////
	public void setNewOwner(UUID uuid){
		ArrayList<UUID> uuids = new ArrayList<UUID>();
		uuids.add(uuid);
		WGM.setOwners(uuids, getRegion());
		WGM.saveRegions(world);
	}
	public int create(Selection sel, Player p){//This should only take the selection and player and make the region, HCM should handle player stuff before-hand

		int error = 0;
		//This method will return:
		// 0 if there were no errors
		// 1 if the selection type was not recognised
		// 2 if the hotel regions overlap

		ProtectedRegion r = null;
		if(sel instanceof CuboidSelection){
			r = new ProtectedCuboidRegion(
					"Hotel-" + name, 
					new BlockVector(sel.getNativeMinimumPoint()), 
					new BlockVector(sel.getNativeMaximumPoint())
					);
			error = 0;
		}
		else if(sel instanceof Polygonal2DSelection){
			int minY = sel.getMinimumPoint().getBlockY();
			int maxY = sel.getMaximumPoint().getBlockY();
			List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
			r = new ProtectedPolygonalRegion("Hotel-"+name, points, minY, maxY);
			error = 0;
		}
		else
			return 1;

		if(WGM.doHotelRegionsOverlap(r, world))
			error = 2;

		WGM.addRegion(world, r);
		WGM.hotelFlags(r,name);
		WGM.addOwner(p, r);
		r.setPriority(5);
		WGM.saveRegions(world);

		return error;
	}
	public void delete(){

	}
	public void rename(){

	}
	public void removeAllRooms(){
		
		if(!exists()) return;

		HotelsFileFinder HFF = new HotelsFileFinder();

		ArrayList<String> fileslist = HFF.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs");

		for(String x : fileslist){


			File file = HotelsConfigHandler.getFile("Signs"+File.separator+x);
			String receptionLoc = Mes.mesnopre("sign.reception");

			if(file.getName().matches("^"+receptionLoc+"-.+-.+")){//Reception file
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				World world = Bukkit.getWorld(config.getString("Reception.location.world").trim());

				int locx = config.getInt("Reception.location.x");
				int locy = config.getInt("Reception.location.y");
				int locz = config.getInt("Reception.location.z");
				Block block = world.getBlockAt(locx,locy,locz);

				Material mat = block.getType();

				if(!mat.equals(Material.SIGN)&&!mat.equals(Material.SIGN_POST)&&!mat.equals(Material.WALL_SIGN))
					file.delete();

				Sign sign = (Sign) block.getState();
				String Line1 = ChatColor.stripColor(sign.getLine(0));
				String Line2 = ChatColor.stripColor(sign.getLine(1));

				if(!Line1.matches(receptionLoc))
					file.delete();

				String hotelName = Line2.split(" ")[0];

				if(WGM.hasRegion(world, "Hotel-"+hotelName)){
					if(WGM.getRegion(world,"Hotel-"+hotelName).contains(locx, locy, locz)){
						block.setType(Material.AIR);
						file.delete();
					}
					else{
						block.setType(Material.AIR);
						file.delete();
					}
				}
				else{
					block.setType(Material.AIR);
					file.delete();
				}


			}else{
				String[] parts = x.split("-");
				String chotelName = parts[0];
				if(chotelName.equalsIgnoreCase(name)){
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					int locx = config.getInt("Sign.location.coords.x");
					int locy = config.getInt("Sign.location.coords.y");
					int locz = config.getInt("Sign.location.coords.z");
					Block signblock = world.getBlockAt(locx, locy, locz);
					signblock.setType(Material.AIR);
					signblock.breakNaturally();
					file.delete();
				}
			}
		}
	}
}