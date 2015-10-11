package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.GameLoop;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsCommandHandler implements CommandExecutor {
	private HotelsMain plugin;
	public HotelsCommandHandler(HotelsMain instance)
	{
		this.plugin = instance;
	}
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