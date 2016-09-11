package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.events.RoomCreateEvent;
import kernitus.plugin.Hotels.events.RoomRentEvent;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Room {

	private WorldGuardManager WGM = new WorldGuardManager();

	private Hotel hotel;
	private int num;
	public YamlConfiguration sconfig;
	private World world;

	public Room(Hotel hotel, int num){
		this.hotel = hotel;
		this.num = num;
		this.sconfig = getSignConfig();
		this.world = hotel.getWorld();
	}
	public Room(Hotel hotel, String num){
		this.hotel = hotel;
		this.num = Integer.parseInt(num);
		this.sconfig = getSignConfig();
		this.world = hotel.getWorld();
	}

	//////////////////////
	///////Getters////////
	//////////////////////
	public boolean exists(){
		return WGM.hasRegion(world, "hotel-"+hotel.getName()+"-"+num);
	}
	public int getNum(){
		return num;
	}
	public Hotel getHotel(){
		return hotel;
	}
	public ProtectedRegion getRegion(){
		return WGM.getRoomRegion(world, hotel.getName(), num);
	}
	public String getRegionId(){
		return getRegion().getId();
	}
	public OfflinePlayer getRenter(){
		String renter = sconfig.getString("Sign.renter");
		if(renter!=null)
			return Bukkit.getOfflinePlayer(UUID.fromString(renter));
		else
			return null;
	}
	public boolean isRenter(UUID uuid){
		return getRenter().getUniqueId().equals(uuid);
	}
	public boolean isRented(){
		return getRenter() != null;
	}
	public int getTime(){
		return sconfig.getInt("Sign.time");
	}
	public boolean isPermanent(){
		return getTime() == 0;
	}
	public double getCost(){
		return sconfig.getDouble("Sign.cost");
	}
	public World getWorld(){
		return world;
	}
	public World getWorldFromConfig(){
		String world = sconfig.getString("Sign.location.world");
		return world==null||world.isEmpty() ? null : Bukkit.getWorld(world);
	}
	public String getHotelNameFromConfig(){
		return sconfig.getString("Sign.hotel");
	}
	public int getRoomNumFromConfig(){
		return sconfig.getInt("Sign.room");
	}
	public Location getSignLocation(){
		return new Location(Bukkit.getWorld(sconfig.getString("Sign.location.world")),sconfig.getInt("Sign.location.coords.x"),sconfig.getInt("Sign.location.coords.y"),sconfig.getInt("Sign.location.coords.z"));
	}
	public Location getDefaultHome(){
		return new Location(getWorldFromConfig(),sconfig.getDouble("Sign.defaultHome.x"),sconfig.getDouble("Sign.defaultHome.y"),sconfig.getDouble("Sign.defaultHome.z"),(float) sconfig.getDouble("Sign.defaultHome.pitch"),(float) sconfig.getDouble("Sign.defaultHome.yaw"));
	}
	public Location getUserHome(){
		return new Location(getWorldFromConfig(),sconfig.getDouble("Sign.userHome.x"),sconfig.getDouble("Sign.userHome.y"),sconfig.getDouble("Sign.userHome.z"),(float) sconfig.getDouble("Sign.userHome.pitch"),(float) sconfig.getDouble("Sign.userHome.yaw"));
	}
	public int getTimesExtended(){
		return sconfig.getInt("Sign.extended");
	}
	public int getExpiryMinute(){
		return sconfig.getInt("Sign.expiryDate");
	}
	public int getRentMinute(){
		return sconfig.getInt("Sign.timeRentedAt");
	}
	public ArrayList<OfflinePlayer> getRenters(){
		return null;
	}
	public boolean isFree(){
		if(exists()){
			OfflinePlayer renter = getRenter();
			if(renter==null||!renter.hasPlayedBefore())
				return true; //No valid renter
		}
		return false; //There must be a renter
	}
	public boolean isFreeOrNotSetup(){
		if(exists()){
			OfflinePlayer renter = getRenter();
			if(renter==null||!renter.hasPlayedBefore())
				return true;
		}
		else
			return true; //Room sign has not been placed

		return false; //Hasn't returned by now, room must be rented
	}

	//////////////////////
	///////Setters////////
	//////////////////////
	public void setRentTime(long timeInMins){
		sconfig.set("Sign.time", timeInMins);
	}
	private void setHotelNameInConfig(String name){
		sconfig.set("Sign.hotel", name);
	}
	private void setRoomNumInConfig(int num){
		sconfig.set("Sign.room", num);
	}
	public void setCost(Double cost){
		sconfig.set("Sign.cost", cost);
	}
	private void setSignLocation(Location l){
		sconfig.set("Sign.location.world", l.getWorld());
		sconfig.set("Sign.location.coords.x", l.getX());
		sconfig.set("Sign.location.coords.y", l.getY());
		sconfig.set("Sign.location.coords.z", l.getZ());
	}
	public void setDefaultHome(Location l){
		sconfig.set("Sign.defaultHome.x", l.getX());
		sconfig.set("Sign.defaultHome.y", l.getY());
		sconfig.set("Sign.defaultHome.z", l.getZ());
		sconfig.set("Sign.defaultHome.pitch", l.getPitch());
		sconfig.set("Sign.defaultHome.yaw", l.getYaw());
	}
	public void setUserHome(Location l){
		sconfig.set("Sign.userHome.x", l.getX());
		sconfig.set("Sign.userHome.y", l.getY());
		sconfig.set("Sign.userHome.z", l.getZ());
		sconfig.set("Sign.userHome.pitch", l.getPitch());
		sconfig.set("Sign.userHome.yaw", l.getYaw());
	}
	public void setTimesExtended(int times){
		sconfig.set("Sign.extended", times);
	}
	public void incrementTimesExtended(){
		sconfig.set("Sign.extended", sconfig.getInt("Sign.extended")+1);
	}
	public void setNewExpiryDate(){
		long expiryDate = getExpiryMinute();
		long time = getTime();
		sconfig.set("Sign.expiryDate", expiryDate+time);
	}
	public void setTimeRentedAt(long time){
		sconfig.set("Sign.timeRentedAt", time);
	}
	public void setExpiryTime(){
		long time = getTime();
		if(time==0)
			sconfig.set("Sign.expiryDate", 0);
		else
			sconfig.set("Sign.expiryDate", (System.currentTimeMillis()/1000/60)+time);
	}
	public void setRenter(UUID uuid){
		//Setting them as members of the region
		DefaultDomain dd = new DefaultDomain();
		dd.addPlayer(uuid);
		getRegion().setMembers(dd);
		WGM.setMember(uuid, getRegion());
		//Placing their UUID in the sign config
		sconfig.set("Sign.renter", uuid);
		saveSignConfig();
	}
	public void rent(Player p){
		UUID uuid = p.getUniqueId();
		if(!exists() || !doesSignFileExist()){
			p.sendMessage(Mes.mes("chat.commands.rent.invalidData"));
			return;
		}

		setRenter(uuid);
		//Set in config time rented at and expiry time
		long currentMin = System.currentTimeMillis()/1000/60;
		setTimeRentedAt(currentMin);

		//Setting room flags back in case they were changed to allow players in
		WGM.roomFlags(getRegion(),num);
		Bukkit.getServer().getPluginManager().callEvent(new RoomRentEvent(this));
	}
	public boolean create(Selection sel){
		// This method will return true if there were no errors,
		// and false if there were

		//Creating room region
		ProtectedRegion r = null;

		String hotelName = hotel.getName();

		if(sel instanceof CuboidSelection){
			r = new ProtectedCuboidRegion(
					"Hotel-"+hotelName+"-"+num, 
					new BlockVector(sel.getNativeMinimumPoint()), 
					new BlockVector(sel.getNativeMaximumPoint())
					);
		}
		else if(sel instanceof Polygonal2DSelection){
			int minY = sel.getMinimumPoint().getBlockY();
			int maxY = sel.getMaximumPoint().getBlockY();
			List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
			r = new ProtectedPolygonalRegion("Hotel-"+hotelName+"-"+num, points, minY, maxY);
		}
		else
			return false;

		WGM.addRegion(world, r);
		WGM.roomFlags(r,num);
		if(HotelsConfigHandler.getyml("config.yml").getBoolean("settings.stopOwnersEditingRentedRooms"))
			r.setPriority(1);
		else
			r.setPriority(10);

		WGM.makeRoomAccessible(r);
		WGM.saveRegions(world);

		//Calling RoomCreateEvent
		Bukkit.getServer().getPluginManager().callEvent(new RoomCreateEvent(this));
		return true;
	}
	private void removeSignAndFile(){
		Block b = world.getBlockAt(getSignLocation());
		Material mat = b.getType();
		if(mat.equals(Material.SIGN)||mat.equals(Material.SIGN_POST)||mat.equals(Material.WALL_SIGN))
			b.setType(Material.AIR);
		deleteSignFile();
	}
	public int renumber(int newNum){
		//Method will return errorLevel variable to know where it stopped:
		//0: no errors
		//1: new num too big
		//2: hotel non existant
		//3: room non existant
		//4: sign file does not exist
		//5: hotel region doesn't exist
		//6: sign is not within hotel region
		//7: block at signfile location is not a sign

		int oldNum = newNum;
		File oldFile = getSignFile();

		if(newNum > 100000)
			return 1;

		if(WGM.hasRegion(world, hotel.getRegionId()))
			return 2;

		if(WGM.hasRegion(world, getRegionId()))
			return 3;

		if(doesSignFileExist()) return 4;

		File signFile = getSignFile();

		YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);

		Location signLocation = getSignLocation();

		Block b = world.getBlockAt(signLocation);

		if(!hotel.exists()){
			removeSignAndFile(); return 5; }

		if(!hotel.getRegion().contains(signLocation.getBlockX(),signLocation.getBlockY(),signLocation.getBlockZ())){
			removeSignAndFile(); return 6; }

		Material mat = b.getType();

		if(!(mat.equals(Material.SIGN)||mat.equals(Material.SIGN_POST)||mat.equals(Material.WALL_SIGN)))
			return 7;

		Sign s = (Sign) b;

		s.setLine(1, Mes.mesnopre("sign.room.name")+" "+newNum+" - "+s.getLine(1).split(" ")[3]);
		s.update();
		config.set("Sign.room", Integer.valueOf(newNum));
		config.set("Sign.region", "hotel-"+hotel+"-"+newNum);

		saveSignConfig();

		this.num = newNum;

		oldFile.renameTo(getSignFile());						

		//Changing number in entry/exit messages
		ProtectedRegion r = WGM.getRegion(world, hotel.getRegionId()+"-"+oldNum);

		if(Mes.flagValue("room.map-making.GREETING").equalsIgnoreCase("true"))
			r.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.mesnopre("message.room.enter").replaceAll("%room%", String.valueOf(newNum))));
		if(Mes.flagValue("room.map-making.FAREWELL").equalsIgnoreCase("true"))
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.mesnopre("message.room.exit").replaceAll("%room%", String.valueOf(newNum))));
		WGM.renameRegion("Hotel-"+hotel+"-"+oldNum, "Hotel-"+hotel+"-"+newNum, world);
		WGM.saveRegions(world);

		return 0;
	}
	public boolean remove(){
		if(exists()){//If region exists
			removeRegion();

			if(doesSignFileExist()) {
				deleteSignFile();
				return true;
			}
		}
		return false;
	}
	public void removeRegion(){
		WGM.removeRegion(world, getRegion());
		WGM.saveRegions(world);
	}
	public void addFriend(){

	}
	public void removeFriend(){

	}
	///Config stuff
	private File getSignFile(){
		return new File("plugins"+File.separator+"Hotels"+File.separator+"Signs"+File.separator+hotel.getName()+"-"+num+".yml");
	}
	private YamlConfiguration getSignConfig(){
		return YamlConfiguration.loadConfiguration(getSignFile());
	}
	public boolean doesSignFileExist(){
		return getSignFile().exists();
	}
	public void saveSignConfig(Player p){//When creating a room sign
		try {
			sconfig.save(getSignFile());
		} catch (IOException e) {
			p.sendMessage(Mes.mes("chat.sign.place.fileFail"));
			e.printStackTrace();
		}
	}
	public void saveSignConfig(){
		try {
			sconfig.save(getSignFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void createSignConfig(Player p, long timeInMins, double cost, Location signLocation){
		if(!doesSignFileExist()){
			setRentTime(timeInMins);
			setHotelNameInConfig(hotel.getName());
			setRoomNumInConfig(num);
			setCost(cost);
			setSignLocation(signLocation);
		}
		saveSignConfig(p);
	}
	public void deleteSignFile(){
		getSignFile().delete();
	}
}