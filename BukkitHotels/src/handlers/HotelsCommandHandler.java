package handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;

import java.io.File;
import java.util.UUID;

import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HotelsCommandHandler implements CommandExecutor {
	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain hCH)
	  {
	    this.plugin = hCH;
	  }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,String[] args) {
		if(cmd.getName().equalsIgnoreCase("Hotels")){
			if(sender.hasPermission("hotels.admin")||sender.isOp()){
				
				if(args.length == 0){
					sender.sendMessage("§4==========Hotels==========");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" plugin by kernitus");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion());
					sender.sendMessage("§4Type §3/hotels help §4for help with the hotels");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("1"))||(args.length == 1)&&(args[0].equalsIgnoreCase("help")))){
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
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("enter"))&&(sender instanceof Player)){
					sender.sendMessage(ChatColor.GREEN+"You have entered hotel creation mode.");
					HotelsCreationMode.checkFolder();
					HotelsCreationMode.saveInventory(sender);
					HotelsCreationMode.saveArmour(sender);
					HotelsCreationMode.giveItems(sender);
					if(plugin.getConfig().getBoolean("HCM.bossBar")==true)
					BarAPI.setMessage((Player) sender, "§2Hotel Creation Mode");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("exit"))&&(sender instanceof Player)){
					sender.sendMessage(ChatColor.GREEN+"You have exited hotel creation mode.");
					HotelsCreationMode.loadInventory(sender);
					HotelsCreationMode.loadArmour(sender);
					
					if(plugin.getConfig().getBoolean("HCM.bossBar")==true){
						BarAPI.removeBar((Player) sender);
					}
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("reset"))&&(sender instanceof Player)){
					HotelsCreationMode.resetInventoryFiles(sender);
					sender.sendMessage("§2The inventory files have been reset");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode")||(args.length == 1)&&(args[0].equalsIgnoreCase("cm")))){
					sender.sendMessage(ChatColor.DARK_RED+"Please specify "+ChatColor.YELLOW+"enter"+ChatColor.DARK_RED+" or "+ChatColor.YELLOW+"exit "+ChatColor.DARK_RED+"mode");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))&&(sender instanceof Player)){
					Player p = (Player) sender;
					UUID playerUUID = p.getUniqueId();
					File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
					if(file.exists()){
						HotelsCreationMode.checkHotelsFolder();
						HotelsCreationMode.worldGuardSetup(args[1], sender);
						HotelsCreationMode.saveHotelFile(args[1], sender);
					}
					else
						sender.sendMessage("§4Could not create hotel. Did you enter Hotel Creation Mode? (§3§o/hotels cm enter§r§4)");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))||(args.length == 1)&&(args[0].equalsIgnoreCase("create")||(args.length == 1)&&(args[0].equalsIgnoreCase("c"))&&!(sender instanceof Player))){
					sender.sendMessage("§4The console can't create a hotel!");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("create")||(args[0].equalsIgnoreCase("c")))&&(sender instanceof Player)){
					sender.sendMessage(ChatColor.GREEN+"You have exited hotel creation mode.");
					HotelsCreationMode.loadInventory(sender);
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode"))&&!(sender instanceof Player)){
					sender.sendMessage("§4The console can't use hotel creation mode!");
				}
				}
		}
		return false;
	}
}
