package kernitus.plugin.Hotels.Signs;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;

public class RoomSign extends AbstractSign{

	private Room room;

	public RoomSign(Room room){
		this.room = room;
	}
	public RoomSign(Hotel hotel, int num){
		room = new Room(hotel, num);
	}
	public RoomSign(Hotel hotel, String num){
		room = new Room(hotel, Integer.parseInt(num));
	}
	public RoomSign(World world, String hotelName, int num){
		room = new Room(world, hotelName, num);
	}
	public RoomSign(World world, String hotelName, String num){
		room = new Room(world, hotelName, num);
	}
	public RoomSign(String hotelName, int num){
		room = new Room(hotelName, num);
	}
	public RoomSign(String hotelName, String num){
		room = new Room(hotelName, Integer.parseInt(num));
	}
	public void update(){
		Block b = getBlock();
		Material mat = b.getType();
		if(!mat.equals(Material.SIGN_POST) && !mat.equals(Material.WALL_SIGN)) return;
		Sign s = (Sign) b;

		if(!room.isFree())
			s.setLine(2, SignManager.TimeFormatter(room.getExpiryMinute()-System.currentTimeMillis()/1000/60));
		else{
			s.setLine(2, SignManager.TimeFormatter(room.getTime()));
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
		return (mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN)) ? (Sign) b : null;
	}
	public String[] getSignLines(){
		Sign s = getSign();
		return s!=null ? s.getLines() : new String[4];
	}
	public World getWorldFromConfig(){
		String world = getConfig().getString("Sign.location.world"); //Could be name or UUID

		if(world==null || world.isEmpty()) return null; //String useless, can't proceed

		World w = null;
		//Checking if it's a world name
		w = Bukkit.getWorld(world);
		if(w!=null) return w;

		//Checking if it's a world UUID
		UUID id = UUID.fromString(world);
		if(id!=null)
			w = Bukkit.getWorld(id);

		return w;
	}
	public Location getLocation(){
		World world = getWorldFromConfig();

		if(world==null) return null;

		YamlConfiguration config = getConfig();

		int x = config.getInt("Room.location.x");
		int y = config.getInt("Room.location.y");
		int z = config.getInt("Room.location.z");

		return new Location(world, x, y, z);
	}
	public File getFile(){
		return HotelsConfigHandler.getSignFile(room.getHotel().getName(), room.getNum());
	}
	public Room getRoom(){
		return room;
	}
	public String getHotelNameFromSign(){
		String firstLine = getSignLines()[0];
		return firstLine!=null ? firstLine.split(" ")[0] : null;
	}
	public int getRoomNumFromSign(){
		String secondLine = getSignLines()[1];
		if(secondLine!=null)
			return Integer.parseInt(secondLine.split(" ")[1]);
		else return 0;
	}
	public void removeSign(){
		Block b = getBlock();
		Material mat = b.getType();
		if(mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN)){
			Hotel hotel = room.getHotel();
			if(getHotelNameFromSign().matches(hotel.getName())){
				if(hotel.getRegion().contains(b.getX(), b.getY(), b.getZ()))
					b.setType(Material.AIR);
			}
		}
	}
}