package handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import managers.HotelsFileFinder;
import managers.WorldGuardManager;
import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsCommandHandler implements CommandExecutor {
	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain hCH)
	{
		this.plugin = hCH;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,String[] args) {
		if(cmd.getName().equalsIgnoreCase("Hotels")){
			if(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.commands")||sender.hasPermission("hotels.*")))){

				if(args.length == 0){
					sender.sendMessage("§4==========Hotels==========");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" plugin by kernitus");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion());
					sender.sendMessage("§4Type §3/hotels help §4for help with creating a hotel");
					sender.sendMessage("§4Type §3/hotels commands §4for help with the commands");
				}
				else if(args.length==1&&args[0].equalsIgnoreCase("commands")){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§2--Hotels plugin command help page--");
					sender.sendMessage("§e/hotels creationmode [enter/exit] - §aEnter/exit creation mode");
					sender.sendMessage("§e/hotels help - §aDisplays help page");
					sender.sendMessage("§e/hotels list - §aList all hotels in current world");
					sender.sendMessage("§e/hotels create [hotelname] - §aCreate a hotel with current selection");
					sender.sendMessage("§e/hotels delete [hotelname] - §aDelete specified hotel");
					sender.sendMessage("§e/hotels room [hotelname] [roomnum] - §aCreate room with current selection within specified hotel");
					sender.sendMessage("§4==========================");
				}
				else if((args.length == 1)&&(args[0].equalsIgnoreCase("help"))||(args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("1")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§2--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 1- §9Selection of hotel cuboid");
					sender.sendMessage(ChatColor.YELLOW+"Type §3§o/hotels cm enter");
					sender.sendMessage(ChatColor.YELLOW+"Take your WorldEdit wand in hand");
					sender.sendMessage(ChatColor.YELLOW+"Left click and right click the opposite corners of your hotel");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/hotels help 2§r§4 to get to page 2");
					sender.sendMessage("§4==========================");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("2")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§2--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 2- §9Creation of the hotel");
					sender.sendMessage(ChatColor.YELLOW+"Type §3§o/hotels create nameofhotel");
					sender.sendMessage(ChatColor.YELLOW+"Go die in a hole");
					sender.sendMessage(ChatColor.YELLOW+"Then explode in mid-air");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/hotels help 3§r§4 to get to page 3");
					sender.sendMessage("§4==========================");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("3")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§2--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 3- §9Creation of a room cuboid");
					sender.sendMessage(ChatColor.YELLOW+"Type §3§o/hotels wand room");
					sender.sendMessage(ChatColor.YELLOW+"Left click and right click the corners of your room");
					sender.sendMessage(ChatColor.YELLOW+"Type §3§o/hotels room <hotel> <roomnum>");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/hotels help 4§r§4 to get to page 4");
					sender.sendMessage("§4==========================");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("4")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§2--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 4- §9Adding a sign");
					sender.sendMessage(ChatColor.YELLOW+"Grab a sign and place it next to the door of the room");
					sender.sendMessage(ChatColor.YELLOW+"Type on the sign:");
					sender.sendMessage(ChatColor.YELLOW+"[Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"<hotelname>");
					sender.sendMessage(ChatColor.YELLOW+"<roomnumber>");
					sender.sendMessage(ChatColor.YELLOW+"<cost:time>");
					sender.sendMessage(ChatColor.DARK_RED+"Type §3§o/hotels help 5§r§4 to get to page 5");
					sender.sendMessage("§4==========================");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("5")))){
					sender.sendMessage("§4==========================");
					sender.sendMessage("§2--Hotels plugin help page--");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 5- §9Example of a sign");
					sender.sendMessage(ChatColor.YELLOW+"Example of a sign:");
					sender.sendMessage(ChatColor.YELLOW+"[Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"TheBestHotel");
					sender.sendMessage(ChatColor.YELLOW+"15");
					sender.sendMessage(ChatColor.YELLOW+"200:3d");
					sender.sendMessage(ChatColor.DARK_RED+"Last page. Type §3§o/hotels help§r§4 to get to page 1");
					sender.sendMessage("§4==========================");
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("enter"))&&(sender instanceof Player))
						&& (sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(ChatColor.GREEN+"You have entered hotel creation mode.");
					HotelsCreationMode.checkFolder();
					HotelsCreationMode.saveInventory(sender);
					HotelsCreationMode.saveArmour(sender);
					HotelsCreationMode.giveItems(sender);
					if(plugin.getConfig().getBoolean("HCM.bossBar")==true)
						BarAPI.setMessage((Player) sender, "§2Hotel Creation Mode");
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("exit"))&&(sender instanceof Player))
						&&(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(ChatColor.GREEN+"You have exited hotel creation mode.");
					HotelsCreationMode.loadInventory(sender);
					HotelsCreationMode.loadArmour(sender);

					if(plugin.getConfig().getBoolean("HCM.bossBar")==true){
						BarAPI.removeBar((Player) sender);
					}
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("reset"))&&(sender instanceof Player))
						&&(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					HotelsCreationMode.resetInventoryFiles(sender);
					sender.sendMessage("§2The inventory files have been reset");
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode")||
						(args.length == 1)&&(args[0].equalsIgnoreCase("cm"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){

					sender.sendMessage(ChatColor.DARK_RED+"Please specify "+ChatColor.YELLOW+"enter"+ChatColor.DARK_RED+" or "+ChatColor.YELLOW+"exit "+ChatColor.DARK_RED+"mode");
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
								sender.sendMessage("§4You do not have permission!");
						}
					}if(!(sender instanceof Player)){
						if(args.length>=2){
							String p = args[1];							
							check(p, sender);
						}
						else
							sender.sendMessage("Please specify player");
					}
				}
				else if((((args.length==2)||(args.length==3))&&(args[0].equalsIgnoreCase("roomlist")||args[0].equalsIgnoreCase("rlist"))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.listrooms")||sender.hasPermission("hotels.*")))))){
					if(args[1]!=null){
						if(sender instanceof Player){
							Player p = (Player) sender;
							World w = p.getWorld();
							String hotel = args[1];
							if(WorldGuardManager.getWorldGuard().getRegionManager(w).hasRegion("hotel-"+hotel)){
								listRooms(hotel,w,sender);
							}
							else
								sender.sendMessage("§4Specified hotel does not exist");
						}
						else if(!(sender instanceof Player)){
							if(args.length!=2){
								World w = Bukkit.getWorld(args[2]);
								String hotel = args[1];
								if(w!=null){
									if(WorldGuardManager.getWorldGuard().getRegionManager(w).hasRegion("hotel-"+hotel)){
										listRooms(hotel,w,sender);
									}
									else
										sender.sendMessage("Specified hotel does not exist");
								}
								else
									sender.sendMessage("World does not exist");
							}
							else
								sender.sendMessage("Please specify world");
						}
						else
							sender.sendMessage("How did you get here");
					}
					else
						sender.sendMessage(ChatColor.DARK_RED+"Please specify hotel");
				}
				else if((((args.length==2)||(args.length==1))&&(args[0].equalsIgnoreCase("hotelslist")||args[0].equalsIgnoreCase("hlist"))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.listhotels")||sender.hasPermission("hotels.*")))))){
					if(sender instanceof Player){
						Player p = (Player) sender;
						World w = p.getWorld();
						listHotels(w,sender);
					}
					else if(!(sender instanceof Player)){
						if(!args[1].isEmpty()){
							World w = Bukkit.getWorld(args[1]);
							listHotels(w,sender);
						}
						else{
							sender.sendMessage("Please specify world");
						}
					}
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))&&(sender instanceof Player))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					Player p = (Player) sender;
					UUID playerUUID = p.getUniqueId();
					File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
					if(file.exists()){
						HotelsCreationMode.hotelSetup(args[1], sender);
					}
					else
						sender.sendMessage("§4Could not create hotel. Did you enter Hotel Creation Mode? (§3§o/hotels cm enter§r§4)");
				}
				else if(((args.length == 2)||(args.length == 3)&&(args[0].equalsIgnoreCase("delete")||(args[0].equalsIgnoreCase("del"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					if(sender instanceof Player){
						Player p = (Player) sender;
						World world = p.getWorld();
						removeSigns(args[1],world,sender);
						removeRegions(args[1],world,sender);
					}
					else if((sender instanceof Player)&&(args.length == 3)){
						World world = Bukkit.getWorld(args[2]);
						removeSigns(args[1],world,sender);
						removeRegions(args[1],world,sender);
					}
					else{}
				}
				else if(((args.length == 1)&&(args[0].equalsIgnoreCase("rlist")||(args[0].equalsIgnoreCase("roomlist"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.listrooms")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage("§cPlease specify the hotel name");
				}
				else if(((args.length == 1)&&(args[0].equalsIgnoreCase("delete")||(args[0].equalsIgnoreCase("del"))))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.createmode")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage("§cPlease specify the hotel name");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))||(args.length == 1)&&(args[0].equalsIgnoreCase("create")||(args.length == 1)&&(args[0].equalsIgnoreCase("c"))&&!(sender instanceof Player))){
					sender.sendMessage("§4The console can't create a hotel!");
				}
				else if(((args.length == 2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))&&(sender instanceof Player))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.create")||sender.hasPermission("hotels.*"))))){
					sender.sendMessage(ChatColor.DARK_RED+"Give a name to your hotel!");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode"))&&!(sender instanceof Player)){
					sender.sendMessage("§4The console can't use hotel creation mode!");
				}
				else if(((args.length == 3)&&(args[0].equalsIgnoreCase("room"))&&(sender instanceof Player))&&
						(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.sign.create")||sender.hasPermission("hotels.*"))))){
					String hotelName = args[1];
					Player p = (Player) sender;

					if(!(WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).hasRegion("Hotel-"+hotelName)))
						sender.sendMessage("§4The specified hotel does not exist");
					else{
						try{
							int roomNum = Integer.parseInt(args[2]);
							HotelsCreationMode.roomSetup(hotelName, roomNum, sender);
							hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1);
							String roomNums = String.valueOf(roomNum);
							roomNums = roomNums.substring(0, 1).toUpperCase() + roomNums.substring(1);
							sender.sendMessage("§aYou created room "+roomNum+" of the "+hotelName+" hotel");
						} catch(NumberFormatException e){
							sender.sendMessage("§4The room number is not an integer!");
						}
					}	
				}
				else if(((args.length ==1)||(args.length ==2))&&(args[0].equalsIgnoreCase("room"))){
					sender.sendMessage("§4Correct Usage: §6§o/hotels room hotelname roomnum");
				}
				else {
					sender.sendMessage("§4Unknown argument. Try §3§o/hotels");
				}
			}
		}
		return false;
	}
	private void removeRoom(String hotelName,int roomNum,World world,CommandSender sender){
		if(WorldGuardManager.getWorldGuard().getRegionManager(world).hasRegion("Hotel-"+hotelName)){//If hotel exists
			WorldGuardManager.getWorldGuard().getRegionManager(world).removeRegion("Hotel-"+hotelName);

			Map<String, ProtectedRegion> regionlist = WorldGuardManager.getWorldGuard().getRegionManager(world).getRegions();
			int Counter = regionlist.size();
			while(Counter>0){
				if(WorldGuardManager.getWorldGuard().getRegionManager(world).hasRegion("Hotel-"+hotelName+"-"+Counter)){
					ProtectedRegion goodregion = WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion("Hotel-"+hotelName+"-"+Counter);
					WorldGuardManager.getWorldGuard().getRegionManager(world).removeRegion(goodregion.getId());
					Counter--;
				}
				else
					Counter--;
			}
			try {
				WorldGuardManager.getWorldGuard().getRegionManager(world).save();
				sender.sendMessage("§aSuccessfully deleted hotel regions");
			} catch (StorageException e) {
				sender.sendMessage("§4Could not delete hotel regions");
				e.printStackTrace();
			}

		}
	}
	private void removeRegions(String hotelName,World world,CommandSender sender){
		if(WorldGuardManager.getWorldGuard().getRegionManager(world).hasRegion("Hotel-"+hotelName)){
			WorldGuardManager.getWorldGuard().getRegionManager(world).removeRegion("Hotel-"+hotelName);

			Map<String, ProtectedRegion> regionlist = WorldGuardManager.getWorldGuard().getRegionManager(world).getRegions();
			int Counter = regionlist.size();
			while(Counter>0){
				if(WorldGuardManager.getWorldGuard().getRegionManager(world).hasRegion("Hotel-"+hotelName+"-"+Counter)){
					ProtectedRegion goodregion = WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion("Hotel-"+hotelName+"-"+Counter);
					WorldGuardManager.getWorldGuard().getRegionManager(world).removeRegion(goodregion.getId());
					Counter--;
				}
				else
					Counter--;
			}
			try {
				WorldGuardManager.getWorldGuard().getRegionManager(world).save();
				sender.sendMessage("§aSuccessfully deleted hotel regions");
			} catch (StorageException e) {
				sender.sendMessage("§4Could not delete hotel regions");
				e.printStackTrace();
			}

		}
	}
	private void check(String player, CommandSender sender){
		sender.sendMessage("§a==Rented rooms list for "+player+"==");
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		List<World> worlds = Bukkit.getWorlds();
		int f;
		for(f=0; f<worlds.size(); f++){
			World w = worlds.get(f);
			regions = WorldGuardManager.getWorldGuard().getRegionManager(w).getRegions();
			ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
			int i;
			if(!(rlist.length<0)){
				for(i=0; i<rlist.length; i++){
					ProtectedRegion r = rlist[i];
					if(r.getId().startsWith("hotel-")){ //If it's a hotel
						if(r.getId().matches("^hotel-.+-.+")){ //If it's a room
							Player p = Bukkit.getPlayer(player);
							if(p!=null){
								UUID puuid = p.getUniqueId();
								if(r.getMembers().contains(puuid)){
									String[] rId = r.getId().split("-");
									String hotelname = rId[1].replaceAll("-", "");
									String roomnum = rId[2].replaceAll("-", "");
									File file = new File("plugins//Hotels//Signs//"+hotelname+"-"+roomnum+".yml");
									YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
									double expirydate = config.getDouble("Sign.expiryDate");
									expirydate = expirydate-(System.currentTimeMillis()/1000/60);
									if(expirydate>0){
									DecimalFormat df = new DecimalFormat("###.#");
									hotelname = hotelname.substring(0, 1).toUpperCase() + hotelname.substring(1);
									sender.sendMessage("§6Hotel: §c"+hotelname+"    §6Room: §c"+roomnum+"    §6Expires in: "+df.format(expirydate)+" minutes");
								}
								}
							}
							else
								sender.sendMessage("§4User does not exist!");
						}
					}
				}
			}
		}
	}
	private void listHotels(World w, CommandSender sender){
		sender.sendMessage("§a==Hotel list==");
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WorldGuardManager.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		int i;
		for(i=0; i<rlist.length; i++){
			ProtectedRegion r = rlist[i];
			if(r.getId().startsWith("hotel-")){ //If it's a hotel
				if(!r.getId().matches("^hotel-.+-.+")){ //if it's not a room
					String hotelName = (r.getId().replaceFirst("hotel-", "")).toLowerCase();
					hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1);
					sender.sendMessage("§6Hotel: §c"+hotelName);
				}
			}
		}
	}
	private void listRooms(String hotel, World w, CommandSender sender){
		String hotelName = hotel.substring(0, 1).toUpperCase() + hotel.substring(1);
		sender.sendMessage("§a==Room list for "+hotelName+" hotel==");
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WorldGuardManager.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		int i;
		if(!(rlist.length<0)){
			for(i=0; i<rlist.length; i++){
				ProtectedRegion r = rlist[i];
				if(r.getId().startsWith("hotel-")){ //If it's a hotel
					if(r.getId().matches("^hotel-.+-.+")){ //If it's a room
						String roomnum = (r.getId().replaceAll("hotel-.+-", ""));
						sender.sendMessage("§6Room n: §c"+roomnum);
					}
				}
			}
		}
		else
			sender.sendMessage("§4There are no rooms in that hotel");	
	}
	private void removeSigns(String hotelName,World world,CommandSender sender){
		if(WorldGuardManager.getWorldGuard().getRegionManager(world).hasRegion("Hotel-"+hotelName)){
			ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins//Hotels//Signs");

			for(String x: fileslist){
				File file = new File("plugins//Hotels//Signs//"+x);
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
					sender.sendMessage("§aSuccessfully destroyed sign "+x);
				}
			}
			sender.sendMessage("§aSuccessfully destroyed all signs");
		}
	}
}
