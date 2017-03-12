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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.events.HotelCreateEvent;
import kernitus.plugin.Hotels.events.HotelDeleteEvent;
import kernitus.plugin.Hotels.events.HotelRenameEvent;
import kernitus.plugin.Hotels.exceptions.EventCancelledException;
import kernitus.plugin.Hotels.exceptions.HotelAlreadyPresentException;
import kernitus.plugin.Hotels.exceptions.HotelNonExistentException;
import kernitus.plugin.Hotels.handlers.HTConfigHandler;
import kernitus.plugin.Hotels.managers.HTFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.HTWorldGuardManager;
import kernitus.plugin.Hotels.signs.ReceptionSign;
import kernitus.plugin.Hotels.trade.HotelBuyer;
import kernitus.plugin.Hotels.trade.TradesHolder;

public class Hotel {

	private World world;
	private String name;
	public YamlConfiguration hconfig;

	public Hotel(World world, String name){
		this.world = world;
		this.name = name.toLowerCase();
		hconfig = getHotelConfig();
	}
	public Hotel(String name){
		//Use only when world is unknown, due to extra calculations involved to find it
		this.name = name;
		for(Hotel hotel : HotelsAPI.getAllHotels()){
			if(hotel.getName().equalsIgnoreCase(name))
				world = hotel.getWorld();
		}
		hconfig = getHotelConfig();
	}
	public Hotel(String name, CommandSender sender){
		this.name = name;
		if(sender instanceof Player) world = ((Player) sender).getWorld();
		else{
			for(Hotel hotel : HotelsAPI.getAllHotels()){
				if(hotel.getName().equalsIgnoreCase(name))
					world = hotel.getWorld();
			}
		}
		hconfig = getHotelConfig();
	}
	///////////////////////
	////////Getters////////
	///////////////////////
	public boolean exists(){
		return (world == null || name == null) ? false : HTWorldGuardManager.hasRegion(world, "hotel-"+name);
	}
	public World getWorld(){
		return world;
	}
	public String getName(){
		return name;
	}
	public ProtectedRegion getRegion(){
		return HTWorldGuardManager.getHotelRegion(world, name);
	}
	public ArrayList<Room> getRooms(){
		ArrayList<Room> rooms = new ArrayList<Room>();
		for(ProtectedRegion r : HTWorldGuardManager.getRegions(world)){
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
		ArrayList<String> fileList = HTFileFinder.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs"+File.separator+"Reception"+File.separator+name.toLowerCase());
		ArrayList<ReceptionSign> signs = new ArrayList<ReceptionSign>();

		for(String x : fileList)
			signs.add(new ReceptionSign(this, x.replace(".yml", "")));
		return signs;
	}
	public File getHotelFile(){
		return HTConfigHandler.getHotelFile(name.toLowerCase());
	}
	public YamlConfiguration getHotelConfig(){
		return HTConfigHandler.getHotelConfig(name.toLowerCase());
	}
	public DefaultDomain getOwners(){
		return getRegion().getOwners();
	}
	public Location getHome(){
		if(world == null ||
				!hconfig.contains("Hotel.home.x", true) ||
				!hconfig.contains("Hotel.home.y", true) ||
				!hconfig.contains("Hotel.home.z", true)
				) return null;

		return new Location(world,
				hconfig.getDouble("Hotel.home.x"),
				hconfig.getDouble("Hotel.home.y"),
				hconfig.getDouble("Hotel.home.z"),
				(float) hconfig.getDouble("Hotel.home.yaw"),
				(float) hconfig.getDouble("Hotel.home.pitch"));
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
	public void rename(String newName) throws EventCancelledException, HotelNonExistentException{
		HotelRenameEvent hre = new HotelRenameEvent(this, newName);
		Bukkit.getPluginManager().callEvent(hre);
		newName = hre.getNewName(); //In case it was modified by the event

		if(hre.isCancelled()) throw new EventCancelledException();
		if(!exists()) throw new HotelNonExistentException();

		//Rename rooms
		ArrayList<Room> rooms = getRooms();

		for(Room room : rooms){
			room.renameRoom(newName);
			File hotelsFile = getHotelFile();
			File newHotelsfile = HTConfigHandler.getHotelFile(newName.toLowerCase()+".yml");
			hotelsFile.renameTo(newHotelsfile);
		}
		HTWorldGuardManager.renameRegion("hotel-" + name, "hotel-" + newName, world);
		name = newName;
		ProtectedRegion r = getRegion();

		if(Mes.flagValue("hotel.map-making.GREETING").equalsIgnoreCase("true"))
			r.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.getStringNoPrefix("message.hotel.enter").replaceAll("%hotel%", name)));
		if(Mes.flagValue("hotel.map-making.FAREWELL") != null)
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.getStringNoPrefix("message.hotel.exit").replaceAll("%hotel%", name)));

		updateReceptionSigns();
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
	public void delete() throws EventCancelledException, HotelNonExistentException{
		HotelDeleteEvent hde = new HotelDeleteEvent(this);
		Bukkit.getPluginManager().callEvent(hde);
		if(hde.isCancelled()) throw new EventCancelledException();
		if(!exists()) throw new HotelNonExistentException();

		//Remove all reception signs and files
		deleteAllReceptionSigns();
		//Remove all rooms including regions, signs and files
		for(Room room : getRooms())
			room.delete();
		//Remove Hotel file if existent
		deleteHotelFile();
		//Delete hotel region
		HTWorldGuardManager.removeRegion(world, getRegion());
	}
	public boolean isOwner(UUID uuid){
		return getOwners().contains(uuid);
	}
	public boolean isOwner(String name){
		return getOwners().contains(name);
	}
	public void create(ProtectedRegion region) throws EventCancelledException, HotelAlreadyPresentException{
		HotelCreateEvent hce = new HotelCreateEvent(this, region);
		Bukkit.getPluginManager().callEvent(hce); //Call HotelCreateEvent
		if(hce.isCancelled()) throw new EventCancelledException();

		//In case a listener modified this stuff
		world = hce.getWorld();
		name = hce.getName();
		region = hce.getRegion();
		if(HTWorldGuardManager.doHotelRegionsOverlap(region, world)) throw new HotelAlreadyPresentException();

		HTWorldGuardManager.addRegion(world, region);
		HTWorldGuardManager.hotelFlags(region, name, world);
		HTWorldGuardManager.saveRegions(world);
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
		HTWorldGuardManager.setOwners(uuids, getRegion());
		HTWorldGuardManager.saveRegions(world);
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
		HTWorldGuardManager.addOwner(p, getRegion());
	}
	public void addOwner(UUID uuid){
		addOwner(Bukkit.getOfflinePlayer(uuid));
	}
	public void removeOwner(OfflinePlayer p){
		HTWorldGuardManager.removeOwner(p, getRegion());
	}
	public void removeOwner(UUID uuid){
		removeOwner(Bukkit.getOfflinePlayer(uuid));
	}
}