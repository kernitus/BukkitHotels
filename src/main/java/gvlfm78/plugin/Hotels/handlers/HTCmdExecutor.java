package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.utilities.HTSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class HTCmdExecutor implements CommandExecutor {

	private HotelsMain plugin;

	public HTCmdExecutor(HotelsMain plugin){
		this.plugin = plugin;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args){
		String label = cmd.getLabel();
		if(!label.equalsIgnoreCase("hotels")) return false;

		//todo for 1st of same command 1st place one with more args and w/out player, then most args w/ player, then rest
		//todo check player only ones where Hotel is create to get world as it is now integrated in Hotel constructor
		//todo have method for adding subcommands
		//Place main command label first, then aliases
		//Make main command label case-sensitively match the locale path for the usage message to work
		List<HTSubCommand> subCommands = Arrays.asList(
				new HTSubCommand("hotels.commands", 1, () -> HTCmdSurrogate.cmdCommands(sender), "commands"),
				new HTSubCommand(true,"hotels.create", 2, () -> HTCmdSurrogate.cmdCreate(sender, args[1]), "create", "c"),
				new HTSubCommand("hotels.create", 2, () -> HTCmdSurrogate.cmdHelp(sender, args[1]), "help"),
				new HTSubCommand("hotels.create", 1, () -> HTCmdSurrogate.cmdHelp(sender, null), "help"),
				new HTSubCommand("enter",true,"hotels.creationmode", 2, () -> HTCmdSurrogate.cmdCreationModeEnter(sender), "creationMode", "cm"),
				new HTSubCommand("exit",true,"hotels.creationmode", 2, () -> HTCmdSurrogate.cmdCreationModeExit(sender), "creationMode", "cm"),
				new HTSubCommand("reset",true,"hotels.creationmode", 2, () -> HTCmdSurrogate.cmdCreationModeReset(sender), "creationMode", "cm"),
				new HTSubCommand("hotels.check.others", 2, () -> HTCmdSurrogate.cmdCheck(args[1], sender), "check"),
				new HTSubCommand(true, "hotels.check", 1, () -> HTCmdSurrogate.cmdCheckSelf(sender), "check"),
				new HTSubCommand("hotels.reload", 1, () -> HTCmdSurrogate.cmdReload(sender), "reload"),
				new HTSubCommand(true, "hotels.rent", 3, () -> HTCmdSurrogate.cmdRent(sender, args[1], args[2]), "rent"),
				new HTSubCommand("add", true,"hotels.friend", 5, () -> HTCmdSurrogate.cmdFriendAdd(sender, args[2], args[3], args[4]), "friend", "f"),
				new HTSubCommand("remove",true, "hotels.friend", 5, () -> HTCmdSurrogate.cmdFriendRemove(sender, args[2], args[3], args[4]), "friend", "f"),
				new HTSubCommand("list", "hotels.friend.list.admin", 4, () -> HTCmdSurrogate.cmdFriendList(sender, args[2], args[3]), "friend", "f"),
				new HTSubCommand("list", true, "hotels.friend", 4, () -> HTCmdSurrogate.cmdFriendListIfOwner(sender, args[2], args[3]), "friend", "f"),
				new HTSubCommand(true, "hotels.list.rooms", 2, () -> HTCmdSurrogate.cmdRoomListPlayer(sender, args[1]), "roomlist", "rlist"),
				new HTSubCommand("hotels.list.rooms", 2, () -> HTCmdSurrogate.cmdRoomList(sender, args[1], null), "roomlist", "rlist"),
				new HTSubCommand("hotels.list.hotels", 3, () -> HTCmdSurrogate.cmdHotelsList(sender, args[1]), "hotelslist", "hlist", "list"),
				new HTSubCommand(true, "hotels.list.hotels", 2, () -> HTCmdSurrogate.cmdHotelsListPlayer(sender), "hotelslist", "hlist", "list"),
				new HTSubCommand("hotels.delete.room.admin", 3, () -> HTCmdSurrogate.cmdDeleteRoom(sender, args[1], args[2]), "deleteroom", "delr"),
				new HTSubCommand(true,"hotels.delete.room", 3, () -> HTCmdSurrogate.cmdDeleteRoomUser(sender, args[1], args[2]), "deleteroom", "delr"),
				new HTSubCommand("hotels.rename.admin", 3, () -> HTCmdSurrogate.cmdRenameHotel(sender, args[1], args[2]), "rename", "ren"),
				new HTSubCommand(true, "hotels.rename.admin", 3, () -> HTCmdSurrogate.cmdRenameHotelUser(sender, args[1], args[2]), "rename", "ren"),
				new HTSubCommand(true, "hotels.rename", 3, () -> HTCmdSurrogate.cmdRenameHotelUser(sender, args[1], args[2]), "rename", "ren"),
				new HTSubCommand("hotels.renumber", 4, () -> HTCmdSurrogate.cmdRenumber(sender, args[1], args[2], args[3]), "renumber", "renum"),
				new HTSubCommand("hotels.delete.admin", 2, () -> HTCmdSurrogate.cmdDeleteHotel(sender, args[1]), "delete", "del"),
				new HTSubCommand("hotels.delete", 2, () -> HTCmdSurrogate.cmdDeleteHotelUser(sender, args[1]), "delete", "del"),
				new HTSubCommand("hotels.remove", 4, () -> HTCmdSurrogate.cmdRemovePlayer(sender, args[1], args[2], args[3]), "remove"),
				new HTSubCommand(true, "hotels.sign.create", 3, () -> HTCmdSurrogate.cmdRoomCreate(sender, args[1], args[2]), "room"),
				new HTSubCommand(true, "hotels.sign.create", 2, () -> HTCmdSurrogate.cmdRoomCreate(sender, args[1]), "room"),
				new HTSubCommand(true, "hotels.sethome", 1, () -> HTCmdSurrogate.cmdSetHome(sender),"sethome"),
				new HTSubCommand(true, "hotels.home.admin", 3, () -> HTCmdSurrogate.cmdHomeRoom(sender, args[1], args[2])),
				new HTSubCommand(true, "hotels.home", 3, () -> HTCmdSurrogate.cmdHomeRoomUser(sender, args[1], args[2]), "home", "hm"),
				new HTSubCommand(true, "hotels.home",2, () -> HTCmdSurrogate.cmdHomeHotel(sender, args[1]), "home", "hm"),
				new HTSubCommand(true, "hotels.sell.hotel", 4, () -> HTCmdSurrogate.cmdSellHotel(sender, args[1], args[2], args[3]), "sellhotel", "sellh"),
				new HTSubCommand(true, "hotels.buy.hotel", 2, () -> HTCmdSurrogate.cmdBuyHotel(sender, args[1]), "buyhotel", "buyh"),
				new HTSubCommand(true, "hotels.sell.room", 5, () -> HTCmdSurrogate.cmdSellRoom(sender, args[1], args[2], args[3], args[4]), "sellroom", "sellr"),
				new HTSubCommand(true, "hotels.buy.room", 3, () -> HTCmdSurrogate.cmdBuyRoom(sender, args[1], args[2]), "buyroom", "buyr"),
				new HTSubCommand("hotels.reset.toggle.admin", 3, () -> HTCmdSurrogate.cmdRoomReset(sender, args[1], args[2], true), "roomreset"),
				new HTSubCommand("hotels.reset.toggle", 3, () -> HTCmdSurrogate.cmdRoomReset(sender, args[1], args[2], false), "roomreset"),
				new HTSubCommand("hotels.reset.reset", 3, () -> HTCmdSurrogate.cmdResetRoom(sender, args[1], args[2]), "resetroom"),
				new HTSubCommand("", 1, () -> HTCmdSurrogate.cmdCommands(sender), "commands"),
				new HTSubCommand("", 0, () -> HTCmdSurrogate.cmdMainPage(sender), "")
		);

		boolean matchedCommand = false;
		boolean consoleRejected = false;
		String pathToUsageToDisplay = null;

		for (HTSubCommand command : subCommands) {

			if(args.length > 0 && !command.isAlias(args[0])) continue;
			matchedCommand = true;

			if(args.length > 1 && !command.isSubSubCommand(args[1])) continue;//todo doesn't return any message

			if(command.needsPlayer() && !(sender instanceof Player)){
				consoleRejected = true;
				continue;
			}
			else consoleRejected = false;

			if(!Mes.hasPerm(sender, command.getPermission())){
				Mes.mes(sender ,"chat.noPermission");
				continue;
			}

			if(args.length < command.getMinArgs()){
				//If it gets to here it must wait until we have looped through fully to display message
				//in case another instance of the same subcommand comes up and matches
				pathToUsageToDisplay = "chat.commands." + command.getLabels()[0] + ".usage";
				continue;
			}
			else if(pathToUsageToDisplay != null && !pathToUsageToDisplay.isEmpty())
				pathToUsageToDisplay = null; //Resetting it as another instance of same subcommand was found and matched

			command.run();
			return true;
		}

		//Unknown sub-command
		if(!matchedCommand){ Mes.mes(sender, "chat.commands.unknownArg"); return false; }

		//Console rejected
		if(consoleRejected) Mes.mes(sender, "chat.commands.consoleRejected");

		//Display command usage
		if(pathToUsageToDisplay != null && !pathToUsageToDisplay.isEmpty())
			Mes.mes(sender, pathToUsageToDisplay);

		return false; //Shouldn't be getting here anyway, but you never know
	}
}