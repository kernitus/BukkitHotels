package kernitus.plugin.Hotels.signs;

import java.io.File;
import java.io.IOException;
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
import kernitus.plugin.Hotels.handlers.HTConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;

public class ReceptionSign extends AbstractSign {

	private Hotel hotel;
	private String num;
	private YamlConfiguration config;

	public ReceptionSign(Hotel hotel, int num){
		this.hotel = hotel;
		this.num = String.valueOf(num);
		config = YamlConfiguration.loadConfiguration(getFile());
	}
	public ReceptionSign(Hotel hotel, String num){
		this.hotel = hotel;
		this.num = num;
		config = YamlConfiguration.loadConfiguration(getFile());
	}
	public void update(){
		Sign s = getSign();

		if(s==null) return;

		s.setLine(0, (Mes.getStringNoPrefix("sign.reception.reception")));
		s.setLine(1, (Mes.getStringNoPrefix("sign.reception.hotel").replaceAll("%hotel%", getHotelName())));
		s.setLine(2, (ChatColor.DARK_BLUE + String.valueOf(hotel.getTotalRoomCount()) + ChatColor.BLACK + " " + Mes.getStringNoPrefix("sign.room.total")));
		s.setLine(3, (ChatColor.GREEN + String.valueOf(hotel.getFreeRoomCount()) + ChatColor.BLACK + " " + Mes.getStringNoPrefix("sign.room.free")));
		s.update();
	}
	public Block getBlock(){
		Location l = getLocation();
		return l!=null ? l.getBlock() : null;
	}
	public Sign getSign(){
		Block b = getBlock();
		if(b==null) return null;

		Material mat = b.getType();
		return (mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN)) ? (Sign) b.getState() : null;
	}
	public World getWorldFromConfig(){
		String world = config.getString("location.world"); //Could be name or UUID

		if(world==null || world.isEmpty()) return null; //String useless, can't proceed

		World w = null;
		//Checking if it's a world name
		w = Bukkit.getWorld(world);
		if(w!=null) return w;

		//Checking if it's a world UUID
		UUID id = UUID.fromString(world);
		if(id!=null)
			w = Bukkit.getWorld(id);

		if(w!=null) return w; //Successfully got world from UUID

		//Failed, return null
		return null;
	}
	public String getHotelName(){
		return config.getString("hotel");
	}
	public Location getLocation(){
		World world = getWorldFromConfig();

		if(world==null) return null;

		int x = config.getInt("location.x");
		int y = config.getInt("location.y");
		int z = config.getInt("location.z");

		return new Location(world, x, y, z);
	}
	public File getFile(){
		return HTConfigHandler.getReceptionFile(hotel.getName(), num);
	}
	public String getNumber(){
		return num;
	}
	public Hotel getHotel(){
		return hotel;
	}
	public void removeSign(){
		Sign s = getSign();
		if(s==null) return;
		Block b = s.getBlock();

		if(s!=null){
			String Line1 = ChatColor.stripColor(s.getLine(0));
			if(Line1.matches("Reception") || ChatColor.stripColor(Line1).matches(ChatColor.stripColor(Mes.getStringNoPrefix("Sign.reception")))){
				b.setType(Material.AIR);
			}
		}
	}
	public void saveConfig(){
		try {
			config.save(getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setHotelNameInConfig(String name){
		config.set("hotel", name);
		saveConfig();
	}
}
