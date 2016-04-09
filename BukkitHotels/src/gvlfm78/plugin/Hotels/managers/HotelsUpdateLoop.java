package kernitus.plugin.Hotels.managers;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.HotelsUpdateChecker;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

public class HotelsUpdateLoop extends BukkitRunnable{
	HotelsMain plugin;
	
	public HotelsUpdateLoop(HotelsMain instance) {
		this.plugin = instance;
	}
	HotelsMessageManager HMM = new HotelsMessageManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);
	
	@Override
	public void run() {
	
		HotelsUpdateChecker updateChecker = new HotelsUpdateChecker(plugin, "http://dev.bukkit.org/bukkit-plugins/hotels/files.rss");
		updateChecker.updateNeeded();
		if(HConH.getconfigyml().getBoolean("settings.checkForUpdates")){
			if(updateChecker.updateNeeded()){
				String updateAvailable = HMM.mesnopre("main.updateAvailable").replaceAll("%version%", updateChecker.getVersion());
				String updateLink = HMM.mesnopre("main.updateAvailableLink").replaceAll("%link%", updateChecker.getLink());
				plugin.getLogger().info(updateAvailable);
				plugin.getLogger().info(updateLink);
				
				YamlConfiguration queue = HConH.getMessageQueue();

				queue.set("messages.update.available", updateAvailable);
				queue.set("messages.update.link", updateLink);
				HConH.saveMessageQueue(queue);
				
				Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
				for(Player p:players){
					if(p.isOp()||p.hasPermission("hotels.*")){
						p.sendMessage(ChatColor.BLUE+updateAvailable);
						p.sendMessage(ChatColor.BLUE+updateLink);
					}
				}
			}
		}
		
	}

}
