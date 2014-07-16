package managers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class GameLoop extends BukkitRunnable {

	FilenameFilter SignFileFilter;

	@Override
	public void run() {
		//int list = new File("plugins//Hotels//Signs").listFiles().length;
		
		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins//Hotels//Signs");
		
		while(!fileslist.isEmpty());{
			File file = new File(fileslist.listIterator().next());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			int expirydate = config.getInt("Sign.expirydate");
			if(expirydate<System.currentTimeMillis()){
				Player p = (Player) config.get("Sign.renter");
				World world = (World) config.get("Sign.location.world");
				String r = config.getString("Sign.region");
				ProtectedCuboidRegion region = (ProtectedCuboidRegion) WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion(r);
				WorldGuardManager.removeMember(p, region);
				config.addDefault("Sign.renter", null);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileslist.listIterator().remove();
			}
		}
	}
}
