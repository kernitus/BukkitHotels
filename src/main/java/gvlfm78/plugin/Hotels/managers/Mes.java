package kernitus.plugin.Hotels.managers;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

public class Mes {

	public static String mes(String path){
		String mes = HotelsConfigHandler.getLocale().getString(path);
		if(mes!=null){
			//Prefix
			String prefix = (HotelsConfigHandler.getLocale().getString("chat.prefix") + " ");
			mes = ChatColor.translateAlternateColorCodes('&', (prefix + mes));
		}
		else
			mes = ChatColor.DARK_RED + "Message " + ChatColor.GOLD + path + ChatColor.DARK_RED + " is null!";
		return mes;
	}

	public static String mesnopre(String path){
		String mes = HotelsConfigHandler.getLocale().getString(path);
		if(mes!=null)
			mes = ChatColor.translateAlternateColorCodes('&', mes);
		else
			mes = ChatColor.DARK_RED + "Message " + ChatColor.GOLD + path + ChatColor.DARK_RED + " is null!";
		return mes;
	}
	public static boolean hasPerm(CommandSender sender, String perm){
		if(sender instanceof Player){
			Player player = (Player) sender;
			return hasPerm(player, perm);
		}
		return true;
	}
	public static boolean hasPerm(Player player, String perm){
		return player.hasPermission("hotels.*") || player.hasPermission(perm) || player.hasPermission(perm+".user") || player.hasPermission(perm+".admin");
	}
	public static String flagValue(String path){
		String mes = HotelsConfigHandler.getFlags().getString(path);
		if(mes==null)
			mes = ChatColor.DARK_RED + "Message " + ChatColor.GOLD + path + ChatColor.DARK_RED +" is null!";
		return mes;
	}
	public static void debugConsole(String mes){
		if(HotelsConfigHandler.getconfigyml().isBoolean("debug")){
			Logger.getLogger("Minecraft").info("[Hotels] " + mes );
		}
	}
	public static void debugPlayer(Player p, String mes){
		if(HotelsConfigHandler.getconfigyml().isBoolean("debug")){
			String prefix = (HotelsConfigHandler.getLocale().getString("chat.prefix") + " ");
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix + mes) ));
		}
	}
}