package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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
							if(args[1].equalsIgnoreCase("enter"))
								HCE.cmdCreateModeEnter(p);
							else if(args[1].equalsIgnoreCase("exit"))
								HCE.cmdCreateModeExit(p);
							else if(args[1].equalsIgnoreCase("reset"))
								HCE.cmdCreateModeReset(p);
							else
								p.sendMessage(Mes.mes("chat.commands.creationMode.noarg"));
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
						if(args[1]==sender.getName()){						
							HCE.check(args[1], sender);
						}
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
				}else
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
									if(!(sender instanceof Player) || (sender instanceof Player && room.isRenter(((Player) sender).getUniqueId()))){
										HCE.cmdFriendList(sender,args[2],args[3]);
									}
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
								String hotelname = args[1].toLowerCase();
								String roomnum = args[2];
								if(WGM.hasRegion(world, "hotel-"+hotelname)){
									if(WGM.isOwner(p, "hotel-"+hotelname, p.getWorld())||Mes.hasPerm(p, "hotels.delete.room.admin")){
										if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
											if(Mes.hasPerm(p, "hotels.delete.rooms.admin")||SM.isRoomFree(hotelname, roomnum, world)){
												HCE.removeRoom(args[1],roomnum, world,sender);
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
					sender.sendMessage(Mes.mes("chat.noPermission"));
				}
				if(args.length > 1){//They must specify the hotel name
					if(sender instanceof Player){//Get world from player
						Player p = (Player) sender;
						World world = p.getWorld();
						Hotel hotel = new Hotel(world, args[1]);
						if(!hotel.exists()){
							p.sendMessage(Mes.mes("chat.commands.hotelNonExistant")); return false; }

						if(hotel.isOwner(p.getUniqueId())||Mes.hasPerm(p, "hotels.delete.admin")){
							if(Mes.hasPerm(p, "hotels.delete.admin")||!hotel.hasRentedRooms())
								hotel.delete();
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

				String playerUUID = p.getUniqueId().toString();
				Location loc = p.getLocation();
				double x = loc.getX();
				double y = loc.getY();
				double z = loc.getZ();
				World w = p.getWorld();
				float pitch = loc.getPitch();
				float yaw = loc.getYaw();
				ApplicableRegionSet regions = WorldGuardManager.getRM(w).getApplicableRegions(loc);

				if(regions.size()<=0){
					sender.sendMessage(Mes.mes("chat.commands.sethome.notInHotelRegion")); return false; }

				Hotel hotel;
				Room room;

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
					YamlConfiguration sconfig = room.sconfig;
					if(Mes.hasPerm(p, "hotels.sethome.admin") || WGM.isOwner(p, hotel.getRegion().getId(), w)){
						sconfig.set("Sign.defaultHome.x", x);
						sconfig.set("Sign.defaultHome.y", y);
						sconfig.set("Sign.defaultHome.z", z);
						sconfig.set("Sign.defaultHome.pitch", pitch);
						sconfig.set("Sign.defaultHome.yaw", yaw);
						if(room.saveSignConfig())
							sender.sendMessage(Mes.mes("chat.commands.sethome.defaultHomeSet"));
					}
					else { //It's a user doing this
						if(room.isRenter(p.getUniqueId())){//They are the room renter
							sconfig.set("Sign.userHome.x", x);
							sconfig.set("Sign.userHome.y", y);
							sconfig.set("Sign.userHome.z", z);
							sconfig.set("Sign.userHome.pitch", pitch);
							sconfig.set("Sign.userHome.yaw", yaw);
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

							YamlConfiguration hotelConfig = hotel.getHotelConfig();
							hotelConfig.set("Hotel.home.x", x);
							hotelConfig.set("Hotel.home.y", y);
							hotelConfig.set("Hotel.home.z", z);
							hotelConfig.set("Hotel.home.pitch", pitch);
							hotelConfig.set("Hotel.home.yaw", yaw);
							if(hotel.saveHotelConfig(hotelConfig))
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
					Player p = (Player) sender;
					World w = p.getWorld();
					if(Mes.hasPerm(p, "hotels.home")){
						if(args.length>1){
							if(args.length>2){
								String hotelName = args[1].toLowerCase();
								String roomNum = args[2].toLowerCase();
								Map<String, ProtectedRegion> regionlist = WorldGuardManager.getRM(w).getRegions();
								int regionsFound = 0;
								for(ProtectedRegion region : regionlist.values()){
									String regionId = region.getId();
									if(regionId.matches("hotel-"+hotelName+"-"+roomNum)){
										regionsFound++;
										//Room matching command has been found, check if there is user home set
										File signFile = HotelsConfigHandler.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
										YamlConfiguration signConfig = HotelsConfigHandler.getyml(signFile);
										String uhx = signConfig.getString("Sign.userHome.x");
										String uhy = signConfig.getString("Sign.userHome.y");
										String uhz = signConfig.getString("Sign.userHome.z");
										String uhpitch = signConfig.getString("Sign.userHome.pitch");
										String uhyaw = signConfig.getString("Sign.userHome.yaw");
										if(uhx!=null&&uhy!=null&&uhz!=null&&uhpitch!=null&&uhyaw!=null){
											double x = Double.parseDouble(uhx);
											double y = Double.parseDouble(uhy);
											double z = Double.parseDouble(uhz);
											float pitch = Float.parseFloat(uhpitch);
											float yaw = Float.parseFloat(uhyaw);
											//Checking if player is renter or has permission
											if((Mes.hasPerm(p, "hotels.home.admin"))||p.getUniqueId().toString().matches(signConfig.getString("Sign.renter"))){
												Location daloc = new Location(w, x, y, z, yaw, pitch);
												p.teleport(daloc);
											}
											else
												sender.sendMessage(Mes.mes("chat.commands.home.notRenterNoPermission"));
										}
										else{ //Else check if there is a default home
											String dhx = signConfig.getString("Sign.defaultHome.x");
											String dhy = signConfig.getString("Sign.defaultHome.y");
											String dhz = signConfig.getString("Sign.defaultHome.z");
											String dhpitch = signConfig.getString("Sign.defaultHome.pitch");
											String dhyaw = signConfig.getString("Sign.defaultHome.yaw");
											if(dhx!=null&&dhy!=null&&dhz!=null&&dhpitch!=null&&dhyaw!=null){
												double x = Double.parseDouble(dhx);
												double y = Double.parseDouble(dhy);
												double z = Double.parseDouble(dhz);
												float pitch = Float.parseFloat(dhpitch);
												float yaw = Float.parseFloat(dhyaw);
												if(Mes.hasPerm(p, "hotels.home.admin")||signConfig.getString("Sign.renter")==null||p.getUniqueId().toString().matches(signConfig.getString("Sign.renter"))){
													Location daloc = new Location(w, x, y, z, yaw, pitch);
													p.teleport(daloc);
												}
												else
													sender.sendMessage(Mes.mes("chat.commands.home.notRenterNoPermission"));
											}
											else{ //No home is set
												sender.sendMessage(Mes.mes("chat.commands.home.noHomeSet"));
												//For future: if set in config, find centre of region and send player there
											}
										}
									}
								}
								if(regionsFound<1)
									sender.sendMessage(Mes.mes("chat.commands.home.regionNotFound"));
							}//Try hotel home
							else{
								String hotelName = args[1].toLowerCase();
								Map<String, ProtectedRegion> regionlist = WorldGuardManager.getRM(w).getRegions();
								int regionsFound = 0;
								for(ProtectedRegion region : regionlist.values()){
									String regionId = region.getId();
									if(regionId.matches("hotel-"+hotelName)){
										regionsFound++;
										File hotelFile = HotelsConfigHandler.getFile("Hotels"+File.separator+hotelName+".yml");
										YamlConfiguration hotelConfig = HotelsConfigHandler.getyml(hotelFile);
										String hx = hotelConfig.getString("Hotel.home.x");
										String hy = hotelConfig.getString("Hotel.home.y");
										String hz = hotelConfig.getString("Hotel.home.z");
										String hpitch = hotelConfig.getString("Hotel.home.pitch");
										String hyaw = hotelConfig.getString("Hotel.home.yaw");
										if(hx!=null&&hy!=null&&hz!=null&&hpitch!=null&&hyaw!=null){
											double x = Double.parseDouble(hx);
											double y = Double.parseDouble(hy);
											double z = Double.parseDouble(hz);
											float pitch = Float.parseFloat(hpitch);
											float yaw = Float.parseFloat(hyaw);
											Location daloc = new Location(w, x, y, z);
											daloc.setPitch(pitch);
											daloc.setYaw(yaw);
											p.teleport(daloc);
										}
										else
											sender.sendMessage(Mes.mes("chat.commands.home.noHomeSet"));	
									}
								}
								if(regionsFound<1)
									sender.sendMessage(Mes.mes("chat.commands.home.regionNotFound"));
							}
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.home.usage"));
					}
					else
						sender.sendMessage(Mes.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(Mes.mes("chat.commands.home.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("reload")){
				HConH.reloadConfigs();
				sender.sendMessage(Mes.mes("chat.commands.reload.success"));
			}
			else if(args[0].equalsIgnoreCase("sellhotel")||args[0].equalsIgnoreCase("sellh")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					if(Mes.hasPerm(player, "hotels.sell.hotel")){
						if(args.length>=4){//If they have all necessary arguments
							World world = player.getWorld();
							if(WGM.hasRegion(world, "hotel-"+args[1])){//If specified hotel exists
								if(WGM.isOwner(player, "hotel-"+args[1], world)){
									@SuppressWarnings("deprecation")
									Player buyer = Bukkit.getPlayerExact(args[2]);
									if(buyer!=null&&buyer.isOnline()){
										int price;
										try{
											price = Integer.parseInt(args[3]);
										}
										catch(NumberFormatException e){
											sender.sendMessage(Mes.mes("chat.commands.sellhotel.invalidPrice"));
											return false;
										}
										String hotelName = args[1].toLowerCase();
										File hotelFile = HotelsConfigHandler.getFile("Hotels"+File.separator+hotelName+".yml");
										YamlConfiguration hotelconf = YamlConfiguration.loadConfiguration(hotelFile);
										String previousBuyer = hotelconf.getString("Hotel.sell.buyer");
										if(previousBuyer==null||previousBuyer.isEmpty()||!buyer.getUniqueId().toString().equals(previousBuyer)){
											hotelconf.set("Hotel.sell.buyer", buyer.getUniqueId().toString());
											hotelconf.set("Hotel.sell.price", price);
											try {
												hotelconf.save(hotelFile);
											} catch (IOException e) {
												e.printStackTrace();
											}
											sender.sendMessage(Mes.mes("chat.commands.sellhotel.sellingAsked").replaceAll("%buyer%", buyer.getName()));
											buyer.sendMessage(Mes.mes("chat.commands.sellhotel.selling")
													.replaceAll("%seller%", player.getName())
													.replaceAll("%hotel%", args[1])
													.replaceAll("%price%", String.valueOf(price))
													);
										}
										else
											sender.sendMessage(Mes.mes("chat.commands.sellhotel.sellingAlreadyAsked").replaceAll("%buyer%", buyer.getName()));
									}
									else
										sender.sendMessage(Mes.mes("chat.commands.sellhotel.buyerNotOnline"));
								}
								else
									sender.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));	
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.sellhotel.usage"));
					}
					else
						sender.sendMessage(Mes.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(Mes.mesnopre("chat.commands.sellhotel.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("buyhotel")||args[0].equalsIgnoreCase("buyh")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					if(args.length>=2){
						World world = player.getWorld();
						if(WGM.hasRegion(world, "hotel-"+args[1])){
							String hotelName = args[1].toLowerCase();
							File hotelFile = HotelsConfigHandler.getFile("Hotels"+File.separator+hotelName+".yml");
							YamlConfiguration hotelconf = YamlConfiguration.loadConfiguration(hotelFile);
							String configBuyer = hotelconf.getString("Hotel.sell.buyer");
							if(configBuyer!=null&&!configBuyer.isEmpty()&&configBuyer.matches(player.getUniqueId().toString())){
								//They are the buyer the hotel owner has specified
								double balance = HotelsMain.economy.getBalance(player);
								int price = hotelconf.getInt("Hotel.sell.price");
								if((balance-price)>=0){
									//Player has enough money
									HotelsMain.economy.withdrawPlayer(player, price);
									ProtectedRegion region = WGM.getRegion(world, "hotel-"+args[1]);
									Set<String> owners = region.getOwners().getPlayers();
									String onlineOwner = "";
									for(String name:owners){//Paying all owners
										@SuppressWarnings("deprecation")
										OfflinePlayer p = Bukkit.getOfflinePlayer(name);
										if(p.isOnline()){
											Player op = (Player) p;
											WGM.removeOwner(op, region);
											HotelsMain.economy.depositPlayer(op, price);
											onlineOwner = op.getName();
											op.sendMessage(Mes.mes("chat.commands.sellhotel.success")
													.replaceAll("%hotel%", hotelName)
													.replaceAll("%buyer%", player.getName())
													.replaceAll("%price%", String.valueOf(price))
													);
										}
									}
									WGM.addOwner(player, region);
									player.sendMessage(Mes.mes("chat.commands.buyhotel.success")
											.replaceAll("%hotel%", hotelName)
											.replaceAll("%seller%", onlineOwner)
											.replaceAll("%price%", String.valueOf(price))
											);

									hotelconf.set("Hotel.sell.buyer", null);
									hotelconf.set("Hotel.sell.price", null);
									try {
										hotelconf.save(hotelFile);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								else
									sender.sendMessage(Mes.mes("chat.commands.buyhotel.notEnoughMoney"));
							}
							else
								sender.sendMessage(Mes.mes("chat.commands.buyhotel.notOnSale"));	
						}
						else
							sender.sendMessage(Mes.mes("chat.commands.hotelNonExistant"));	
					}
					else
						sender.sendMessage(Mes.mesnopre("chat.commands.buyhotel.usage"));
				}
				else
					sender.sendMessage(Mes.mesnopre("chat.commands.buyhotel.consoleRejected"));

			}
			//Other command
			else {
				sender.sendMessage(Mes.mes("chat.commands.unknownArg"));
			}
		}
		//Command is not /hotels
		return false;
	}
}