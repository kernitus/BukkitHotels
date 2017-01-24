package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Signs.RoomSign;
import kernitus.plugin.Hotels.events.RentExpiryEvent;
import kernitus.plugin.Hotels.events.RoomDeleteEvent;
import kernitus.plugin.Hotels.events.RoomRentEvent;
import kernitus.plugin.Hotels.events.RoomRenumberEvent;
import kernitus.plugin.Hotels.events.RoomSignUpdateEvent;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.handlers.HotelsMessageQueue;
import kernitus.plugin.Hotels.handlers.MessageType;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.TerrainManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;
import kernitus.plugin.Hotels.trade.RoomBuyer;
import kernitus.plugin.Hotels.trade.TradesHolder;

public class Room {

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
	public Room(World world, String hotelName, int num){
		this.hotel = new Hotel(world, hotelName);
		this.num = num;
		this.sconfig = getSignConfig();
		this.world = world;
	}
	public Room(World world, String hotelName, String num){
		this.hotel = new Hotel(world, hotelName);
		this.num = Integer.parseInt(num);
		this.sconfig = getSignConfig();
		this.world = world;
	}
	public Room(String hotelName, int num){
		//Use only when world is unknown, due to extra calculations involved to find it
		this.hotel = new Hotel(hotelName);
		this.num = num;
		this.sconfig = getSignConfig();
		this.world = hotel.getWorld();
	}
	public Room(String hotelName, String num){
		//Use only when world is unknown, due to extra calculations involved to find it
		this.hotel = new Hotel(hotelName);
		this.num = Integer.parseInt(num);
		this.sconfig = getSignConfig();
		this.world = hotel.getWorld();
	}

	//////////////////////
	///////Getters////////
	//////////////////////
	public boolean exists(){
		return world==null ? false : WorldGuardManager.hasRegion(world, "hotel-" + hotel.getName() + "-" + num);
	}
	public int getNum(){
		return num;
	}
	public Hotel getHotel(){
		return hotel;
	}
	public ProtectedRegion getRegion(){
		return WorldGuardManager.getRoomRegion(world, hotel.getName(), num);
	}
	public OfflinePlayer getRenter(){
		String renter = sconfig.getString("Sign.renter");
		return renter != null ? Bukkit.getOfflinePlayer(UUID.fromString(renter)) : null;
	}
	public boolean isRenter(UUID uuid){
		OfflinePlayer renter = getRenter();
		return renter==null ? false : renter.getUniqueId().equals(uuid);
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
		return getRoomSign().getWorldFromConfig();
	}
	public RoomSign getRoomSign(){
		return new RoomSign(this);
	}
	public String getHotelNameFromConfig(){
		return sconfig.getString("Sign.hotel");
	}
	public int getRoomNumFromConfig(){
		return sconfig.getInt("Sign.room");
	}
	public Location getSignLocation(){
		return new Location(getWorldFromConfig(), sconfig.getInt("Sign.location.coords.x"), sconfig.getInt("Sign.location.coords.y"), sconfig.getInt("Sign.location.coords.z"));
	}
	public Block getBlockAtSignLocation(){
		return world.getBlockAt(getSignLocation());
	}
	public boolean isBlockAtSignLocationSign(){
		Material mat = getBlockAtSignLocation().getType();
		return mat.equals(Material.WALL_SIGN) || mat.equals(Material.SIGN_POST);
	}
	public Sign getSign(){
		return isBlockAtSignLocationSign() ? ((Sign) getBlockAtSignLocation().getState()) : null;
	}
	public Location getDefaultHome(){
		World world = getWorldFromConfig();
		return (world == null || !sconfig.contains("Sign.defaultHome", true)) ? null : new Location(world,sconfig.getDouble("Sign.defaultHome.x"),sconfig.getDouble("Sign.defaultHome.y"),sconfig.getDouble("Sign.defaultHome.z"),(float) sconfig.getDouble("Sign.defaultHome.yaw"),(float) sconfig.getDouble("Sign.defaultHome.pitch"));
	}
	public Location getUserHome(){
		World world = getWorldFromConfig();
		return (world == null || !sconfig.contains("Sign.userHome", true)) ? null : new Location(world,sconfig.getDouble("Sign.userHome.x"),sconfig.getDouble("Sign.userHome.y"),sconfig.getDouble("Sign.userHome.z"),(float) sconfig.getDouble("Sign.userHome.yaw"),(float) sconfig.getDouble("Sign.userHome.pitch"));
	}
	public int getTimesExtended(){
		return sconfig.getInt("Sign.extended");
	}
	public long getExpiryMinute(){
		return sconfig.getLong("Sign.expiryDate");
	}
	public long getRentMinute(){
		return sconfig.getLong("Sign.timeRentedAt");
	}
	public long getRentTime(){
		return sconfig.getLong("Sign.time");
	}
	public boolean isFree(){
		OfflinePlayer renter = null;
		if(exists() && getSignFile().exists()) //Check if region & file exist
			renter = getRenter();
		return renter==null || !renter.hasPlayedBefore();
	}
	public boolean isFreeOrNotSetup(){
		return !getSignFile().exists() ? true : isFree();
	}
	public boolean isNotSetup(){
		return !getSignFile().exists();
	}
	public List<String> getFriendsList(){
		return sconfig.getStringList("Sign.friends");
	}
	public RoomBuyer getBuyer(){
		return TradesHolder.getBuyerFromRoom(this);
	}
	public boolean getShouldReset(){
		return sconfig.getBoolean("Sign.reset");
	}
	//////////////////////
	///////Setters////////
	//////////////////////
	public void createRegion(ProtectedRegion r){
		WorldGuardManager.addRegion(world, r);
		WorldGuardManager.saveRegions(world);
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
	public void setCost(double cost){
		sconfig.set("Sign.cost", cost);
	}
	private void setSignLocation(Location l){
		sconfig.set("Sign.location.world", l.getWorld().getUID().toString());
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
		WorldGuardManager.setMember(uuid, getRegion());
		//Placing their UUID in the sign config
		sconfig.set("Sign.renter", uuid.toString());
	}
	public void rent(Player p){
		if(!exists() || !doesSignFileExist()){
			p.sendMessage(Mes.mes("chat.commands.rent.invalidData"));
			return;
		}

		RoomRentEvent rre = new RoomRentEvent(this, p);
		Bukkit.getPluginManager().callEvent(rre);
		if(rre.isCancelled()) return;
		p = rre.getRenter();


		setRenter(p.getUniqueId());
		//Set in config time rented at and expiry time
		long currentMin = System.currentTimeMillis()/1000/60;
		setTimeRentedAt(currentMin);
		setExpiryTime();
		saveSignConfig();

		//Setting room flags back in case they were changed to allow players in
		ProtectedRegion region = getRegion();
		WorldGuardManager.roomFlags(region, num, world);
		if(HotelsConfigHandler.getconfigyml().getBoolean("stopOwnersEditingRentedRooms")){
			region.setPriority(10);
			region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
			region.setFlag(DefaultFlag.BLOCK_PLACE, State.DENY);
		}

		WorldGuardManager.saveRegions(world);//Saving WG regions

		updateSign(); //Update this room sign with new info

		//Update this hotel's reception signs
		hotel.updateReceptionSigns();
	}
	public void setShouldReset(boolean value){
		sconfig.set("Sign.reset", value);
		if(value){
			//Create and save schematic file based on room region
			ProtectedRegion region = getRegion();
			WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
			TerrainManager tm = new TerrainManager(wep, world);

			File saveFile = HotelsConfigHandler.getSchematicFile(this);

			// Save the region to a schematic file
			try {
				BlockVector minvec = region.getMinimumPoint();
				BlockVector maxvec = region.getMaximumPoint();
				Location min = new Location(world, minvec.getX(), minvec.getX(), minvec.getZ());
				Location max = new Location(world, maxvec.getX(), maxvec.getY(), maxvec.getZ());
				tm.saveTerrain(saveFile, min, max);
			} catch (Exception e) {
				// Print message that something went wrong
				e.printStackTrace();
			}
		} else
			//Delete schematic file
			HotelsConfigHandler.getSchematicFile(this).delete();
		saveSignConfig();
	}
	public boolean toggleShouldReset(){
		boolean value = !getShouldReset();
		setShouldReset(value);
		return value;
	}
	///Config stuff
	private File getSignFile(){
		return new File("plugins"+File.separator+"Hotels"+File.separator+"Signs"+File.separator+hotel.getName().toLowerCase()+"-"+num+".yml");
	}
	private YamlConfiguration getSignConfig(){
		return YamlConfiguration.loadConfiguration(getSignFile());
	}
	public boolean doesSignFileExist(){
		return getSignFile().exists();
	}
	public boolean saveSignConfig(){
		try {
			sconfig.save(getSignFile());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void deleteSignFile(){
		getSignFile().delete();
	}
	public void deleteSignAndFile(){
		Location loc = getSignLocation();
		Block b = world.getBlockAt(loc);
		Material mat = b.getType();
		if(mat.equals(Material.WALL_SIGN) || mat.equals(Material.SIGN_POST)){
			Sign s = (Sign) b.getState();
			String Line1 = ChatColor.stripColor(s.getLine(0));
			if(Line1.matches("Reception") || Line1.matches(Mes.mesnopre("Sign.reception"))){
				if(WorldGuardManager.getRegion(world,"Hotel-"+hotel.getName()).contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())){
					deleteSignFile();
				}
			}
		}
	}

	public HotelsResult renumber(String newNum){
		return renumber(Integer.parseInt(newNum));
	}

	public HotelsResult renumber(int newNum){
		int oldNum = num;
		Hotel hotel = getHotel();
		String hotelName = hotel.getName();

		if(newNum>100000)
			return HotelsResult.NEW_NUM_TOO_BIG;

		if(!hotel.exists())
			return HotelsResult.HOTEL_NON_EXISTENT;


		if(!exists())
			return HotelsResult.ROOM_NON_EXISTENT;

		if(!doesSignFileExist()){
			return HotelsResult.FILE_NON_EXISTENT;
		}

		Block sign = getBlockAtSignLocation();
		Material mat = sign.getType();


		if(mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN)){
			sign.setType(Material.AIR);
			deleteSignFile();
			return HotelsResult.BLOCK_NOT_SIGN;
		}

		Sign s = (Sign) sign.getState();

		String Line2 = ChatColor.stripColor(s.getLine(1));

		Location signLocation = getSignLocation();

		if(!hotel.getRegion().contains(signLocation.getBlockX(),signLocation.getBlockY(),signLocation.getBlockZ())){
			sign.setType(Material.AIR);
			deleteSignFile();
			return HotelsResult.OUT_OF_REGION;
		}

		RoomRenumberEvent rre = new RoomRenumberEvent(this, oldNum);
		Bukkit.getPluginManager().callEvent(rre);
		if(rre.isCancelled()) return HotelsResult.CANCELLED;

		s.setLine(1, Mes.mesnopre("sign.room.name") + " " + newNum + " - " + Line2.split(" ")[3]);
		s.update();

		sconfig.set("Sign.room", Integer.valueOf(newNum));
		sconfig.set("Sign.region", "hotel-"+hotel+"-"+newNum);
		saveSignConfig();

		File newFile = HotelsConfigHandler.getFile("Signs"+File.separator+hotelName+"-"+newNum+".yml");
		getSignFile().renameTo(newFile);

		//Renaming region and changing number in greet/farewell messages
		ProtectedRegion oldRegion = WorldGuardManager.getRegion(world, "hotel-"+hotel+"-"+num);

		if(Mes.flagValue("room.map-making.GREETING").equalsIgnoreCase("true"))
			oldRegion.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.mesnopre("message.room.enter").replaceAll("%room%", String.valueOf(newNum))));
		if(Mes.flagValue("room.map-making.FAREWELL").equalsIgnoreCase("true"))
			oldRegion.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.mesnopre("message.room.exit").replaceAll("%room%", String.valueOf(newNum))));
		WorldGuardManager.renameRegion("Hotel-"+hotelName+"-"+num, "Hotel-"+hotelName+"-"+newNum, world);
		WorldGuardManager.saveRegions(world);

		num = newNum;
		sconfig = getSignConfig();

		return HotelsResult.SUCCESS;
	}

	public boolean createSignConfig(Player p, long timeInMins, double cost, Location signLocation){
		if(!doesSignFileExist()){
			setRentTime(timeInMins);
			setHotelNameInConfig(hotel.getName());
			setRoomNumInConfig(num);
			setCost(cost);
			setSignLocation(signLocation);
		}
		return saveSignConfig();
	}

	public void updateSign(){
		Sign s = getSign();

		if(s==null) return;

		long currentMins = System.currentTimeMillis()/1000/60;

		long remainingTime;

		if(isFree()){
			remainingTime = getTime();
			s.setLine(3, ChatColor.GREEN + Mes.mesnopre("sign.vacant"));
		}
		else{
			remainingTime = getExpiryMinute() - currentMins;
			s.setLine(3, ChatColor.DARK_RED + getRenter().getName());
		}

		String formattedRemainingTime = SignManager.TimeFormatter(remainingTime);

		RoomSignUpdateEvent rsue = new RoomSignUpdateEvent(this, s, remainingTime, formattedRemainingTime);

		Bukkit.getPluginManager().callEvent(rsue); //Call Room Sign Update event
		if(rsue.isCancelled()){ return; } //If event has been cancelled return

		formattedRemainingTime = rsue.getFormattedRemainingTime(); //Getting FRT from event in case another plugin modified it

		s.setLine(2, formattedRemainingTime);

		s.update();
	}

	public boolean remove(){

		if(!exists())
			return false;

		WorldGuardManager.removeRegion(world, getRegion());

		WorldGuardManager.saveRegions(world);

		if(!doesSignFileExist())
			return false;

		deleteSignFile();

		return true;
	}

	public HotelsResult removePlayer(OfflinePlayer playerToRemove){
		if(world==null)
			return HotelsResult.WORLD_NON_EXISTENT;

		if(!hotel.exists())
			return HotelsResult.HOTEL_NON_EXISTENT;

		if(!exists())
			return HotelsResult.ROOM_NON_EXISTENT;

		if(!playerToRemove.hasPlayedBefore())
			return HotelsResult.USER_NON_EXISTENT;

		if(isFree())
			return HotelsResult.IS_NOT_RENTED;

		ProtectedRegion r = getRegion();
		WorldGuardManager.removeMember(playerToRemove, r);

		if(HotelsConfigHandler.getconfigyml().getBoolean("settings.stopOwnersEditingRentedRooms")){
			r.setFlag(DefaultFlag.BLOCK_BREAK, null);
			r.setFlag(DefaultFlag.BLOCK_PLACE, null);
			r.setPriority(1);
		}

		//Config stuff
		sconfig.set("Sign.renter", null);
		sconfig.set("Sign.timeRentedAt", null);
		sconfig.set("Sign.expiryDate", null);
		sconfig.set("Sign.friends", null);
		sconfig.set("Sign.extended", null);
		sconfig.set("Sign.userHome", null);
		saveSignConfig();

		updateSign();

		getHotel().updateReceptionSigns();

		//Make free room accessible to all players if set in config
		WorldGuardManager.makeRoomAccessible(r);

		return HotelsResult.SUCCESS;
	}

	public void renameRoom(String newHotelName){
		WorldGuardManager.renameRegion(getRegion().getId(), "Hotel-" + newHotelName + "-" + String.valueOf(num), world);
		WorldGuardManager.saveRegions(world);
		hotel = new Hotel(world, newHotelName);
	}

	public HotelsResult addFriend(OfflinePlayer friend){

		if(!doesSignFileExist())
			return HotelsResult.FILE_NON_EXISTENT;

		if(!isRented())
			return HotelsResult.IS_NOT_RENTED;	

		if(!friend.hasPlayedBefore())
			return HotelsResult.USER_NON_EXISTENT;

		//Adding player as region member

		WorldGuardManager.addMember(friend, getRegion());
		//Adding player to config under friends list
		List<String> stringList = getFriendsList();
		stringList.add(friend.getUniqueId().toString());
		sconfig.set("Sign.friends", stringList);

		saveSignConfig();

		return HotelsResult.SUCCESS;
	}

	public HotelsResult removeFriend(OfflinePlayer friend){
		if(!doesSignFileExist())
			return HotelsResult.FILE_NON_EXISTENT;

		if(!isRented())
			return HotelsResult.IS_NOT_RENTED;

		if(!getFriendsList().contains(friend.getUniqueId().toString()))
			return HotelsResult.FRIEND_NOT_IN_LIST;

		//Removing player as region member
		WorldGuardManager.removeMember(friend, getRegion());

		//Removing player from config under friends list
		List<String> stringList = sconfig.getStringList("Sign.friends");
		stringList.remove(friend.getUniqueId().toString());
		sconfig.set("Sign.friends", stringList);

		saveSignConfig();

		return HotelsResult.SUCCESS;
	}

	public void delete(){
		RoomDeleteEvent rde = new RoomDeleteEvent(this);
		Bukkit.getPluginManager().callEvent(rde);
		if(rde.isCancelled()) return;
		WorldGuardManager.removeRegion(world, getRegion());
		WorldGuardManager.saveRegions(world);
		deleteSignAndFile();
	}

	public void createRegion(ProtectedRegion region, Player p){
		World world = p.getWorld();
		ProtectedRegion hotelRegion = WorldGuardManager.getRegion(world, "hotel-"+hotel.getName());
		if(!Mes.hasPerm(p, "hotels.create")){ p.sendMessage(Mes.mes("chat.noPermission")); return; }
		if(WorldGuardManager.doesRoomRegionOverlap(region, world)){ p.sendMessage(Mes.mes("chat.commands.room.alreadyPresent")); return; }
		if(!WorldGuardManager.isOwner(p, hotelRegion) && !Mes.hasPerm(p, "hotels.create.admin")){ p.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat")); return; }
		WorldGuardManager.addRegion(world, region);
		WorldGuardManager.roomFlags(region, num, world);
		if(HotelsConfigHandler.getconfigyml().getBoolean("settings.stopOwnersEditingRentedRooms"))
			region.setPriority(1);
		else
			region.setPriority(10);
		WorldGuardManager.makeRoomAccessible(region);
		WorldGuardManager.saveRegions(p.getWorld());
		p.sendMessage(Mes.mes("chat.commands.room.success").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotel.getName()));
	}

	public void unrent(){
		ProtectedRegion region = getRegion();
		String hotelName = getHotel().getName();

		if(!isBlockAtSignLocationSign()) return;
		Sign sign = (Sign) getBlockAtSignLocation().getState();

		OfflinePlayer p = getRenter(); //Getting renter
		List<String> friendList = getFriendsList(); //Getting friend list

		//Calling rent expiry event
		RentExpiryEvent ree = new RentExpiryEvent(this, p, friendList);
		Bukkit.getPluginManager().callEvent(ree);

		if(ree.isCancelled()) return;

		//Removing renter
		WorldGuardManager.removeMember(p, region); //Removing renter as member of room region
		//Removing friends
		for(String currentFriend : friendList){
			OfflinePlayer cf = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentFriend));
			WorldGuardManager.removeMember(cf, region);
		}

		//If set in config, make room accessible to all players now that it is not rented
		WorldGuardManager.makeRoomAccessible(region);
		if(HotelsConfigHandler.getconfigyml().getBoolean("settings.stopOwnersEditingRentedRooms")){
			region.setFlag(DefaultFlag.BLOCK_BREAK, null);
			region.setFlag(DefaultFlag.BLOCK_PLACE, null);
			region.setPriority(1);
		}

		Mes.debugConsole(Mes.mesnopre("sign.rentExpiredConsole").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotelName).replaceAll("%player%", p.getName()));

		if(p.isOnline())
			( (Player) p).sendMessage(Mes.mes("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotelName));

		else //Player is offline, place their expiry message in the message queue
			HotelsMessageQueue.addMessage(MessageType.expiry, p.getUniqueId(), Mes.mes("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotelName));

		sconfig.set("Sign.renter", null);
		sconfig.set("Sign.timeRentedAt", null);
		sconfig.set("Sign.expiryDate", null);
		sconfig.set("Sign.friends", null);
		sconfig.set("Sign.extended", null);
		sconfig.set("Sign.userHome", null);

		saveSignConfig();

		//Setting sign to say "Vacant"
		sign.setLine(3, ChatColor.GREEN + Mes.mesnopre("sign.vacant"));
		//Resetting time on sign to default
		sign.setLine(2, SignManager.TimeFormatter(sconfig.getLong("Sign.time")));
		sign.update();

		if(getShouldReset())
			resetRoom();
	}

	public void resetRoom(){
		WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		TerrainManager tm = new TerrainManager(wep, world);
		try {
			tm.loadSchematic(HotelsConfigHandler.getSchematicFile(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkRent(){ //Checks if rent has expired, if so unrents
		//Returns true if anything changed, so we know if Reception signs for this hotel need to be updated
		File file = getSignFile();
		if(!exists()){
			file.delete();
			Mes.debugConsole("Room sign " + file.getName() + " was deleted as the room doesn't exist");
			return true;
		}

		Block signBlock = getBlockAtSignLocation(); //Getting block at location where sign should be

		if(!isBlockAtSignLocationSign()){
			file.delete();//If block is not a sign, delete it
			Mes.debugConsole(Mes.mesnopre("sign.delete.location").replaceAll("%filename%", file.getName()));
			return true;
		}

		String hotelName = getHotel().getName();

		Sign sign = (Sign) signBlock.getState(); //Getting sign object
		if(!hotelName.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))){//If hotelName on sign doesn't match that in config
			file.delete();
			Mes.debugConsole(Mes.mesnopre("sign.delete.hotelName").replaceAll("%filename%", file.getName()));
			return true;
		}

		String[] Line2parts = ChatColor.stripColor(sign.getLine(1)).split("\\s");
		int roomNumfromSign = Integer.valueOf(Line2parts[1].trim()); //Room Number 
		if(getRoomNumFromConfig()!=roomNumfromSign){ //If roomNum on sign doesn't match that in config
			file.delete();
			Mes.debugConsole(Mes.mesnopre("sign.delete.roomNum").replaceAll("%filename%", file.getName()));
			return true;
		}


		if(sconfig.get("Sign.expiryDate") != null){

			long expiryDate = getExpiryMinute();
			if(expiryDate==0){ return false; }//Rent is permanent
			if(expiryDate > (System.currentTimeMillis()/1000/60)){//If rent has not expired, update time remaining on sign
				updateSign();
			}
			else{//Rent has expired
				if(!isRented()) return true;  //Rent expired but there's no renter, something went wrong, just quit
				unrent();
				return true;
			}
			return false;
		}

		boolean wasRented = isRented();

		//The expiry date is null
		sconfig.set("Sign.renter", null);
		sconfig.set("Sign.timeRentedAt", null);
		sconfig.set("Sign.expiryDate", null);
		sconfig.set("Sign.friends", null);
		sconfig.set("Sign.extended", null);

		saveSignConfig();

		sign.setLine(3, ChatColor.GREEN + Mes.mesnopre("sign.vacant"));
		sign.setLine(2, SignManager.TimeFormatter(sconfig.getLong("Sign.time")));
		sign.update();

		return wasRented;
	}
	public void setBuyer(UUID uuid, double price){
		TradesHolder.addRoomBuyer(Bukkit.getPlayer(uuid), this, price);
	}
	public void removeBuyer(){
		TradesHolder.removeHotelBuyer(TradesHolder.getBuyerFromRoom(this).getPlayer());
	}
}