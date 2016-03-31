package kernitus.plugin.Hotels.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

public class HotelsMessageManager {

	private HotelsMain plugin;
	public HotelsMessageManager(HotelsMain instance)
	{
		this.plugin = instance;
	}

	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	//Prefix
	String prefix = (HConH.getLocale().getString("chat.prefix")+" ");

	public String mes(String path){
		String mes = HConH.getLocale().getString(path);
		if(mes!=null){
			mes = (prefix+mes).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1");
		}
		else
			mes = ChatColor.DARK_RED + "Message "+ChatColor.GOLD+path+ChatColor.DARK_RED+" is null!";
		return mes;
	}

	public String mesnopre(String path){
		String mes = HConH.getLocale().getString(path);
		if(mes!=null){
			mes = mes.replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1");
		}
		else
			mes = ChatColor.DARK_RED + "Message "+ChatColor.GOLD+path+ChatColor.DARK_RED+" is null!";
		return mes;
	}
	public boolean hasPerm(CommandSender sender, String perm){
		if(sender instanceof Player){
			Player player = (Player) sender;
			return hasPerm(player, perm);
		}
		else
			return true;
	}
	public boolean hasPerm(Player player, String perm){
		if(player.isOp())
			return true;
		if(HConH.getconfigyml().getBoolean("settings.use-permissions")==false)
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
	public String flagValue(String path){
		String mes = HConH.getFlags().getString(path);
		if(mes==null)
			mes = ChatColor.DARK_RED + "Message "+ChatColor.GOLD+path+ChatColor.DARK_RED+" is null!";
		return mes;
	}
}