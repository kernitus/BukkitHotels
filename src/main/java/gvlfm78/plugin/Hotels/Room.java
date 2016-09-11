package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

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
	public void createRegion(ProtectedRegion r){
		WGM.addRegion(world, r);
		WGM.saveRegions(world);
	}
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
	public void saveSignConfig(){
		try {
			sconfig.save(getSignFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveSignConfig(Player p){//When creating a room sign
		try {
			sconfig.save(getSignFile());
		} catch (IOException e) {
			p.sendMessage(Mes.mes("chat.sign.place.fileFail"));
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