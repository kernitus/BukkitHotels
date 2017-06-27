package kernitus.plugin.Hotels.handlers;

import java.io.IOException;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.managers.Mes;

public class RoomCommand implements CommandExecutor {

	private HotelsMain plugin;

	public RoomCommand(HotelsMain plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getLabel().equalsIgnoreCase("room")) return false;
		if(!(sender instanceof Player)) return false;
		
		Player p = (Player) sender;
		World w = p.getWorld();
			
		if(args.length < 4){
			Mes.mes(p, ""); //room command usage
			return false;
		}
		
		Hotel hotel = new Hotel(w, args[0]);
		
		if(!hotel.exists()){
			 Mes.mes(p,"chat.commands.hotelNonExistent");
			 return false;
		}
		
		Room room = new Room(hotel, args[3]);
		
		if(!room.exists()){
			Mes.mes(p,"chat.commands.roomNonExistent");
			 return false;
		}
		
		//Check if player is in specified hotel region
		Location loc = p.getLocation();
		ProtectedRegion r = hotel.getRegion();
		
		if(!r.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())){
			Mes.mes(sender ,"something"); //TODO You're not in the specified hotel region!
			return false;
		}
		
		Block target = p.getTargetBlock(new HashSet<Material>(), 100);
		
		Material mat = target.getType();
		
		if(!mat.equals(Material.SIGN_POST) && ! mat.equals(Material.WALL_SIGN)){
			Mes.mes(p,"something"); //TODO You're not looking at a sign!
			return false;
		}
		
		//The sign must also be in the hotel region
		//It also musn't be in the room region
		try {
			room.createSignConfig(p, args[1], args[2], target.getLocation());
		} catch (IOException e) {
			e.printStackTrace();
			//TODO Could not create sign file
		}
		
		
		//Parse the time and price
		
		//hotelname time price roomnum
		return false;
	}
}
