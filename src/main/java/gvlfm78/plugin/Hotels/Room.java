package kernitus.plugin.Hotels;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.events.RentExpiryEvent;
import kernitus.plugin.Hotels.events.RoomDeleteEvent;
import kernitus.plugin.Hotels.events.RoomRentEvent;
import kernitus.plugin.Hotels.events.RoomRenumberEvent;
import kernitus.plugin.Hotels.events.RoomSignUpdateEvent;
import kernitus.plugin.Hotels.exceptions.BlockNotSignException;
import kernitus.plugin.Hotels.exceptions.EventCancelledException;
import kernitus.plugin.Hotels.exceptions.FriendNotFoundException;
import kernitus.plugin.Hotels.exceptions.HotelNonExistentException;
import kernitus.plugin.Hotels.exceptions.NotRentedException;
import kernitus.plugin.Hotels.exceptions.NumberTooLargeException;
import kernitus.plugin.Hotels.exceptions.OutOfRegionException;
import kernitus.plugin.Hotels.exceptions.RenterNonExistentException;
import kernitus.plugin.Hotels.exceptions.RoomNonExistentException;
import kernitus.plugin.Hotels.exceptions.RoomNotSetupException;
import kernitus.plugin.Hotels.exceptions.RoomSignInRoomException;
import kernitus.plugin.Hotels.exceptions.UserNonExistentException;
import kernitus.plugin.Hotels.exceptions.ValuesNotMatchingException;
import kernitus.plugin.Hotels.exceptions.WorldNonExistentException;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.handlers.HotelsMessageQueue;
import kernitus.plugin.Hotels.handlers.MessageType;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.TerrainManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;
import kernitus.plugin.Hotels.signs.RoomSign;
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
		this(hotel, Integer.parseInt(num));
	}
	public Room(World world, String hotelName, int num){
		this.hotel = new Hotel(world, hotelName);
		this.num = num;
		this.sconfig = getSignConfig();
		this.world = world;
	}
	public Room(World world, String hotelName, String num){
		this(world, hotelName, Integer.parseInt(num));
	}
	public Room(String hotelName, int num){
		//Use only when world is unknown, due to extra calculations involved to find it
		hotel = new Hotel(hotelName);
		this.num = num;
		sconfig = getSignConfig();
		world = hotel.getWorld();
	}
	public Room(String hotelName, String num){
		this(hotelName, Integer.parseInt(num));
	}
	public Room(String hotelName, int num, CommandSender sender){
		this.num = num;
		if(sender instanceof Player){
			world = ((Player) sender).getWorld();
			hotel = new Hotel(world, hotelName);
		}
		else{
			hotel = new Hotel(hotelName);
			world = hotel.getWorld();
		}
		sconfig = getSignConfig();
	}
	public Room(String hotelName, String num, CommandSender sender){
		this(hotelName, Integer.parseInt(num), sender);
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
		if(world == null ||
				!sconfig.contains("Sign.defaultHome.x") ||
				!sconfig.contains("Sign.defaultHome.y") ||
				!sconfig.contains("Sign.defaultHome.z")
				) return null;

		return new Location(world,
				sconfig.getDouble("Sign.defaultHome.x"),
				sconfig.getDouble("Sign.defaultHome.y"),
				sconfig.getDouble("Sign.defaultHome.z"),
				(float) sconfig.getDouble("Sign.defaultHome.yaw"),
				(float) sconfig.getDouble("Sign.defaultHome.pitch"));
	}
	public Location getUserHome(){
		World world = getWorldFromConfig();
		if(world == null ||
				!sconfig.contains("Sign.userHome.x") ||
				!sconfig.contains("Sign.userHome.y") ||
				!sconfig.contains("Sign.userHome.z")
				) return null;

		return new Location(world,
				sconfig.getDouble("Sign.userHome.x"),
				sconfig.getDouble("Sign.userHome.y"),
				sconfig.getDouble("Sign.userHome.z"),
				(float) sconfig.getDouble("Sign.userHome.yaw"),
				(float) sconfig.getDouble("Sign.userHome.pitch"));
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
	public List<String> getFriends(){
		return sconfig.getStringList("Sign.friends");
	}
	public RoomBuyer getBuyer(){
		return TradesHolder.getBuyerFromRoom(this);
	}
	public boolean shouldReset(){
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
	public void rent(Player p) throws IOException, EventCancelledException {
		if(!exists() || !doesSignFileExist()){
			Mes.mes(p, "chat.commands.rent.invalidData");
			return;
		}

		RoomRentEvent rre = new RoomRentEvent(this, p);
		Bukkit.getPluginManager().callEvent(rre);
		if(rre.isCancelled()) throw new EventCancelledException();
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
	public void setShouldReset(boolean value) throws IOException, WorldEditException, RoomNotSetupException, RoomSignInRoomException, DataException {
		if(isNotSetup()) throw new RoomNotSetupException();


		sconfig.set("Sign.reset", value);
		if(value){
			Block b = getSign().getBlock();
			if(getRegion().contains(b.getX(), b.getY(), b.getZ())) throw new RoomSignInRoomException();
			//Create and save schematic file based on room region
			TerrainManager tm = new TerrainManager(world);

			File schematicFile = HotelsConfigHandler.getSchematicFile(this);

			// Save the region to a schematic file
			tm.saveTerrain(schematicFile, world, getRegion());
		} else deleteSchematic();

		saveSignConfig();
	}
	public boolean toggleShouldReset() throws IOException, WorldEditException, RoomNotSetupException, RoomSignInRoomException, DataException {
		boolean value = !shouldReset();
		setShouldReset(value);
		return value;
	}
	///Config stuff
	private File getSignFile(){
		return HotelsConfigHandler.getSignFile(hotel.getName(), num);
	}
	private YamlConfiguration getSignConfig(){
		return YamlConfiguration.loadConfiguration(getSignFile());
	}
	public boolean doesSignFileExist(){
		return getSignFile().exists();
	}
	public void saveSignConfig() throws IOException {
		sconfig.save(getSignFile());
	}

	public void deleteSignFile(){
		File file = getSignFile();
		if(file.exists()) file.delete();
	}
	public void deleteSignAndFile(){
		Location loc = getSignLocation();
		Block b = world.getBlockAt(loc);
		Material mat = b.getType();
		if(mat.equals(Material.WALL_SIGN) || mat.equals(Material.SIGN_POST)){
			Sign s = (Sign) b.getState();
			String Line1 = ChatColor.stripColor(s.getLine(0));
			if(Line1.equalsIgnoreCase(hotel.getName())){
				if(WorldGuardManager.getHotelRegion(world, hotel.getName()).contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
					deleteSignFile();
				b.setType(Material.AIR);
			}
		}
	}
	public void deleteSchematic(){
		File file = HotelsConfigHandler.getSchematicFile(this);
		if(file.exists()) file.delete();
	}

	public void renumber(String newNum) throws NumberFormatException, NumberTooLargeException, HotelNonExistentException, RoomNonExistentException, BlockNotSignException, OutOfRegionException, EventCancelledException, IOException {
		renumber(Integer.parseInt(newNum));
	}

	public void renumber(int newNum) throws NumberTooLargeException, HotelNonExistentException, RoomNonExistentException, BlockNotSignException, OutOfRegionException, EventCancelledException, IOException {
		int oldNum = num;
		Hotel hotel = getHotel();
		String hotelName = hotel.getName();

		if(newNum>100000) throw new NumberTooLargeException();

		if(!hotel.exists()) throw new HotelNonExistentException();

		if(!exists()) throw new RoomNonExistentException();

		if(!doesSignFileExist()) throw new FileNotFoundException();

		Block sign = getBlockAtSignLocation();
		Material mat = sign.getType();

		if(mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN)){
			sign.setType(Material.AIR);
			deleteSignFile();
			throw new BlockNotSignException();
		}

		Sign s = (Sign) sign.getState();

		String Line2 = ChatColor.stripColor(s.getLine(1));

		Location signLocation = getSignLocation();

		if(!hotel.getRegion().contains(signLocation.getBlockX(),signLocation.getBlockY(),signLocation.getBlockZ())){
			sign.setType(Material.AIR);
			deleteSignFile();
			throw new OutOfRegionException();
		}

		RoomRenumberEvent rre = new RoomRenumberEvent(this, oldNum);
		Bukkit.getPluginManager().callEvent(rre);
		if(rre.isCancelled()) throw new EventCancelledException();

		s.setLine(1, Mes.getStringNoPrefix("sign.room.name") + " " + newNum + " - " + Line2.split(" ")[3]);
		s.update();

		sconfig.set("Sign.room", Integer.valueOf(newNum));
		sconfig.set("Sign.region", "hotel-"+hotel+"-"+newNum);
		saveSignConfig();

		File newFile = HotelsConfigHandler.getFile("Signs"+File.separator+hotelName+"-"+newNum+".yml");
		getSignFile().renameTo(newFile);

		//Renaming region and changing number in greet/farewell messages
		ProtectedRegion oldRegion = WorldGuardManager.getRegion(world, "hotel-"+hotel+"-"+num);

		if(Mes.flagValue("room.map-making.GREETING").equalsIgnoreCase("true"))
			oldRegion.setFlag(DefaultFlag.GREET_MESSAGE, (Mes.getStringNoPrefix("message.room.enter").replaceAll("%room%", String.valueOf(newNum))));
		if(Mes.flagValue("room.map-making.FAREWELL").equalsIgnoreCase("true"))
			oldRegion.setFlag(DefaultFlag.FAREWELL_MESSAGE, (Mes.getStringNoPrefix("message.room.exit").replaceAll("%room%", String.valueOf(newNum))));
		WorldGuardManager.renameRegion("Hotel-"+hotelName+"-"+num, "Hotel-"+hotelName+"-"+newNum, world);
		WorldGuardManager.saveRegions(world);

		num = newNum;
		sconfig = getSignConfig();
	}

	public void createSignConfig(Player p, long timeInMins, double cost, Location signLocation) throws IOException{
		if(!doesSignFileExist()){
			setRentTime(timeInMins);
			setHotelNameInConfig(hotel.getName());
			setRoomNumInConfig(num);
			setCost(cost);
			setSignLocation(signLocation);
		}
		saveSignConfig();
	}

	public void updateSign() throws EventCancelledException {
		Sign s = getSign();

		if(s==null) return;

		long remainingTime;

		if(isFree()){
			remainingTime = getTime();
			s.setLine(3, ChatColor.GREEN + Mes.getStringNoPrefix("sign.vacant"));
		}
		else{
			remainingTime = getExpiryMinute() - (System.currentTimeMillis()/1000/60);
			s.setLine(3, ChatColor.DARK_RED + getRenter().getName());
		}

		String formattedRemainingTime = SignManager.TimeFormatter(remainingTime);

		RoomSignUpdateEvent rsue = new RoomSignUpdateEvent(this, s, remainingTime, formattedRemainingTime);

		Bukkit.getPluginManager().callEvent(rsue); //Call Room Sign Update event
		if(rsue.isCancelled()) throw new EventCancelledException();

		formattedRemainingTime = rsue.getFormattedRemainingTime(); //Getting FRT from event in case another plugin modified it

		s.setLine(2, formattedRemainingTime);

		s.update();
	}

	public void renameRoom(String newHotelName){
		WorldGuardManager.renameRegion(getRegion().getId(), "Hotel-" + newHotelName + "-" + String.valueOf(num), world);
		WorldGuardManager.saveRegions(world);
		hotel = new Hotel(world, newHotelName);
	}

	public void addFriend(OfflinePlayer friend) throws UserNonExistentException, NotRentedException, IOException {

		if(!doesSignFileExist()) throw new FileNotFoundException();

		if(!isRented()) throw new NotRentedException();	

		if(!friend.hasPlayedBefore()) throw new UserNonExistentException();

		//Adding player as region member

		WorldGuardManager.addMember(friend, getRegion());
		//Adding player to config under friends list
		List<String> stringList = getFriends();
		stringList.add(friend.getUniqueId().toString());
		sconfig.set("Sign.friends", stringList);

		saveSignConfig();
	}

	public void removeFriend(OfflinePlayer friend) throws NotRentedException, FriendNotFoundException, IOException {
		if(!doesSignFileExist()) throw new FileNotFoundException();

		if(!isRented()) throw new NotRentedException();

		if(!getFriends().contains(friend.getUniqueId().toString()))
			throw new FriendNotFoundException();

		//Removing player as region member
		WorldGuardManager.removeMember(friend, getRegion());

		//Removing player from config under friends list
		List<String> stringList = sconfig.getStringList("Sign.friends");
		stringList.remove(friend.getUniqueId().toString());
		sconfig.set("Sign.friends", stringList);

		saveSignConfig();
	}

	public void delete() throws EventCancelledException {
		RoomDeleteEvent rde = new RoomDeleteEvent(this);
		Bukkit.getPluginManager().callEvent(rde);
		if(rde.isCancelled()) throw new EventCancelledException();
		WorldGuardManager.removeRegion(world, getRegion());
		WorldGuardManager.saveRegions(world);
		deleteSignAndFile();
		deleteSchematic();
	}

	public void createRegion(ProtectedRegion region, Player p){
		World world = p.getWorld();
		ProtectedRegion hotelRegion = WorldGuardManager.getRegion(world, "hotel-"+hotel.getName());
		if(!Mes.hasPerm(p, "hotels.create")){ Mes.mes(p, "chat.noPermission"); return; }
		if(WorldGuardManager.doesRoomRegionOverlap(region, world)){ Mes.mes(p, "chat.commands.room.alreadyPresent"); return; }
		if(!WorldGuardManager.isOwner(p, hotelRegion) && !Mes.hasPerm(p, "hotels.create.admin")){ Mes.mes(p, "chat.commands.youDoNotOwnThat"); return; }
		WorldGuardManager.addRegion(world, region);
		WorldGuardManager.roomFlags(region, num, world);
		if(HotelsConfigHandler.getconfigyml().getBoolean("stopOwnersEditingRentedRooms"))
			region.setPriority(1);
		else
			region.setPriority(10);
		WorldGuardManager.makeRoomAccessible(region);
		WorldGuardManager.saveRegions(p.getWorld());
		p.sendMessage(Mes.getString("chat.commands.room.success").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotel.getName()));
	}

	public void unrent() throws IOException, BlockNotSignException, WorldEditException, EventCancelledException, WorldNonExistentException, HotelNonExistentException, RoomNonExistentException, NotRentedException, DataException {
		if(world==null) throw new WorldNonExistentException();

		if(!hotel.exists()) throw new HotelNonExistentException();

		if(!exists()) throw new RoomNonExistentException();

		if(isFree()) throw new NotRentedException();

		ProtectedRegion region = getRegion();
		String hotelName = getHotel().getName();

		if(!isBlockAtSignLocationSign()) throw new BlockNotSignException();

		OfflinePlayer p = getRenter(); //Getting renter
		List<String> friendList = getFriends(); //Getting friend list

		//Calling rent expiry event
		RentExpiryEvent ree = new RentExpiryEvent(this, p, friendList);
		Bukkit.getPluginManager().callEvent(ree);

		if(ree.isCancelled()) throw new EventCancelledException();

		//Removing renter
		WorldGuardManager.removeMember(p, region); //Removing renter as member of room region
		//Removing friends
		for(String currentFriend : friendList){
			OfflinePlayer cf = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentFriend));
			WorldGuardManager.removeMember(cf, region);
		}

		//If set in config, make room accessible to all players now that it is not rented
		WorldGuardManager.makeRoomAccessible(region);
		if(HotelsConfigHandler.getconfigyml().getBoolean("stopOwnersEditingRentedRooms")){
			region.setFlag(DefaultFlag.BLOCK_BREAK, null);
			region.setFlag(DefaultFlag.BLOCK_PLACE, null);
			region.setPriority(1);
		}

		Mes.debug(Mes.getStringNoPrefix("sign.rentExpiredConsole").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotelName).replaceAll("%player%", p.getName()));

		if(p.isOnline())
			p.getPlayer().sendMessage(Mes.getString("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotelName));

		else //Player is offline, place their expiry message in the message queue
			HotelsMessageQueue.addMessage(MessageType.expiry, p.getUniqueId(), Mes.getString("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(num)).replaceAll("%hotel%", hotelName));

		sconfig.set("Sign.renter", null);
		sconfig.set("Sign.timeRentedAt", null);
		sconfig.set("Sign.expiryDate", null);
		sconfig.set("Sign.friends", null);
		sconfig.set("Sign.extended", null);
		sconfig.set("Sign.userHome", null);

		saveSignConfig();

		updateSign();

		if(shouldReset()) reset();
	}

	public void reset() throws IOException, WorldEditException, DataException {
		TerrainManager tm = new TerrainManager(world);
		ProtectedRegion region = getRegion();
		Vector origin = tm.getOriginFromRegion(tm.getRegionFromProtectedRegion(world, region));
		Location loc = new Location(world, origin.getX(), origin.getY(), origin.getZ());
		tm.loadSchematic(HotelsConfigHandler.getSchematicFile(this), loc, region);
	}

	public void checkRent() throws IOException, ValuesNotMatchingException, RoomNonExistentException, BlockNotSignException, RenterNonExistentException, WorldEditException, EventCancelledException, WorldNonExistentException, HotelNonExistentException, NotRentedException, DataException {
		//Checks if rent has expired, if so unrents
		File file = getSignFile();
		if(!exists()){
			file.delete();
			Mes.debug(Mes.getStringNoPrefix("sign.delete.roomNonExistent").replaceAll("%filename%", file.getName()));
			throw new RoomNonExistentException();
		}

		Block signBlock = getBlockAtSignLocation(); //Getting block at location where sign should be

		if(!isBlockAtSignLocationSign()){
			file.delete(); deleteSchematic();
			Mes.debug(Mes.getStringNoPrefix("sign.delete.location").replaceAll("%filename%", file.getName()));
			throw new BlockNotSignException();
		}

		String hotelName = getHotel().getName();

		Sign sign = (Sign) signBlock.getState();
		if(!hotelName.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))){//If hotelName on sign doesn't match that in config
			file.delete(); deleteSchematic();
			Mes.debug(Mes.getStringNoPrefix("sign.delete.hotelName").replaceAll("%filename%", file.getName()));
			throw new ValuesNotMatchingException();
		}

		String[] Line2parts = ChatColor.stripColor(sign.getLine(1)).split("\\s");
		int roomNumfromSign = Integer.valueOf(Line2parts[1].trim()); //Room Number 
		if(getRoomNumFromConfig()!=roomNumfromSign){ //If roomNum on sign doesn't match that in config
			file.delete(); deleteSchematic();
			Mes.debug(Mes.getStringNoPrefix("sign.delete.roomNum").replaceAll("%filename%", file.getName()));
			throw new ValuesNotMatchingException();
		}


		if(sconfig.get("Sign.expiryDate") != null){

			long expiryDate = getExpiryMinute();
			if(expiryDate==0) return; //Rent is permanent
			if(expiryDate > (System.currentTimeMillis()/1000/60)){//If rent has not expired, update time remaining on sign
				updateSign(); return; }
			else{//Rent has expired
				if(!isRented()) throw new RenterNonExistentException();  //Rent expired but there's no renter, something went wrong, just quit
				unrent();
				throw new RenterNonExistentException();
			}
		}

		boolean wasRented = isRented();

		//The expiry date is null
		sconfig.set("Sign.renter", null);
		sconfig.set("Sign.timeRentedAt", null);
		sconfig.set("Sign.expiryDate", null);
		sconfig.set("Sign.friends", null);
		sconfig.set("Sign.extended", null);

		saveSignConfig();

		sign.setLine(3, ChatColor.GREEN + Mes.getStringNoPrefix("sign.vacant"));
		sign.setLine(2, SignManager.TimeFormatter(sconfig.getLong("Sign.time")));
		sign.update();

		//If room was rented but there was no expiry date somethign went wrong
		if(wasRented) throw new ValuesNotMatchingException();
	}
	public void setBuyer(UUID uuid, double price){
		TradesHolder.addRoomBuyer(Bukkit.getPlayer(uuid), this, price);
	}
	public void removeBuyer(){
		TradesHolder.removeHotelBuyer(TradesHolder.getBuyerFromRoom(this).getPlayer());
	}
}