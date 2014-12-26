package managers;

import kernitus.plugin.Hotels.HotelsListener;
import kernitus.plugin.Hotels.HotelsMain;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
			if(file.getName().matches("^Reception-.+-.+")){
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				World world = Bukkit.getWorld(config.getString("Reception.location.world").trim());
				int locx = config.getInt("Reception.location.x");
				int locy = config.getInt("Reception.location.y");
				int locz = config.getInt("Reception.location.z");
				Block b = world.getBlockAt(locx,locy,locz);
				Location l = b.getLocation();
				if(HotelsListener.updateReceptionSign(l)==true){
					file.delete();
					b.setType(Material.AIR);
					plugin.getLogger().info("Reception file "+file.getName()+" did not match in-game characteristics and has been deleted");
				}
			}
			else{
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				String hotelName = config.getString("Sign.hotel");
				World world = Bukkit.getWorld(config.getString("Sign.location.world").trim());
				int roomNum = config.getInt("Sign.room");
				int locx = config.getInt("Sign.location.coords.x");
				int locy = config.getInt("Sign.location.coords.y");
				int locz = config.getInt("Sign.location.coords.z");
				Block signblock = world.getBlockAt(locx, locy, locz);

				if((signblock.getType().equals(Material.WALL_SIGN))||(signblock.getType().equals(Material.SIGN_POST))||(signblock.getType().equals(Material.SIGN))){ 
					Sign sign = (Sign) signblock.getState();
					if(hotelName.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))){
						String[] Line2parts = ChatColor.stripColor(sign.getLine(1)).split("\\s");
						int roomNumfromSign = Integer.valueOf(Line2parts[1].trim()); //Room Number
						if(roomNum==roomNumfromSign){				

							long expirydate = config.getLong("Sign.expiryDate");
							if(expirydate<System.currentTimeMillis()/1000/60){
								String r = config.getString("Sign.region");
								ProtectedCuboidRegion region = (ProtectedCuboidRegion) WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion(r);
								if(config.getString("Sign.renter")!=null){
									Player p = Bukkit.getPlayer(UUID.fromString(config.getString("Sign.renter")));
									WorldGuardManager.removeMember(p, region);
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
									plugin.getLogger().info(p.getName()+"'s rent of room "+roomNum+" of the "+hotelName+" hotel has expired");
									if(p.isOnline())
										p.sendMessage("§9Your rent of room "+roomNum+" of the "+hotelName+" hotel has expired");
								}
							}
						}
						else{
							file.delete();
							plugin.getLogger().info("Sign file "+file.getName()+" did not match in-game roomNum and has been deleted");}
					}
					else{
						file.delete();
						plugin.getLogger().info("Sign file "+file.getName()+" did not match in-game hotelname and has been deleted");}
				}
				else{
					file.delete();
					plugin.getLogger().info("Sign file "+file.getName()+" did not match in-game location and has been deleted");}
			}
		}
	}
}
