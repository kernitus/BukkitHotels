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
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Signs.ReceptionSign;
import kernitus.plugin.Hotels.events.HotelDeleteEvent;
import kernitus.plugin.Hotels.events.HotelRenameEvent;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;
import kernitus.plugin.Hotels.trade.HotelBuyer;
import kernitus.plugin.Hotels.trade.TradesHolder;

public class Hotel {

	private WorldGuardManager WGM = new WorldGuardManager();

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
		return new Location(world, hconfig.getDouble("Hotel.home.x"),hconfig.getDouble("Hotel.home.y"),hconfig.getDouble("Hotel.home.z"),(float) hconfig.getDouble("Hotel.home.pitch"),(float) hconfig.getDouble("Hotel.home.yaw"));
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
	public void rename(String newName){
		String oldName = name;
		//Rename rooms
		ArrayList<Room> rooms = getRooms();

		for(Room room : rooms){
			room.renameRoom(newName);
			File hotelsFile = getHotelFile();
			File newHotelsfile = HotelsConfigHandler.getHotelFile(newName.toLowerCase()+".yml");
			hotelsFile.renameTo(newHotelsfile);
		}
		WGM.renameRegion("hotel-" + name, "hotel-" + newName, world);
		name = newName;
		ProtectedRegion r = getRegion();

		if(Mes.flagValue("hotel.map-making.GREETING").equalsIgnoreCase("true"))
			r.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.mesnopre("message.hotel.enter").replaceAll("%hotel%", name)));
		if(Mes.flagValue("hotel.map-making.FAREWELL") != null)
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.mesnopre("message.hotel.exit").replaceAll("%hotel%", name)));
		
		updateReceptionSigns();
		
		Bukkit.getPluginManager().callEvent(new HotelRenameEvent(this, oldName));
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
	public void delete(){
		//Remove all reception signs and files
		deleteAllReceptionSigns();
		//Remove all rooms including regions, signs and files
		for(Room room : getRooms()){
			room.delete();
		}
		//Remove Hotel file if existant
		deleteHotelFile();
		Bukkit.getPluginManager().callEvent(new HotelDeleteEvent(this));
	}
	public boolean isOwner(UUID uuid){
		return getOwners().contains(uuid);
	}
	public boolean isOwner(String name){
		return getOwners().contains(name);
	}
	public void create(ProtectedRegion region, Player p){
		World world = p.getWorld();
		if(WGM.doHotelRegionsOverlap(region, world)){ p.sendMessage(Mes.mes("chat.commands.create.hotelAlreadyPresent")); return; }

		WGM.addRegion(world, region);
		WGM.hotelFlags(region, name, world);
		WGM.addOwner(p, region);
		region.setPriority(5);
		WGM.saveRegions(world);
		String idHotelName = region.getId();
		String[] partsofhotelName = idHotelName.split("-");
		p.sendMessage(Mes.mes("chat.creationMode.hotelCreationSuccessful").replaceAll("%hotel%", partsofhotelName[1]));
		int ownedHotels = HotelsAPI.getHotelsOwnedBy(p.getUniqueId()).size();
		int maxHotels = HotelsConfigHandler.getconfigyml().getInt("settings.max_hotels_owned");

		String hotelsLeft = String.valueOf(maxHotels-ownedHotels);

		if(!Mes.hasPerm(p, "hotels.create.admin"))//If the player has hotel limit display message
			p.sendMessage(Mes.mes("chat.commands.create.creationSuccess").replaceAll("%tot%", String.valueOf(ownedHotels)).replaceAll("%left%", String.valueOf(hotelsLeft)));
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
		WGM.setOwners(uuids, getRegion());
		WGM.saveRegions(world);
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
		WGM.addOwner(p, getRegion());
	}
	public void addOwner(UUID uuid){
		addOwner(Bukkit.getOfflinePlayer(uuid));
	}
	public void removeOwner(OfflinePlayer p){
		WGM.removeOwner(p, getRegion());
	}
	public void removeOwner(UUID uuid){
		removeOwner(Bukkit.getOfflinePlayer(uuid));
	}
}