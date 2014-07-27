package managers;

import kernitus.plugin.Hotels.HotelsMain;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class GameLoop extends BukkitRunnable {

	FilenameFilter SignFileFilter;
	HotelsMain plugin;

	public GameLoop(HotelsMain plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		//int list = new File("plugins//Hotels//Signs").listFiles().length;
		File dir = new File("plugins//Hotels//Signs");
		if(!(dir.exists()))
			dir.mkdir();
		
		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins//Hotels//Signs");

		for(String x: fileslist){
			File file = new File("plugins//Hotels//Signs//"+x);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			String hotelName = config.getString("Sign.hotel");
			World world = Bukkit.getWorld(config.getString("Sign.location.world").trim());
			int roomNum = config.getInt("Sign.room");
			int locx = config.getInt("Sign.location.coords.x");
			int locy = config.getInt("Sign.location.coords.y");
			int locz = config.getInt("Sign.location.coords.z");
			Block signblock = world.getBlockAt(locx, locy, locz);

			if((signblock.getType().equals(Material.SIGN))||(signblock.getType().equals(Material.SIGN_POST))){
				Sign sign = (Sign) signblock.getState();
				if(hotelName.equalsIgnoreCase(sign.getLine(0).replaceAll("[§][\\w]", ""))){
					String[] Line2parts = sign.getLine(1).split("\\s");
					int roomNumfromSign = Integer.valueOf(Line2parts[1].trim()); //Room Number
					if(roomNum==roomNumfromSign){					

						long expirydate = config.getLong("Sign.expiryDate");
						if(expirydate<System.currentTimeMillis()/1000/60){
							String r = config.getString("Sign.region");
							ProtectedCuboidRegion region = (ProtectedCuboidRegion) WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion(r);
							if(!(config.getString("Sign.renter")==null)){
								Player p = Bukkit.getPlayer(UUID.fromString(config.getString("Sign.renter")));
								WorldGuardManager.removeOwner(p, region);
								config.set("Sign.renter", null);
								config.set("Sign.timeRentedAt", null);
								config.set("Sign.expiryDate", null);
								try {
									config.save(file);
								} catch (IOException e) {
									e.printStackTrace();
								}
								sign.setLine(3, "§aVacant");
								sign.update();
								if(p.isOnline())
									p.sendMessage("§9Your rent of room "+roomNum+" of the "+hotelName+" hotel has expired");
							}
						}
					}
					else
						file.delete();
				}
				else
					file.delete();
			}
			else
				file.delete();	
		}
	}
}
