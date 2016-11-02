package kernitus.plugin.Hotels.updateChecker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import kernitus.plugin.Hotels.HotelsMain;

public class HotelsUpdateListener implements Listener{

	private HotelsMain plugin;
	
	public HotelsUpdateListener(HotelsMain plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		if(p.hasPermission("hotels.*")){
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable () {
				public void run() {

					HotelsUpdateChecker updateChecker = new HotelsUpdateChecker(plugin);

					// Checking for updates
					updateChecker.sendUpdateMessages(p);
				}
			},20L);
		}
	}
	
}
