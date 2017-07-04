package kernitus.plugin.Hotels.managers;

import kernitus.plugin.Hotels.handlers.HTConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Mes {

	public static String getString(String path, String... substitute){
		return addPrefix(getStringNoPrefix(path, substitute));
	}
	public static String getStringNoPrefix(String path, String... substitutes){
		String mes = getRawString(path);
		return mes != null ? ChatColor.translateAlternateColorCodes('&', applySubstitutes(mes, substitutes)) :
				ChatColor.DARK_RED + "Message " + ChatColor.GOLD + path + ChatColor.DARK_RED + " is null!";
	}

	//Utility methods
	private static String getRawString(String path){
		return HTConfigHandler.getLocale().getString(path);
	}
	private static boolean containsString(String path){
		return HTConfigHandler.getLocale().contains(path);
	}
	private static String addPrefix(String message){
		return ChatColor.translateAlternateColorCodes('&',
				getRawString("chat.prefix") + " " + message);
	}
	private static String applySubstitutes(String message, String... substitute){
		//Even slots are variable to substitute with following uneven slot
		for(int i = 0; i < substitute.length; i += 2)
			message = message.replaceAll(substitute[i], substitute[i+1]);
		return message;
	}

	//Message senders
	//with prefix:
	public static void mes(CommandSender s, String path, String... substitute){
		mesRelay(s, path, true, substitute);
	}
	//without prefix:
	public static void mesNoPrefix(CommandSender s, String path, String... substitute){
		mesRelay(s, path, false, substitute);
	}
	private static void mesRelay(CommandSender s, String path, boolean usePrefix, String... substitute){
		String mes;
		mes = usePrefix ? getString(path) : getStringNoPrefix(path);

		mes = applySubstitutes(mes, substitute);

		s.sendMessage(mes);
	}

	//send all submessages, such as in help pages
	public static void mesAllSub(CommandSender s, String parentPath, String... substitute) {
		mesAllSub(s, parentPath, false, substitute);
	}
	//send all submessages, such as in help pages
	public static void mesAllSub(CommandSender s, String parentPath, boolean usePrefix, String... substitute){
		//Loop through all sub-paths until a null one is found
		for(int i = 1; containsString(parentPath + "." + i); i++)
			mesRelay(s, parentPath + "." + i, usePrefix, substitute);
	}


	public static boolean hasPerm(CommandSender sender, String perm){
		return !(sender instanceof Player) || hasPerm(((Player) sender), perm);
	}
	public static boolean hasPerm(Player player, String perm){
		return perm == null || perm.isEmpty() ||
				player.hasPermission("hotels.*") ||
				player.hasPermission(perm) ||
				player.hasPermission(perm + ".user") ||
				player.hasPermission(perm + ".admin");
	}
	public static String flagValue(String path){
		String mes = HTConfigHandler.getFlags().getString(path);
		if(mes == null)
			mes = ChatColor.DARK_RED + "Message " + ChatColor.GOLD + path + ChatColor.DARK_RED + " is null!";
		return mes;
	}
	public static void debug(String mes){
		if(HTConfigHandler.getconfigYML().getBoolean("debug"))
			Logger.getLogger("Minecraft").info("[Hotels][Debug] " + mes );
	}
	public static void debug(Player p, String mes){
		if(HTConfigHandler.getconfigYML().getBoolean("debug"))
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', (addPrefix("[Debug] " + mes)) ));
	}
	public static void printConsole(String mes){
		Logger.getLogger("Minecraft").info("[Hotels] " + mes );
	}
}