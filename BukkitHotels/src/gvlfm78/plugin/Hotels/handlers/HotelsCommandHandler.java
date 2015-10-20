package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLbl,String[] args){
		if(cmd.getLabel().equalsIgnoreCase("Hotels")){
			if(args.length==0){//Fallback screen
				sender.sendMessage(("&4==========Hotels==========").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&2"+plugin.getDescription().getName()+" plugin by kernitus").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&2"+plugin.getDescription().getName()+" version "+plugin.getDescription().getVersion()).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&4Type &3/hotels help &4for help with creating a hotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&4Type &3/hotels commands &4for help with the commands").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				sender.sendMessage(("&4==========================").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			//Command checks
			if(args[0].equalsIgnoreCase("commands")){//Commands list
				if(plugin.getConfig().getBoolean("settings.commands.onlyDisplayAllowed")==false)
					HCE.cmdCommandsAll(sender);
				else
					HCE.cmdCommandsOnly(sender);
			}
			else if(args[0].equalsIgnoreCase("create")|args[0].equalsIgnoreCase("c")){//Create command
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length>1){
						if(hasPerm(sender, "hotels.create")){
							HCE.cmdCreate(p, args[1]);
						}
						p.sendMessage(HMM.mes("chat.noPermission"));
					}
					sender.sendMessage(HMM.mes("chat.commands.create.noName"));
				}
				sender.sendMessage(HMM.mes("chat.commands.create.consoleRejected"));
			}
			else if(args[0].equalsIgnoreCase("help")){//Help pages
				if(args[1].equalsIgnoreCase("1"))
					HCE.cmdHelp1(sender);
				else if(args[1].equalsIgnoreCase("2"))
					HCE.cmdHelp2(sender);
				else if(args[1].equalsIgnoreCase("3"))
					HCE.cmdHelp3(sender);
				else if(args[1].equalsIgnoreCase("4"))
					HCE.cmdHelp4(sender);
				else if(args[1].equalsIgnoreCase("5"))
					HCE.cmdHelp5(sender);
				else
					HCE.cmdHelp1(sender);
			}
			else if((args[0].equalsIgnoreCase("createmode"))||(args[0].equalsIgnoreCase("cm"))){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(hasPerm(p, "hotels.createmode")){
						if(args[1].equalsIgnoreCase("enter"))
							HCE.cmdCreateModeEnter(p);
						else if(args[1].equalsIgnoreCase("exit"))
							HCE.cmdCreateModeExit(p);
						else if(args[1].equalsIgnoreCase("reset"))
							HCE.cmdCreateModeReset(p);
						else
							p.sendMessage(HMM.mes("chat.commands.creationMode.noarg"));
					}
					else
						p.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage("chat.commands.creationMode.consoleRejected");
			}
			else if(args[0].equalsIgnoreCase("check")){
				if(sender instanceof Player){
					if(args.length==1){
						String p = sender.getName();
						HCE.check(p, sender);
					}
					else if(args.length>=2){
						if(args[1]==sender.getName()){
							String p = args[1];							
							HCE.check(p, sender);
						}
						else if(sender.hasPermission("hotels.check.others")){
							String p = args[1];							
							HCE.check(p, sender);
						}
						else
							sender.sendMessage(HMM.mes("chat.noPermission"));
					}
				}else{
					if(args.length>=2){
						String p = args[1];							
						HCE.check(p, sender);
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.noPlayer"));
				}
			}
			else if(args[0].equalsIgnoreCase("reload")){
				if(hasPerm(sender, "hotels.reload"))
					HCE.cmdReload(sender);
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			else if(args[0].equalsIgnoreCase("rent")){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(hasPerm(p, "hotels.rent")){
						if(args.length<3)
							p.sendMessage(HMM.mes("chat.commands.rent.usage"));
						else
							HCE.cmdRent(sender, args[1], args[2]);
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}else
					sender.sendMessage(HMM.mes("chat.commands.rent.consoleRejected").replaceAll("(?i)&([a-fk-r0-9])", ""));
			}
			else if((args[0].equalsIgnoreCase("friend"))||(args[0].equalsIgnoreCase("f"))){
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(hasPerm(p, "hotels.friend")){
						if(args[1].equalsIgnoreCase("add")){
							if(args.length>4)
							HCE.cmdFriendAdd(args[2],args[3],args[4]);
							else
								sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
						}
						else if(args[1].equalsIgnoreCase("remove")){
							if(args.length>4)
							HCE.cmdFriendRemove(args[2],args[3],args[4]);
							else
								sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
						}
						else if(args[1].equalsIgnoreCase("list")){
							if(args.length>3)
							HCE.cmdFriendList(args[2],args[3]);
							else
								sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.friend.usage"));
					}
					else
						sender.sendMessage(HMM.mes("chat.noPermission"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.friend.consoleRejected"));
			}
			else if((args[0].equalsIgnoreCase("roomlist"))||(args[0].equalsIgnoreCase("rlist"))){
				if(hasPerm(sender, "hotels.list.rooms")){
					if(args[1]!=null){
						if(sender instanceof Player){//Is player
							Player p = (Player) sender;
							if(args.length>2){//Has specified world
							World w = Bukkit.getWorld(args[2]);
							if(w!=null)
							HCE.cmdRoomListPlayer(p, args[1], w);
							else
								sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
							}
							else{//Has not specified world
								World w = p.getWorld();
								HCE.cmdRoomListPlayer(p, args[1], w);
							}
						}
						else{//Not a player issuing the command
							if(args.length>2){
								World w = Bukkit.getWorld(args[2]);
								if(w!=null)
							HCE.cmdRoomListNonPlayer(sender, args[1],w);
								else
									sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.noWorld").replaceAll("(?i)&([a-fk-r0-9])", ""));
						}
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.listRooms.usage").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else
					sender.sendMessage(HMM.mes("chat.noPermission"));
			}
			
			//Other commands
		}
		//Command is not /hotels
		return false;
	}

	public boolean hasPerm(CommandSender sender, String perm){		
		if(sender.isOp())
			return true;
		if(plugin.getConfig().getBoolean("settings.use-permissions")==false)
			return true;
		else{
			if(sender.hasPermission("hotels.*"))
				return true;
			else if(sender.hasPermission(perm))
				return true;
		}
		return false;
	}
}