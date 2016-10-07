package kernitus.plugin.Hotels.tasks;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;

public class ReceptionTask extends BukkitRunnable{

	private HotelsMain plugin;
	public ReceptionTask(HotelsMain plugin){
		this.plugin = plugin;
	}
	private SignManager SM = new SignManager(plugin);

	//Task to update reception signs
	@Override
	public void run(){

		
		
		ArrayList<String> fileList = HotelsFileFinder.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs"+File.separator+"Reception"); // E:\Plugin\Server\plugins\Hotels\Signs
		if(fileList==null) return;
		for(String x : fileList){//Looping through all files in Signs directory
			File file = HotelsConfigHandler.getFile("Signs" + File.separator + x);
			if(file.getName().matches("Reception-.+-.+")){
				//It's a reception sign
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				World world = Bukkit.getWorld(config.getString("Reception.location.world"));
				int locx = config.getInt("Reception.location.x");
				int locy = config.getInt("Reception.location.y");
				int locz = config.getInt("Reception.location.z");
				Block b = world.getBlockAt(locx,locy,locz);
				Location l = b.getLocation();
				if(SM.updateReceptionSign(l) == true){
					file.delete();
					b.setType(Material.AIR);
					plugin.getLogger().info(Mes.mesnopre("sign.delete.reception").replaceAll("%filename%", file.getName()));
				}
			}
		}
	}
}
