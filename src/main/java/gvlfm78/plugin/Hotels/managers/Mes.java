package kernitus.plugin.Hotels.managers;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

public class Mes {

	public static String getString(String path){
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

	public static String getStringNoPrefix(String path){
		String mes = HotelsConfigHandler.getLocale().getString(path);
		return mes!=null ? ChatColor.translateAlternateColorCodes('&', mes) : "Message " + path + " is null!";
	}
	public static void mes(Player p, String path){
		p.sendMessage(getString(path));
	}
	public static void mes(CommandSender s, String path){
		if(s instanceof Player) mes( (Player) s, path);
		else s.sendMessage(getStringNoPrefix(path));
	}
	public static boolean hasPerm(CommandSender sender, String perm){
		return sender instanceof Player ? hasPerm( ( (Player) sender), perm) : true;
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
	public static void debug(String mes){
		if(HotelsConfigHandler.getconfigyml().getBoolean("debug"))
			Logger.getLogger("Minecraft").info("[Hotels][Debug] " + mes );
	}
	public static void debug(Player p, String mes){
		if(HotelsConfigHandler.getconfigyml().getBoolean("debug")){
			String prefix = (HotelsConfigHandler.getLocale().getString("chat.prefix") + "[Debug] ");
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', (prefix + mes) ));
		}
	}
	public static void printConsole(String mes){
		Logger.getLogger("Minecraft").info("[Hotels] " + mes );
	}
}