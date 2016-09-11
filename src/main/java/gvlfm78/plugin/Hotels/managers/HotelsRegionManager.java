package kernitus.plugin.Hotels.managers;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.HotelsMain;

public class HotelsRegionManager {

	private HotelsMain plugin;
	public HotelsRegionManager(HotelsMain plugin){
		this.plugin = plugin;
	}
	public void makeRoomAccessible(ProtectedRegion region){
		if(plugin.getConfig().getBoolean("settings.allowPlayersIntoFreeRooms")){
			region.setFlag(DefaultFlag.INTERACT, null);
			region.setFlag(DefaultFlag.USE, null);
			makeRoomContainersAccessible(region);
		}
	}
	public void makeRoomContainersAccessible(ProtectedRegion region){
		if(plugin.getConfig().getBoolean("settings.allowPlayersToOpenContainersInFreeRooms")){
			region.setFlag(DefaultFlag.CHEST_ACCESS, null);
		}
	}

}
