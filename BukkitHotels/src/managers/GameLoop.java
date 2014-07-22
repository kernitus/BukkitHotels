package managers;

import kernitus.plugin.Hotels.HotelsMain;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
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
		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins//Hotels//Signs");

		for(String x: fileslist){
			File file = new File("plugins//Hotels//Signs//"+x);

			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

			int expirydate = config.getInt("Sign.expirydate");
			if(expirydate<System.currentTimeMillis()){
				World world = Bukkit.getWorld(config.getString("Sign.location.world").trim());
				String r = config.getString("Sign.region");
				ProtectedCuboidRegion region = (ProtectedCuboidRegion) WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion(r);
				if(!(config.getString("Sign.renter")==null)){
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
					String hotelName = config.getString("Sign.hotel");
					int roomNum = config.getInt("Sign.room");
					int locx = config.getInt("Sign.location.coords.x");
					int locy = config.getInt("Sign.location.coords.y");
					int locz = config.getInt("Sign.location.coords.z");
					Block signblock = world.getBlockAt(locx, locy, locz);
					Sign sign = (Sign) signblock.getState();
					sign.setLine(3, "§aVacant");
					sign.update();
					if(p.isOnline())
						p.sendMessage("§9Your rent of room "+roomNum+" of the "+hotelName+" hotel has expired");
				}
			}
		}
	}
}
