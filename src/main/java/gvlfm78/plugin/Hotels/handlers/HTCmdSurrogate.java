package kernitus.plugin.Hotels.handlers;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.DataException;
import kernitus.plugin.Hotels.*;
import kernitus.plugin.Hotels.events.HotelSaleEvent;
import kernitus.plugin.Hotels.events.RoomSaleEvent;
import kernitus.plugin.Hotels.exceptions.*;
import kernitus.plugin.Hotels.managers.HTSignManager;
import kernitus.plugin.Hotels.managers.HTWorldGuardManager;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.trade.HotelBuyer;
import kernitus.plugin.Hotels.trade.RoomBuyer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HTCmdSurrogate {

	public static void cmdMainPage(CommandSender sender){
		PluginDescriptionFile description = HotelsMain.getPluginDescription();
		Mes.mesAllSub(sender, "chat.commands.mainpage", "%plugin%", description.getName(), "%version%", description.getVersion());
	}
	public static void cmdCreate(CommandSender s, String hotelName){//Hotel creation command
		Player p = (Player) s;
		UUID playerUUID = p.getUniqueId();
		if(HTCreationMode.isInCreationMode(playerUUID))
			HTCreationMode.hotelSetup(hotelName, p);
		else
			Mes.mes(p, "chat.commands.create.fail");
	}
	public static void cmdCommands(CommandSender s){
		cmdCommands(s, HTConfigHandler.getconfigYML().getBoolean("onlyDisplayAllowedCommands", true));
	}
	public static void cmdCommands(CommandSender s, boolean onlyPermitted){
		s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.header"));
		s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.subheader"));
		s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.help"));

		if(Mes.hasPerm(s,"hotels.createmode") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.creationMode"));

		if(Mes.hasPerm(s,"hotels.create") || onlyPermitted){
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.create"));
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.room"));}

		if(Mes.hasPerm(s,"hotels.renumber") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.renum"));
		if(Mes.hasPerm(s,"hotels.rename") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.rename"));

		if(Mes.hasPerm(s, "hotels.sethome") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.sethome"));
		if(Mes.hasPerm(s, "hotels.home") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.home"));

		if(Mes.hasPerm(s,"hotels.check") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.check"));
		if(Mes.hasPerm(s,"hotels.list.hotels") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.list"));
		if(Mes.hasPerm(s,"hotels.list.rooms") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.rlist"));

		if(Mes.hasPerm(s,"hotels.friend") || onlyPermitted){
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.friend"));
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.friendList"));}

		if(Mes.hasPerm(s, "hotels.sell.hotel") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.sellh"));
		if(Mes.hasPerm(s, "hotels.buy.hotel") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.buyh"));

		if(Mes.hasPerm(s, "hotels.sell.room") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.sellr"));
		if(Mes.hasPerm(s, "hotels.buy.room") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.buyr"));

		if(Mes.hasPerm(s, "hotels.resetroom.toggle") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.roomreset"));
		if(Mes.hasPerm(s, "hotels.resetroom.reset") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.resetroom"));

		if(Mes.hasPerm(s,"hotels.reload") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.reload"));

		if(Mes.hasPerm(s,"hotels.remove") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.remove"));
		if(Mes.hasPerm(s,"hotels.delete.rooms") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.delr"));
		if(Mes.hasPerm(s,"hotels.delete") || onlyPermitted)
			s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.delete"));

		s.sendMessage(Mes.getStringNoPrefix("chat.commands.commands.footer"));
	}
	public static void cmdHelp(CommandSender sender, String pageNum){
		final int last = 5; //Last help page number

		Mes.mesNoPrefix(sender, "chat.commands.help.header");
		Mes.mesNoPrefix(sender, "chat.commands.help.subheader");

		int page;
		try{
			page = Integer.parseInt(pageNum);
			if(page < 1 || page > last) page = 1;
		}
		catch(Exception e){
			page = 1;
		}

		int next = page + 1;
		if(next > last) next = 1; //Loop back to first page

		Mes.mesAllSub(sender, "chat.commands.help.page" + page);

		Mes.mesNoPrefix(sender, "chat.commands.help.prefooter", "%num%", String.valueOf(next));
		Mes.mesNoPrefix(sender, "chat.commands.help.footer");
	}
	public static void cmdCreationModeEnter(CommandSender sender){
		Player p = (Player) sender;
		if(HTCreationMode.isInCreationMode(p.getUniqueId())){
			Mes.mes(p, "chat.commands.creationMode.alreadyIn"); return; }

		HTCreationMode.saveInventory(p);
		HTCreationMode.giveItems(p);
		Mes.mes(p, "chat.commands.creationMode.enter");
	}
	public static void cmdCreationModeExit(CommandSender sender){
		Player p = (Player) sender;
		if(!HTCreationMode.isInCreationMode(p.getUniqueId())){
			Mes.mes(p, "chat.commands.creationMode.notAlreadyIn"); return; }

		Mes.mes(p, "chat.commands.creationMode.exit");
		HTCreationMode.loadInventory(p);
	}
	public static void cmdCreationModeReset(CommandSender sender){
		Player p = (Player) sender;
		HTCreationMode.resetInventoryFiles(p.getUniqueId());
		Mes.mes(p, "chat.commands.creationMode.reset");
	}
	public static void cmdCheckSelf(CommandSender sender){
		Player p = (Player) sender;
		cmdCheck(p.getName(), sender);
	}
	public static void cmdCheck(String playerName, CommandSender sender){

		@SuppressWarnings("deprecation")
		OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
		if(p==null || !p.hasPlayedBefore()){ Mes.mes(sender, "chat.commands.userNonExistent"); return; }

		//Printing out owned hotels first
		ArrayList<Hotel> hotels = HotelsAPI.getHotelsOwnedBy(p.getUniqueId());

		Mes.mes(sender, "chat.commands.check.headerHotels", "%player%", playerName);

		if(hotels.size() < 1){ Mes.mes(sender, "chat.commands.check.noHotels"); return; }

		for(Hotel hotel : hotels){
			String hotelName = hotel.getName();
			int total = hotel.getTotalRoomCount();
			int free = hotel.getFreeRoomCount();
			Mes.mes(sender, "chat.commands.check.lineHotels",
					"%player%", playerName,
					"%hotel%", hotelName,
					"%total%", String.valueOf(total),
					"%free%", String.valueOf(free)
			);
		}

		//And printing out rented rooms
		ArrayList<Room> rooms = HotelsAPI.getRoomsRentedBy(p.getUniqueId());

		Mes.mes(sender, "chat.commands.check.headerRooms", "%player%", playerName);
		if(rooms.size() < 0){ Mes.mes(sender, "chat.commands.check.noRooms"); return; }

		for(Room room : rooms){//looping through rented rooms
			Hotel hotel = room.getHotel();
			String hotelName = hotel.getName();
			String roomNum = String.valueOf(room.getNum());

			long expiryDate = room.getExpiryMinute();

			if(expiryDate>0){
				String timeleft = HTSignManager.TimeFormatter(expiryDate - (System.currentTimeMillis()/1000/60) );
				Mes.mes(sender, "chat.commands.check.lineRooms",
						"%hotel%", hotelName,
						"%room%", roomNum,
						"%timeleft%", timeleft
				);
			}
			else//Room is permanently rented
				Mes.mes(sender, "chat.commands.check.lineRooms",
						"%hotel%", hotelName,
						"%room%", roomNum,
						"%timeleft%", Mes.getStringNoPrefix("sign.permanent")
				);
		}
	}
	public static void cmdReload(CommandSender sender){
		HTConfigHandler.reloadConfigs();
		Mes.mes(sender ,"chat.commands.reload.success");
	}
	public static void cmdRent(CommandSender sender, String hotelName, String roomNum){
		Player p = (Player) sender;
		Hotel hotel = new Hotel(p.getWorld(), hotelName);

		//If Hotel exists
		if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return; }

		Room room = new Room(hotel, roomNum);
		Location signLoc = room.getSignLocation();

		int x = signLoc.getBlockX();
		int y = signLoc.getBlockY();
		int z = signLoc.getBlockZ();
		//If sign is within region
		if(!hotel.getRegion().contains(x, y, z)){ Mes.mes(p, "chat.sign.use.signOutOfRegion"); return; }

		if(!room.doesSignFileExist()){ Mes.mes(p, "chat.sign.use.fileNonExistent"); return; }

		if(!hotel.getName().equalsIgnoreCase(room.getHotelNameFromConfig())){ Mes.mes(p, "chat.sign.use.differentHotelNames"); return; }
		if(!(room.getNum().equals(room.getRoomNumFromConfig()))){ Mes.mes(p, "chat.sign.use.differentRoomNums"); return; }
		HTSignManager.rentRoom(p, room);
	}
	public static void cmdFriendAdd(CommandSender sender, String hotelName, String roomNum, String friendName){
		Player player = (Player) sender;
		Room room = new Room(player.getWorld(), hotelName, roomNum);

		if(!room.doesSignFileExist()){ Mes.mes(player, "chat.commands.wrongData"); return; }

		if(!room.isRenter(player.getUniqueId())){
			Mes.mes(player, "chat.commands.friend.notRenter"); return; }

		@SuppressWarnings("deprecation")
		OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);

		if(player.getUniqueId().equals(friend.getUniqueId())){
			Mes.mes(player, "chat.commands.friend.addYourself"); return; }

		try {
			room.addFriend(friend);
			Mes.mes(player, "chat.commands.friend.addSuccess", "%friend%", friend.getName());
		} catch (UserNonExistentException e) {
			Mes.mes(player, "chat.commands.friend.nonExistent");
		} catch (NotRentedException e) {
			Mes.mes(player, "chat.commands.friend.noRenter");
		} catch (IOException e) {
			Mes.mes(player, "chat.commands.wrongData");
		} catch (UserAlreadyThereException e) {
			Mes.mes(player, "chat.commands.friend.alreadyFriend");
		}
	}
	public static void cmdFriendRemove(CommandSender sender, String hotelName, String roomNum, String friendName){
		Player player = (Player) sender;
		Room room = new Room(player.getWorld(), hotelName, roomNum);

		if(!room.doesSignFileExist()){ Mes.mes(player, "chat.commands.wrongData"); return; }

		if(!room.isRenter(player.getUniqueId())){
			Mes.mes(player, "chat.commands.friend.notRenter"); return; }

		@SuppressWarnings("deprecation")
		OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);

		try {
			room.removeFriend(friend);
			Mes.mes(player, "chat.commands.friend.removeSuccess", "%friend%", friend.getName());
		} catch (NotRentedException e) {
			Mes.mes(player, "chat.commands.friend.noRenter");
		} catch (FriendNotFoundException e) {
			Mes.mes(player, "chat.commands.friend.friendNotInList");
		} catch (IOException e) {
			Mes.mes(player, "chat.commands.wrongData");
		}
	}
	public static void cmdFriendList(CommandSender s, String hotelName, String roomNum, boolean skipRenterCheck){
		Room room = new Room(hotelName, roomNum, s);

		if(!room.doesSignFileExist()){ Mes.mes(s, "chat.commands.wrongData"); return; }

		if(room.isFree()){ Mes.mes(s, "chat.commands.friend.noRenter"); return; }

		if(!skipRenterCheck && !room.isRenter(((Player) s).getUniqueId())){ Mes.mes(s,"chat.commands.friend.notRenter"); return; }

		List<UUID> ids = room.getFriends();

		if(ids.isEmpty()) {
			Mes.mes(s, "chat.commands.friend.noFriends");
			return;
		}

		Mes.mes(s, "chat.commands.friend.list.heading", "%room%", roomNum, "%hotel%", hotelName);

		for(UUID currentFriend : ids){
			OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(currentFriend);
			String friendName = friend.getName();
			Mes.mes(s, "chat.commands.friend.list.line", "%name%", friendName);
		}

		Mes.mes(s, "chat.commands.friend.list.footer");
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void cmdHelperAdd(CommandSender sender, String hotelName, String helperName){
		Player player = (Player) sender;
		Hotel hotel = new Hotel(hotelName, sender);

		if(!hotel.exists()){ Mes.mes(player, "chat.commands.hotelNonExistent"); return; }

		if(!hotel.isOwner(player.getUniqueId())){
			Mes.mes(player, "chat.commands.youDoNotOwnThat"); return; }

		@SuppressWarnings("deprecation")
		OfflinePlayer helper = Bukkit.getServer().getOfflinePlayer(helperName);

		if(player.getUniqueId().equals(helper.getUniqueId())){
			Mes.mes(player, "chat.commands.helper.addYourself"); return; }

		try {
			hotel.addHelper(helper);
			Mes.mes(player, "chat.commands.helper.addSuccess", "%helper%", helper.getName());
		} catch (UserNonExistentException e) {
			Mes.mes(player, "chat.commands.userNonExistent");
		} catch (IOException e) {
			Mes.mes(player, "chat.commands.wrongData");
		} catch (UserAlreadyThereException e) {
			Mes.mes(player, "chat.commands.helper.alreadyHelper");
		} catch (HotelNonExistentException e) {
			Mes.mes(player, "chat.commands.hotelNonExistent");
		}
	}
	public static void cmdHelperRemove(CommandSender sender, String hotelName, String helperName){
		Player player = (Player) sender;
		Hotel hotel = new Hotel(hotelName, sender);

		if(!hotel.exists()){ Mes.mes(player, "chat.commands.hotelNonExistent"); return; }

		if(!hotel.isOwner(player.getUniqueId())){
			Mes.mes(player, "chat.commands.youDoNotOwnThat"); return; }

		@SuppressWarnings("deprecation")
		OfflinePlayer helper = Bukkit.getServer().getOfflinePlayer(helperName);

		try {
			hotel.removeHelper(helper.getUniqueId());
			Mes.mes(player, "chat.commands.helper.removeSuccess", "%helper%", helper.getName());
		} catch (FriendNotFoundException e) {
			Mes.mes(player, "chat.commands.helper.helperNotInList");
		} catch (IOException e) {
			Mes.mes(player, "chat.commands.wrongData");
		}
	}
	public static void cmdHelperList(CommandSender s, String hotelName, boolean skipOwnerCheck){
		Hotel hotel = new Hotel(hotelName, s);

		if(!hotel.exists()){ Mes.mes(s, "chat.commands.hotelNonExistent"); return; }

		if(!skipOwnerCheck && hotel.isOwner(((Player) s).getUniqueId())){ Mes.mes(s, "chat.commands.youDoNotOwnThat"); return; }

		List<UUID> ids = hotel.getHelpers();

		if(ids.isEmpty()) {
			Mes.mes(s, "chat.commands.helper.noHelpers");
			return;
		}

		Mes.mes(s, "chat.commands.helper.list.heading", "%hotel%", hotelName);

		for(UUID currentHelper : ids){
			OfflinePlayer helper = Bukkit.getServer().getOfflinePlayer(currentHelper);
			String helperName = helper.getName();
			Mes.mes(s, "chat.commands.helper.list.line", "%name%", helperName);
		}

		Mes.mes(s, "chat.commands.helper.list.footer");
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void cmdRenumber(CommandSender sender, String hotelName, String roomNum, String newNum){
		Hotel hotel = new Hotel(hotelName, sender);
		Room room = new Room(hotel, roomNum);

		if(!Mes.hasPerm(sender, "hotels.renumber.admin") && !hotel.isOwner(((Player) sender).getUniqueId())){
			Mes.mes(sender, "chat.commands.youDoNotOwnThat"); return; }

		try {
			room.renumber(String.valueOf(Integer.parseInt(newNum))); //This is to check the number is actually a number
			Mes.mes(sender, "chat.commands.renumber.success", "%oldnum%", roomNum, "%newnum%", newNum, "%hotel%", hotelName);
		} catch (NumberFormatException e) {
			Mes.mes(sender, "chat.commands.room.roomNumInvalid");
			e.printStackTrace();
		} catch (NumberTooLargeException e) {
			Mes.mes(sender, "chat.commands.renumber.newNumTooBig");
		} catch (HotelNonExistentException e) {
			Mes.mes(sender, "chat.commands.hotelNonExistent");
		} catch (RoomNonExistentException e) {
			Mes.mes(sender, "chat.commands.roomNonExistent");
		} catch (BlockNotSignException e) {
			Mes.mes(sender, "chat.commands.rent.invalidLocation");
		} catch (OutOfRegionException e) {
			Mes.mes(sender, "chat.sign.place.outOfRegion");
		} catch (EventCancelledException e) {
		} catch (FileNotFoundException e) {
			Mes.mes(sender, "chat.sign.use.fileNonExistent");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void cmdDeleteRoomUser(CommandSender sender, String hotelName, String roomNum){
		Player p = (Player) sender;
		Hotel hotel = new Hotel(hotelName, sender);
		if(!hotel.isOwner(p.getUniqueId())){ Mes.mes(p ,"chat.commands.youDoNotOwnThat"); return; }
		Room room = new Room(hotel, roomNum);
		if(!room.isFree()){ Mes.mes(p, "chat.commands.deleteRoom.roomRented"); return; }
		cmdDeleteRoom(sender, hotelName, roomNum);
	}
	public static void cmdDeleteRoom(CommandSender sender, String hotelName, String roomNum){
		cmdDeleteRoom(sender, new Room(hotelName, roomNum, sender));
	}
	public static void cmdDeleteRoom(CommandSender sender, Room room){
		Hotel hotel = room.getHotel();

		if(!hotel.exists()){ Mes.mes(sender ,"chat.commands.hotelNonExistent"); return; }

		if(!room.exists()){ Mes.mes(sender,"chat.commands.roomNonExistent"); return; }

		try {
			room.delete();
			Mes.mes(sender, "chat.commands.removeRoom.success");
		} catch (EventCancelledException e) {}
	}
	public static void cmdDeleteHotelUser(CommandSender sender, String hotelName){
		//Check if they are the hotel owner
		Player p = (Player) sender;
		Hotel hotel = new Hotel(hotelName, p);
		if(!hotel.isOwner(p.getUniqueId())){ Mes.mes(sender, "chat.commands.youDoNotOwnThat"); return; }
		if(hotel.hasRentedRooms()){ Mes.mes(sender, "chat.commands.deleteHotel.hasRentedRooms"); return; }
	}
	public static void cmdDeleteHotel(CommandSender sender, String hotelName){
		cmdDeleteHotel(sender, new Hotel(hotelName, sender));
	}
	public static void cmdDeleteHotel(CommandSender sender, Hotel hotel){
		try {
			hotel.delete();
			Mes.mes(sender, "chat.commands.removeSigns.success");
		} catch (EventCancelledException e) {
		} catch (HotelNonExistentException e) {
			Mes.mes(sender, "chat.commands.hotelNonExistent");
		}
	}
	public static void cmdRenameHotelUser(CommandSender sender, String hotelName, String newName){
		//Check if they are owners
		Player p = (Player) sender;
		Hotel hotel = new Hotel(hotelName, p);
		if(!hotel.exists()){ Mes.mes(sender, "chat.commands.hotelNonExistent"); return; }
		if(!hotel.isOwner(p.getUniqueId())){ Mes.mes(sender, "chat.commands.youDoNotOwnThat"); return; }
		cmdRenameHotel(sender, hotel, newName);
	}
	public static void cmdRenameHotel(CommandSender sender, String hotelName, String newName){
		cmdRenameHotel(sender, new Hotel(hotelName, sender), newName);
	}
	public static void cmdRenameHotel(CommandSender sender, Hotel hotel, String newName){
		if(!hotel.exists()){ Mes.mes(sender, "chat.commands.hotelNonExistent"); return; }
		try {
			hotel.rename(newName);
			Mes.mes(sender, "chat.commands.rename.success", "%hotel%", newName);
		} catch (EventCancelledException e) {
		} catch (HotelNonExistentException e) {
			Mes.mes(sender, "chat.commands.hotelNonExistent");
		}
	}
	public static void cmdRemovePlayer(CommandSender sender, String playerName, String hotelName, String roomNum){

		Room room = new Room(hotelName, roomNum, sender);

		System.out.println("ROOM world: " + room.getWorld());

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

		if(!room.isRenter(player.getUniqueId())){ Mes.mes(sender, "chat.commands.remove.playerNotRenter"); return; }

		try {
			room.unrent();
			Mes.mes(sender, "chat.commands.remove.success", "%player%", playerName, "%room%", roomNum, "%hotel%", hotelName);
		} catch (HotelNonExistentException e) {
			Mes.mes(sender, "chat.commands.hotelNonExistent");
		} catch (WorldNonExistentException e) {
			Mes.mes(sender, "chat.commands.worldNonExistent");
		} catch (RoomNonExistentException e) {
			Mes.mes(sender, "chat.commands.roomNonExistent");
		} catch (NotRentedException e) {
			Mes.mes(sender, "chat.commands.remove.noRenter");
		} catch (EventCancelledException e) {
			//Event was cancelled, don't do anything
		} catch (BlockNotSignException e) {
			Mes.mes(sender, "chat.commands.rent.invalidLocation");
			e.printStackTrace();
		} catch (IOException | DataException | WorldEditException e) {
			Mes.mes(sender, "chat.commands.somethingWentWrong");
			e.printStackTrace();
		}
	}
	public static void cmdRoomCreate(CommandSender sender, String hotelName){
		Hotel hotel = new Hotel(hotelName, sender);
		int roomNum = hotel.getNextNewRoom();
		if(roomNum != 0) cmdRoomCreate(sender, hotel, String.valueOf(roomNum));
		else Mes.mes(sender, "chat.commands.room.nextNewRoomFail");
	}
	public static void cmdRoomCreate(CommandSender sender, String hotelName, String roomNum){
		cmdRoomCreate(sender, new Hotel(hotelName, sender), roomNum);
	}
	public static void cmdRoomCreate(CommandSender sender, Hotel hotel, String roomNum){
		Player p = (Player) sender;
		if(!HTCreationMode.isInCreationMode(p.getUniqueId())){ Mes.mes(p, "chat.commands.creationMode.notAlreadyIn");  return; }

		if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return; }

		try{
			int number = Integer.parseInt(roomNum); //To check if room number is valid
			HTCreationMode.roomSetup(hotel.getName(), String.valueOf(number), p);
		} catch(NumberFormatException e){
			Mes.mes(p, "chat.commands.room.roomNumInvalid");
		}
	}
	public static void cmdHotelsListPlayer(CommandSender sender){
		cmdHotelsList(((Player) sender).getWorld(), sender);
	}
	public static void cmdHotelsList(CommandSender sender, String worldName){
		World w = Bukkit.getWorld(worldName);
		if(w != null)	HTCmdSurrogate.cmdHotelsList(w, sender);
		else Mes.mes(sender ,"chat.commands.worldNonExistent");
	}
	public static void cmdHotelsList(World w, CommandSender sender){
		Mes.mes(sender, "chat.commands.listHotels.heading");

		for(Hotel hotel : HotelsAPI.getHotelsInWorld(w)){
			String name = WordUtils.capitalizeFully(hotel.getName());

			String repeated = StringUtils.repeat(" ", 10-name.length());
			Mes.mes(sender, "chat.commands.listHotels.line",
					"%hotel%", name,
					"%total%", String.valueOf(hotel.getTotalRoomCount()),
					"%free%", String.valueOf(hotel.getFreeRoomCount()),
					"%space%", repeated
			);
		}
	}
	public static void cmdSetHome(CommandSender sender){
		Player p = (Player) sender;
		Location loc = p.getLocation();
		World w = p.getWorld();

		Hotel hotel = HotelsAPI.getHotelAtLocation(loc);

		if(hotel==null){ Mes.mes(p, "chat.commands.sethome.notInHotelRegion"); return; }

		Room room = HotelsAPI.getRoomAtLocation(loc, hotel.getName());

		if(room==null) { //They're just in a hotel region
			if(!Mes.hasPerm(p, "hotels.sethome.admin") && !HTWorldGuardManager.isOwner(p, hotel.getRegion().getId(), w)){
				Mes.mes(p, "chat.commands.youDoNotOwnThat"); return; }

			hotel.setHome(p.getLocation());

			if(hotel.saveHotelConfig())
				Mes.mes(p, "chat.commands.sethome.hotelHomeSet");

			return;
		}

		if(room.isNotSetup()){ Mes.mes(p, "chat.sign.use.nonExistentRoom"); return;}

		//If they are the renter set user home
		if(room.isRenter(p.getUniqueId())){
			room.setUserHome(p.getLocation());

			try {
				room.saveSignConfig();
				Mes.mes(p, "chat.commands.sethome.userHomeSet");
			} catch (IOException e) {
				Mes.mes(p, "chat.commands.somethingWentWrong");
				e.printStackTrace();
			}
		} //If they aren't renter but are hotel owner set default home
		else if(Mes.hasPerm(p, "hotels.sethome.admin") || HTWorldGuardManager.isOwner(p, hotel.getRegion().getId(), w)){
			room.setDefaultHome(p.getLocation());
			try {
				room.saveSignConfig();
				Mes.mes(p, "chat.commands.sethome.defaultHomeSet");
			} catch (IOException e) {
				Mes.mes(p, "chat.commands.somethingWentWrong");
				e.printStackTrace();
			}
		}
		else Mes.mes(p, "chat.commands.home.notRenterNoPermission");
	}
	public static void cmdHomeHotel(CommandSender sender, String hotelName) {
		Hotel hotel = new Hotel(hotelName, sender);

		Player p = (Player) sender;

		if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return; }
		Location loc = hotel.getHome();

		if(loc != null) p.teleport(loc);
		else Mes.mes(p, "chat.commands.home.noHomeSet");
	}
	public static void cmdHomeRoomUser(CommandSender sender, String hotelName, String roomNum) {
		Player p = (Player) sender;
		Room room = getHomeRoom(sender, hotelName, roomNum);
		if(!room.exists()){	Mes.mes(p, "chat.commands.roomNonExistent"); return; }
		if(!room.isRenter(p.getUniqueId())){ Mes.mes(p, "chat.commands.home.notRenterNoPermission"); return; }
		cmdHomeRoom(p, room);
	}
	private static Room getHomeRoom(CommandSender sender, String hotelName, String roomNum) {
		try {
			Integer.parseInt(roomNum);
		} catch (NumberFormatException e) {
			Mes.mes(sender, "chat.commands.room.roomNumInvalid");
		}

		return new Room(hotelName, roomNum, sender);
	}
	public static void cmdHomeRoom(CommandSender sender, String hotelName, String roomNum) {
		Player p = (Player) sender;
		Room room = getHomeRoom(p, hotelName, roomNum);
		if(!room.exists()){	Mes.mes(p, "chat.commands.roomNonExistent"); return; }
		cmdHomeRoom(p, room);
	}
	public static void cmdHomeRoom(Player p, Room room){
		//Check if there is a user home
		Location userLoc = room.getUserHome();
		Location defLoc = room.getDefaultHome();

		if(userLoc != null) p.teleport(userLoc);
		else if(defLoc != null) p.teleport(defLoc);

	}
	public static void cmdSellHotel(CommandSender sender, String hotelName, String buyerName, String priceString){
		Player p = (Player) sender;

		World world = p.getWorld();
		Hotel hotel = new Hotel(world, hotelName);

		if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return; }

		if(!hotel.isOwner(p.getUniqueId())){ Mes.mes(p, "chat.commands.youDoNotOwnThat"); return; }

		@SuppressWarnings("deprecation")
		Player buyer = Bukkit.getPlayerExact(buyerName);
		if(buyer == null || !buyer.isOnline()){	Mes.mes(p, "chat.commands.sellhotel.buyerNotOnline"); return; }

		int price;
		try{
			price = Integer.parseInt(priceString);
		}
		catch(Exception e){
			Mes.mes(p, "chat.commands.sellhotel.invalidPrice");
			return;
		}

		if(p.getUniqueId().equals(buyer.getUniqueId())){ Mes.mes(p, "chat.commands.sellhotel.selfSale"); return; }

		if(hotel.getBuyer() != null && buyer.getUniqueId().equals(hotel.getBuyer().getPlayer().getUniqueId())){
			Mes.mes(p, "chat.commands.sellhotel.sellingAlreadyAsked", "%buyer%", buyer.getName()); return; }

		hotel.setBuyer(buyer.getUniqueId(), price);

		Mes.mes(p, "chat.commands.sellhotel.sellingAsked", "%buyer%", buyer.getName());
		Mes.mes(buyer, "chat.commands.sellhotel.selling", "%seller%", p.getName(), "%hotel%", hotelName, "%price%", priceString);
	}
	public static void cmdBuyHotel(CommandSender sender, String hotelName){
		Player p = (Player) sender;
		Hotel hotel = new Hotel(hotelName, sender);

		if(!hotel.exists()){Mes.mes(p, "chat.commands.hotelNonExistent"); return; }

		HotelBuyer hb = hotel.getBuyer();
		if(hb == null){ Mes.mes(p, "chat.commands.buyhotel.notOnSale"); return; }
		Player buyer = hb.getPlayer();

		if(buyer == null || !buyer.hasPlayedBefore() || !buyer.getUniqueId().equals(p.getUniqueId())){ Mes.mes(p, "chat.commands.buyhotel.notOnSale"); return; }

		//They are the buyer the hotel owner has specified
		double balance = HotelsMain.economy.getBalance(p);
		double price = hb.getPrice();

		if((balance - price) < 0){ Mes.mes(p, "chat.commands.buyhotel.notEnoughMoney"); return; }

		//Player has enough money
		HotelsMain.economy.withdrawPlayer(p, price);
		String onlineOwner = "";

		HotelSaleEvent hse = new HotelSaleEvent(hb, price);
		Bukkit.getPluginManager().callEvent(hse);
		if(hse.isCancelled()) return;

		//In case they were modified by an event listener
		hb = hse.getHotelBuyer();
		price = hse.getRevenue();

		//Remove all hotel helpers
		hotel.getHelpers().forEach(helper -> {
			try {
				hotel.removeHelper(helper);
			} catch (Exception e) {}
		});

		for(UUID uuid : hotel.getOwners().getUniqueIds()){//Paying all owners
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			onlineOwner = op.getName();

			if(!op.isOnline()){
				HTMessageQueue.addMessage(MessageType.revenue, uuid,
						Mes.getString("chat.commands.sellHotel.success",
								"%hotel%", hotel.getName(),
								"%buyer%", p.getName(),
								"%price%", String.valueOf(price)
						));
				return;
			}

			Mes.mes(op.getPlayer(), "chat.commands.sellhotel.success",
					"%hotel%", hotel.getName(),
					"%buyer%", p.getName(),
					"%price%", String.valueOf(price)
			);

			hotel.removeOwner(op); //Removing old owner
			HotelsMain.economy.depositPlayer(op, price); //Paying old owner
		}

		hotel.addOwner(p);
		for(Room room : hotel.getRooms())
			room.setOwner(p);

		Mes.mes(p, "chat.commands.buyhotel.success",
				"%hotel%", hotel.getName(),
				"%seller%", onlineOwner,
				"%price%", String.valueOf(price)
		);

		hotel.removeBuyer();
		hotel.saveHotelConfig();
	}
	public static void cmdSellRoom(CommandSender sender, String hotelName, String roomNum, String buyerName, String priceString){
		Player p = (Player) sender;
		Hotel hotel = new Hotel(hotelName, p);

		if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return; }

		Room room = new Room(hotel, roomNum);

		if(!room.isRenter(p.getUniqueId())){ Mes.mes(p, "chat.commands.friend.notRenter"); return; }

		@SuppressWarnings("deprecation")
		Player buyer = Bukkit.getPlayerExact(buyerName);
		if(buyer == null || !buyer.isOnline()){	Mes.mes(p, "chat.commands.sellhotel.buyerNotOnline"); return; }

		int price;

		try{
			price = Integer.parseInt(priceString);
		}
		catch(NumberFormatException e){
			Mes.mes(p, "chat.commands.sellhotel.invalidPrice");
			return;
		}

		if(p.getUniqueId().equals(buyer.getUniqueId())){ Mes.mes(p, "chat.commands.sellhotel.selfSale"); return; }

		if(room.getBuyer() != null && buyer.getUniqueId().equals(room.getBuyer().getPlayer().getUniqueId())){
			Mes.mes(p, "chat.commands.sellroom.sellingAlreadyAsked", "%buyer%", buyer.getName()); return; }

		room.setBuyer(buyer.getUniqueId(), price);

		Mes.mes(p, "chat.commands.sellroom.sellingAsked", "%buyer%", buyer.getName());

		Mes.mes(buyer, "chat.commands.sellroom.selling",
				"%seller%", p.getName(),
				"%hotel%", hotelName,
				"%price%", priceString,
				"%room%", room.getNum()
		);
	}
	public static void cmdBuyRoom(CommandSender sender, String hotelName, String roomNum){
		Player p = (Player) sender;
		Hotel hotel = new Hotel(hotelName, p);

		if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return; }

		Room room = new Room(hotel, roomNum);

		if(!room.exists()){ Mes.mes(p, "chat.commands.roomNonExistent"); return; }

		RoomBuyer rb = room.getBuyer();
		if(rb == null){ Mes.mes(p, "chat.commands.buyroom.notOnSale"); return; }
		Player buyer = rb.getPlayer();

		if(buyer == null || !buyer.hasPlayedBefore() || !buyer.getUniqueId().equals(p.getUniqueId())){ Mes.mes(p, "chat.commands.buyroom.notOnSale"); return; }

		//They are the buyer the room owner has specified
		double balance = HotelsMain.economy.getBalance(p);
		double price = rb.getPrice();

		if((balance - price) < 0){ Mes.mes(p, "chat.commands.buyhotel.notEnoughMoney"); return; }

		OfflinePlayer owner = room.getRenter();

		//Player has enough money
		HotelsMain.economy.withdrawPlayer(p, price);

		OfflinePlayer op = room.getRenter();

		RoomSaleEvent rse = new RoomSaleEvent(rb, price);
		Bukkit.getPluginManager().callEvent(rse);
		if(rse.isCancelled()) return;
		//In case they were modified by an event listener
		rb = rse.getRoomBuyer();
		price = rse.getRevenue();

		String message = Mes.getString("chat.commands.sellroom.success",
				"%room%", room.getNum(),
				"%buyer%", p.getName(),
				"%price%", String.valueOf(price),
				"%hotel%", hotel.getName()
		);

		if(owner.isOnline()) owner.getPlayer().sendMessage(message);
		else HTMessageQueue.addMessage(MessageType.revenue, op.getUniqueId(),message);

		room.setRenter(p.getUniqueId()); //Removing old owner

		HotelsMain.economy.depositPlayer(op, price); //Paying old owner

		Mes.mes(p, "chat.commands.buyroom.success",
				"%room%", room.getNum(),
				"%seller%", owner.getName(),
				"%price%", String.valueOf(price),
				"%hotel%", hotel.getName()
		);

		room.removeBuyer();

		try {
			room.saveSignConfig();
		} catch (IOException e) {
			Mes.mes(p, "chat.commands.somethingWentWrong");
			e.printStackTrace();
		}
	}

	public static void cmdRoomReset(CommandSender sender, String hotelName, String roomName, boolean isAdmin){
		//Command to toggle resetting of rooms upon rent expiry
		Room room = new Room(hotelName, roomName, sender);
		if(!room.exists()){ Mes.mes(sender, "chat.commands.roomNonExistent"); return; }

		if(!isAdmin && !room.getHotel().getOwners().contains(((Player) sender).getUniqueId())){ Mes.mes(sender, "chat.commands.youDoNotOwnThat");  return; }

		try {
			if(room.toggleShouldReset())
				Mes.mes(sender, "chat.commands.roomreset.enable", "%hotel%", hotelName, "%room%", roomName);
			else
				Mes.mes(sender, "chat.commands.roomreset.disable", "%hotel%", hotelName, "%room%", roomName);
		} catch (DataException | IOException | WorldEditException e) {
			Mes.mes(sender, "chat.commands.somethingWentWrong");
			e.printStackTrace();
		}
		catch (RoomNotSetupException e) {
			Mes.mes(sender, "chat.commands.resetroom.notSetup");
		} catch (RoomSignInRoomException e) {
			Mes.mes(sender, "chat.sign.place.inRoomRegion");
		}
	}
	public static void cmdResetRoom(CommandSender sender, String hotelName, String roomName){
		Room room = new Room(hotelName, roomName, sender);

		if(!room.shouldReset()) { Mes.mes(sender, "chat.commands.resetroom.notSetup"); return; }

		try {
			room.reset();
			Mes.mes(sender, "chat.commands.resetroom.success",
					"%room%", room.getNum(),
					"%hotel%", hotelName
			);
		} catch (DataException | IOException | WorldEditException e) {
			Mes.mes(sender, "chat.commands.somethingWentWrong");
			e.printStackTrace();
		}
	}
	public static void cmdRoomsList(CommandSender sender, String hotelName){
		Hotel hotel = new Hotel(hotelName, sender);
		if(!hotel.exists()){ Mes.mes(sender, "chat.commands.hotelNonExistent"); return; }

		ArrayList<Room> rooms = hotel.getRooms();

		hotelName = WordUtils.capitalizeFully(hotel.getName());

		if(rooms.size() <= 0){ Mes.mes(sender, "chat.commands.roomslist.noRooms"); return; }

		Mes.mes(sender, "chat.commands.roomslist.heading", "%hotel%", hotelName);

		for(Room room : rooms){
			String roomNum = String.valueOf(room.getNum());

			String rep = StringUtils.repeat(" ", 10-roomNum.length());
			String state;

			if(room.doesSignFileExist()){
				if(room.isFree()) //Vacant
					state = ChatColor.GREEN+Mes.getStringNoPrefix("sign.vacant");
				else //Occupied
					state = ChatColor.BLUE+Mes.getStringNoPrefix("sign.occupied");
				Mes.mes(sender, "chat.commands.roomslist.line", "%room%", roomNum, "%state%", state, "%space%", rep);
			}
		}
	}
}