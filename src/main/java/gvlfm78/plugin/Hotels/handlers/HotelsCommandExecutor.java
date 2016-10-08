package kernitus.plugin.Hotels.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsAPI;
import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCommandExecutor {

	private SignManager SM;
	private HotelsCreationMode HCM;
	private WorldGuardManager WGM;
	
	public HotelsCommandExecutor(HotelsMain plugin){
		
		SM = new SignManager(plugin);
		HCM = new HotelsCreationMode(plugin);
		WGM = new WorldGuardManager();
	}

	public void cmdCreate(Player p,String hotelName){//Hotel creation command
		UUID playerUUID = p.getUniqueId();
		if(HCM.isInCreationMode(playerUUID))
			HCM.hotelSetup(hotelName, p);
		else
			p.sendMessage(Mes.mes("chat.commands.create.fail"));
	}
	public void cmdCommandsAll(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.commands.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.help"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.creationMode"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.create"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.room"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.renum"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.rename"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.sethome"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.home"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.check"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.list"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.rlist"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.friend"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.friendList"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.sellh"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.buyh"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.reload"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.remove"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.delete"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.delr"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.footer"));
	}
	public void cmdCommandsOnly(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.commands.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.commands.help"));

		if(Mes.hasPerm(s,"hotels.createmode"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.creationMode"));

		if(Mes.hasPerm(s,"hotels.create")){
			s.sendMessage(Mes.mesnopre("chat.commands.commands.create"));
			s.sendMessage(Mes.mesnopre("chat.commands.commands.room"));}

		if(Mes.hasPerm(s,"hotels.renumber"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.renum"));
		if(Mes.hasPerm(s,"hotels.rename"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.rename"));

		if(Mes.hasPerm(s, "hotels.sethome"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.sethome"));
		if(Mes.hasPerm(s, "hotels.home"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.home"));

		if(Mes.hasPerm(s,"hotels.check"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.check"));
		if(Mes.hasPerm(s,"hotels.list.hotels"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.list"));
		if(Mes.hasPerm(s,"hotels.list.rooms"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.rlist"));

		if(Mes.hasPerm(s,"hotels.friend")){
			s.sendMessage(Mes.mesnopre("chat.commands.commands.friend"));
			s.sendMessage(Mes.mesnopre("chat.commands.commands.friendList"));}

		if(Mes.hasPerm(s, "hotels.sell.room")){
			s.sendMessage(Mes.mesnopre("chat.commands.commands.sellh"));
			s.sendMessage(Mes.mesnopre("chat.commands.commands.buyh"));
		}

		if(Mes.hasPerm(s,"hotels.reload"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.reload"));

		if(Mes.hasPerm(s,"hotels.remove"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.remove"));
		if(Mes.hasPerm(s,"hotels.delete.rooms"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.delr"));
		if(Mes.hasPerm(s,"hotels.delete"))
			s.sendMessage(Mes.mesnopre("chat.commands.commands.delete"));

		s.sendMessage(Mes.mesnopre("chat.commands.commands.footer"));
	}
	public void cmdHelp1(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.help.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page1.1"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page1.2"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page1.3"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page1.4"));
		s.sendMessage((Mes.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "2"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp2(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.help.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page2.1"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page2.2"));
		s.sendMessage((Mes.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "3"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp3(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.help.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page3.1"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page3.2"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page3.3"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page3.4"));
		s.sendMessage((Mes.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "4"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp4(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.help.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.1"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.2"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.3"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.4"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.5"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.6"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page4.7"));
		s.sendMessage((Mes.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "5"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp5(CommandSender s){
		s.sendMessage(Mes.mesnopre("chat.commands.help.header"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.1"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.2"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.3"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.4"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.5"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.6"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.7"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.page5.8"));
		s.sendMessage((Mes.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "1"));
		s.sendMessage(Mes.mesnopre("chat.commands.help.footer"));
	}

	public void cmdCreateModeEnter(Player p){
		if(!HCM.isInCreationMode(p.getUniqueId())){
			HCM.saveInventory(p);
			HCM.giveItems(p);
			p.sendMessage(Mes.mes("chat.commands.creationMode.enter"));
		}
		else
			p.sendMessage(Mes.mes("chat.commands.creationMode.alreadyIn"));
	}
	public void cmdCreateModeExit(Player p){
		if(HCM.isInCreationMode(p.getUniqueId())){
			p.sendMessage(Mes.mes("chat.commands.creationMode.exit"));
			HCM.loadInventory(p);
		}
		else
			p.sendMessage(Mes.mes("chat.commands.creationMode.notAlreadyIn"));
	}
	public void cmdCreateModeReset(Player p){
		HCM.resetInventoryFiles(p);
		p.sendMessage(Mes.mes("chat.commands.creationMode.reset"));
	}
	public void cmdRent(CommandSender sender ,String hotelName, String roomNum){

		if(!(sender instanceof Player))
			sender.sendMessage(Mes.mes("chat.commands.rent.consoleRejected"));	
		else{
			Player p = (Player) sender;
			World world = p.getWorld();
			Room room = new Room(world,hotelName,roomNum);
			room.rent(p);
		}	
	}
	public void cmdFriendAdd(Player player, String hotelName, String roomNum, String friendName){

		Room room = new Room(player.getWorld(), hotelName, roomNum);

		if(!room.isRenter(player.getUniqueId())){
			player.sendMessage(Mes.mes("chat.commands.friend.notRenter")); return; }

		@SuppressWarnings("deprecation")
		OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);

		if(player.getUniqueId().equals(friend.getUniqueId())){
			player.sendMessage(Mes.mes("chat.commands.friend.addYourself")); return; }

		switch(room.addFriend(friend)){
		case 1: player.sendMessage(Mes.mes("chat.commands.friend.wrongData")); break;
		case 2: player.sendMessage(Mes.mes("chat.commands.friend.noRenter")); break;
		case 3: player.sendMessage(Mes.mes("chat.commands.friend.nonExistant")); break;
		default: player.sendMessage(Mes.mes("chat.commands.friend.addSuccess").replaceAll("%friend%", friend.getName()));
		}

	}
	public void cmdFriendRemove(Player player, String hotelName, String roomNum, String friendName){
		Room room = new Room(player.getWorld(), hotelName, roomNum);

		if(!room.isRenter(player.getUniqueId())){
			player.sendMessage(Mes.mes("chat.commands.friend.notRenter")); return; }

		@SuppressWarnings("deprecation")
		OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);

		switch(room.removeFriend(friend)){
		case 1: player.sendMessage(Mes.mes("chat.commands.friend.wrongData")); break;
		case 2: player.sendMessage(Mes.mes("chat.commands.friend.noRenter")); break;
		case 3: player.sendMessage(Mes.mes("chat.commands.friend.friendNotInList")); break;
		default: player.sendMessage(Mes.mes("chat.commands.friend.removeSuccess").replaceAll("%friend%", friend.getName())); break;
		}
	}
	public void cmdFriendList(CommandSender s, String hotelName, String roomNum){
		Room room = new Room(hotelName, roomNum);

		if(!room.doesSignFileExist()){
			s.sendMessage(Mes.mes("chat.commands.friend.wrongData")); return; }

		if(room.isFree()){
			s.sendMessage(Mes.mes("chat.commands.friend.noRenter")); return; }


		List<String> stringList = room.getFriendsList();

		if(stringList.isEmpty()){
			s.sendMessage(Mes.mes("chat.commands.friend.noFriends"));	
		}

		s.sendMessage(Mes.mes("chat.commands.friend.list.heading").replaceAll("%room%", roomNum).replaceAll("%hotel%", hotelName));

		for(String currentFriend : stringList){
			OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentFriend));
			String friendName = friend.getName();
			s.sendMessage(Mes.mes("chat.commands.friend.list.line").replaceAll("%name%", friendName));
		}
		s.sendMessage(Mes.mes("chat.commands.friend.list.footer"));
	}
	public void cmdRoomListPlayer(Player p, String hotelName, World w){
		cmdRoomListPlayer(p, hotelName, w);
	}
	public void cmdRoomListPlayer(CommandSender s, String hotelName, World w){
		Hotel hotel = new Hotel(w, hotelName);
		if(hotel.exists())
			listRooms(hotel,s);
		else
			s.sendMessage(Mes.mes("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
	}
	public void renumber(Room room, String newNum, CommandSender sender){
		int oldNum = room.getNum();
		Hotel hotel = room.getHotel();
		String hotelName = hotel.getName();

		if(!(sender instanceof Player)){
			sender.sendMessage(Mes.mes("chat.commands.renumber.fail").replaceAll("%oldnum%", String.valueOf(oldNum)));
			return;
		}

		Player player = (Player) sender;

		if(!WGM.isOwner(player, "hotel-"+hotelName, player.getWorld()) || Mes.hasPerm(player, "hotels.renumber.admin")){
			player.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
			return;
		}

		int errorLevel = room.renumber(newNum);
		switch(errorLevel){
		case 1: player.sendMessage(Mes.mes("chat.commands.renumber.newNumTooBig")); break;
		case 2: player.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); break;
		case 3: player.sendMessage(Mes.mes("chat.commands.roomNonExistant")); break;
		case 4: player.sendMessage(Mes.mes("chat.use.fileNonExistant")); break;
		case 5: player.sendMessage(Mes.mes("chat.sign.place")); break;//Not a sign
		case 6: player.sendMessage(Mes.mes("chat.sign.place.outOfRegion")); break;
		default: player.sendMessage(Mes.mes("chat.commands.renumber.success").replaceAll("%oldnum%", String.valueOf(oldNum)).replaceAll("%newnum%", String.valueOf(newNum)).replaceAll("%hotel%", hotel.getName()));
		}
	}

	public void renameHotel(String oldName, String newName, World world, CommandSender sender){
		Hotel hotel = new Hotel(world, oldName);
		if(!hotel.exists()){ sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); return; }
		hotel.rename(newName);
		sender.sendMessage(Mes.mes("chat.commands.rename.success").replaceAll("%hotel%" , newName));
	}
	public void removeRoom(String hotelName, String roomNum, World world, CommandSender sender){
		Room room = new Room(world, hotelName, roomNum);
		if(room.remove())
			sender.sendMessage(Mes.mes("chat.commands.removeRoom.success"));
		else
			sender.sendMessage(Mes.mes("chat.commands.removeRoom.fail"));
	}
	public void removePlayer(World world, String hotelName, String roomNum, String toRemovePlayer, CommandSender sender){

		Room room = new Room(world, hotelName, roomNum);

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(toRemovePlayer);

		if(!room.isRenter(player.getUniqueId())){
			sender.sendMessage(Mes.mes("chat.commands.remove.playerNotRenter")); return; }

		switch(room.removePlayer(player)){
		case 1: sender.sendMessage(Mes.mes("chat.commands.worldNonExistant")); break;
		case 2: sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); break;
		case 3: sender.sendMessage(Mes.mes("chat.commands.roomNonExistant")); break;
		case 4: sender.sendMessage(Mes.mes("chat.commands.userNonExistant")); break;
		case 5: sender.sendMessage(Mes.mes("chat.commands.remove.noRenter")); break;
		default: sender.sendMessage(Mes.mes("chat.commands.remove.success").replaceAll("%player%", toRemovePlayer).replaceAll("%room%", roomNum).replaceAll("%hotel%", hotelName)); break;
		}
	}
	public void check(String playername, CommandSender sender){

		@SuppressWarnings("deprecation")
		OfflinePlayer p = Bukkit.getOfflinePlayer(playername);
		if(p!=null && p.hasPlayedBefore()){ sender.sendMessage(Mes.mes("chat.commands.userNonExistant")); return; }

		//Printing out owned hotels first
		ArrayList<Hotel> hotels = HotelsAPI.getHotelsOwnedBy(p.getUniqueId());

		sender.sendMessage(Mes.mes("chat.commands.check.headerHotels").replaceAll("%player%", playername));
		if(hotels.size()>0){
			for(Hotel hotel : hotels){
				String hotelName = hotel.getName();
				int total = hotel.getTotalRoomCount();
				int free = hotel.getFreeRoomCount();
				sender.sendMessage(Mes.mes("chat.commands.check.lineHotels")
						.replaceAll("%player%", playername)
						.replaceAll("%hotel%", hotelName)
						.replaceAll("%total%", String.valueOf(total))
						.replaceAll("%free%", String.valueOf(free))
						);
			}
		}
		else
			sender.sendMessage(Mes.mes("chat.commands.check.noHotels"));

		//And printing out rented rooms
		ArrayList<Room> rooms = HotelsAPI.getRoomsRentedBy(p.getUniqueId());

		sender.sendMessage(Mes.mes("chat.commands.check.headerRooms").replaceAll("%player%", playername));
		if(rooms.size()<0){ sender.sendMessage(Mes.mes("chat.commands.check.noRooms")); return; }

		for(Room room : rooms){//looping through rented rooms
			Hotel hotel = room.getHotel();
			String hotelName = hotel.getName();
			String roomNum = String.valueOf(room.getNum());

			long expiryDate = room.getExpiryMinute();

			if(expiryDate>0){
				long currentmins = System.currentTimeMillis()/1000/60;
				String timeleft = SM.TimeFormatter(expiryDate-currentmins);
				sender.sendMessage(Mes.mes("chat.commands.check.lineRooms")
						.replaceAll("%hotel%", hotelName).replaceAll("%room%", roomNum).replaceAll("%timeleft%", String.valueOf(timeleft)));
			}
			else//Room is permanently rented
				sender.sendMessage(Mes.mes("chat.commands.check.lineRooms")
						.replaceAll("%hotel%", hotelName).replaceAll("%room%", roomNum).replaceAll("%timeleft%", Mes.mesnopre("sign.permanent")));
		}
	}
	public void listHotels(World w, CommandSender sender){
		sender.sendMessage(Mes.mes("chat.commands.listHotels.heading"));

		ArrayList<Hotel> hotels = HotelsAPI.getHotelsInWorld(w);

		for(Hotel hotel : hotels){
			String name = WordUtils.capitalizeFully(hotel.getName());

			String repeated = StringUtils.repeat(" ", 10-name.length());
			sender.sendMessage(Mes.mes("chat.commands.listHotels.line").replaceAll("%hotel%", name)
					.replaceAll("%total%", String.valueOf(hotel.getTotalRoomCount()))
					.replaceAll("%free%", String.valueOf(hotel.getFreeRoomCount()))
					.replaceAll("%space%", repeated)
					);
		}
	}
	public void listRooms(Hotel hotel, CommandSender sender){

		ArrayList<Room> rooms = hotel.getRooms();

		String hotelName = WordUtils.capitalizeFully(hotel.getName());

		sender.sendMessage(Mes.mes("chat.commands.listRooms.heading").replaceAll("%hotel%", hotelName));

		if(rooms.size()<=0){ sender.sendMessage(Mes.mes("chat.commands.listRooms.noRooms")); return; }

		for(Room room : rooms){
			String roomNum = String.valueOf(room.getNum());

			String rep = StringUtils.repeat(" ", 10-roomNum.length());
			String state = "";
			
			if(room.doesSignFileExist()){
				if(room.isFree()) //Vacant
					state = ChatColor.GREEN+Mes.mesnopre("sign.vacant");
				else //Occupied
					state = ChatColor.BLUE+Mes.mesnopre("sign.occupied");
				sender.sendMessage(Mes.mes("chat.commands.listRooms.line")
						.replaceAll("%room%", roomNum)
						.replaceAll("%state%", state)
						.replaceAll("%space%", rep)
						);
			}
		}
	}
}