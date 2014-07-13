package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.HotelsMain;

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
				if(sender instanceof Player){
					Player player = (Player) sender;
				}
				else{
				}
				
				if(args.length == 0){
					sender.sendMessage("§4==========Hotels==========");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" plugin by kernitus");
					sender.sendMessage("§2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion());
					sender.sendMessage("§4Type §3/hotels help §4for help with the hotels");
				}
				else if((args.length == 1)&&(args[0].equalsIgnoreCase("help")||(args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("1"))))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 1- Selection of hotel cuboid");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels create enter");
					sender.sendMessage(ChatColor.YELLOW+"Take your WorldEdit wand in hand");
					sender.sendMessage(ChatColor.YELLOW+"Left and right click click the opposite corners of your hotel");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 2 to get to page 2");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("2")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 2- Creation of the hotel");
					sender.sendMessage(ChatColor.YELLOW+"Type §3/hotels create §onameofhotel");
					sender.sendMessage(ChatColor.YELLOW+"Go die in a hole");
					sender.sendMessage(ChatColor.YELLOW+"Then explode in mid-air");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 3 to get to page 3");
			}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("3")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 3- Creation of a room cuboid");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels wand room");
					sender.sendMessage(ChatColor.YELLOW+"Left click and right click the corners of your room");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels room <hotel> <roomnum>");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 4 to get to page 4");
			}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("4")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 4- Adding a sign");
					sender.sendMessage(ChatColor.YELLOW+"Grab a sign and place it next to the door of the room");
					sender.sendMessage(ChatColor.YELLOW+"Type on the sign:");
					sender.sendMessage(ChatColor.YELLOW+"[Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"<hotelname>");
					sender.sendMessage(ChatColor.YELLOW+"<roomnumber>");
					sender.sendMessage(ChatColor.YELLOW+"<cost:time>");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 5 to get to page 5");
		}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("5")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 5- Example of a sign");
					sender.sendMessage(ChatColor.YELLOW+"Example of a sign:");
					sender.sendMessage(ChatColor.YELLOW+"[Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"TheBestHotel");
					sender.sendMessage(ChatColor.YELLOW+"15");
					sender.sendMessage(ChatColor.YELLOW+"200:3d");
					sender.sendMessage(ChatColor.DARK_RED+"Last page. Type /hotels help to get to page 1");
		}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("enter"))&&(sender instanceof Player)){
					sender.sendMessage(ChatColor.GREEN+"You have entered hotel creation mode.");
					HotelsCreationMode.checkFolder();
					HotelsCreationMode.saveInventory(sender);
					HotelsCreationMode.getSelection(sender);
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))&&(args[1].equalsIgnoreCase("exit"))&&(sender instanceof Player)){
					sender.sendMessage(ChatColor.GREEN+"You have exited hotel creation mode.");
					HotelsCreationMode.loadInventory(sender);
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode"))&&!(sender instanceof Player)){
					sender.sendMessage("§4The console can't use hotel creation mode!");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("createmode")||(args[0].equalsIgnoreCase("cm")))||(args.length == 1)&&(args[0].equalsIgnoreCase("createmode"))){
					sender.sendMessage(ChatColor.DARK_RED+"Please specify "+ChatColor.YELLOW+"enter"+ChatColor.DARK_RED+" or "+ChatColor.YELLOW+"exit "+ChatColor.DARK_RED+"mode");
				}
			}
		}
		return false;
	}
}
