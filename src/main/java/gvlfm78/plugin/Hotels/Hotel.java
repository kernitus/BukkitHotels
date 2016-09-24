package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class Hotel {

	private WorldGuardManager WGM = new WorldGuardManager();

	private World world;
	private String name;

	public Hotel(World world, String name){
		this.world = world;
		this.name = name;
	}
	public Hotel(String name){
		//To use only when world is unknown, due to extra calculations involved to find it
		this.name = name;
		for(Hotel hotel : HotelsAPI.getAllHotels()){
			if(hotel.getName().equalsIgnoreCase(name))
				this.world = hotel.getWorld();
		}
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
	public ArrayList<File> getAllReceptionSignFiles(){
		ArrayList<String> fileList = HotelsFileFinder.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs");
		ArrayList<File> files = new ArrayList<File>();

		for(String x : fileList){
			File file = HotelsConfigHandler.getReceptionFile(x);
			if(file.getName().matches("^Reception-.+-.+"))
				files.add(file);
		}
		return files;
	}
	public File getHotelFile(){
		return HotelsConfigHandler.getHotelFile(name.toLowerCase());
	}
	public YamlConfiguration getHotelConfig(){
		return HotelsConfigHandler.getHotelConfig(name.toLowerCase());
	}
	public DefaultDomain getOwners(){
		return getRegion().getOwners();
	}
	public boolean isBlockWithinHotelRegion(Block b){
		return getRegion().contains(b.getX(),b.getY(),b.getZ());
	}
	public void rename(String newName){
		//Rename rooms
		ArrayList<Room> rooms = getRooms();

		for(Room room : rooms){
			room.renameRoom(newName);
			File hotelsFile = getHotelFile();
			File newHotelsfile = HotelsConfigHandler.getHotelFile(newName.toLowerCase()+".yml");
			hotelsFile.renameTo(newHotelsfile);
		}
		WGM.renameRegion("hotel-"+name, "hotel-"+newName, world);
		name = newName;
		ProtectedRegion r = getRegion();

		if(Mes.flagValue("hotel.map-making.GREETING").equalsIgnoreCase("true"))
			r.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.mesnopre("message.hotel.enter").replaceAll("%hotel%", name)));
		if(Mes.flagValue("hotel.map-making.FAREWELL")!=null)
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.mesnopre("message.hotel.exit").replaceAll("%hotel%", name)));
	}
	public void removeAllSigns(){
		deleteAllReceptionSigns();

		for(Room room : getRooms()){
			room.deleteSignAndFile();
		}
	}
	public void deleteAllReceptionSigns(){
		for(File file : getAllReceptionSignFiles()){
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			World worldFromSign = Bukkit.getWorld(config.getString("Reception.location.world"));
			int locx = config.getInt("Reception.location.x");
			int locy = config.getInt("Reception.location.y");
			int locz = config.getInt("Reception.location.z");
			Block b = worldFromSign.getBlockAt(locx,locy,locz);
			if(world==worldFromSign){
				Material mat = b.getType();
				if(mat.equals(Material.SIGN)||mat.equals(Material.SIGN_POST)||mat.equals(Material.WALL_SIGN)){
					Sign s = (Sign) b.getState();
					String Line1 = ChatColor.stripColor(s.getLine(0));
					if(Line1.matches("Reception")||Line1.matches(Mes.mesnopre("Sign.reception"))){
						if(getRegion().contains(locx, locy, locz)){
							b.setType(Material.AIR);
						}
					}
				}
			}
			file.delete();
		}
	}
	public void deleteHotelFile(){
		getHotelFile().delete();
	}
	public boolean createHotelFile(){
		try {
			getHotelFile().createNewFile();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	public boolean saveHotelConfig(YamlConfiguration config){
		try {
			config.save(getHotelFile());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	public void delete(){
		//Remove all reception signs and files
		deleteAllReceptionSigns();
		//Remove all rooms including regions, signs and files
		for(Room room : getRooms()){
			room.delete();
		}
		//Remove Hotel file if existant
		deleteHotelFile();
	}
	public boolean isOwner(UUID uuid){
		return getOwners().contains(uuid);
	}
	public boolean isOwner(String name){
		return getOwners().contains(name);
	}
	public int getNextNewRoom(){
		for(Room room : getRooms()){
			if(!room.exists())
				return room.getNum();
		}
		return 0;
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
}