package kernitus.plugin.Hotels.handlers;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.DataException;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsAPI;
import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.events.HotelSaleEvent;
import kernitus.plugin.Hotels.events.RoomSaleEvent;
import kernitus.plugin.Hotels.exceptions.EventCancelledException;
import kernitus.plugin.Hotels.exceptions.HotelNonExistentException;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;
import kernitus.plugin.Hotels.trade.HotelBuyer;
import kernitus.plugin.Hotels.trade.RoomBuyer;

public class HotelsCommandHandler implements CommandExecutor {



	private HotelsMain plugin;
	private HotelsCommandExecutor HCE;

	public HotelsCommandHandler(HotelsMain plugin){
		this.plugin = plugin;

		HCE = new HotelsCommandExecutor();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getLabel().equalsIgnoreCase("Hotels")){
			boolean isPlayer = sender instanceof Player;
			int length = args.length;
			if(length<1){//Fallback screen
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&4==========Hotels==========")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&2" + plugin.getDescription().getName() + " plugin by kernitus")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&2" + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion())));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&4Type &3/hotels help &4for help with creating a hotel")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&4Type &3/hotels commands &4for help with the commands")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&4==========================")));
				return false;
			}
			//Command checks
			if(args[0].equalsIgnoreCase("commands")){//Commands list
				if(plugin.getConfig().getBoolean("settings.commands.onlyDisplayAllowed"))
					HCE.cmdCommandsOnly(sender);
				else
					HCE.cmdCommandsAll(sender);
			}
			else if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")){//Create command
				if(!isPlayer){ Mes.mes(sender, "chat.commands.create.consoleRejected"); return false; }
				Player p = (Player) sender;
				if(length<2){ Mes.mes(p ,"chat.commands.create.noName"); return false; }
				if(Mes.hasPerm(sender, "hotels.create"))
					HCE.cmdCreate(p, args[1]);
				else
					Mes.mes(p ,"chat.noPermission");		
			}
			else if(args[0].equalsIgnoreCase("help")){//Help pages
				if(!Mes.hasPerm(sender, "hotels.create")){ Mes.mes(sender, "chat.noPermission"); return false; }
				if(length<2){ HCE.cmdHelp1(sender); return false; } //If they didn't specify a help page show first
				switch(args[1]){
				case "1": HCE.cmdHelp1(sender); break;
				case "2": HCE.cmdHelp2(sender); break;
				case "3": HCE.cmdHelp3(sender); break;
				case "4": HCE.cmdHelp4(sender); break;
				case "5": HCE.cmdHelp5(sender); break;
				default: HCE.cmdHelp1(sender); //if help page doesn't exist show first
				}
			}
			else if(args[0].equalsIgnoreCase("createmode") || args[0].equalsIgnoreCase("cm")){
				if(!isPlayer){ Mes.mes(sender, "chat.commands.creationMode.consoleRejected"); return false; }
				Player p = (Player) sender;
				if(length<2){ Mes.mes(p ,"chat.commands.creationMode.noarg"); return false; }
				if(!Mes.hasPerm(p, "hotels.createmode")){ Mes.mes(p ,"chat.noPermission"); return false; }

				switch(args[1].toLowerCase()){
				case "enter": HCE.cmdCreateModeEnter(p); break;
				case "exit": HCE.cmdCreateModeExit(p); break;
				case "reset": HCE.cmdCreateModeReset(p); break;
				default: Mes.mes(p ,"chat.commands.creationMode.noarg");
				}
			}

			else if(args[0].equalsIgnoreCase("check")){
				if(isPlayer){
					if(length==1)
						HCE.check(sender.getName(), sender);
					else if(length>=2){
						if(args[1].equalsIgnoreCase(sender.getName()))					
							HCE.check(args[1], sender);
						else if(sender.hasPermission("hotels.check.others"))					
							HCE.check(args[1], sender);
						else
							Mes.mes((Player) sender ,"chat.noPermission");
					}
				} else if(length>=2)					
					HCE.check(args[1], sender);
				else
					Mes.mes(sender ,"chat.commands.noPlayer");
			}

			else if(args[0].equalsIgnoreCase("reload")){
				if(!Mes.hasPerm(sender, "hotels.reload")){ Mes.mes(sender ,"chat.noPermission"); return false; }
				HotelsConfigHandler.reloadConfigs();
				Mes.mes(sender ,"chat.commands.reload.success");	
			}
			else if(args[0].equalsIgnoreCase("rent")){
				if(!isPlayer){ Mes.mes(sender ,"chat.commands.rent.consoleRejected"); return false; }
				Player p = (Player) sender;
				if(!Mes.hasPerm(p, "hotels.rent")){ Mes.mes(p ,"chat.noPermission"); return false; }
				if(length<3)
					Mes.mes(p ,"chat.commands.rent.usage");
				else
					HCE.cmdRent(sender, args[1], args[2]);
			}
			else if(args[0].equalsIgnoreCase("friend") || args[0].equalsIgnoreCase("f")){
				if(!isPlayer){ Mes.mes(sender ,"chat.commands.friend.consoleRejected"); return false; }
				Player p = (Player) sender;
				if(!Mes.hasPerm(p, "hotels.friend")){ Mes.mes(p ,"chat.noPermission"); return false; }
				if(length<2){ Mes.mes(p ,"chat.commands.friend.usage"); return false; }

				switch(args[1].toLowerCase()){


				case "add":
					if(length>4)
						HCE.cmdFriendAdd(p,args[2],args[3],args[4]);
					else
						Mes.mes(p ,"chat.commands.friend.usage");
					break;

				case "remove":
					if(length>4)
						HCE.cmdFriendRemove(p, args[2],args[3],args[4]);
					else
						Mes.mes(p ,"chat.commands.friend.usage");
					break;

				case "list":
					if(length<4){ Mes.mes(p ,"chat.commands.friend.usage");  return false; }
					Room room = new Room(args[2],args[3]);
					if(!(isPlayer) || (isPlayer && room.isRenter(((Player) sender).getUniqueId())))
						HCE.cmdFriendList(sender,args[2],args[3]);
					else
						Mes.mes(p ,"chat.commands.friend.notRenter");			
					break;

				default:
					Mes.mes(p ,"chat.commands.friend.usage");	
				}

			}
			else if(args[0].equalsIgnoreCase("roomlist") || args[0].equalsIgnoreCase("rlist")){
				if(!Mes.hasPerm(sender, "hotels.list.rooms")){ Mes.mes(sender ,"chat.noPermission"); return false; }
				if(length<2){ Mes.mes(sender ,"chat.commands.listRooms.usage"); return false; }
				if(isPlayer){//Is player
					Player p = (Player) sender;
					World w = p.getWorld();
					HCE.cmdRoomListPlayer(p, args[1], w);
				}
				else{//Not a player issuing the command
					Hotel hotel = new Hotel(args[1]);

					if(!hotel.exists()){ Mes.mes(sender ,"chat.commands.hotelNonExistent"); return false; }

					World w = hotel.getWorld();

					HCE.cmdRoomListPlayer(sender, args[1], w);
				}	
			}
			else if(args[0].equalsIgnoreCase("hotelslist") || args[0].equalsIgnoreCase("hlist") || args[0].equalsIgnoreCase("list")){
				if(!Mes.hasPerm(sender, "hotels.list.hotels")){ Mes.mes(sender ,"chat.noPermission"); return false; }

				World w = null;

				if(length<2 && isPlayer)
					w = ((Player) sender).getWorld();
				else if(length>1)
					w = Bukkit.getWorld(args[1]);
				else
					Mes.mes(sender ,"chat.commands.noWorld");

				if(w!=null)
					HCE.listHotels(w,sender);
				else
					Mes.mes(sender ,"chat.commands.worldNonExistent");				
			}
			else if(args[0].equalsIgnoreCase("deleteroom") || args[0].equalsIgnoreCase("delr")){
				if(!Mes.hasPerm(sender, "hotels.delete.rooms")){ Mes.mes(sender ,"chat.noPermission"); return false; }

				if(length<3){ Mes.mes(sender ,"chat.commands.deleteRoom.usage"); return false; }

				World world = (isPlayer && length!=4) ? ((Player) sender).getWorld() : Bukkit.getWorld(args[3]);

				String hotelName = args[1];
				String roomNum = args[2];
				Hotel hotel = new Hotel(world, hotelName);

				if(!hotel.exists()){ Mes.mes(sender ,"chat.commands.hotelNonExistent"); return false; }

				if(!Mes.hasPerm(sender, "hotels.delete.room.admin") && isPlayer){
					Player p = (Player) sender; 
					if(!hotel.isOwner(p.getUniqueId())){ Mes.mes(p ,"chat.commands.youDoNotOwnThat"); return false; }
				}

				Room room = new Room(hotel, roomNum);

				if(!room.exists()){ Mes.mes(sender,"chat.commands.roomNonExistent"); return false; }

				if(Mes.hasPerm(sender, "hotels.delete.rooms.admin") || room.isFree())
					HCE.removeRoom(args[1], roomNum, world, sender);
				else
					Mes.mes(sender, "chat.commands.deleteRoom.roomRented");	
			}
			else if(args[0].equalsIgnoreCase("rename") || args[0].equalsIgnoreCase("ren")){
				if(!Mes.hasPerm(sender, "hotels.rename")){ Mes.mes(sender ,"chat.noPermission"); return false; }

				if(length<3){ Mes.mes(sender, "chat.commands.rename.usage"); return false; }

				World world;

				if(isPlayer && length==3)
					world = ((Player) sender).getWorld();
				else if(length > 3)
					world = Bukkit.getWorld(args[3]);
				else{ Mes.mes(sender, "chat.commands.noWorld"); return false; }

				if(world==null){ Mes.mes(sender, "chat.commands.worldNonExistent"); return false; }

				if(!Mes.hasPerm(sender, "hotels.rename.admin") && 
						!(isPlayer && WorldGuardManager.isOwner((Player) sender, WorldGuardManager.getHotelRegion(world, args[1])))){
					Mes.mes(sender, "chat.commands.youDoNotOwnThat"); return false; }

				Hotel hotel = new Hotel(world, args[1]);

				try {
					hotel.rename(args[2]);
					sender.sendMessage(Mes.getString("chat.commands.rename.success").replaceAll("%hotel%" , args[2]));
				} catch (EventCancelledException e) {
				} catch (HotelNonExistentException e) {
					Mes.mes(sender, "chat.commands.hotelNonExistent");
				}
			}
			else if(args[0].equalsIgnoreCase("renumber") || args[0].equalsIgnoreCase("renum")){
				if(!Mes.hasPerm(sender, "hotels.renumber")){ Mes.mes(sender, "chat.noPermission"); return false;}

				if(length<4){ Mes.mes(sender, "chat.commands.renumber.usage"); return false; }

				World world;
				if(length>4)
					world = Bukkit.getWorld(args[4]);
				else if(isPlayer)
					world = ((Player) sender).getWorld();
				else{ Mes.mes(sender, "chat.commands.renumber.usage"); return false; }

				if(world==null){ Mes.mes(sender, "chat.commands.worldNonExistent"); return false; }

				Room room = new Room(world, args[1], args[2]);
				HCE.renumber(room,args[3],sender);
			}

			else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")){
				if(!Mes.hasPerm(sender, "hotels.delete")){ Mes.mes(sender, "chat.noPermission"); return false;	}

				if(length < 2){ Mes.mes(sender, "chat.commands.noHotel"); return false; }

				Hotel hotel;

				if(isPlayer){//Get world from player
					Player p = (Player) sender;
					hotel = new Hotel(p.getWorld(), args[1]);
				} else 
					hotel = new Hotel(args[1]);

				if(!hotel.exists()){ Mes.mes(sender, "chat.commands.hotelNonExistent"); return false; }

				if(isPlayer && !(hotel.isOwner(((Player) sender).getUniqueId()) || Mes.hasPerm(sender, "hotels.delete.admin") ) ){
					Mes.mes(sender, "chat.commands.youDoNotOwnThat"); return false; }

				if(!Mes.hasPerm(sender, "hotels.delete.admin") && hotel.hasRentedRooms()){
					Mes.mes(sender, "chat.commands.deleteHotel.hasRentedRooms"); return false; }

				try {
					hotel.delete();
					Mes.mes(sender, "chat.commands.removeSigns.success");
				} catch (EventCancelledException e) {
					Mes.debug("Something");
				} catch (HotelNonExistentException e) {
					Mes.mes(sender, "chat.commands.hotelNonExistent");
				}
			}

			else if(args[0].equalsIgnoreCase("remove")){
				if(!Mes.hasPerm(sender, "hotels.remove")){ Mes.mes(sender, "chat.noPermission"); return false; }

				if(length<4){ Mes.mes(sender, "chat.commands.remove.usage"); return false; }

				if(length>4)
					HCE.removePlayer(Bukkit.getWorld(args[4]), args[2], args[3], args[1], sender);
				else if(isPlayer)
					HCE.removePlayer(((Player) sender).getWorld(), args[2], args[3], args[1], sender);
				else
					Mes.mes(sender, "chat.commands.noWorld");	
			}

			else if(args[0].equalsIgnoreCase("room") && isPlayer){
				Player p = (Player) sender;
				if(length<2){ Mes.mes(p, "chat.commands.room.usage"); return false; }

				if(!Mes.hasPerm(p, "hotels.sign.create")){ Mes.mes(p, "chat.noPermission"); return false; }

				if(!HotelsCreationMode.isInCreationMode(p.getUniqueId())){ Mes.mes(p, "chat.commands.creationMode.notAlreadyIn");  return false; }

				Hotel hotel = new Hotel(p.getWorld(), args[1]);
				String hotelName = hotel.getName();

				if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return false; }

				//Hotel exists, therefore proceed
				if(length>2){
					try{
						int roomNum = Integer.parseInt(args[2]);
						HotelsCreationMode.roomSetup(hotelName, roomNum, p);
					} catch(NumberFormatException e){
						Mes.mes(p, "chat.commands.room.roomNumInvalid");
					}
				} else { //Player did not specify room number
					int roomNum = hotel.getNextNewRoom();
					if(roomNum!=0)
						HotelsCreationMode.roomSetup(hotelName, roomNum, p);
					else Mes.mes(p, "chat.commands.room.nextNewRoomFail");
				}
			}

			else if(args[0].equalsIgnoreCase("sethome")){
				if(!isPlayer){ Mes.mes(sender, "chat.commands.sethome.consoleRejected"); return false;	}

				Player p = (Player) sender;

				if(!Mes.hasPerm(p, "hotels.sethome")){ Mes.mes(p, "chat.noPermission"); return false; }

				Location loc = p.getLocation();
				World w = p.getWorld();

				Hotel hotel = HotelsAPI.getHotelAtLocation(loc);

				if(hotel==null){ Mes.mes(p, "chat.commands.sethome.notInHotelRegion"); return false; }

				Room room = HotelsAPI.getRoomAtLocation(loc, hotel.getName());

				if(room!=null){//They're in a room region

					if(room.isNotSetup()){ Mes.mes(p, "chat.sign.use.nonExistentRoom"); return false;}

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
					else if(Mes.hasPerm(p, "hotels.sethome.admin") || WorldGuardManager.isOwner(p, hotel.getRegion().getId(), w)){
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
				else { //They're just in a hotel region
					if(!Mes.hasPerm(p, "hotels.sethome.admin") && !WorldGuardManager.isOwner(p, hotel.getRegion().getId(), w)){
						Mes.mes(p, "chat.commands.youDoNotOwnThat"); return false; }

					hotel.setHome(p.getLocation());

					if(hotel.saveHotelConfig())
						Mes.mes(p, "chat.commands.sethome.hotelHomeSet");
				}
			}

			else if(args[0].equalsIgnoreCase("home") || args[0].equalsIgnoreCase("hm")){
				if(!isPlayer){ Mes.mes(sender, "chat.commands.home.consoleRejected"); return false; }

				Player p = (Player) sender;
				World w = p.getWorld();

				if(!Mes.hasPerm(p, "hotels.home")){ Mes.mes(p, "chat.noPermission"); return false; }
				if(length<2){ Mes.mes(p, "chat.commands.home.usage"); return false; }

				if(length>2){
					try {
						Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						Mes.mes(p, "chat.commands.room.roomNumInvalid");
						return false;
					}

					Room room = new Room(w, args[1], args[2]);
					if(!room.exists()){	Mes.mes(p, "chat.commands.home.regionNotFound"); return false; }

					if(!Mes.hasPerm(p, "hotels.home.admin") && !room.isRenter(p.getUniqueId())){ Mes.mes(p, "chat.commands.home.notRenterNoPermission"); return false;	}
					//Check if there is a user home
					Location userLoc = room.getUserHome();
					Location defLoc = room.getDefaultHome();

					if(userLoc!=null) p.teleport(userLoc);
					else if(defLoc!=null) p.teleport(defLoc);
					else Mes.mes(p, "chat.commands.home.noHomeSet");

				}//Haven't specified room, try hotel home
				else{
					Hotel hotel = new Hotel(w,args[1]);

					if(!hotel.exists()){ Mes.mes(p, "chat.commands.home.regionNotFound"); return false; }
					Location loc = hotel.getHome();

					if(loc!=null) p.teleport(loc);
					else Mes.mes(p, "chat.commands.home.noHomeSet");	
				}
			}
			else if(args[0].equalsIgnoreCase("sellhotel") || args[0].equalsIgnoreCase("sellh")){
				if(!isPlayer){ Mes.mes(sender, "chat.commands.sellhotel.consoleRejected"); return false;}

				Player p = (Player) sender;

				if(!Mes.hasPerm(p, "hotels.sell.hotel")){ Mes.mes(p, "chat.noPermission"); return false; }

				if(length<3){ Mes.mes(p, "chat.commands.sellhotel.usage"); return false; }

				World world = p.getWorld();
				Hotel hotel = new Hotel(world,args[1]);

				if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return false; }

				if(!hotel.isOwner(p.getUniqueId())){ Mes.mes(p, "chat.commands.youDoNotOwnThat"); return false; }

				@SuppressWarnings("deprecation")
				Player buyer = Bukkit.getPlayerExact(args[2]);
				if(buyer == null || !buyer.isOnline()){	Mes.mes(p, "chat.commands.sellhotel.buyerNotOnline"); return false; }

				int price;
				try{
					price = Integer.parseInt(args[3]);
				}
				catch(NumberFormatException e){
					Mes.mes(p, "chat.commands.sellhotel.invalidPrice");
					return false;
				}

				if(hotel.getBuyer()!=null || buyer.getUniqueId().equals(hotel.getBuyer().getPlayer().getUniqueId())){
					sender.sendMessage(Mes.getString("chat.commands.sellhotel.sellingAlreadyAsked").replaceAll("%buyer%", buyer.getName())); return false; }

				hotel.setBuyer(buyer.getUniqueId(), price);

				sender.sendMessage(Mes.getString("chat.commands.sellhotel.sellingAsked").replaceAll("%buyer%", buyer.getName()));

				buyer.sendMessage(Mes.getString("chat.commands.sellhotel.selling")
						.replaceAll("%seller%", p.getName())
						.replaceAll("%hotel%", args[1])
						.replaceAll("%price%", String.valueOf(price))
						);
			}

			else if(args[0].equalsIgnoreCase("buyhotel") || args[0].equalsIgnoreCase("buyh")){
				if(!isPlayer){ Mes.mes(sender, "chat.commands.buyhotel.consoleRejected"); return false; }

				Player p = (Player) sender;
				if(length<2){ Mes.mes(p, "chat.commands.buyhotel.usage"); return false; }

				World world = p.getWorld();
				Hotel hotel = new Hotel(world, args[1]);

				if(!hotel.exists()){Mes.mes(p, "chat.commands.hotelNonExistent"); return false; }

				HotelBuyer hb = hotel.getBuyer();
				Player buyer = hb.getPlayer();

				if(!buyer.hasPlayedBefore() || !buyer.equals(p) || buyer == null){ Mes.mes(p, "chat.commands.buyhotel.notOnSale"); return false; }

				//They are the buyer the hotel owner has specified
				double balance = HotelsMain.economy.getBalance(p);
				double price = hb.getPrice();

				if((balance-price)<0){ Mes.mes(p, "chat.commands.buyhotel.notEnoughMoney"); return false; }

				//Player has enough money
				HotelsMain.economy.withdrawPlayer(p, price);
				String onlineOwner = "";

				String taxString = plugin.getConfig().getString("tax");
				double revenue = price;
				boolean isPercentage = taxString.matches("\\d+%");
				double tax;

				if(isPercentage) taxString = taxString.replaceAll("%", "");
				try{
					tax = Double.parseDouble(taxString);
				}
				catch(Exception e){
					Mes.mes(p, "chat.commands.sellHotel.invalidPrice");
					return false;
				}

				revenue = isPercentage ? revenue * (1 - tax/100) : revenue - tax;

				if(revenue<0) revenue = 0;

				HotelSaleEvent hse = new HotelSaleEvent(hb, revenue);
				Bukkit.getPluginManager().callEvent(hse);
				if(hse.isCancelled()) return false;
				//In case they were modified by an event listener
				hb = hse.getHotelBuyer();
				revenue = hse.getRevenue();

				for(UUID uuid : hotel.getOwners().getUniqueIds()){//Paying all owners

					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);

					if(!op.isOnline()){
						HotelsMessageQueue.addMessage(MessageType.revenue, uuid, Mes.getString("chat.commands.sellHotel.success")
								.replaceAll("%hotel%", hotel.getName())
								.replaceAll("%buyer%", p.getName())
								.replaceAll("%price%", String.valueOf(price)));
						return false; }

					onlineOwner = p.getName();

					p.sendMessage(Mes.getString("chat.commands.sellhotel.success")
							.replaceAll("%hotel%", hotel.getName())
							.replaceAll("%buyer%", p.getName())
							.replaceAll("%price%", String.valueOf(price))
							);
					hotel.removeOwner(op); //Removing old owner

					HotelsMain.economy.depositPlayer(op, revenue); //Paying old owner
				}

				hotel.addOwner(p);
				p.sendMessage(Mes.getString("chat.commands.buyhotel.success")
						.replaceAll("%hotel%", hotel.getName())
						.replaceAll("%seller%", onlineOwner)
						.replaceAll("%price%", String.valueOf(price))
						);

				hotel.removeBuyer();
				hotel.saveHotelConfig();

			}
			else if(args[0].equalsIgnoreCase("sellroom") || args[0].equalsIgnoreCase("sellr")){
				if(!isPlayer){ Mes.mes(sender, ("chat.commands.sellroom.consoleRejected")); return false;}

				Player p = (Player) sender;

				if(!Mes.hasPerm(p, "hotels.sell.room")){ Mes.mes(p, "chat.noPermission"); return false; }

				if(length<4){ Mes.mes(p, "chat.commands.sellroom.usage"); return false; }

				World world = p.getWorld();
				Hotel hotel = new Hotel(world, args[1]);

				if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return false; }

				Room room = new Room(hotel, args[2]);

				if(!room.isRenter(p.getUniqueId())){ Mes.mes(p, "chat.commands.friend.notRenter"); return false; }

				@SuppressWarnings("deprecation")
				Player buyer = Bukkit.getPlayerExact(args[3]);
				if(buyer == null || !buyer.isOnline()){	Mes.mes(p, "chat.commands.sellhotel.buyerNotOnline"); return false; }

				int price;

				try{
					price = Integer.parseInt(args[4]);
				}
				catch(NumberFormatException e){
					Mes.mes(p, "chat.commands.sellhotel.invalidPrice");
					return false;
				}

				if(room.getBuyer()!=null || buyer.getUniqueId().equals(room.getBuyer().getPlayer().getUniqueId())){
					sender.sendMessage(Mes.getString("chat.commands.sellroom.sellingAlreadyAsked").replaceAll("%buyer%", buyer.getName())); return false; }

				room.setBuyer(buyer.getUniqueId(), price);

				sender.sendMessage(Mes.getString("chat.commands.sellroom.sellingAsked").replaceAll("%buyer%", buyer.getName()));

				buyer.sendMessage(Mes.getString("chat.commands.sellroom.selling")
						.replaceAll("%seller%", p.getName())
						.replaceAll("%hotel%", args[1])
						.replaceAll("%price%", String.valueOf(price))
						.replaceAll("%room%", String.valueOf(room.getNum()))
						);
			}

			else if(args[0].equalsIgnoreCase("buyroom") || args[0].equalsIgnoreCase("buyr")){
				if(!isPlayer){ Mes.mes(sender, ("chat.commands.buyroom.consoleRejected")); return false; }

				Player p = (Player) sender;
				if(length<3){ Mes.mes(p, "chat.commands.buyroom.usage"); return false; }

				World world = p.getWorld();
				Hotel hotel = new Hotel(world, args[1]);

				if(!hotel.exists()){ Mes.mes(p, "chat.commands.hotelNonExistent"); return false; }

				Room room = new Room(hotel, args[2]);

				if(!room.exists()){ Mes.mes(p, "chat.commands.roomNonExistent"); return false; }

				RoomBuyer rb = room.getBuyer();
				Player buyer = rb.getPlayer();

				if(!buyer.hasPlayedBefore() || !buyer.equals(p) || buyer == null){ Mes.mes(p, "chat.commands.buyroom.notOnSale"); return false; }

				//They are the buyer the room owner has specified
				double balance = HotelsMain.economy.getBalance(p);
				double price = rb.getPrice();

				if((balance-price)<0){ Mes.mes(p, "chat.commands.buyhotel.notEnoughMoney"); return false; }

				//Player has enough money
				HotelsMain.economy.withdrawPlayer(p, price);
				String onlineOwner = "";

				String taxString = plugin.getConfig().getString("tax");
				double revenue = price;
				boolean isPercentage = taxString.matches("\\d+%");
				double tax;

				if(isPercentage)
					taxString = taxString.replaceAll("%", "");
				try{
					tax = Double.parseDouble(taxString);
				}
				catch(Exception e){
					Mes.mes(p, "chat.commands.sellHotel.invalidPrice");
					return false;
				}

				revenue = isPercentage ? revenue * (1 - tax/100) : revenue - tax;

				if(revenue<0) revenue = 0;

				OfflinePlayer op = room.getRenter();

				RoomSaleEvent rse = new RoomSaleEvent(rb, revenue);
				Bukkit.getPluginManager().callEvent(rse);
				if(rse.isCancelled()) return false;
				//In case they were modified by an event listener
				rb = rse.getRoomBuyer();
				revenue = rse.getRevenue();

				String message = Mes.getString("chat.commands.sellroom.success")
						.replaceAll("%room%", String.valueOf(room.getNum()))
						.replaceAll("%buyer%", p.getName())
						.replaceAll("%price%", String.valueOf(price))
						.replaceAll("%hotel%", hotel.getName());

				if(op.isOnline()){
					onlineOwner = p.getName();
					p.sendMessage(message);
				}
				else
					HotelsMessageQueue.addMessage(MessageType.revenue, op.getUniqueId(),message);

				room.setRenter(p.getUniqueId()); //Removing old owner

				HotelsMain.economy.depositPlayer(op, revenue); //Paying old owner

				p.sendMessage(Mes.getString("chat.commands.buyroom.success")
						.replaceAll("%room%", String.valueOf(room.getNum()))
						.replaceAll("%seller%", onlineOwner)
						.replaceAll("%price%", String.valueOf(price))
						.replaceAll("%hotel%", hotel.getName()) );

				room.removeBuyer();

				try {
					room.saveSignConfig();
				} catch (IOException e) {
					Mes.mes(p, "chat.commands.somethingWentWrong");
					e.printStackTrace();
				}

			}
			else if(args[0].equalsIgnoreCase("roomreset")){
				if(args.length<3){ Mes.mes(sender, "chat.commands.roomreset.usage"); return false; }
				//Command to toggle resetting of rooms upon rent expiry
				Room room = new Room(args[1], args[2]);
				if(!room.exists()){ Mes.mes(sender, "chat.commands.roomNonExistent"); return false; }

				try {
					if(room.toggleShouldReset())
						sender.sendMessage(Mes.getString("chat.commands.roomreset.enable").replaceAll("%hotel%", args[1]).replaceAll("%room%", args[2]));
					else
						sender.sendMessage(Mes.getString("chat.commands.roomreset.disable").replaceAll("%hotel%", args[1]).replaceAll("%room%", args[2]));
				} catch (DataException | IOException | WorldEditException e) {
					Mes.mes(sender, "chat.commands.somethingWentWrong");
					e.printStackTrace();
				}
			}
			else if(args[0].equalsIgnoreCase("resetroom")){
				if(args.length<3){ Mes.mes(sender, "chat.commands.resetroom.usage"); return false; }
				Room room = new Room(args[1], args[2]);

				if(!room.getShouldReset()) { Mes.mes(sender, "chat.commands.resetroom.notSetup"); return false; }
				
				try {
					room.resetRoom();
					sender.sendMessage(Mes.getString("chat.commands.resetroom.success").replaceAll("%room%", String.valueOf(room.getNum())).replaceAll("%hotel%", room.getHotel().getName()) );
				} catch (DataException | IOException | WorldEditException e) {
					Mes.mes(sender, "chat.commands.somethingWentWrong");
					e.printStackTrace();
				}	
			}
			//Other argument
			else
				Mes.mes(sender, "chat.commands.unknownArg");
		}
		//Command is not /hotels
		return false;
	}
}