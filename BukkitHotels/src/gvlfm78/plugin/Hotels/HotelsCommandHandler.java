package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.HotelsMain;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
				Player player = (Player) sender;
				if(args.length == 0){
					sender.sendMessage(ChatColor.DARK_RED+"==========Hotels==========");
					sender.sendMessage(ChatColor.GREEN+plugin.getDescription().getName()+" plugin by "+plugin.getDescription().getAuthors());
					sender.sendMessage(ChatColor.GREEN+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion());
					sender.sendMessage(ChatColor.DARK_RED+"Type "+ChatColor.DARK_AQUA+"/hotels help "+ChatColor.DARK_RED+"for help with the hotels");
				}
				else if((args.length == 1)&&(args[0].equalsIgnoreCase("help")||(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("1"))))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 1- Creation of hotel cuboid");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels wand hotel");
					sender.sendMessage(ChatColor.YELLOW+"Left click and right click the corners of your hotel");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels create <name>");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 2 to get to page 2");
				}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("2")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 2- Creation of a room cuboid");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels wand room");
					sender.sendMessage(ChatColor.YELLOW+"Left click and right click the corners of your room");
					sender.sendMessage(ChatColor.YELLOW+"Type /hotels room <hotel> <roomnum>");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 3 to get to page 3");
			}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("3")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 3- Adding a sign");
					sender.sendMessage(ChatColor.YELLOW+"Grab a sign and place it next to the door of the room");
					sender.sendMessage(ChatColor.YELLOW+"Type on the sign:");
					sender.sendMessage(ChatColor.YELLOW+"[Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"<hotelname>");
					sender.sendMessage(ChatColor.YELLOW+"<roomnumber>");
					sender.sendMessage(ChatColor.YELLOW+"<cost:time>");
					sender.sendMessage(ChatColor.DARK_RED+"Type /hotels help 4 to get to page 4");
		}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("help")&&(args[1].equalsIgnoreCase("4")))){
					sender.sendMessage(ChatColor.GREEN+"Hotels plugin help page");
					sender.sendMessage(ChatColor.DARK_RED+"-Page 4- Example of a sign");
					sender.sendMessage(ChatColor.YELLOW+"Example of a sign:");
					sender.sendMessage(ChatColor.YELLOW+"[Hotels]");
					sender.sendMessage(ChatColor.YELLOW+"TheBestHotel");
					sender.sendMessage(ChatColor.YELLOW+"15");
					sender.sendMessage(ChatColor.YELLOW+"200:3d");
					sender.sendMessage(ChatColor.DARK_RED+"Last page. Type /hotels help 1 to get to page 1");
		}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("wand")&&(args[1].equalsIgnoreCase("hotel")))){
					player.getInventory().addItem(new ItemStack(Material.GOLD_HOE, 1));
		}
				else if((args.length == 2)&&(args[0].equalsIgnoreCase("wand")&&(args[1].equalsIgnoreCase("room")))){
					player.getInventory().addItem(new ItemStack(Material.IRON_HOE, 1));
		}
				else if((args.length == 1)&&(args[0].equalsIgnoreCase("wand"))){
					sender.sendMessage(ChatColor.DARK_RED+"Please specify either hotel or room wand");
				}
				else if((args.length == 1)&&(args[0].equalsIgnoreCase("create"))){
					sender.sendMessage(ChatColor.GREEN+"You have entered hotel creation mode.");
					
				}
		}
		}
		return false;
	}
}