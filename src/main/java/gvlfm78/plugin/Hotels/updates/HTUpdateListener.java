package kernitus.plugin.Hotels.updates;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import kernitus.plugin.Hotels.HotelsMain;

public class HTUpdateListener implements Listener{

	private HotelsMain plugin;
	private final File pluginFile;

	public HTUpdateListener(HotelsMain plugin, File pluginFile){
		this.plugin = plugin;
		this.pluginFile = pluginFile;
	}


	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		if(p.hasPermission("hotels.*")){
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable () {
				public void run() {

					HTUpdateChecker updateChecker = new HTUpdateChecker(plugin, pluginFile);

					// Checking for updates
					updateChecker.sendUpdateMessages(p);
				}
			},20L);
		}
	}
}
