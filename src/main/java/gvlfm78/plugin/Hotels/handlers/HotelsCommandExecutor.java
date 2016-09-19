package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsAPI;
import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCommandExecutor {

	private HotelsMain plugin;
	public HotelsCommandExecutor(HotelsMain plugin){
		this.plugin = plugin;
	}

	SignManager SM = new SignManager(plugin);
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	WorldGuardManager WGM = new WorldGuardManager();
	HotelsConfigHandler HCH = new HotelsConfigHandler(plugin);
	HotelsFileFinder HFF = new HotelsFileFinder();

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
		HCM.checkFolder();
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
	public void cmdReload(CommandSender s,Plugin pluginstance){
		HCH.reloadConfigs();
		s.sendMessage(Mes.mes("chat.commands.reload.success"));
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
	public void cmdRoomListPlayer(Player p, String hotel, World w){
		if(WGM.hasRegion(w, "hotel-"+hotel))
			listRooms(hotel,w,p);
		else
			p.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));
	}
	public void cmdRoomListPlayer(CommandSender s, String hotel, World w){
		if(WGM.hasRegion(w, "hotel-"+hotel)){
			listRooms(hotel,w,s);
		}
		else
			s.sendMessage(Mes.mes("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
	}
	public void renumber(String hotelName, int oldNum, int newNum, World world, CommandSender sender){
		Hotel hotel = new Hotel(world, hotelName);
		Room room = new Room(hotel, oldNum);

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

	}
	public void removeRoom(String hotelName, String roomNum, World world, CommandSender sender){
		Room room = new Room(world, hotelName, roomNum);
		boolean wentWell = room.remove();
		if(wentWell)
			sender.sendMessage(Mes.mes("chat.commands.removeRoom.success"));
		else
			sender.sendMessage(Mes.mes("chat.commands.removeRoom.fail"));
	}
	public void removeRegions(String hotelName,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName)){
			WGM.removeRegion(world,"Hotel-"+hotelName);
			Collection<ProtectedRegion> regionlist = WorldGuardManager.getRegions(world);

			for(ProtectedRegion values : regionlist){
				if(values.getId().matches("hotel-"+hotelName+"-"+"[0-9]+")){
					ProtectedRegion goodregion = values;
					WorldGuardManager.getRM(world).removeRegion(goodregion.getId());
				}
			}
			WGM.saveRegions(world);
			sender.sendMessage(Mes.mes("chat.commands.removeRegions.success"));
		}
		else{
			if(sender instanceof Player)
				sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));
			else
				sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
		}
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
		Collection<ProtectedRegion> regions;
		List<World> worlds = Bukkit.getWorlds();
		Map<ProtectedRegion,World> hotels = new HashMap<ProtectedRegion,World>();
		List<ProtectedRegion> rooms = new ArrayList<ProtectedRegion>();
		@SuppressWarnings("deprecation")
		OfflinePlayer p = Bukkit.getOfflinePlayer(playername);
		if(p!=null&&p.hasPlayedBefore()){

			for(World w:worlds){//Looping through all the regions in all the worlds & separating rooms from hotels
				regions = WorldGuardManager.getRegions(w);

				if(regions.size()>0){
					for(ProtectedRegion r : regions){
						if(r.getId().toLowerCase().startsWith("hotel-")){ //If it's a hotel
							if(r.getId().toLowerCase().matches("^hotel-.+-.+")){//If it's a room
								if(r.getMembers().contains(WorldGuardManager.getWorldGuard().wrapOfflinePlayer(p)))//They are the renter
									rooms.add(r);//Add to hotels list
							}
							else{
								if(r.getOwners().contains(WorldGuardManager.getWorldGuard().wrapOfflinePlayer(p)))//They are the owner
									hotels.put(r,w);//Add to rooms list
							}
						}
					}
				}
			}
			//Printing out owned hotels first
			sender.sendMessage(Mes.mes("chat.commands.check.headerHotels").replaceAll("%player%", playername));
			if(hotels.size()>0){
				for(ProtectedRegion hr:hotels.keySet()){
					String[] rId = hr.getId().toLowerCase().split("-");
					String hotelName = rId[1];
					World world = hotels.get(hr);
					Hotel hotel = new Hotel(world,hotelName);
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
			sender.sendMessage(Mes.mes("chat.commands.check.headerRooms").replaceAll("%player%", playername));
			if(rooms.size()>0){
				for(ProtectedRegion r:rooms){//looping through rented rooms
					String[] rId = r.getId().toLowerCase().split("-");
					String hotelName = rId[1];
					String roomNum = rId[2];

					File file = HotelsConfigHandler.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					long expiryDate = config.getLong("Sign.expiryDate");

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
			else
				sender.sendMessage(Mes.mes("chat.commands.check.noRooms"));
		}
		else
			sender.sendMessage(Mes.mes("chat.commands.userNonExistant"));
	}
	public void listHotels(World w, CommandSender sender){
		sender.sendMessage(Mes.mes("chat.commands.listHotels.heading"));

		ArrayList<Hotel> hotels = HotelsAPI.getHotelsInWorld(w);

		for(Hotel hotel : hotels){
			String name = hotel.getName();

			name = WordUtils.capitalizeFully(name);

			int spaceAmount = 10-name.length();

			String space = " ";
			String repeated = StringUtils.repeat(space, spaceAmount);
			sender.sendMessage(Mes.mes("chat.commands.listHotels.line").replaceAll("%hotel%", name)
					.replaceAll("%total%", String.valueOf(hotel.getTotalRoomCount()))
					.replaceAll("%free%", String.valueOf(hotel.getFreeRoomCount()))
					.replaceAll("%space%", repeated)
					);
		}
	}
	public void listRooms(String hotelName, World w, CommandSender sender){
		Hotel hotel = new Hotel(w, hotelName);

		ArrayList<Room> rooms = hotel.getRooms();

		hotelName = WordUtils.capitalizeFully(hotelName);

		sender.sendMessage(Mes.mes("chat.commands.listRooms.heading").replaceAll("%hotel%", hotelName));

		if(rooms.size()<=0){
			sender.sendMessage(Mes.mes("chat.commands.listRooms.noRooms"));
			return;
		}

		for(Room room : rooms){
			String roomNum = String.valueOf(room.getNum());
			int spaceamount = 10-roomNum.length();
			String space = " ";
			String rep = StringUtils.repeat(space, spaceamount);
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
	public void removeSigns(String hotelName,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName)){
			ArrayList<String> fileslist = HFF.listFiles("plugins//Hotels//Signs");
			for(String x: fileslist){
				File file = HotelsConfigHandler.getFile("Signs"+File.separator+x);
				String receptionLoc = Mes.mesnopre("sign.reception");
				if(file.getName().matches("^"+receptionLoc+"-.+-.+")){
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					World worldsign = Bukkit.getWorld(config.getString("Reception.location.world").trim());
					int locx = config.getInt("Reception.location.x");
					int locy = config.getInt("Reception.location.y");
					int locz = config.getInt("Reception.location.z");
					Block b = worldsign.getBlockAt(locx,locy,locz);
					if(world==worldsign){
						if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
							Sign s = (Sign) b.getState();
							String Line1 = ChatColor.stripColor(s.getLine(0));
							String Line2 = ChatColor.stripColor(s.getLine(1));
							if(Line1.matches("Reception")){
								String[] Line1split = Line2.split(" ");
								String hotelname = Line1split[0];
								if(WGM.hasRegion(worldsign, "Hotel-"+hotelname)){
									if(WGM.getRegion(worldsign,"Hotel-"+hotelname).contains(locx, locy, locz)){
										b.setType(Material.AIR);
										file.delete();
									}
									else{
										b.setType(Material.AIR);
										file.delete();
									}
								}
								else{
									b.setType(Material.AIR);
									file.delete();
								}
							}
							else
								file.delete();
						}
						else
							file.delete();
					}
				}else{
					String[] parts = x.split("-");
					String chotelName = parts[0];
					if(chotelName.equalsIgnoreCase(hotelName)){
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						int locx = config.getInt("Sign.location.coords.x");
						int locy = config.getInt("Sign.location.coords.y");
						int locz = config.getInt("Sign.location.coords.z");
						Block signblock = world.getBlockAt(locx, locy, locz);
						signblock.setType(Material.AIR);
						signblock.breakNaturally();
						file.delete();
					}
				}
			}
			sender.sendMessage(Mes.mes("chat.commands.removeSigns.success"));
		}
	}
	public int nextNewRoom(World w, String hotel){
		if(WGM.hasRegion(w, "Hotel-"+hotel)){
			Collection <ProtectedRegion> regions = WorldGuardManager.getRegions(w);
			for(int i=0; i<regions.size(); i++){
				if(!WGM.hasRegion(w, "Hotel-"+hotel+"-"+(i+1)))
					return i+1;
			}
		}
		return 0;
	}
}