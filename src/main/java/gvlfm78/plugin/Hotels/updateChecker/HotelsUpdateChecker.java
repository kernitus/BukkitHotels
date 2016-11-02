package kernitus.plugin.Hotels.updateChecker;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.Mes;

public class HotelsUpdateChecker {

	private HotelsMain plugin;
	private final BukkitUpdateChecker BUC;
	private final SpigotUpdateChecker SUC;

	public HotelsUpdateChecker(HotelsMain plugin){
		this.plugin = plugin;
		BUC = new BukkitUpdateChecker(plugin);
		SUC = new SpigotUpdateChecker(plugin);
	}

	private String[] getUpdateMessages(){
		String[] updateMessages = new String[2];
		if(Bukkit.getVersion().toLowerCase().contains("spigot")){
			//Get messages from Spigot update checker
			if(SUC.getResult().name().equalsIgnoreCase("UPDATE_AVAILABLE")){
				//An update is available
				updateMessages[0] = Mes.mesnopre("main.updateAvailable").replaceAll("%version%",SUC.getVersion());
				updateMessages[1] = Mes.mesnopre("main.updateAvailableLink").replaceAll("%link%","https://www.spigotmc.org/resources/hotels.2047/updates/");
			}
		}
		else//Get messages from bukkit update checker
			updateMessages = BUC.updateMessages();
		return updateMessages;
	}

	public void sendUpdateMessages(Player p){//Sends messages to a player
		for(String message : getUpdateMessages()){
			if(message!=null&&!message.isEmpty())//If there was no update/check is disabled message will be null
				p.sendMessage(message);
		}
	}
	public void sendUpdateMessages(Logger l){//Sends messages to console
		for(String message : getUpdateMessages()){
			message = ChatColor.stripColor(message);
			if(message!=null&&!message.isEmpty())//If there was no update/check is disabled message will be null
				l.info(message);
		}
	}
	public void sendUpdateMessages(CommandSender s){
		if(s instanceof Player){
			Player p = (Player) s;
			sendUpdateMessages(p);
		}
		else{
			sendUpdateMessages(plugin.getLogger());
		}
	}
}