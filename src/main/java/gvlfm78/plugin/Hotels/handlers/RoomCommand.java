package kernitus.plugin.Hotels.handlers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import kernitus.plugin.Hotels.HTCreationMode;
import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.exceptions.RoomSignInRoomException;
import kernitus.plugin.Hotels.managers.HTSignManager;
import kernitus.plugin.Hotels.managers.Mes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashSet;

public class RoomCommand implements CommandExecutor {

	private HotelsMain plugin;

	/**
	 Command equivalent of setting up a room sign
	 User points at sign and executes
	 /room <hotelName> <time> <price> <roomNum>
	 also if they have room selected but
	 room isn't created it does it for them
	 */
	public RoomCommand(HotelsMain plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getLabel().equalsIgnoreCase("room")) return false;
		if(!(sender instanceof Player)){ Mes.mes(sender ,"chat.commands.consoleRejected"); return false; }
		
		Player p = (Player) sender;

		if(!Mes.hasPerm(p, "hotels.sign.create")){ Mes.mes(p, "chat.noPermission"); return false; }
			
		if(args.length < 4){
			Mes.mes(p, "chat.room.usage");
			return false;
		}

		if(!HTCreationMode.isInCreationMode(p.getUniqueId())){ Mes.mes(p, "chat.commands.creationMode.notAlreadyIn");  return false; }

		World w = p.getWorld();

		HashSet<Material> transparent_blocks = new HashSet<Material>();

		transparent_blocks.add(Material.AIR);

		Block target = p.getTargetBlock(transparent_blocks, 100);
		
		Material mat = target.getType();
		
		if(mat != Material.SIGN_POST && mat != Material.WALL_SIGN){
			Mes.mes(p,"chat.room.notSign");
			return false;
		}

		Sign sign = (Sign) target.getState();

		//We now check whether the room was already created but sign wasn't setup.
		//If that isn't the case we try to create it now for them to make it easier
		HTCreationMode.roomSetup(args[0], args[3], p);

		try {
			HTSignManager.createRoomSign(sign, p, args[0], args[3], args[1], args[2]);
		} catch (RoomSignInRoomException e) {
			Mes.mes(p, "sign.place.inRoomRegion");
		}

		return true;
	}
}
