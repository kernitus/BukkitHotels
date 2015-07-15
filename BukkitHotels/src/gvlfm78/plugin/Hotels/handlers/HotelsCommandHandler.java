package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.GameLoop;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsCommandHandler implements CommandExecutor {
	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain instance)
	{
		this.plugin = instance;
	}
	SignManager SM = new SignManager(plugin);
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);
	HotelsFileFinder HFF = new HotelsFileFinder(plugin);

	//Prefix
	YamlConfiguration locale = HConH.getLocale();
	String prefix = (locale.getString("chat.prefix").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+" ");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,String[] args) {
		if(cmd.getName().equalsIgnoreCase("Hotels")){
			if(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.commands")||sender.hasPermission("hotels.*")))){
				if(args.length==0){
					sender.sendMessage("§4==========Hotels==========");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" plugin by kernitus");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion());
					sender.sendMessage("§4Type §3/hotels help §4for help with creating a hotel");
					sender.sendMessage("§4Type §3/hotels commands §4for help with the commands");
					sender.sendMessage("§4==========================");
				}
				else if(args.length>=1&&args[0].equalsIgnoreCase("commands")){
					sender.sendMessage("§4================================");
					sender.sendMessage("§5--Hotels plugin command help page--");
					sender.sendMessage("§6/ht [creationmode|cm] [enter/exit] - §aEnter/exit creation mode");
					sender.sendMessage("§6/ht help - §aDisplays help page");
					sender.sendMessage("§6/ht list <world> - §aLists all hotels in current/specified world");
					sender.sendMessage("§6/ht rlist [hotel] <world> - §aLists all rooms in specified hotel in current/specified world");
					sender.sendMessage("§6/ht check <player> - §aLists all rooms rented by you/specified player");
					sender.sendMessage("§6/ht remove [player] [hotel] [room] - §aRemoves player from his rented room");
					sender.sendMessage("§6/ht [create|c] [hotelname] - §aCreate a hotel with current selection");
					sender.sendMessage("§6/ht room [hotelname] <roomnum> - §aCreate room with current selection within specified hotel");
					sender.sendMessage("§6/ht delete [hotelname] - §aDelete specified hotel");
					sender.sendMessage("§6/ht delr [hotelname] [roomnum] - §aDeletes specified room");
					sender.sendMessage("§6/ht rename [oldname] [newname] - §aRenames specified hotel");
					sender.sendMessage("§6/ht renum [hotel] [oldnum] [newnum] - §aRenumbers specified room");
					sender.sendMessage("§6/ht friend [add/remove] [hotel] [room] [player] - §aAdds/removes a player from the list of friends that can access the specified room");
					sender.sendMessage("§6/ht friend list [hotel] [room] - §aLists players in friend list of specified hotel room");
					sender.sendMessage("§4================================");
				}
				else if((args.length==1)&&(args[0].equalsIgnoreCase("help"))||(args.length>1)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("1")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§5--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 1- §9Selection of hotel cuboid");
					sender.sendMessage(ChatColor.YELLOW+"1. Type §3§o/ht [creationmode§r§3|§3§ocm] enter");
					sender.sendMessage(ChatColor.YELLOW+"2. Take your WorldEdit wand in hand");
					sender.sendMessage(ChatColor.YELLOW+"3. Left click and right click opposing corners of your hotel");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/ht help 2§r§4 to get to page 2");
					sender.sendMessage("§4==========================");
				}
				else if((args.length>1)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("2")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§5--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 2- §9Creation of the hotel");
					sender.sendMessage(ChatColor.YELLOW+"1. Type §3§o/ht [create§r§3|§oc] [nameofhotel]");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/ht help 3§r§4 to get to page 3");
					sender.sendMessage("§4==========================");
				}
				else if((args.length>1)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("3")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§5--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 3- §9Creation of a room");
					sender.sendMessage(ChatColor.YELLOW+"1. Get out your WorldEdit wand again");
					sender.sendMessage(ChatColor.YELLOW+"2. Left click and right click opposing corners of the room");
					sender.sendMessage(ChatColor.YELLOW+"3. Type §3§o/ht room [hotel] [roomnum]");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/ht help 4§r§4 to get to page 4");
					sender.sendMessage("§4==========================");
				}
				else if((args.length>1)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("4")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§5--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 4- §9Adding a sign");
					sender.sendMessage(ChatColor.YELLOW+"1. Grab a sign and place it outside of the room");
					sender.sendMessage(ChatColor.YELLOW+"2. Type on the sign:");
					sender.sendMessage(ChatColor.YELLOW+"    [Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"    <hotelname>");
					sender.sendMessage(ChatColor.YELLOW+"    <roomnum:cost>");
					sender.sendMessage(ChatColor.YELLOW+"    <time>");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/ht help 5§r§4 to get to page 5");
					sender.sendMessage("§4==========================");
				}
				else if((args.length>1)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("5")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§5--Hotels plugin help page--");
					sender.sendMessage("§4-Page 5- §9Example of a sign");
					sender.sendMessage("§e1. Example of a sign:");
					sender.sendMessage("§e    [Hotels]");
					sender.sendMessage("§e    TheBestHotel");
					sender.sendMessage("§e    15:1m3k");
					sender.sendMessage("§e    3d 6m 2s");
					sender.sendMessage("§9§oYou can use §20 §9§oas a time to make the rent infinite");
					sender.sendMessage("§9§oIn cost, §5t§9 = §210§9, §5h§9 = §2100§9, §5k§9 = §21000§9, §5m§9 = §2million§9 §5b§9 = §2billion (1000 million)");
					sender.sendMessage(ChatColor.DARK_RED+"Last page. Type §3§o/ht help§r§4 to get to page 1");
					sender.sendMessage("§4==========================");
				}
				else if(((args.length>0)&&(args[0].equalsIgnoreCase("reload"))&&(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.reload")||sender.hasPermission("hotels.*")))))){
					HConH.reloadConfigs(plugin);
					sender.sendMessage(prefix+locale.getString("chat.commands.reload.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("enter"))&&(sender instanceof Player))
						&& (sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.creationMode.enter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					HCM.checkFolder();
					HCM.saveInventory(sender);
					HCM.saveArmour(sender);
					HCM.giveItems(sender);
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("exit"))&&(sender instanceof Player))
						&&(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.creationMode.exit").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					HCM.loadInventory(sender);
					HCM.loadArmour(sender);
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("reset"))&&(sender instanceof Player))
						&&(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					HCM.resetInventoryFiles(sender);
					sender.sendMessage(prefix+locale.getString("chat.commands.creationMode.reset").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode")||
						(args.length == 1)&&(args[0].equalsIgnoreCase("cm"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){

					sender.sendMessage(prefix+locale.getString("chat.commands.creationMode.noarg").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if((args.length>3)&&(args[0].equalsIgnoreCase("friend")||(args[0].equalsIgnoreCase("f")))){
					if(sender instanceof Player){
						if(sender.hasPermission("hotels.friend")){
							//Adding a friend?
							if(args[1].equalsIgnoreCase("add")){
								if(args.length>4){
									String hotel = args[2];
									String room = args[3];
									String friendName = args[4];
									File signFile = new File("plugins//Hotels//Signs//"+hotel+"-"+room+".yml");
									if(signFile.exists()){
										YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
										String renterUUID = signConfig.getString("Sign.renter");
										if(renterUUID!=null){
											Player pl = (Player) sender;
											if(pl.getUniqueId().equals(UUID.fromString(renterUUID))){
												@SuppressWarnings("deprecation")
												OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);
												if(friend.hasPlayedBefore()){
													if(!pl.getUniqueId().equals(friend.getUniqueId())){
														//Adding player as region member
														World fromConfigWorld = Bukkit.getWorld(signConfig.getString("Sign.location.world"));
														String fromConfigRegionName = signConfig.getString("Sign.region");
														ProtectedRegion r = WGM.getRegion(fromConfigWorld, fromConfigRegionName);
														WGM.addMember(friend, r);
														//Adding player to config under friends list
														List<String> stringList = signConfig.getStringList("Sign.friends");
														stringList.add(friend.getUniqueId().toString());
														signConfig.set("Sign.friends", stringList);

														try {
															signConfig.save(signFile);
														} catch (IOException e) {
															e.printStackTrace();
														}
														//Friend /name/ added successfully
														sender.sendMessage(prefix+locale.getString("chat.commands.friend.addSuccess").replaceAll("%friend%", friend.getName()).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
													}
													else
														sender.sendMessage(prefix+locale.getString("chat.commands.friend.addYourself").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
												}
												else
													sender.sendMessage(prefix+locale.getString("chat.commands.friend.nonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
											}
											else
												sender.sendMessage(prefix+locale.getString("chat.commands.friend.notRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										}
										else
											sender.sendMessage(prefix+locale.getString("chat.commands.friend.noRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));	
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.friend.wrongData").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.friend.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							//Removing a friend?
							else if(args[1].equalsIgnoreCase("remove")){
								if(args.length>4){
									String hotel = args[2];
									String room = args[3];
									String friendName = args[4];
									File signFile = new File("plugins//Hotels//Signs//"+hotel+"-"+room+".yml");
									if(signFile.exists()){
										YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
										String renterUUID = signConfig.getString("Sign.renter");
										Player pl = (Player) sender;
										if(renterUUID!=null){
											if(pl.getUniqueId().equals(UUID.fromString(renterUUID))){
												@SuppressWarnings("deprecation")
												OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);
												if(signConfig.getStringList("Sign.friends").contains(friend.getUniqueId().toString())){
													//Removing player as region member
													World fromConfigWorld = Bukkit.getWorld(signConfig.getString("Sign.location.world"));
													String fromConfigRegionName = signConfig.getString("Sign.region");
													ProtectedRegion r = WGM.getRegion(fromConfigWorld, fromConfigRegionName);
													WGM.removeMember(friend, r);
													//Removing player from config under friends list
													List<String> stringList = signConfig.getStringList("Sign.friends");
													stringList.remove(friend.getUniqueId().toString());
													signConfig.set("Sign.friends", stringList);

													try {
														signConfig.save(signFile);
													} catch (IOException e) {
														e.printStackTrace();
													}
													//Friend /name/ removed successfully
													sender.sendMessage(prefix+locale.getString("chat.commands.friend.removeSuccess").replaceAll("%friend%", friend.getName()).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
												}
												else
													sender.sendMessage(prefix+locale.getString("chat.commands.friend.friendNotInList").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
											}
											else
												sender.sendMessage(prefix+locale.getString("chat.commands.friend.notRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										}
										else
											sender.sendMessage(prefix+locale.getString("chat.commands.friend.noRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.friend.wrongData").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.friend.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							else if(args[1].equalsIgnoreCase("list")){
								//Listing friends in specified hotel+room
								String hotel = args[2];
								String room = args[3];
								File signFile = new File("plugins//Hotels//Signs//"+hotel+"-"+room+".yml");
								if(signFile.exists()){
									YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
									String renterUUID = signConfig.getString("Sign.renter");
									Player pl = (Player) sender;
									if(renterUUID!=null){
										if(pl.getUniqueId().equals(UUID.fromString(renterUUID))){
											List<String> stringList = signConfig.getStringList("Sign.friends");
											if(!stringList.isEmpty()){
												hotel = hotel.substring(0, 1).toUpperCase() + hotel.substring(1);
												sender.sendMessage(prefix+locale.getString("chat.commands.friend.list.heading").replaceAll("%room%", room).replaceAll("%hotel%", hotel).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
												for(String currentFriend : stringList){
													OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentFriend));
													String friendName = friend.getName();
													sender.sendMessage(prefix+locale.getString("chat.commands.friend.list.line").replaceAll("%name%", friendName).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));//TODO
												}
												sender.sendMessage(prefix+locale.getString("chat.commands.friend.list.footer").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
											}
											else
												sender.sendMessage(prefix+locale.getString("chat.commands.friend.noFriends").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));	
										}
										else
											sender.sendMessage(prefix+locale.getString("chat.commands.friend.notRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));	
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.friend.noRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.friend.wrongData").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.friend.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
						else
							sender.sendMessage(prefix+locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					else
						sender.sendMessage(prefix+locale.getString("chat.commands.friend.consoleRejected").replaceAll("(?i)&([a-fk-r0-9])", ""));
				}
				else if((((args.length==2)||(args.length==1))&&(args[0].equalsIgnoreCase("check"))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&((sender.hasPermission("hotels.check")||(sender.hasPermission("hotels.check.others"))
								||sender.hasPermission("hotels.*"))))))){
					if(sender instanceof Player){
						if(args.length==1){
							String p = sender.getName();
							check(p, sender);
						}
						else if(args.length>=2){
							if(args[1]==sender.getName()){
								String p = args[1];							
								check(p, sender);
							}
							else if(sender.hasPermission("hotels.check.others")){
								String p = args[1];							
								check(p, sender);
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}if(!(sender instanceof Player)){
						if(args.length>=2){
							String p = args[1];							
							check(p, sender);
						}
						else
							sender.sendMessage(prefix+locale.getString("chat.commands.noPlayer").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
				}
				else if((((args.length>1))&&(args[0].equalsIgnoreCase("roomlist")||args[0].equalsIgnoreCase("rlist"))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.list.rooms")||sender.hasPermission("hotels.*")))))){
					if(args[1]!=null){
						if(sender instanceof Player){
							if(args.length>1){
								Player p = (Player) sender;
								World w = p.getWorld();
								String hotel = args[1];
								if(WGM.hasRegion(w, "hotel-"+hotel)){
									listRooms(hotel,w,sender);
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							else{
								World w = Bukkit.getWorld(args[2]);
								if(w!=null){
									String hotel = args[1];
									if(WGM.hasRegion(w, "hotel-"+hotel)){
										listRooms(hotel,w,sender);
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
						}
						else if(!(sender instanceof Player)){
							if(args.length!=2){
								World w = Bukkit.getWorld(args[2]);
								String hotel = args[1];
								if(w!=null){
									if(WGM.hasRegion(w, "hotel-"+hotel)){
										listRooms(hotel,w,sender);
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
						}
						else
							sender.sendMessage("How did you get here");
					}
					else
						sender.sendMessage(prefix+locale.getString("chat.commands.noHotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if((args.length>0)&&(args[0].equalsIgnoreCase("hotelslist")||args[0].equalsIgnoreCase("hlist")||args[0].equalsIgnoreCase("list"))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.list.hotels")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						if(args.length==1){
							Player p = (Player) sender;
							World w = p.getWorld();
							listHotels(w,sender);
						}
						else{
							World w = Bukkit.getWorld(args[1]);
							if(w!=null){
								listHotels(w,sender);
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}
					else if(!(sender instanceof Player)){
						if(!args[1].isEmpty()){
							World w = Bukkit.getWorld(args[1]);
							listHotels(w,sender);
						}
						else{
							sender.sendMessage(prefix+locale.getString("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
						}
					}
				}
				else if(((args.length>1)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))&&(sender instanceof Player))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.create")||sender.hasPermission("hotels.*"))))){
					Player p = (Player) sender;
					UUID playerUUID = p.getUniqueId();
					File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
					if(file.exists()){
						HCM.hotelSetup(args[1], sender, plugin);
					}
					else
						sender.sendMessage(prefix+locale.getString("chat.commands.create.fail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if((args.length>2)&&(args[0].equalsIgnoreCase("deleteroom")||(args[0].equalsIgnoreCase("delr")))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.delete.rooms")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						if(args.length>=3){
							if(args.length!=4){
								Player p = (Player) sender;
								World world = p.getWorld();
								String hotelname = args[1];
								String roomnum = args[2];
								if(WGM.hasRegion(world, "hotel-"+hotelname)){
									if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
										removeRoom(args[1],roomnum, world,sender);
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.roomNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							else{
								World world = Bukkit.getWorld(args[3]);
								String hotelname = args[1];
								String roomnum = args[2];
								if(WGM.hasRegion(world, "hotel-"+hotelname)){
									if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
										removeRoom(args[1],roomnum, world,sender);
									}
									else
										sender.sendMessage(prefix+locale.getString("chat.commands.roomNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
						}
						else
							sender.sendMessage(prefix+locale.getString("chat.commands.deleteRoom.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					else if(!(sender instanceof Player)){
						if(args.length>=4){
							World world = Bukkit.getWorld(args[3]);
							String hotelname = args[1];
							String roomnum = args[2];
							if(WGM.hasRegion(world, "hotel-"+hotelname)){
								if(WGM.hasRegion(world, "hotel-"+hotelname+"-"+roomnum)){
									removeRoom(args[1],roomnum, world,sender);
								}
								else
									sender.sendMessage(prefix+locale.getString("chat.commands.roomNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}
				}
				else if(((args.length>2)&&(args[0].equalsIgnoreCase("rename")||args[0].equalsIgnoreCase("ren")))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.rename")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						if(args.length==3){
							Player p = (Player) sender;
							World world = p.getWorld();
							renameHotel(args[1],args[2],world,sender);
						}
						else{
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								renameHotel(args[1],args[2],world,sender);
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}
					else if(!(sender instanceof Player)){
						if(args.length>3){
							World world = Bukkit.getWorld(args[3]);
							if(world!=null){
								renameHotel(args[1],args[2],world,sender);
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
						else
							sender.sendMessage(prefix+locale.getString("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
				}
				else if(((args.length>3)&&(args[0].equalsIgnoreCase("renumber")||args[0].equalsIgnoreCase("renum")))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.renumber")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						if(args.length==4){
							Player p = (Player) sender;
							World world = p.getWorld();
							renumber(args[1],args[2],args[3],world,sender);
						}
						else{
							World world = Bukkit.getWorld(args[4]);
							if(world!=null){
								renumber(args[1],args[2],args[3],world,sender);
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}
					else if(!(sender instanceof Player)){
						if(args.length>4){
							World world = Bukkit.getWorld(args[4]);
							if(world!=null){
								renumber(args[1],args[2],args[3],world,sender);
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
						else
							sender.sendMessage(prefix+locale.getString("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
				}
				else if(((args.length==2||args.length==3)&&(args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("del")))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.delete")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						Player p = (Player) sender;
						World world = p.getWorld();
						removeSigns(args[1],world,sender);
						removeRegions(args[1],world,sender);
					}
					else if((args.length == 3)){
						World world = Bukkit.getWorld(args[2]);
						removeSigns(args[1],world,sender);
						removeRegions(args[1],world,sender);
					}
				}
				else if((args.length>=4)&&args[0].equalsIgnoreCase("remove")&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.remove")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						Player p = (Player) sender;
						if(args.length>4){
							World w = Bukkit.getWorld(args[4]);
							removePlayer(w, args[2], args[3], args[1], sender);
						}
						else
							removePlayer(p.getWorld(), args[2], args[3], args[1], sender);
					}
					else if(args.length>=5)
						removePlayer(Bukkit.getWorld(args[4]), args[2], args[3], args[1], sender);
					else
						sender.sendMessage(prefix+locale.getString("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
				}

				else if(((args.length<4)&&(args[0].equalsIgnoreCase("remove")))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.renumber")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.remove.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length<4)&&(args[0].equalsIgnoreCase("renumber")||(args[0].equalsIgnoreCase("renum"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.renumber")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.renumber.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length<3)&&(args[0].equalsIgnoreCase("rename")||(args[0].equalsIgnoreCase("ren"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.rename")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.rename.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length<3)&&(args[0].equalsIgnoreCase("deleteroom")||(args[0].equalsIgnoreCase("delr"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.delete.rooms")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.deleteRoom.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length==1)&&(args[0].equalsIgnoreCase("roomlist")||(args[0].equalsIgnoreCase("rlist"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.list.rooms")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.noHotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length==1)&&(args[0].equalsIgnoreCase("delete")||(args[0].equalsIgnoreCase("del"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.noHotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if((args.length==2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))||(args.length == 1)&&(args[0].equalsIgnoreCase("create")||(args.length == 1)&&(args[0].equalsIgnoreCase("c"))&&!(sender instanceof Player))){
					sender.sendMessage(prefix+locale.getString("chat.commands.create.consoleRejected").replaceAll("(?i)&([a-fk-r0-9])", ""));
				}
				else if(((args.length==2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))&&(sender instanceof Player))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.create")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(prefix+locale.getString("chat.commands.create.noName").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if((args.length==2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode"))&&!(sender instanceof Player)){
					sender.sendMessage(prefix+locale.getString("chat.commands.creationMode.consoleRejected").replaceAll("(?i)&([a-fk-r0-9])", ""));
				}
				else if((args.length<4)&&(args[0].equalsIgnoreCase("friend")||(args[0].equalsIgnoreCase("f")))){
					if(sender.hasPermission("chat.commands.friend"))
						sender.sendMessage(prefix+locale.getString("chat.commands.friend.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					else
						sender.sendMessage(prefix+locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else if(((args.length>=2)&&(args[0].equalsIgnoreCase("room"))&&(sender instanceof Player))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.sign.create")||sender.hasPermission("hotels.*"))))){
					String hotelName = args[1];
					Player p = (Player) sender;

					if(!(WGM.hasRegion(p.getWorld(), "Hotel-"+hotelName)))
						sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					else{
						if(args.length>2){
							try{
								int roomNum = Integer.parseInt(args[2]);
								HCM.roomSetup(hotelName, roomNum, sender,plugin);
								hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1);
								String roomNums = String.valueOf(roomNum);
								roomNums = roomNums.substring(0, 1).toUpperCase() + roomNums.substring(1);
								sender.sendMessage(prefix+locale.getString("chat.commands.room.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%room%", String.valueOf(roomNum))
										.replaceAll("%hotel%", hotelName));
							} catch(NumberFormatException e){
								sender.sendMessage(prefix+locale.getString("chat.commands.room.roomNumInvalid").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
						}
						//Player did not specify room number
						else{
							int roomNum = nextNewRoom(p.getWorld(),hotelName);
							if(roomNum!=0){
								HCM.roomSetup(hotelName, roomNum, sender,plugin);
								hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1);
								String roomNums = String.valueOf(roomNum);
								roomNums = roomNums.substring(0, 1).toUpperCase() + roomNums.substring(1);
								sender.sendMessage(prefix+locale.getString("chat.commands.room.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%room%", String.valueOf(roomNum))
										.replaceAll("%hotel%", hotelName));
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.room.nextNewRoomFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}
				}
				else if(((args.length ==1)||(args.length ==2))&&(args[0].equalsIgnoreCase("room"))){
					sender.sendMessage(prefix+locale.getString("chat.commands.room.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else {
					sender.sendMessage(prefix+locale.getString("chat.commands.unknownArg").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
			}
		}
		return false;
	}
	/*private void setHome(CommandSender sender){
		if(sender instanceof Player){
			Player p = (Player) sender;
			Location loc = p.getLocation();
			World w = p.getWorld();
			ApplicableRegionSet regions = WGM.getWorldGuard().getRegionManager(w).getApplicableRegions(loc);
			ArrayList<ProtectedRegion> rf = null;
			for(ProtectedRegion r : regions) {
			    //Regions that match player's location
				rf.add(r);
			}
			if(rf!=null){
				rf.contains(o)
				for(ProtectedRegion r : rf){

				}
			}
			else
				//Player is not in any region

		}
		}*/
	private void renumber(String hotel,String oldnum,String newnum, World world,CommandSender sender){
		if(Integer.parseInt(newnum)<100000){
			if(WGM.hasRegion(world, "Hotel-"+hotel)){
				if(WGM.hasRegion(world, "Hotel-"+hotel+"-"+oldnum)){
					WGM.renameRegion("Hotel-"+hotel+"-"+oldnum, "Hotel-"+hotel+"-"+newnum, world);

					File file = new File("plugins//Hotels//Signs//"+hotel+"-"+oldnum+".yml");
					if(file.exists()){
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						World signworld = Bukkit.getWorld(config.getString("Sign.location.world").trim());
						int signx = config.getInt("Sign.location.coords.x");
						int signy = config.getInt("Sign.location.coords.y");
						int signz = config.getInt("Sign.location.coords.z");
						Block b = signworld.getBlockAt(signx,signy,signz);

						if(world==signworld){
							if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
								Sign s = (Sign) b.getState();
								String Line1 = ChatColor.stripColor(s.getLine(0));
								String Line2 = ChatColor.stripColor(s.getLine(1));
								String signroom = Line2.split(" ")[1];
								if(Line1.toLowerCase().matches(hotel.toLowerCase())){
									if(WGM.hasRegion(signworld, "Hotel-"+hotel)){
										if(WGM.getRegion(signworld, "Hotel-"+hotel).contains(signx, signy, signz)){
											if(signroom.trim().toLowerCase().matches(oldnum.trim().toLowerCase())){
												String roomS = prefix+locale.getString("chat.commands.unknownArg").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1");
												s.setLine(1, roomS.replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+newnum+" - "+Line2.split(" ")[3]);
												s.update();
												config.set("Sign.room", Integer.valueOf(newnum));
												config.set("Sign.region", "hotel-"+hotel+"-"+newnum);
												try {
													config.save(file);
												} catch (IOException e) {
													e.printStackTrace();
												}
												File newfile = new File("plugins//Hotels//Signs//"+hotel+"-"+newnum+".yml");
												file.renameTo(newfile);
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
							else{
								b.setType(Material.AIR);
								file.delete();
							}
						}
						else{
							b.setType(Material.AIR);
							file.delete();
						}
						ProtectedRegion r = WGM.getRegion(world, "Hotel-"+hotel+"-"+newnum);
						String idHotelName = r.getId();
						String[] partsofhotelName = idHotelName.split("-");
						String fromIdhotelName = partsofhotelName[1].substring(0, 1).toUpperCase() + partsofhotelName[1].substring(1);
						r.setFlag(DefaultFlag.GREET_MESSAGE, ("&cWelcome to Room "+newnum));
						try {
							WGM.getWorldGuard().getRegionManager(world).save();
							sender.sendMessage(prefix+locale.getString("chat.commands.renumber.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%oldnum%", oldnum).replaceAll("%newnum%", partsofhotelName[2]).replaceAll("%hotel%", fromIdhotelName));
						} catch (StorageException e) {
							sender.sendMessage(prefix+locale.getString("chat.commands.renumber.fail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%oldnum%", oldnum));
							e.printStackTrace();
						}
					}
					else
						sender.sendMessage(prefix+locale.getString("chat.commands.renumber.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%oldnum%", oldnum).replaceAll("%newnum%", newnum).replaceAll("%hotel%", hotel));
				}
				else
					sender.sendMessage(prefix+locale.getString("chat.commands.roomNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			else
				sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
		else
			sender.sendMessage(prefix+locale.getString("chat.commands.renumber.newNumTooBig").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}
	private void renameHotel(String oldname,String newname, World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+oldname)){
			WGM.renameRegion("Hotel-"+oldname, "Hotel-"+newname, world);
			ProtectedRegion r = WGM.getRegion(world, "Hotel-"+newname);
			String idHotelName = r.getId();
			String[] partsofhotelName = idHotelName.split("-");
			String fromIdhotelName = partsofhotelName[1].substring(0, 1).toUpperCase() + partsofhotelName[1].substring(1);
			r.setFlag(DefaultFlag.GREET_MESSAGE, ("&cWelcome to the "+fromIdhotelName+" hotel"));
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, ("&gCome back soon to the "+fromIdhotelName+" hotel"));
			sender.sendMessage(prefix+locale.getString("chat.commands.rename.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%hotel%" , fromIdhotelName));
			Map<String, ProtectedRegion> regionlist = WGM.getWorldGuard().getRegionManager(world).getRegions();
			//Rename rooms
			for(int i = regionlist.size(); i>0; i--){
				if(WGM.hasRegion(world, "Hotel-"+oldname+"-"+i)){
					WGM.renameRegion("Hotel-"+oldname+"-"+i, "Hotel-"+newname+"-"+i, world);
				}
			}
			try {
				WGM.getWorldGuard().getRegionManager(world).save();
			} catch (StorageException e) {
				sender.sendMessage(prefix+locale.getString("chat.commands.rename.failRooms").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				e.printStackTrace();
			}
		}
		else
			sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}
	private void removeRoom(String hotelName,String roomNum,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName+"-"+roomNum)){//If region exists
			WGM.getWorldGuard().getRegionManager(world).removeRegion("Hotel-"+hotelName+"-"+roomNum);//Delete region
			try {
				WGM.getWorldGuard().getRegionManager(world).save();
				sender.sendMessage(prefix+locale.getString("chat.commands.removeRoom.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			} catch (StorageException e) {
				sender.sendMessage(prefix+locale.getString("chat.commands.removeRoom.fail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				e.printStackTrace();
			}

		}
	}
	private void removeRegions(String hotelName,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName)){
			WGM.getWorldGuard().getRegionManager(world).removeRegion("Hotel-"+hotelName);
			Map<String, ProtectedRegion> regionlist = WGM.getWorldGuard().getRegionManager(world).getRegions();

			for(ProtectedRegion values : regionlist.values()){
				if(values.getId().matches("hotel-"+hotelName+"-"+"[0-9]+")){
					ProtectedRegion goodregion = values;
					WGM.getWorldGuard().getRegionManager(world).removeRegion(goodregion.getId());
				}
			}

			try {
				WGM.getWorldGuard().getRegionManager(world).save();
				sender.sendMessage(prefix+locale.getString("chat.commands.removeRegions.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			} catch (StorageException e) {
				sender.sendMessage(prefix+locale.getString("chat.commands.removeRegions.fail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				e.printStackTrace();
			}
		}
		else{
			if(sender instanceof Player)
				sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			else
				sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
		}
	}
	private void removePlayer(World w, String hotel, String room,String toRemovePlayer,CommandSender sender){
		if(w!=null){
			if(WGM.hasRegion(w, "hotel-"+hotel)){
				if(WGM.hasRegion(w, "hotel-"+hotel+"-"+room)){
					@SuppressWarnings("deprecation")
					Player player = Bukkit.getOfflinePlayer(toRemovePlayer).getPlayer();
					if(player!=null){
						File file = new File("plugins//Hotels//Signs//"+hotel+"-"+room+".yml");
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						String renter = config.getString("Sign.renter");
						if(renter!=null){
							Player pfromfile = Bukkit.getOfflinePlayer(UUID.fromString(renter)).getPlayer();
							if(player.equals(pfromfile)){
								ProtectedRegion r = WGM.getWorldGuard().getRegionManager(w).getRegion("hotel-"+hotel+"-"+room);
								WGM.removeMember(player, r);
								//Config stuff
								config.set("Sign.renter", null);
								config.set("Sign.timeRentedAt", null);
								config.set("Sign.expiryDate", null);
								config.set("Sign.friends", null);
								config.set("Sign.extended", null);
								try {
									config.save(file);
								} catch (IOException e) {
									e.printStackTrace();
								}
								//Gameloop?
								GameLoop gameloop = new GameLoop(this);
								gameloop.run();
								sender.sendMessage(prefix+locale.getString("chat.commands.remove.success").replaceAll("%player%", player.getName()).replaceAll("%room%", room)
										.replaceAll("%hotel%", hotel).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
							else
								sender.sendMessage(prefix+locale.getString("chat.commands.remove.playerNotRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));	
						}
						else
							sender.sendMessage(prefix+locale.getString("chat.commands.remove.noRenter").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					else
						sender.sendMessage(prefix+locale.getString("chat.commands.userNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else
					sender.sendMessage(prefix+locale.getString("chat.commands.roomNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			else
				sender.sendMessage(prefix+locale.getString("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
		else
			sender.sendMessage(prefix+locale.getString("chat.commands.worldNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}
	private void check(String playername, CommandSender sender){
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		List<World> worlds = Bukkit.getWorlds();
		@SuppressWarnings("deprecation")
		OfflinePlayer p = Bukkit.getOfflinePlayer(playername);
		if(p!=null&&p.hasPlayedBefore()){
			sender.sendMessage(prefix+locale.getString("chat.commands.check.heading").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%player%", playername));
			for(World w:worlds){
				regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
				ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
				if(rlist.length>0){
					for(ProtectedRegion r:rlist){
						if(r.getId().toLowerCase().startsWith("hotel-")){ //If it's a hotel
							if(r.getId().toLowerCase().matches("^hotel-.+-.+")){ //If it's a room
								if(r.getMembers().contains(WGM.getWorldGuard().wrapOfflinePlayer(p))){
									String[] rId = r.getId().toLowerCase().split("-");
									String hotelname = rId[1].replaceAll("-", "");
									String roomnum = rId[2].replaceAll("-", "");
									File file = new File("plugins//Hotels//Signs//"+hotelname+"-"+roomnum+".yml");
									YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
									long expiryDate = config.getLong("Sign.expiryDate");
									hotelname = hotelname.substring(0, 1).toUpperCase() + hotelname.substring(1);
									if(expiryDate>0){
										long currentmins = System.currentTimeMillis()/1000/60;
										String timeleft = SM.TimeFormatter(expiryDate-currentmins);
										sender.sendMessage(prefix+locale.getString("chat.commands.check.line").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")
												.replaceAll("%hotel%", hotelname).replaceAll("%room%", roomnum).replaceAll("%timeleft%", String.valueOf(timeleft)));
									}
									else{
										sender.sendMessage(prefix+locale.getString("chat.commands.check.line").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")
												.replaceAll("%hotel%", hotelname).replaceAll("%room%", roomnum).replaceAll("%timeleft%", locale.getString("sign.permanent")));
									}
										
								}
							}
						}
					}
				}
			}
			sender.sendMessage(prefix+locale.getString("chat.commands.check.footer").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%player%", playername));
		}
		else
			sender.sendMessage(prefix+locale.getString("chat.commands.userNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}
	private void listHotels(World w, CommandSender sender){
		sender.sendMessage(prefix+locale.getString("chat.commands.listHotels.heading").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		for(ProtectedRegion r:rlist){
			String id = r.getId();
			if(id.startsWith("hotel-")){ //If it's a hotel
				if(!id.matches("^hotel-.+-.+")){ //if it's not a room
					String hotelName = (id.replaceFirst("hotel-", "")).toLowerCase();
					hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1);
					sender.sendMessage(prefix+locale.getString("chat.commands.listHotels.line").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%hotel%", hotelName));
				}
			}
		}
		sender.sendMessage(prefix+locale.getString("chat.commands.listHotels.footer").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}
	private void listRooms(String hotel, World w, CommandSender sender){
		String hotelName = hotel.substring(0, 1).toUpperCase() + hotel.substring(1);
		sender.sendMessage(prefix+locale.getString("chat.commands.listRooms.heading").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%hotel%", hotelName));
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		if(rlist.length>0){
			for(ProtectedRegion r : rlist){
				String id = r.getId();
				if(id.startsWith("hotel-")){ //If it's a hotel
					if(id.matches("^hotel-"+hotel.toLowerCase()+"-.+")){ //If it's a room of the specified hotel
						String roomnum = (id.replaceAll("hotel-.+-", ""));
						sender.sendMessage(prefix+locale.getString("chat.commands.listRooms.line").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%room%", roomnum));
					}
				}
			}
		}
		else
			sender.sendMessage(prefix+locale.getString("chat.commands.listRooms.noRooms").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		sender.sendMessage(prefix+locale.getString("chat.commands.listRooms.footer").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1").replaceAll("%hotel%", hotelName));
	}
	private void removeSigns(String hotelName,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName)){
			ArrayList<String> fileslist = HFF.listFiles("plugins//Hotels//Signs");
			for(String x: fileslist){
				File file = new File("plugins//Hotels//Signs//"+x);
				String receptionLoc = locale.getString("sign.reception").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1");
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
									if(WGM.getWorldGuard().getRegionManager(worldsign).getRegion("Hotel-"+hotelname).contains(locx, locy, locz)){
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
				sender.sendMessage(prefix+locale.getString("chat.commands.removeSigns.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
		}
	}
	private int nextNewRoom(World w, String hotel){
		if(WGM.hasRegion(w, "Hotel-"+hotel)){
			Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
			regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
			for(int i=0; i<regions.size(); i++){
				if(!WGM.hasRegion(w, "Hotel-"+hotel+"-"+(i+1)))
					return i+1;
			}
		}
		else
			return 0;
		return 0;
	}
}
