package kernitus.plugin.Hotels.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

public class Mes {

	public static String mes(String path){
		String mes = HotelsConfigHandler.getLocale().getString(path);
		if(mes!=null){
			//Prefix
			String prefix = (HotelsConfigHandler.getLocale().getString("chat.prefix")+" ");
			mes = ChatColor.translateAlternateColorCodes('&',(prefix+mes));
		}
		else
			mes = ChatColor.DARK_RED + "Message "+ChatColor.GOLD+path+ChatColor.DARK_RED+" is null!";
		return mes;
	}

	public static String mesnopre(String path){
		String mes = HotelsConfigHandler.getLocale().getString(path);
		if(mes!=null){
			mes = ChatColor.translateAlternateColorCodes('&',mes);
		}
		else
			mes = ChatColor.DARK_RED + "Message "+ChatColor.GOLD+path+ChatColor.DARK_RED+" is null!";
		return mes;
	}
	public static boolean hasPerm(CommandSender sender, String perm){
		if(sender instanceof Player){
			Player player = (Player) sender;
			return hasPerm(player, perm);
		}
		else
			return true;
	}
	public static boolean hasPerm(Player player, String perm){
		if(player.isOp())
			return true;
		else{
			if(player.hasPermission("hotels.*"))
				return true;
			else if(player.hasPermission(perm))
				return true;
			else if(player.hasPermission(perm+".user"))
				return true;
			else if(player.hasPermission(perm+".admin"))
				return true;
		}
		return false;
	}
	public static String flagValue(String path){
		String mes = HotelsConfigHandler.getFlags().getString(path);
		if(mes==null)
			mes = ChatColor.DARK_RED + "Message "+ChatColor.GOLD+path+ChatColor.DARK_RED+" is null!";
		return mes;
	}
}