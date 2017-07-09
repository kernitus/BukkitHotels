package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.utilities.HTSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class HTCmdExecutor implements CommandExecutor {

	private List<HTSubCommand> subCommands;

	public HTCmdExecutor(){
		//Place main command label first, then aliases
		//Make main command label case-sensitively match the locale path for the usage message to work
		subCommands = Arrays.asList(
				new HTSubCommand("hotels.commands", 1, (sendah, argz) -> HTCmdSurrogate.cmdCommands(sendah), "commands"),
				new HTSubCommand(true,"hotels.create", 2, (sendah, argz) -> HTCmdSurrogate.cmdCreate(sendah, argz[1]), "create", "c"),
				new HTSubCommand("hotels.create", 2, (sendah, argz) -> HTCmdSurrogate.cmdHelp(sendah, argz[1]), "help"),
				new HTSubCommand("hotels.create", 1, (sendah, argz) -> HTCmdSurrogate.cmdHelp(sendah, null), "help"),
				new HTSubCommand("enter",true,"hotels.creationmode", 2, (sendah, argz) -> HTCmdSurrogate.cmdCreationModeEnter(sendah), "creationMode", "cm"),
				new HTSubCommand("exit",true,"hotels.creationmode", 2, (sendah, argz) -> HTCmdSurrogate.cmdCreationModeExit(sendah), "creationMode", "cm"),
				new HTSubCommand("reset",true,"hotels.creationmode", 2, (sendah, argz) -> HTCmdSurrogate.cmdCreationModeReset(sendah), "creationMode", "cm"),
				new HTSubCommand("hotels.check.others", 2, (sendah, argz) -> HTCmdSurrogate.cmdCheck(argz[1], sendah), "check"),
				new HTSubCommand(true, "hotels.check", 1, (sendah, argz) -> HTCmdSurrogate.cmdCheckSelf(sendah), "check"),
				new HTSubCommand("hotels.reload", 1, (sendah, argz) -> HTCmdSurrogate.cmdReload(sendah), "reload"),
				new HTSubCommand(true, "hotels.rent", 3, (sendah, argz) -> HTCmdSurrogate.cmdRent(sendah, argz[1], argz[2]), "rent"),
				new HTSubCommand("add", true,"hotels.friend", 5, (sendah, argz) -> HTCmdSurrogate.cmdFriendAdd(sendah, argz[2], argz[3], argz[4]), "friend", "f"),
				new HTSubCommand("remove",true, "hotels.friend", 5, (sendah, argz) -> HTCmdSurrogate.cmdFriendRemove(sendah, argz[2], argz[3], argz[4]), "friend", "f"),
				new HTSubCommand("list", "hotels.friend.list.admin", 4, (sendah, argz) -> HTCmdSurrogate.cmdFriendList(sendah, argz[2], argz[3]), "friend", "f"),
				new HTSubCommand("list", true, "hotels.friend", 4, (sendah, argz) -> HTCmdSurrogate.cmdFriendListIfOwner(sendah, argz[2], argz[3]), "friend", "f"),
				new HTSubCommand("add", true,"hotels.helper", 4, (sendah, argz) -> HTCmdSurrogate.cmdHelperAdd(sendah, argz[2], argz[3]), "helper", "h"),
				new HTSubCommand("remove",true, "hotels.helper", 4, (sendah, argz) -> HTCmdSurrogate.cmdHelperRemove(sendah, argz[2], argz[3]), "helper", "h"),
				new HTSubCommand("list", "hotels.helper.list.admin", 3, (sendah, argz) -> HTCmdSurrogate.cmdHelperList(sendah, argz[2], true), "helper", "h"),
				new HTSubCommand("list", true, "hotels.helper", 3, (sendah, argz) -> HTCmdSurrogate.cmdHelperList(sendah, argz[2], false), "helper", "h"),
				new HTSubCommand("hotels.list.hotels", 2, (sendah, argz) -> HTCmdSurrogate.cmdHotelsList(sendah, argz[1]), "hotelsList", "hlist", "list"),
				new HTSubCommand(true, "hotels.list.hotels", 1, (sendah, argz) -> HTCmdSurrogate.cmdHotelsListPlayer(sendah), "hotelsList", "hlist", "list"),
				new HTSubCommand("hotels.list.rooms", 2, (sendah, argz) -> HTCmdSurrogate.cmdRoomsList(sendah, argz[1]), "roomslist", "rlist"),
				new HTSubCommand("hotels.delete.room.admin", 3, (sendah, argz) -> HTCmdSurrogate.cmdDeleteRoom(sendah, argz[1], argz[2]), "deleteRoom", "delr"),
				new HTSubCommand(true,"hotels.delete.room", 3, (sendah, argz) -> HTCmdSurrogate.cmdDeleteRoomUser(sendah, argz[1], argz[2]), "deleteRoom", "delr"),
				new HTSubCommand("hotels.rename.admin", 3, (sendah, argz) -> HTCmdSurrogate.cmdRenameHotel(sendah, argz[1], argz[2]), "rename", "ren"),
				new HTSubCommand(true, "hotels.rename.admin", 3, (sendah, argz) -> HTCmdSurrogate.cmdRenameHotelUser(sendah, argz[1], argz[2]), "rename", "ren"),
				new HTSubCommand(true, "hotels.rename", 3, (sendah, argz) -> HTCmdSurrogate.cmdRenameHotelUser(sendah, argz[1], argz[2]), "rename", "ren"),
				new HTSubCommand("hotels.renumber", 4, (sendah, argz) -> HTCmdSurrogate.cmdRenumber(sendah, argz[1], argz[2], argz[3]), "renumber", "renum"),
				new HTSubCommand("hotels.delete.admin", 2, (sendah, argz) -> HTCmdSurrogate.cmdDeleteHotel(sendah, argz[1]), "delete", "del"),
				new HTSubCommand("hotels.delete", 2, (sendah, argz) -> HTCmdSurrogate.cmdDeleteHotelUser(sendah, argz[1]), "delete", "del"),
				new HTSubCommand("hotels.remove", 4, (sendah, argz) -> HTCmdSurrogate.cmdRemovePlayer(sendah, argz[1], argz[2], argz[3]), "remove"),
				new HTSubCommand(true, "hotels.sign.create", 3, (sendah, argz) -> HTCmdSurrogate.cmdRoomCreate(sendah, argz[1], argz[2]), "room"),
				new HTSubCommand(true, "hotels.sign.create", 2, (sendah, argz) -> HTCmdSurrogate.cmdRoomCreate(sendah, argz[1]), "room"),
				new HTSubCommand(true, "hotels.sethome", 1, (sendah, argz) -> HTCmdSurrogate.cmdSetHome(sendah),"sethome"),
				new HTSubCommand(true, "hotels.home.admin", 3, (sendah, argz) -> HTCmdSurrogate.cmdHomeRoom(sendah, argz[1], argz[2]), "home", "hm"),
				new HTSubCommand(true, "hotels.home", 3, (sendah, argz) -> HTCmdSurrogate.cmdHomeRoomUser(sendah, argz[1], argz[2]), "home", "hm"),
				new HTSubCommand(true, "hotels.home",2, (sendah, argz) -> HTCmdSurrogate.cmdHomeHotel(sendah, argz[1]), "home", "hm"),
				new HTSubCommand(true, "hotels.sell.hotel", 4, (sendah, argz) -> HTCmdSurrogate.cmdSellHotel(sendah, argz[1], argz[2], argz[3]), "sellhotel", "sellh"),
				new HTSubCommand(true, "hotels.buy.hotel", 2, (sendah, argz) -> HTCmdSurrogate.cmdBuyHotel(sendah, argz[1]), "buyhotel", "buyh"),
				new HTSubCommand(true, "hotels.sell.room", 5, (sendah, argz) -> HTCmdSurrogate.cmdSellRoom(sendah, argz[1], argz[2], argz[3], argz[4]), "sellroom", "sellr"),
				new HTSubCommand(true, "hotels.buy.room", 3, (sendah, argz) -> HTCmdSurrogate.cmdBuyRoom(sendah, argz[1], argz[2]), "buyroom", "buyr"),
				new HTSubCommand("hotels.reset.toggle.admin", 3, (sendah, argz) -> HTCmdSurrogate.cmdRoomReset(sendah, argz[1], argz[2], true), "roomreset"),
				new HTSubCommand("hotels.reset.toggle", 3, (sendah, argz) -> HTCmdSurrogate.cmdRoomReset(sendah, argz[1], argz[2], false), "roomreset"),
				new HTSubCommand("hotels.reset.reset", 3, (sendah, argz) -> HTCmdSurrogate.cmdResetRoom(sendah, argz[1], argz[2]), "resetroom"),
				new HTSubCommand("", 1, (sendah, argz) -> HTCmdSurrogate.cmdCommands(sendah), "commands"),
				new HTSubCommand("", 0, (sendah, argz) -> HTCmdSurrogate.cmdMainPage(sendah), "")
		);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args){
		String label = cmd.getLabel();
		if(!label.equalsIgnoreCase("hotels")) return false;

		boolean matchedCommand = false;
		boolean consoleRejected = false;
		String pathToUsageToDisplay = null;

		for (HTSubCommand command : subCommands) {

			if(args.length > 0 && !command.isAlias(args[0])) continue;
			matchedCommand = true;

			if(!Mes.hasPerm(sender, command.getPermission())){
				Mes.mes(sender ,"chat.noPermission");
				continue;
			}

			if(command.needsPlayer() && !(sender instanceof Player)){
				consoleRejected = true;
				continue;
			}
			else consoleRejected = false;

			if(args.length > 1 && command.hasSubSubCommand() && !command.isSubSubCommand(args[1])){
				pathToUsageToDisplay = "chat.commands." + command.getLabels()[0] + ".usage";
				continue;
			}
			else if(pathToUsageToDisplay != null && !pathToUsageToDisplay.isEmpty())
				pathToUsageToDisplay = null;

			if(args.length < command.getMinArgs()){
				//If it gets to here it must wait until we have looped through fully to display message
				//in case another instance of the same subcommand comes up and matches
				pathToUsageToDisplay = "chat.commands." + command.getLabels()[0] + ".usage";
				continue;
			}
			else pathToUsageToDisplay = null; //Resetting it as another instance of same subcommand was found and matched

			command.run(sender, args);
			return true;
		}

		//Unknown sub-command
		if(!matchedCommand){ Mes.mes(sender, "chat.commands.unknownArg"); return false; }

		//Display command usage
		if(pathToUsageToDisplay != null && !pathToUsageToDisplay.isEmpty()){
			Mes.mes(sender, pathToUsageToDisplay);
			return false; }

		//Console rejected
		if(consoleRejected){ Mes.mes(sender, "chat.commands.consoleRejected"); return false; }

		return false; //Shouldn't be getting here anyway, but you never know
	}

	public void addSubCommand(HTSubCommand sub){
		subCommands.add(sub);
	}
}