package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class HotelsCommandHandler implements CommandExecutor {
	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain instance)
	{
		this.plugin = instance;
	}
	HotelsMessageManager HMM = new HotelsMessageManager(plugin);
	SignManager SM = new SignManager(plugin);
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);
	HotelsFileFinder HFF = new HotelsFileFinder(plugin);
	HotelsCommandExecutor HCE = new HotelsCommandExecutor(plugin);

	//Prefix
	YamlConfiguration locale = HConH.getLocale();
	String prefix = (locale.getString("chat.prefix").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+" ");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,String[] args){
		if(cmd.getLabel().equalsIgnoreCase("Hotels")){
			if(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")))
				if(sender.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(sender.hasPermission("hotels.commands")||sender.hasPermission("hotels.*")))){
					if(args.length==0){
						sender.sendMessage(("&4==========Hotels==========").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						sender.sendMessage(("&2"+plugin.getDescription().getName()+" plugin by kernitus").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						sender.sendMessage(("&2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion()).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						sender.sendMessage(("&4Type &3/hotels help &4for help with creating a hotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						sender.sendMessage(("&4Type &3/hotels commands &4for help with the commands").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						sender.sendMessage(("&4==========================").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}

				}
		}
		return false;
	}
}