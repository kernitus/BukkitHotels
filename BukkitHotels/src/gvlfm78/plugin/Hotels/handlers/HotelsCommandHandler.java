package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
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

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCommandHandler implements CommandExecutor {

	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain instance){
		this.plugin = instance;
	}

	HotelsMessageManager HMM = new HotelsMessageManager(plugin);
	SignManager SM = new SignManager(plugin);
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);
	HotelsFileFinder HFF = new HotelsFileFinder(plugin);
	HotelsCommandExecutor HCE = new HotelsCommandExecutor(plugin);

	//Prefix
	YamlConfiguration locale = HConH.getLocale();
	String prefix = (locale.getString("chat.prefix")+" ");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl,String[] args){
		if(cmd.getLabel().equalsIgnoreCase("Hotels")){
			if(args.length<1){//Fallback screen
				sender.sendMessage(("&4==========Hotels==========").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&2"+plugin.getDescription().getName()+" plugin by kernitus").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion()).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&4Type &3/hotels help &4for help with creating a hotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&4Type &3/hotels commands &4for help with the commands").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&4==========================").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				return false;
			}
			//Command checks
			if(args[0].equalsIgnoreCase("commands")){//Commands list
				if(plugin.getConfig().getBoolean("settings.commands.onlyDisplayAllowed")==false)
					HCE.cmdCommandsAll(sender);
				else
					HCE.cmdCommandsOnly(sender);
			}
			else if(args[0].equalsIgnoreCase("create")|args[0].equalsIgnoreCase("c")){//Create command
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length>1){
						if(HMM.hasPerm(sender, "hotels.create")){
							HCE.cmdCreate(plugin, p, args[1]);
						}
						else
							p.sendMessage(HMM.mes("chat.noPermission"));
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.create.noName"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.create.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("help")){//Help pages
				if(HMM.hasPerm(sender, "hotels.create")){
					if(args.length>1){
						if(args[1].equalsIgnoreCase("1"))
							HCE.cmdHelp1(sender);
						else if(args[1].equalsIgnoreCase("2"))
							HCE.cmdHelp2(sender);
						else if(args[1].equalsIgnoreCase("3"))
							HCE.cmdHelp3(sender);
						else if(args[1].equalsIgnoreCase("4"))
							HCE.cmdHelp4(sender);
						else if(args[1].equalsIgnoreCase("5"))
							HCE.cmdHelp5(sender);
						else
							HCE.cmdHelp1(sender);
					}
					else
						HCE.cmdHelp1(sender);
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if((args[0].equalsIgnoreCase("createmode"))||(args[0].equalsIgnoreCase("cm"))){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(HMM.hasPerm(p, "hotels.createmode")){
						if(args[1].equalsIgnoreCase("enter"))
							HCE.cmdCreateModeEnter(p);
						else if(args[1].equalsIgnoreCase("exit"))
							HCE.cmdCreateModeExit(p);
						else if(args[1].equalsIgnoreCase("reset"))
							HCE.cmdCreateModeReset(p);
						else
							p.sendMessage(HMM.mes("chat.commands.creationMode.noarg"));
					}
					else
						p.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage("chat.commands.creationMode.consoleRejected");
			}
			else if(args[0].equalsIgnoreCase("check")){
				if(sender instanceof Player){
					if(args.length==1){
						String p = sender.getName();
						HCE.check(p, sender);
					}
					else if(args.length>=2){
						if(args[1]==sender.getName()){
							String p = args[1];							
							HCE.check(p, sender);
						}
						else if(sender.hasPermission("hotels.check.others")){
							String p = args[1];							
							HCE.check(p, sender);
						}
						else
							sender.sendMessage(HMM.mes("chat.noPermission"));
					}
				}else{
					if(args.length>=2){
						String p = args[1];							
						HCE.check(p, sender);
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.noPlayer"));
				}
			}
			else if(args[0].equalsIgnoreCase("reload")){
				if(HMM.hasPerm(sender, "hotels.reload"))
					HCE.cmdReload(sender,plugin);
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("rent")){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(HMM.hasPerm(p, "hotels.rent")){
						if(args.length<3)
							p.sendMessage(HMM.mes("chat.commands.rent.usage"));
						else
							HCE.cmdRent(sender, args[1], args[2]);
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}else
					sender.sendMessage(HMM.mes("chat.commands.rent.consoleRejected").replaceAll("(?i)&([a-fk-r0-9])", ""));
			}
			else if((args[0].equalsIgnoreCase("friend"))||(args[0].equalsIgnoreCase("f"))){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(HMM.hasPerm(p, "hotels.friend")){
						if(args.length>1){
							if(args[1].equalsIgnoreCase("add")){
								if(args.length>4)
									HCE.cmdFriendAdd(sender,args[2],args[3],args[4]);
								else
									sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
							}
							else if(args[1].equalsIgnoreCase("remove")){
								if(args.length>4)
									HCE.cmdFriendRemove(sender,args[2],args[3],args[4]);
								else
									sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
							}
							else if(args[1].equalsIgnoreCase("list")){
								if(args.length>3)
									HCE.cmdFriendList(sender,args[2],args[3]);
								else
									sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.friend.consoleRejected"));
			}
			else if((args[0].equalsIgnoreCase("roomlist"))||(args[0].equalsIgnoreCase("rlist"))){
				if(HMM.hasPerm(sender, "hotels.list.rooms")){
					if(args.length>1){
						if(sender instanceof Player){//Is player
							Player p = (Player) sender;
							if(args.length>2){//Has specified world
								World w = Bukkit.getWorld(args[2]);
								if(w!=null)
									HCE.cmdRoomListPlayer(p, args[1], w);
								else
									sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
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
									sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
						}
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.listRooms.usage"));
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("hotelslist")||args[0].equalsIgnoreCase("hlist")||args[0].equalsIgnoreCase("list")){
				if(HMM.hasPerm(sender, "hotels.list.hotels")){
					if((args.length<2)&&sender instanceof Player){
						World w = ((Player) sender).getWorld();
						HCE.listHotels(w,sender);
					}
					else if(args.length>1){
						World w = Bukkit.getWorld(args[1]);
						if(w!=null)
							HCE.listHotels(w,sender);
						else
							sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.noWorld"));
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("deleteroom")||args[0].equalsIgnoreCase("delr")){
				if(HMM.hasPerm(sender, "hotels.delete.rooms")){
					if(sender instanceof Player){
						if(args.length>=3){
							if(args.length!=4){
								Player p = (Player) sender;
								World world = p.getWorld();
								String hotelname = args[1].toLowerCase();
								String roomnum = args[2];
								if(WGM.hasRegion(world, "hotel-"+hotelname)){
									if(WGM.isOwner(p, "hotel-"+hotelname, p.getWorld())||HMM.hasPerm(p, "hotels.delete.room.admin")){
										if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
											if(HMM.hasPerm(p, "hotels.delete.rooms.admin")||SM.isRoomFree(hotelname, roomnum, world)){//TODO either add another message or use pre-existing one
											HCE.removeRoom(args[1],roomnum, world,sender);
											}
										}
										else
											sender.sendMessage(HMM.mes("chat.commands.roomNonExistant"));
									}
									else
										sender.sendMessage(HMM.mes("chat.commands.youDoNotOwnThat"));
								}
								else
									sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
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
										sender.sendMessage(HMM.mes("chat.commands.roomNonExistant"));
								}
								else
									sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
							}
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.deleteRoom.usage"));
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
									sender.sendMessage(HMM.mes("chat.commands.roomNonExistant"));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.deleteRoom.usage"));
					}
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("rename")||args[0].equalsIgnoreCase("ren")){
				if(HMM.hasPerm(sender, "hotels.rename")){
					if(sender instanceof Player){
						if(args.length==3){
							Player p = (Player) sender;
							World world = p.getWorld();
							HCE.renameHotel(args[1],args[2],world,sender);
						}
						else if(args.length>3){
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								HCE.renameHotel(args[1],args[2],world,sender);
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.rename.usage"));
					}
					else if(!(sender instanceof Player)){
						if(args.length>3){
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								HCE.renameHotel(args[1],args[2],world,sender);
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.noWorld"));
					}
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("renumber")||args[0].equalsIgnoreCase("renum")){
				if(HMM.hasPerm(sender, "hotels.renumber")){
					if(sender instanceof Player){
						if(args.length>3){
							Player p = (Player) sender;
							World world = p.getWorld();
							HCE.renumber(plugin,args[1],args[2],args[3],world,sender);
						}
						else if(args.length>4){
							World world = Bukkit.getWorld(args[4]);
							if(world!=null){
								HCE.renumber(plugin,args[1],args[2],args[3],world,sender);
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.renumber.usage"));
					}
					else if(!(sender instanceof Player)){
						if(args.length>4){
							World world = Bukkit.getWorld(args[4]);
							if(world!=null){
								HCE.renumber(plugin,args[1],args[2],args[3],world,sender);
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.renumber.usage"));
					}
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("del")){
				if(HMM.hasPerm(sender, "hotels.delete")){
					if(args.length>1){
						if(sender instanceof Player){
							Player p = (Player) sender;
							World world = p.getWorld();
							if(WGM.isOwner(p, "hotel-"+args[1], world)||HMM.hasPerm(p, "hotels.delete.admin")){
								HCE.removeSigns(args[1],world,sender);
								HCE.removeRegions(args[1],world,sender);
								File file = HConH.getFile("Hotels"+File.separator+args[1].toLowerCase()+".yml");
								if(file.exists())
									file.delete();
							}
							else
								p.sendMessage(HMM.mes("chat.commands.youDoNotOwnThat"));
						}
						else if((args.length == 3)){
							World world = Bukkit.getWorld(args[2]);
							HCE.removeSigns(args[1],world,sender);
							HCE.removeRegions(args[1],world,sender);
							File file = HConH.getFile("Hotels"+File.separator+args[1].toLowerCase()+".yml");
							if(file.exists())
								file.delete();
						}
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.noHotel"));
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("remove")){
				if(HMM.hasPerm(sender, "hotels.remove")){
					if(args.length>3){
						if(sender instanceof Player){
							Player p = (Player) sender;
							if(args.length>4){
								World w = Bukkit.getWorld(args[4]);
								HCE.removePlayer(w, args[2], args[3], args[1], sender);
							}
							else
								HCE.removePlayer(p.getWorld(), args[2], args[3], args[1], sender);
						}
						else if(args.length>=5)
							HCE.removePlayer(Bukkit.getWorld(args[4]), args[2], args[3], args[1], sender);
						else
							sender.sendMessage(HMM.mes("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.remove.usage"));
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));	
			}
			else if(args[0].equalsIgnoreCase("room")&&sender instanceof Player){
				if(args.length>=2){
					if(HMM.hasPerm(sender, "hotels.sign.create")){
						String hotelName = args[1];
						Player p = (Player) sender;
						if(HCM.isInCreationMode(p.getUniqueId().toString())){
							if(!(WGM.hasRegion(p.getWorld(), "Hotel-"+hotelName)))
								sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
							else{
								if(args.length>2){
									try{
										String room = args[2];
										int roomNum = Integer.parseInt(room);
										HCM.roomSetup(hotelName, String.valueOf(roomNum),p);
									} catch(NumberFormatException e){
										sender.sendMessage(HMM.mes("chat.commands.room.roomNumInvalid"));
									}
								}
								//Player did not specify room number
								else{
									int roomNum = HCE.nextNewRoom(p.getWorld(),hotelName);
									if(roomNum!=0){
										HCM.roomSetup(hotelName, String.valueOf(roomNum),p);
									}
									else
										sender.sendMessage(HMM.mes("chat.commands.room.nextNewRoomFail"));
								}
							}
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.creationMode.notAlreadyIn"));
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.room.usage"));
			}
			else if(args[0].equalsIgnoreCase("sethome")){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(HMM.hasPerm(p, "hotels.sethome")){
						String playerUUID = p.getUniqueId().toString();
						Location loc = p.getLocation();
						double x = loc.getX();
						double y = loc.getY();
						double z = loc.getZ();
						World w = p.getWorld();
						float pitch = loc.getPitch();
						float yaw = loc.getYaw();
						ApplicableRegionSet regions = WGM.getRM(w).getApplicableRegions(loc);
						ArrayList<ProtectedRegion> rf = new ArrayList<ProtectedRegion>();
						for(ProtectedRegion r : regions){
							//Regions that match player's location
							rf.add(r);
						}
						if(!rf.isEmpty()){
							for(ProtectedRegion r : rf){
								String id = r.getId();
								if(id.startsWith("hotel-")){ //If it's a hotel
									if(id.matches("^hotel-.+-.+")){ //If it's a room						
										String hotelandNum = (id.replaceFirst("hotel-", "")).toLowerCase();
										String num = hotelandNum.replaceFirst("\\w+-", "");
										String hotelName = hotelandNum.replaceFirst("-"+num, "");
										File signFile = HConH.getFile("Signs"+File.separator+hotelandNum+".yml");
										YamlConfiguration signConfig = HConH.getyml(signFile);

										//Either admin or hotel owner doing this
										if(HCM.isInCreationMode(p.getUniqueId().toString())){
											if((HMM.hasPerm(p, "hotels.sethome.admin"))||WGM.isOwner(p, "hotel-"+hotelName, w)){
												signConfig.set("Sign.defaultHome.x", x);
												signConfig.set("Sign.defaultHome.y", y);
												signConfig.set("Sign.defaultHome.z", z);
												signConfig.set("Sign.defaultHome.pitch", pitch);
												signConfig.set("Sign.defaultHome.yaw", yaw);
												try {
													signConfig.save(signFile);
													sender.sendMessage(HMM.mes("chat.commands.sethome.defaultHomeSet"));
												} catch (IOException e){
													e.printStackTrace();
												}
											}
											else{ //It's a user doing this
												if((signConfig.getString("Sign.renter")!=null)&&signConfig.getString("Sign.renter").matches(playerUUID)){
													signConfig.set("Sign.userHome.x", x);
													signConfig.set("Sign.userHome.y", y);
													signConfig.set("Sign.userHome.z", z);
													signConfig.set("Sign.userHome.pitch", pitch);
													signConfig.set("Sign.userHome.yaw", yaw);
													try {
														signConfig.save(signFile);
														sender.sendMessage(HMM.mes("chat.commands.sethome.userHomeSet"));
													} catch (IOException e){
														e.printStackTrace();
													}
												}
												else
													sender.sendMessage(HMM.mes("chat.commands.home.notRenterNoPermission"));
											}
										}
										else
											sender.sendMessage(HMM.mes("chat.commands.sethome.notInCreationMode"));
										break;
									}
									else{//It's a hotel warp
										String hotelName = (id.replaceFirst("hotel-", "")).toLowerCase();
										if(HCM.isInCreationMode(p.getUniqueId().toString())){
											if(HMM.hasPerm(p, "hotels.sethome.admin")||WGM.isOwner(p, "hotel-"+hotelName, w)){

												File hotelFile = HConH.getFile("Hotels"+File.separator+hotelName+".yml");
												YamlConfiguration hotelConfig = HConH.getyml(hotelFile);
												hotelConfig.set("Hotel.home.x", x);
												hotelConfig.set("Hotel.home.y", y);
												hotelConfig.set("Hotel.home.z", z);
												hotelConfig.set("Hotel.home.pitch", pitch);
												hotelConfig.set("Hotel.home.yaw", yaw);
												try {
													hotelConfig.save(hotelFile);
													sender.sendMessage(HMM.mes("chat.commands.sethome.hotelHomeSet"));
												} catch (IOException e){
													e.printStackTrace();
												}
											}
											else
												sender.sendMessage(HMM.mes("chat.commands.youDoNotOwnThat"));
										}
										else
											sender.sendMessage(HMM.mes("chat.commands.sethome.notInCreationMode"));
									}
								}
								else
									sender.sendMessage(HMM.mes("chat.commands.sethome.notInHotelRegion"));
							}
						}
						else //Player is not in any region
							sender.sendMessage(HMM.mes("chat.commands.sethome.notInHotelRegion"));
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.sethome.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("home")||args[0].equalsIgnoreCase("hm")){
				if(sender instanceof Player){
					Player p = (Player) sender;
					World w = p.getWorld();
					if(HMM.hasPerm(p, "hotels.home")){
						if(args.length>1){
							if(args.length>2){
								String hotelName = args[1].toLowerCase();
								String roomNum = args[2].toLowerCase();
								Map<String, ProtectedRegion> regionlist = WGM.getRM(w).getRegions();
								int regionsFound = 0;
								for(ProtectedRegion region : regionlist.values()){
									String regionId = region.getId();
									if(regionId.matches("hotel-"+hotelName+"-"+roomNum)){
										regionsFound++;
										//Room matching command has been found, check if there is user home set
										File signFile = HConH.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
										YamlConfiguration signConfig = HConH.getyml(signFile);
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
											if((HMM.hasPerm(p, "hotels.home.admin"))||p.getUniqueId().toString().matches(signConfig.getString("Sign.renter"))){
												Location daloc = new Location(w, x, y, z, yaw, pitch);
												p.teleport(daloc);
											}
											else
												sender.sendMessage(HMM.mes("chat.commands.home.notRenterNoPermission"));
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
												if(HMM.hasPerm(p, "hotels.home.admin")||signConfig.getString("Sign.renter")==null||p.getUniqueId().toString().matches(signConfig.getString("Sign.renter"))){
													Location daloc = new Location(w, x, y, z, yaw, pitch);
													p.teleport(daloc);
												}
												else
													sender.sendMessage(HMM.mes("chat.commands.home.notRenterNoPermission"));
											}
											else{ //No home is set
												sender.sendMessage(HMM.mes("chat.commands.home.noHomeSet"));
												//For future: if set in config, find centre of region and send player there
											}
										}
									}
								}
								if(regionsFound<1)
									sender.sendMessage(HMM.mes("chat.commands.home.regionNotFound"));
							}//Try hotel home
							else{
								String hotelName = args[1].toLowerCase();
								Map<String, ProtectedRegion> regionlist = WGM.getRM(w).getRegions();
								int regionsFound = 0;
								for(ProtectedRegion region : regionlist.values()){
									String regionId = region.getId();
									if(regionId.matches("hotel-"+hotelName)){
										regionsFound++;
										File hotelFile = HConH.getFile("Hotels"+File.separator+hotelName+".yml");
										YamlConfiguration hotelConfig = HConH.getyml(hotelFile);
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
											sender.sendMessage(HMM.mes("chat.commands.home.noHomeSet"));	
									}
								}
								if(regionsFound<1)
									sender.sendMessage(HMM.mes("chat.commands.home.regionNotFound"));
							}
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.home.usage"));
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.home.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("reload")){
				HConH.reloadConfigs(plugin);
				sender.sendMessage(HMM.mes("chat.commands.reload.success"));
			}
			else if(args[0].equalsIgnoreCase("sellhotel")||args[0].equalsIgnoreCase("sellh")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					if(HMM.hasPerm(player, "hotels.sell.hotel")){
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
											sender.sendMessage(HMM.mes("chat.commands.sellhotel.invalidPrice"));
											return false;
										}
										String hotelName = args[1].toLowerCase();
										File hotelFile = HConH.getFile("Hotels"+File.separator+hotelName+".yml");
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
											sender.sendMessage(HMM.mes("chat.commands.sellhotel.sellingAsked").replaceAll("%buyer%", buyer.getName()));
											buyer.sendMessage(HMM.mes("chat.commands.sellhotel.selling")
													.replaceAll("%seller%", player.getName())
													.replaceAll("%hotel%", args[1])
													.replaceAll("%price%", String.valueOf(price))
													);
										}
										else
											sender.sendMessage(HMM.mes("chat.commands.sellhotel.sellingAlreadyAsked").replaceAll("%buyer%", buyer.getName()));
									}
									else
										sender.sendMessage(HMM.mes("chat.commands.sellhotel.buyerNotOnline"));
								}
								else
									sender.sendMessage(HMM.mes("chat.commands.youDoNotOwnThat"));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));	
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.sellhotel.usage"));
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(HMM.mesnopre("chat.commands.sellhotel.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("buyhotel")||args[0].equalsIgnoreCase("buyh")){
				if(sender instanceof Player){
					Player player = (Player) sender;
					if(args.length>=2){
						World world = player.getWorld();
						if(WGM.hasRegion(world, "hotel-"+args[1])){
							String hotelName = args[1].toLowerCase();
							File hotelFile = HConH.getFile("Hotels"+File.separator+hotelName+".yml");
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
											op.sendMessage(HMM.mes("chat.commands.sellhotel.success")
													.replaceAll("%hotel%", hotelName)
													.replaceAll("%buyer%", player.getName())
													.replaceAll("%price%", String.valueOf(price))
													);
										}
									}
									WGM.addOwner(player, region);
									player.sendMessage(HMM.mes("chat.commands.buyhotel.success")
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
									sender.sendMessage(HMM.mes("chat.commands.buyhotel.notEnoughMoney"));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.buyhotel.notOnSale"));	
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));	
					}
					else
						sender.sendMessage(HMM.mesnopre("chat.commands.buyhotel.usage"));
				}
				else
					sender.sendMessage(HMM.mesnopre("chat.commands.buyhotel.consoleRejected"));

			}
			//Other command
			else {
				sender.sendMessage(HMM.mes("chat.commands.unknownArg"));
			}
		}
		//Command is not /hotels
		return false;
	}
}