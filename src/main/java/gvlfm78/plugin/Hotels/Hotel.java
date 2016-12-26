package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Signs.ReceptionSign;
import kernitus.plugin.Hotels.events.HotelCreateEvent;
import kernitus.plugin.Hotels.events.HotelDeleteEvent;
import kernitus.plugin.Hotels.events.HotelRenameEvent;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;
import kernitus.plugin.Hotels.trade.HotelBuyer;
import kernitus.plugin.Hotels.trade.TradesHolder;

public class Hotel {

	private World world;
	private String name;
	public YamlConfiguration hconfig;

	public Hotel(World world, String name){
		this.world = world;
		this.name = name.toLowerCase();
		this.hconfig = getHotelConfig();
	}
	public Hotel(String name){
		//Use only when world is unknown, due to extra calculations involved to find it
		this.name = name;
		for(Hotel hotel : HotelsAPI.getAllHotels()){
			if(hotel.getName().equalsIgnoreCase(name))
				this.world = hotel.getWorld();
		}
		this.hconfig = getHotelConfig();
	}
	///////////////////////
	////////Getters////////
	///////////////////////
	public boolean exists(){
		return (world == null || name == null) ? false : WorldGuardManager.hasRegion(world, "hotel-"+name);
	}
	public World getWorld(){
		return world;
	}
	public String getName(){
		return name;
	}
	public ProtectedRegion getRegion(){
		return WorldGuardManager.getHotelRegion(world, name);
	}
	public ArrayList<Room> getRooms(){
		ArrayList<Room> rooms = new ArrayList<Room>();
		for(ProtectedRegion r : WorldGuardManager.getRegions(world)){
			String id = r.getId();
			if(id.matches("hotel-"+name+"-\\d+")){
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
	public ArrayList<ReceptionSign> getAllReceptionSigns(){
		ArrayList<String> fileList = HotelsFileFinder.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs"+File.separator+"Reception"+File.separator+name.toLowerCase());
		ArrayList<ReceptionSign> signs = new ArrayList<ReceptionSign>();

		for(String x : fileList)
				signs.add(new ReceptionSign(this, x.replace(".yml", "")));
		return signs;
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
	public Location getHome(){
		return new Location(world, hconfig.getDouble("Hotel.home.x"),hconfig.getDouble("Hotel.home.y"),hconfig.getDouble("Hotel.home.z"),(float) hconfig.getDouble("Hotel.home.yaw"),(float) hconfig.getDouble("Hotel.home.pitch"));
	}
	public HotelBuyer getBuyer(){
		return TradesHolder.getBuyerFromHotel(this);
	}
	public int getNextNewRoom(){
		for(Room room : getRooms()){
			if(!room.exists())
				return room.getNum();
		}
		return 0;
	}
	public boolean isBlockWithinHotelRegion(Block b){
		return getRegion().contains(b.getX(),b.getY(),b.getZ());
	}
	public void setName(String name){
		this.name = name;
	}
	public HotelsResult rename(String newName){
		HotelRenameEvent hre = new HotelRenameEvent(this, newName);
		Bukkit.getPluginManager().callEvent(hre);
		newName = hre.getNewName(); //In case it was modified by the event
		
		if(hre.isCancelled()) return HotelsResult.CANCELLED;
		if(!exists()) return HotelsResult.HOTEL_NON_EXISTENT;
		
		//Rename rooms
		ArrayList<Room> rooms = getRooms();

		for(Room room : rooms){
			room.renameRoom(newName);
			File hotelsFile = getHotelFile();
			File newHotelsfile = HotelsConfigHandler.getHotelFile(newName.toLowerCase()+".yml");
			hotelsFile.renameTo(newHotelsfile);
		}
		WorldGuardManager.renameRegion("hotel-" + name, "hotel-" + newName, world);
		name = newName;
		ProtectedRegion r = getRegion();

		if(Mes.flagValue("hotel.map-making.GREETING").equalsIgnoreCase("true"))
			r.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.mesnopre("message.hotel.enter").replaceAll("%hotel%", name)));
		if(Mes.flagValue("hotel.map-making.FAREWELL") != null)
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.mesnopre("message.hotel.exit").replaceAll("%hotel%", name)));
		
		updateReceptionSigns();
		
		return HotelsResult.SUCCESS;
	}
	public void removeAllSigns(){
		deleteAllReceptionSigns();

		for(Room room : getRooms()){
			room.deleteSignAndFile();
		}
	}
	public void deleteAllReceptionSigns(){
		for(ReceptionSign rs : getAllReceptionSigns())
			rs.deleteSignAndConfig();
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
	public boolean saveHotelConfig(){
		try {
			hconfig.save(getHotelFile());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	public HotelsResult delete(){
		HotelDeleteEvent hde = new HotelDeleteEvent(this);
		Bukkit.getPluginManager().callEvent(hde);
		if(hde.isCancelled()) return HotelsResult.CANCELLED;
		if(!exists()) return HotelsResult.HOTEL_NON_EXISTENT;
		
		//Remove all reception signs and files
		deleteAllReceptionSigns();
		//Remove all rooms including regions, signs and files
		for(Room room : getRooms())
			room.delete();
		//Remove Hotel file if existent
		deleteHotelFile();
		return HotelsResult.SUCCESS;
	}
	public boolean isOwner(UUID uuid){
		return getOwners().contains(uuid);
	}
	public boolean isOwner(String name){
		return getOwners().contains(name);
	}
	public HotelsResult create(ProtectedRegion region){
		HotelCreateEvent hce = new HotelCreateEvent(this);
		Bukkit.getPluginManager().callEvent(hce); //Call HotelCreateEvent
		if(hce.isCancelled()) return HotelsResult.CANCELLED;
		
		//In case a listener modified this stuff
		world = hce.getWorld();
		name = hce.getName();
		region = hce.getRegion();
		
		if(WorldGuardManager.doHotelRegionsOverlap(region, world)) return HotelsResult.HOTEL_ALREADY_PRESENT; 
		
		WorldGuardManager.addRegion(world, region);
		WorldGuardManager.hotelFlags(region, name, world);
		region.setPriority(5);
		WorldGuardManager.saveRegions(world);

		return HotelsResult.SUCCESS;
	}
	public void updateReceptionSigns(){
		ArrayList<ReceptionSign> rss = getAllReceptionSigns();
		for(ReceptionSign rs : rss)
			rs.update();
	}
	//////////////////////////
	////////Setters///////////
	//////////////////////////
	public void setNewOwner(UUID uuid){
		ArrayList<UUID> uuids = new ArrayList<UUID>();
		uuids.add(uuid);
		WorldGuardManager.setOwners(uuids, getRegion());
		WorldGuardManager.saveRegions(world);
	}
	public void setBuyer(UUID uuid, double price){
		TradesHolder.addHotelBuyer(Bukkit.getPlayer(uuid), this, price);
	}
	public void removeBuyer(){
		TradesHolder.removeHotelBuyer(TradesHolder.getBuyerFromHotel(this).getPlayer());
	}
	public void setHome(Location loc){
		hconfig.set("Hotel.home.x", loc.getX());
		hconfig.set("Hotel.home.y", loc.getY());
		hconfig.set("Hotel.home.z", loc.getZ());
		hconfig.set("Hotel.home.pitch", loc.getPitch());
		hconfig.set("Hotel.home.yaw", loc.getYaw());
	}
	public void addOwner(OfflinePlayer p){
		WorldGuardManager.addOwner(p, getRegion());
	}
	public void addOwner(UUID uuid){
		addOwner(Bukkit.getOfflinePlayer(uuid));
	}
	public void removeOwner(OfflinePlayer p){
		WorldGuardManager.removeOwner(p, getRegion());
	}
	public void removeOwner(UUID uuid){
		removeOwner(Bukkit.getOfflinePlayer(uuid));
	}
}