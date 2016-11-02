package kernitus.plugin.Hotels.Signs;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;

public class ReceptionSign extends AbstractSign{

	private Hotel hotel;
	private String num;

	public ReceptionSign(Hotel hotel, int num){
		this.hotel = hotel;
		this.num = String.valueOf(num);
	}
	public ReceptionSign(Hotel hotel, String num){
		this.hotel = hotel;
		this.num = num;
	}
	public void update(){
		Block b = getBlock();
		Material mat = b.getType();
		if(!mat.equals(Material.SIGN_POST) && !mat.equals(Material.WALL_SIGN)) return;
		Sign s = (Sign) b;

		s.setLine(0, (ChatColor.GREEN + Mes.mesnopre("sign.reception")));
		s.setLine(1, (ChatColor.DARK_BLUE + hotel.getName() + " Hotel"));
		s.setLine(2, (ChatColor.DARK_BLUE + String.valueOf(hotel.getTotalRoomCount()) + ChatColor.BLACK + " " + Mes.mesnopre("sign.room.total")));
		s.setLine(3, (ChatColor.GREEN + String.valueOf(hotel.getFreeRoomCount()) + ChatColor.BLACK + " " + Mes.mesnopre("sign.room.free")));

		s.update();
	}
	public Block getBlock(){
		return getLocation().getBlock();
	}
	public Sign getSign(){
		Block b = getBlock();
		Material mat = b.getType();
		if(mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN))
			return (Sign) b;
		else return null;
	}
	public Location getLocation(){
		YamlConfiguration config = getConfig();
		String worldUUID = config.getString("Reception.location.world");
		if(worldUUID==null) return null;

		World world = Bukkit.getWorld(worldUUID);
		int x = config.getInt("Reception.location.x");
		int y = config.getInt("Reception.location.y");
		int z = config.getInt("Reception.location.z");

		return new Location(world, x, y, z);
	}
	public File getFile(){
		return HotelsConfigHandler.getReceptionFile(hotel.getName(), num);
	}
	public String getNumber(){
		return num;
	}
	public Hotel getHotel(){
		return hotel;
	}
	public void removeSign(){
		Sign s = getSign();
		Block b = s.getBlock();

		if(s!=null){
			String Line1 = ChatColor.stripColor(s.getLine(0));
			if(Line1.matches("Reception") || Line1.matches(Mes.mesnopre("Sign.reception"))){
				if(hotel.getRegion().contains(b.getX(), b.getY(), b.getZ()))
					b.setType(Material.AIR);
			}
		}
	}
}
