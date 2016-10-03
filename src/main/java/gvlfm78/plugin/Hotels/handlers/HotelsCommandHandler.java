package kernitus.plugin.Hotels.handlers;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCommandHandler implements CommandExecutor {

	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain plugin){
		this.plugin = plugin;
	}

	SignManager SM = new SignManager(plugin);
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	WorldGuardManager WGM = new WorldGuardManager();
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);
	HotelsFileFinder HFF = new HotelsFileFinder();
	HotelsCommandExecutor HCE = new HotelsCommandExecutor(plugin);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl,String[] args){
		if(cmd.getLabel().equalsIgnoreCase("Hotels")){
			if(args.length<1){//Fallback screen
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&4==========Hotels==========")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',("&2"+plugin.getDescription().getName()+" plugin by kernitus")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',("&2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion())));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',("&4Type &3/hotels help &4for help with creating a hotel")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',("&4Type &3/hotels commands &4for help with the commands")));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',("&4==========================")));
				return false;
			}
			//Command checks
			if(args[0].equalsIgnoreCase("commands")){//Commands list
				if(plugin.getConfig().getBoolean("settings.commands.onlyDisplayAllowed"))
					HCE.cmdCommandsOnly(sender);
				else
					HCE.cmdCommandsAll(sender);
			}
			else if(args[0].equalsIgnoreCase("create")|args[0].equalsIgnoreCase("c")){//Create command
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length>1){
						if(Mes.hasPerm(sender, "hotels.create")){
							HCE.cmdCreate(p, args[1]);
						}
						else
							p.sendMessage(Mes.mes("chat.noPermission"));
					}
					else
						sender.sendMessage(Mes.mes("chat.commands.create.noName"));
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.create.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("help")){//Help pages
				if(Mes.hasPerm(sender, "hotels.create")){
					if(args.length>1){
						switch(args[1]){
						case "1": HCE.cmdHelp1(sender); break;
						case "2": HCE.cmdHelp2(sender); break;
						case "3": HCE.cmdHelp3(sender); break;
						case "4": HCE.cmdHelp4(sender); break;
						case "5": HCE.cmdHelp5(sender); break;
						default: HCE.cmdHelp1(sender);
						}
					}
					else
						HCE.cmdHelp1(sender);
				}
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));
			}
			else if((args[0].equalsIgnoreCase("createmode"))||(args[0].equalsIgnoreCase("cm"))){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length>=2){
						if(Mes.hasPerm(p, "hotels.createmode")){
							switch(args[1].toLowerCase()){
							case "enter": HCE.cmdCreateModeEnter(p); break;
							case "exit": HCE.cmdCreateModeExit(p); break;
							case "reset": HCE.cmdCreateModeReset(p); break;
							default: p.sendMessage(Mes.mes("chat.commands.creationMode.noarg"));
							}
						}
						else
							p.sendMessage(Mes.mes("chat.noPermission"));
					}
					else
						p.sendMessage(Mes.mes("chat.commands.creationMode.noarg"));
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.creationMode.consoleRejected"));
			}

			else if(args[0].equalsIgnoreCase("check")){
				if(sender instanceof Player){
					if(args.length==1)
						HCE.check(sender.getName(), sender);
					else if(args.length>=2){
						if(args[1]==sender.getName())					
							HCE.check(args[1], sender);
						else if(sender.hasPermission("hotels.check.others"))					
							HCE.check(args[1], sender);
						else
							sender.sendMessage(Mes.mes("chat.noPermission"));
					}
				} else{
					if(args.length>=2)					
						HCE.check(args[1], sender);
					else
						sender.sendMessage(Mes.mes("chat.commands.noPlayer"));
				}
			}

			else if(args[0].equalsIgnoreCase("reload")){
				if(Mes.hasPerm(sender, "hotels.reload"))
					HCE.cmdReload(sender,plugin);
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("rent")){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(Mes.hasPerm(p, "hotels.rent")){
						if(args.length<3)
							p.sendMessage(Mes.mes("chat.commands.rent.usage"));
						else
							HCE.cmdRent(sender, args[1], args[2]);
					}
					else
						sender.sendMessage(Mes.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.rent.consoleRejected").replaceAll("(?i)&([a-fk-r0-9])", ""));
			}
			else if((args[0].equalsIgnoreCase("friend"))||(args[0].equalsIgnoreCase("f"))){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(Mes.hasPerm(p, "hotels.friend")){
						if(args.length>1){
							if(args[1].equalsIgnoreCase("add")){
								if(args.length>4)
									HCE.cmdFriendAdd(p,args[2],args[3],args[4]);
								else
									sender.sendMessage(Mes.mes("chat.commands.friend.usage"));
							}
							else if(args[1].equalsIgnoreCase("remove")){
								if(args.length>4)
									HCE.cmdFriendRemove(p,args[2],args[3],args[4]);
								else
									sender.sendMessage(Mes.mes("chat.commands.friend.usage"));
							}
							else if(args[1].equalsIgnoreCase("list")){
								if(args.length>3){
									Room room = new Room(args[2],args[3]);
									if(!(sender instanceof Player) || (sender instanceof Player && room.isRenter(((Player) sender).getUniqueId())))
										HCE.cmdFriendList(sender,args[2],args[3]);
									else
										sender.sendMessage(Mes.mes("chat.commands.friend.notRenter"));	
								}
								else
									sender.sendMessage(Mes.mes("chat.commands.friend.usage"));
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.friend.usage"));
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.friend.usage"));
					}
					else
						sender.sendMessage(Mes.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.friend.consoleRejected"));
			}
			else if((args[0].equalsIgnoreCase("roomlist"))||(args[0].equalsIgnoreCase("rlist"))){
				if(Mes.hasPerm(sender, "hotels.list.rooms")){
					if(args.length>1){
						if(sender instanceof Player){//Is player
							Player p = (Player) sender;
							if(args.length>2){//Has specified world
								World w = Bukkit.getWorld(args[2]);
								if(w!=null)
									HCE.cmdRoomListPlayer(p, args[1], w);
								else
									sender.sendMessage(Mes.mes("chat.commands.worldNonExistant"));
							}
							else{//Has not specified world
								World w = p.getWorld();
								HCE.cmdRoomListPlayer(p, args[1], w);
							}
						}
						else{//Not a player issuing the command
							if(args.length>2){
								World w = Bukkit.getWorld(args[2]);
								if(w!=null)
									HCE.cmdRoomListPlayer(sender, args[1],w);
								else
									sender.sendMessage(Mes.mes("chat.commands.worldNonExistant"));
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
						}
					}
					else
						sender.sendMessage(Mes.mes("chat.commands.listRooms.usage"));
				}
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("hotelslist")||args[0].equalsIgnoreCase("hlist")||args[0].equalsIgnoreCase("list")){
				if(Mes.hasPerm(sender, "hotels.list.hotels")){
					if((args.length<2)&&sender instanceof Player){
						World w = ((Player) sender).getWorld();
						HCE.listHotels(w,sender);
					}
					else if(args.length>1){
						World w = Bukkit.getWorld(args[1]);
						if(w!=null)
							HCE.listHotels(w,sender);
						else
							sender.sendMessage(Mes.mes("chat.commands.worldNonExistant"));
					}
					else
						sender.sendMessage(Mes.mes("chat.commands.noWorld"));
				}
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("deleteroom")||args[0].equalsIgnoreCase("delr")){
				if(Mes.hasPerm(sender, "hotels.delete.rooms")){
					if(sender instanceof Player){
						if(args.length>=3){
							if(args.length!=4){
								Player p = (Player) sender;
								World world = p.getWorld();
								String hotelName = args[1].toLowerCase();
								String roomNum = args[2];
								Hotel hotel = new Hotel(world, hotelName);
								if(hotel.exists()){
									if(hotel.isOwner(p.getUniqueId()) || Mes.hasPerm(p, "hotels.delete.room.admin")){
										Room room = new Room(hotel, roomNum);
										if(room.exists()){
											if(Mes.hasPerm(p, "hotels.delete.rooms.admin") || room.isFree()){
												HCE.removeRoom(args[1], roomNum, world,sender);
											}
											else
												sender.sendMessage(Mes.mes("chat.commands.deleteRoom.roomRented"));
										}
										else
											sender.sendMessage(Mes.mes("chat.commands.roomNonExistant"));
									}
									else
										sender.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
								}
								else
									sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));
							}
							else{
								World world = Bukkit.getWorld(args[3]);
								String hotelname = args[1];
								String roomnum = args[2];
								if(WGM.hasRegion(world, "hotel-"+hotelname)){
									if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
										HCE.removeRoom(args[1],roomnum, world,sender);
									}
									else
										sender.sendMessage(Mes.mes("chat.commands.roomNonExistant"));
								}
								else
									sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));
							}
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.deleteRoom.usage"));
					}
					else{
						if(args.length>=4){
							World world = Bukkit.getWorld(args[3]);
							String hotelname = args[1];
							String roomnum = args[2];
							if(WGM.hasRegion(world, "hotel-"+hotelname)){
								if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
									HCE.removeRoom(args[1],roomnum, world,sender);
								}
								else
									sender.sendMessage(Mes.mes("chat.commands.roomNonExistant"));
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.deleteRoom.usage"));
					}
				}
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("rename")||args[0].equalsIgnoreCase("ren")){
				if(Mes.hasPerm(sender, "hotels.rename")){
					if(sender instanceof Player){
						if(args.length==3){
							Player p = (Player) sender;
							if(!WGM.isOwner(p, "hotel-"+args[1], p.getWorld())){
								if(Mes.hasPerm(p, "hotels.rename.admin"))
									HCE.renameHotel(args[1],args[2],p.getWorld(),sender);
								else
									p.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
							}
						}
						else if(args.length>3){
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								HCE.renameHotel(args[1],args[2],world,sender);
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.worldNonExistant"));
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.rename.usage"));
					}
					else if(!(sender instanceof Player)){
						if(args.length>3){
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								HCE.renameHotel(args[1],args[2],world,sender);
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.worldNonExistant"));
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.noWorld"));
					}
				}
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("renumber")||args[0].equalsIgnoreCase("renum")){
				if(!Mes.hasPerm(sender, "hotels.renumber")){
					sender.sendMessage(Mes.mes("chat.noPermission")); return false;}
				if(args.length>4){//They specified the world
					World world = Bukkit.getWorld(args[4]);
					if(world!=null){
						Room room = new Room(world, args[1], args[2]);
						HCE.renumber(room,args[3],sender);
					}
					else
						sender.sendMessage(Mes.mes("chat.commands.worldNonExistant"));
				}
				else if(args.length>3 && sender instanceof Player){
					//They didn't specify the world, so we must get it from the player
					Room room = new Room(((Player) sender).getWorld(), args[1], args[2]);
					HCE.renumber(room, args[3], sender);
				}
				else//No way to get world either from args or player
					sender.sendMessage(Mes.mes("chat.commands.renumber.usage"));
			}

			else if(args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("del")){
				if(!Mes.hasPerm(sender, "hotels.delete")){
					sender.sendMessage(Mes.mes("chat.noPermission")); return false;	}

				if(args.length > 1){//They must specify the hotel name
					if(sender instanceof Player){//Get world from player
						Player p = (Player) sender;
						World world = p.getWorld();
						Hotel hotel = new Hotel(world, args[1]);
						if(!hotel.exists()){
							p.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); return false; }

						if(hotel.isOwner(p.getUniqueId())||Mes.hasPerm(p, "hotels.delete.admin")){
							if(Mes.hasPerm(p, "hotels.delete.admin")||!hotel.hasRentedRooms()){
								hotel.delete();
								p.sendMessage(Mes.mes("chat.commands.removeSigns.success"));
							}
							else
								p.sendMessage(Mes.mes("chat.commands.deleteHotel.hasRentedRooms"));
						}
						else
							p.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
					}
					else{
						Hotel hotel = new Hotel(args[1]);
						hotel.delete();
					}
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.noHotel"));
			}

			else if(args[0].equalsIgnoreCase("remove")){
				if(Mes.hasPerm(sender, "hotels.remove")){
					if(args.length<4){
						sender.sendMessage(Mes.mes("chat.commands.remove.usage")); return false; }
					if(args.length>4)
						HCE.removePlayer(Bukkit.getWorld(args[4]), args[2], args[3], args[1], sender);
					else if(sender instanceof Player)
						HCE.removePlayer(((Player) sender).getWorld(), args[2], args[3], args[1], sender);
					else
						sender.sendMessage(Mes.mes("chat.commands.noWorld"));
				}
				else
					sender.sendMessage(Mes.mes("chat.noPermission"));	
			}

			else if(args[0].equalsIgnoreCase("room") && sender instanceof Player){
				if(args.length<2){
					sender.sendMessage(Mes.mes("chat.commands.room.usage")); return false; }
				if(!Mes.hasPerm(sender, "hotels.sign.create")){
					sender.sendMessage(Mes.mes("chat.noPermission")); return false; }

				Player p = (Player) sender;
				if(HCM.isInCreationMode(p.getUniqueId().toString())){
					Hotel hotel = new Hotel(p.getWorld(), args[1]);
					String hotelName = hotel.getName();
					if(!hotel.exists()){
						sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); return false; }
					else{//Hotel exists, therefore proceed
						if(args.length>2){
							try{
								int roomNum = Integer.parseInt(args[2]);
								HCM.roomSetup(hotelName, roomNum, p);
							} catch(NumberFormatException e){
								sender.sendMessage(Mes.mes("chat.commands.room.roomNumInvalid"));
							}
						} else { //Player did not specify room number
							int roomNum = hotel.getNextNewRoom();
							if(roomNum!=0)
								HCM.roomSetup(hotelName, roomNum, p);
							else
								sender.sendMessage(Mes.mes("chat.commands.room.nextNewRoomFail"));
						}
					}
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.creationMode.notAlreadyIn"));
			}

			else if(args[0].equalsIgnoreCase("sethome")){
				if(sender instanceof Player){
					sender.sendMessage(Mes.mes("chat.commands.sethome.consoleRejected")); return false;	}

				Player p = (Player) sender;
				if(Mes.hasPerm(p, "hotels.sethome")){
					sender.sendMessage(Mes.mes("chat.noPermission")); return false;	}

				Location loc = p.getLocation();
				World w = p.getWorld();
				ApplicableRegionSet regions = WorldGuardManager.getRM(w).getApplicableRegions(loc);

				if(regions.size()<=0){
					sender.sendMessage(Mes.mes("chat.commands.sethome.notInHotelRegion")); return false; }

				Hotel hotel = null;
				Room room = null;

				for(ProtectedRegion r : regions){
					String hotelName = r.getId().replaceFirst("hotel-", "");
					Hotel hotelFound = new Hotel(p.getWorld(), hotelName);
					if(!hotelFound.exists())
						continue;
					hotel = hotelFound;
					String roomNum = hotelName.replaceFirst("\\w+-", "");
					Room roomFound = new Room(p.getWorld(), hotelName, roomNum);

					if(roomFound.exists())//Player in room region
						room = roomFound;
				}

				if(room!=null){//They're in a room region
					if(Mes.hasPerm(p, "hotels.sethome.admin") || WGM.isOwner(p, hotel.getRegion().getId(), w)){
						room.setDefaultHome(p.getLocation());
						if(room.saveSignConfig())
							sender.sendMessage(Mes.mes("chat.commands.sethome.defaultHomeSet"));
					}
					else { //It's a user doing this
						if(room.isRenter(p.getUniqueId())){//They are the room renter
							room.setUserHome(p.getLocation());
							if(room.saveSignConfig())
								sender.sendMessage(Mes.mes("chat.commands.sethome.userHomeSet"));
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.home.notRenterNoPermission"));
					}
				}
				else if(hotel!=null){//They're just in a hotel region
					if(HCM.isInCreationMode(p.getUniqueId().toString())){
						if(Mes.hasPerm(p, "hotels.sethome.admin")||WGM.isOwner(p, hotel.getRegion().getId(), w)){

							hotel.setHome(p.getLocation());

							if(hotel.saveHotelConfig())
								sender.sendMessage(Mes.mes("chat.commands.sethome.hotelHomeSet"));
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
					}
					else
						sender.sendMessage(Mes.mes("chat.commands.sethome.notInCreationMode"));
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.sethome.notInHotelRegion"));
			}

			else if(args[0].equalsIgnoreCase("home")||args[0].equalsIgnoreCase("hm")){
				if(sender instanceof Player){
					sender.sendMessage(Mes.mes("chat.commands.home.consoleRejected")); return false; }
				Player p = (Player) sender;
				World w = p.getWorld();
				if(Mes.hasPerm(p, "hotels.home")){
					sender.sendMessage(Mes.mes("chat.noPermission")); return false; }
				if(args.length<2){
					sender.sendMessage(Mes.mes("chat.commands.home.usage")); return false; }
				if(args.length>2){
					Room room = new Room(w, args[1], args[2]);
					if(!room.exists()){
						sender.sendMessage(Mes.mes("chat.commands.home.regionNotFound")); return false; }

					if(Mes.hasPerm(p, "hotels.home.admin") || room.isRenter(p.getUniqueId())){//They have permission
						//Check if there is a user home
						Location userLoc = room.getUserHome();
						Location defLoc = room.getDefaultHome();
						if(userLoc!=null)
							p.teleport(userLoc);
						else if(defLoc!=null){
							p.teleport(defLoc);
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.home.noHomeSet"));
					}
					else
						sender.sendMessage(Mes.mes("chat.commands.home.notRenterNoPermission"));
				}//Haven't specified room, try hotel home
				else{

					Hotel hotel = new Hotel(w,args[1]);
					if(!hotel.exists()){
						sender.sendMessage(Mes.mes("chat.commands.home.regionNotFound")); return false;	}
					Location loc = hotel.getHome();

					if(loc!=null)
						p.teleport(loc);
					else
						sender.sendMessage(Mes.mes("chat.commands.home.noHomeSet"));	
				}
			}
			else if(args[0].equalsIgnoreCase("reload")){
				HConH.reloadConfigs();
				sender.sendMessage(Mes.mes("chat.commands.reload.success"));
			}

			else if(args[0].equalsIgnoreCase("sellhotel")||args[0].equalsIgnoreCase("sellh")){
				if(!(sender instanceof Player)){
					sender.sendMessage(Mes.mesnopre("chat.commands.sellhotel.consoleRejected")); return false;}

				Player player = (Player) sender;
				if(!Mes.hasPerm(player, "hotels.sell.hotel")){
					sender.sendMessage(Mes.mes("chat.noPermission")); return false; }
				if(args.length<3){//If they have all necessary arguments
					sender.sendMessage(Mes.mes("chat.commands.sellhotel.usage")); return false; }

				World world = player.getWorld();
				Hotel hotel = new Hotel(world,args[1]);
				if(!hotel.exists()){//If specified hotel exists
					sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); return false; }
				if(!hotel.isOwner(player.getUniqueId())){
					sender.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat")); return false; }

				@SuppressWarnings("deprecation")
				Player buyer = Bukkit.getPlayerExact(args[2]);
				if(buyer==null || !buyer.isOnline()){
					sender.sendMessage(Mes.mes("chat.commands.sellhotel.buyerNotOnline")); return false; }
				int price;
				try{
					price = Integer.parseInt(args[3]);
				}
				catch(NumberFormatException e){
					sender.sendMessage(Mes.mes("chat.commands.sellhotel.invalidPrice"));
					return false;
				}
				if(hotel.getBuyer()!=null || buyer.getUniqueId().equals(hotel.getBuyer().getUniqueId())){
					sender.sendMessage(Mes.mes("chat.commands.sellhotel.sellingAlreadyAsked").replaceAll("%buyer%", buyer.getName())); return false; }

				hotel.setBuyer(buyer.getUniqueId());
				hotel.setPrice(price);

				sender.sendMessage(Mes.mes("chat.commands.sellhotel.sellingAsked").replaceAll("%buyer%", buyer.getName()));
				buyer.sendMessage(Mes.mes("chat.commands.sellhotel.selling")
						.replaceAll("%seller%", player.getName())
						.replaceAll("%hotel%", args[1])
						.replaceAll("%price%", String.valueOf(price))
						);
			}

			else if(args[0].equalsIgnoreCase("buyhotel")||args[0].equalsIgnoreCase("buyh")){
				if(!(sender instanceof Player)){ sender.sendMessage(Mes.mesnopre("chat.commands.buyhotel.consoleRejected")); return false; }

				Player player = (Player) sender;
				if(args.length<2){ sender.sendMessage(Mes.mesnopre("chat.commands.buyhotel.usage")); return false; }

				World world = player.getWorld();
				Hotel hotel = new Hotel(world, args[1]);

				if(!hotel.exists()){ sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); return false; }


				OfflinePlayer buyerFromConfig = hotel.getBuyer();

				if(!buyerFromConfig.hasPlayedBefore() || !buyerFromConfig.equals(player) || buyerFromConfig == null){ sender.sendMessage(Mes.mes("chat.commands.buyhotel.notOnSale")); return false; }

				//They are the buyer the hotel owner has specified
				double balance = HotelsMain.economy.getBalance(player);
				double price = hotel.getPrice();

				if((balance-price)<0){ sender.sendMessage(Mes.mes("chat.commands.buyhotel.notEnoughMoney")); return false; }

				//Player has enough money
				HotelsMain.economy.withdrawPlayer(player, price);
				ProtectedRegion region = hotel.getRegion();
				Set<String> owners = region.getOwners().getPlayers();
				String onlineOwner = "";

				for(String name : owners){//Paying all owners
					@SuppressWarnings("deprecation")
					OfflinePlayer op = Bukkit.getOfflinePlayer(name);
					if(op.isOnline()){
						Player p = (Player) op;

						onlineOwner = p.getName();

						p.sendMessage(Mes.mes("chat.commands.sellhotel.success")
								.replaceAll("%hotel%", hotel.getName())
								.replaceAll("%buyer%", player.getName())
								.replaceAll("%price%", String.valueOf(price))
								);
					}
					WGM.removeOwner(op, region); //Removing old owner

					HotelsMain.economy.depositPlayer(op, price); //Paying old owner
				}

				WGM.addOwner(player, region);
				player.sendMessage(Mes.mes("chat.commands.buyhotel.success")
						.replaceAll("%hotel%", hotel.getName())
						.replaceAll("%seller%", onlineOwner)
						.replaceAll("%price%", String.valueOf(price))
						);

				hotel.setBuyer(null);
				hotel.removePrice();
				hotel.saveHotelConfig();

			}
			//Other argument
			else
				sender.sendMessage(Mes.mes("chat.commands.unknownArg"));
		}
		//Command is not /hotels
		return false;
	}
}