package kernitus.plugin.Hotels.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
			mes = ChatColor.DARK_RED + "Message "+path+" is null!";
		return mes;
	}

	public String mesnopre(String path){
		String mes = HConH.getLocale().getString(path);
		if(mes!=null){
			mes = mes.replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1");
		}
		else
			mes = ChatColor.DARK_RED + "Message "+path+" is null!";
		return mes;
	}
	public boolean hasPerm(CommandSender sender, String perm){
		if(sender.isOp())
			return true;
		if(HConH.getconfigyml().getBoolean("settings.use-permissions")==false)
			return true;
		else{
			if(sender.hasPermission("hotels.*"))
				return true;
			else if(sender.hasPermission(perm))
				return true;
		}
		return false;
	}
	public String flagValue(String path){
		String mes = HConH.getFlags().getString(path);
		if(mes==null)
			mes = ChatColor.DARK_RED + "Message "+path+" is null!";
		return mes;
	}
}