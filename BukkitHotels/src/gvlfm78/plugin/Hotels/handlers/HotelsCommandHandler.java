package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class HotelsCommandHandler implements CommandExecutor {
	
	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain instance)
	{
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
					HCE.cmdReload(sender);
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
									if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
										HCE.removeRoom(args[1],roomnum, world,sender);
									}
									else
										sender.sendMessage(HMM.mes("chat.commands.roomNonExistant"));
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
					else if(!(sender instanceof Player)){
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
							HCE.renameHotel(plugin,args[1],args[2],world,sender);
						}
						else if(args.length>3){
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								HCE.renameHotel(plugin,args[1],args[2],world,sender);
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
								HCE.renameHotel(plugin,args[1],args[2],world,sender);
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
							HCE.removeSigns(args[1],world,sender);
							HCE.removeRegions(args[1],world,sender);
						}
						else if((args.length == 3)){
							World world = Bukkit.getWorld(args[2]);
							HCE.removeSigns(args[1],world,sender);
							HCE.removeRegions(args[1],world,sender);
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

						if(!(WGM.hasRegion(p.getWorld(), "Hotel-"+hotelName)))
							sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
						else{
							if(args.length>2){
								try{
									int roomNum = Integer.parseInt(args[2]);
									HCM.roomSetup(hotelName, roomNum, sender,plugin);
									hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1).toLowerCase();
									String roomNums = String.valueOf(roomNum);
									roomNums = roomNums.substring(0, 1).toUpperCase() + roomNums.substring(1).toLowerCase();
									sender.sendMessage(HMM.mes("chat.commands.room.success").replaceAll("%room%", String.valueOf(roomNum))
											.replaceAll("%hotel%", hotelName));
								} catch(NumberFormatException e){
									sender.sendMessage(HMM.mes("chat.commands.room.roomNumInvalid"));
								}
							}
							//Player did not specify room number
							else{
								int roomNum = HCE.nextNewRoom(p.getWorld(),hotelName);
								if(roomNum!=0){
									HCM.roomSetup(hotelName, roomNum, sender,plugin);
									hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1).toLowerCase();
									String roomNums = String.valueOf(roomNum);
									roomNums = roomNums.substring(0, 1).toUpperCase() + roomNums.substring(1).toLowerCase();
									sender.sendMessage(HMM.mes("chat.commands.room.success").replaceAll("%room%", String.valueOf(roomNum))
											.replaceAll("%hotel%", hotelName));
								}
								else
									sender.sendMessage(HMM.mes("chat.commands.room.nextNewRoomFail"));
							}
						}
					}
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.room.usage"));
			}
			else {
				sender.sendMessage(HMM.mes("chat.commands.unknownArg"));
			}
			//Other commands
		}
		//Command is not /hotels
		return false;
	}
}