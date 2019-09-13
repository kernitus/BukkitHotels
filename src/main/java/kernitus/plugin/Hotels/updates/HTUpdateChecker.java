package kernitus.plugin.Hotels.updates;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.Mes;
import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

public class HTUpdateChecker {

	private HotelsMain plugin;
	private final File pluginFile;
	private final SpigetUpdateChecker SUC = new SpigetUpdateChecker();

	public HTUpdateChecker(HotelsMain plugin, File pluginFile){
		this.plugin = plugin;
		this.pluginFile = pluginFile;
	}

	private String[] getUpdateMessages(){
		String[] updateMessages = new String[2];
		
		boolean useSpigot = false;
		String updates = plugin.getConfig().getString("updates");
		if(updates.equalsIgnoreCase("spigot")
				|| (!updates.equalsIgnoreCase("bukkit") && Bukkit.getVersion().toLowerCase().contains("spigot")
						)) useSpigot = true;
		
		if(useSpigot){
			Mes.debug("Using Spigot update checker");
			if(SUC.getNewUpdateAvailable()){
                updateMessages[0] = Mes.getStringNoPrefix("main.updateAvailable").replaceAll("%version%", SUC.getLatestVersion());
                updateMessages[1] = Mes.getStringNoPrefix("main.updateAvailableLink").replaceAll("%link%", SUC.getUpdateURL());
            }
		}
		else{//Get messages from bukkit update checker
			Mes.debug("Using Bukkit update checker");
			Updater updater = new Updater(plugin, 70177, pluginFile, Updater.UpdateType.NO_DOWNLOAD, false);
			if(updater.getResult().equals(UpdateResult.UPDATE_AVAILABLE)){
				//Updater knows local and remote versions are different, but not if it's an update
				String remoteVersion = updater.getLatestName().replaceAll("[A-Za-z\\s]", "");
				if(shouldUpdate(remoteVersion)){
					updateMessages[0] = Mes.getStringNoPrefix("main.updateAvailable").replaceAll("%version%", remoteVersion);
					updateMessages[1] = Mes.getStringNoPrefix("main.updateAvailableLink").replaceAll("%link%", updater.getLatestFileLink());
				}
			}
		}
		return updateMessages;
	}

	public void sendUpdateMessages(Player p){//Sends messages to a player
		for(String message : getUpdateMessages()){
			if(message != null && !message.isEmpty())//If there was no update/check is disabled message will be null
				p.sendMessage(message);
		}
	}
	public static boolean shouldUpdate(String remoteVersion){
		return shouldUpdate(HotelsMain.getPluginDescription().getVersion(), remoteVersion);
	}
	private static boolean shouldUpdate(String localVersion, String remoteVersion) {
		return versionCompare(localVersion, remoteVersion) < 0;
	}
	private static Integer versionCompare(String oldVer, String newVer){
		String[] vals1 = oldVer.split("\\.");
		String[] vals2 = newVer.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i]))
			i++;
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length){
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		else
			return Integer.signum(vals1.length - vals2.length);
	}
}