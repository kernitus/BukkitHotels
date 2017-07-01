package kernitus.plugin.Hotels.signs;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.handlers.HTConfigHandler;
import kernitus.plugin.Hotels.managers.HTSignManager;
import kernitus.plugin.Hotels.managers.Mes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class RoomSign extends AbstractSign{

	private Room room;

	public RoomSign(Room room){
		this.room = room;
	}
	public RoomSign(Hotel hotel, String num){
		room = new Room(hotel, num);
	}
	public RoomSign(World world, String hotelName, String num){
		room = new Room(world, hotelName, num);
	}

	public void update(){
		Block b = getBlock();
		Material mat = b.getType();
		if(mat != Material.SIGN_POST && mat != Material.WALL_SIGN) return;
		Sign s = (Sign) b;

		if(!room.isFree())
			s.setLine(2, HTSignManager.TimeFormatter(
					room.getExpiryMinute() - System.currentTimeMillis() / 1000 / 60));
		else{
			s.setLine(2, HTSignManager.TimeFormatter(room.getTime()));
			s.setLine(3, ChatColor.GREEN + Mes.getStringNoPrefix("sign.vacant"));
		}
		s.update();
	}
	public Block getBlock(){
		return getLocation().getBlock();
	}
	public Sign getSign(){
		Block b = getBlock();
		Material mat = b.getType();
		return mat == Material.SIGN_POST || mat == Material.WALL_SIGN ? (Sign) b : null;
	}
	public String[] getSignLines(){
		Sign s = getSign();
		return s != null ? s.getLines() : new String[4];
	}
	public World getWorldFromConfig(){
		String world = getConfig().getString("Sign.location.world"); //Could be name or UUID

		if(world == null || world.isEmpty()) return null; //String useless, can't proceed

		World w;
		//Checking if it's a world name
		w = Bukkit.getWorld(world);
		if(w != null) return w;

		//Checking if it's a world UUID
		UUID id = UUID.fromString(world);
		w = Bukkit.getWorld(id);

		return w;
	}
	public Location getLocation(){
		World world = getWorldFromConfig();

		if(world == null) return null;

		YamlConfiguration config = getConfig();

		int x = config.getInt("Room.location.x");
		int y = config.getInt("Room.location.y");
		int z = config.getInt("Room.location.z");

		return new Location(world, x, y, z);
	}
	public File getFile(){
		return HTConfigHandler.getSignFile(room.getHotel().getName(), room.getNum());
	}
	public Room getRoom(){
		return room;
	}
	public String getHotelNameFromSign(){
		String firstLine = getSignLines()[0];
		return firstLine != null ? firstLine.split(" ")[0] : null;
	}
	public int getRoomNumFromSign(){
		String secondLine = getSignLines()[1];
		if(secondLine != null)
			return Integer.parseInt(secondLine.split(" ")[1]);
		else return 0;
	}
	public void removeSign(){
		Block b = getBlock();
		Material mat = b.getType();
		if(mat != Material.SIGN_POST && mat != Material.WALL_SIGN) return;
		Hotel hotel = room.getHotel();
		if(getHotelNameFromSign().matches(hotel.getName())
				&& hotel.getRegion().contains(b.getX(), b.getY(), b.getZ())){
				b.setType(Material.AIR);
		}
	}
}