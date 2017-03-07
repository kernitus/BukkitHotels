/**
 * 
 */
package kernitus.plugin.Hotels.tasks;

import java.io.IOException;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.DataException;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.managers.Mes;

/**
 * Queue room resets and stagger them to minimise lag
 *
 */
public class RoomResetQueue {

	private static HotelsMain PLUGIN = HotelsMain.getHotels();
	private static long lastAdding;
	
	public static void add(final Room room){
		
		long delay = PLUGIN.getConfig().getLong("roomResetDelay", 5L);
		long currentTime = System.currentTimeMillis();
		
		//This makes it so there is a constant minimum time difference between tasks
		long secondsUntilLastTaskIsExecuted = delay - (currentTime - lastAdding)/1000;
		if(secondsUntilLastTaskIsExecuted < 0) secondsUntilLastTaskIsExecuted = 0;
		
		Bukkit.getScheduler().runTaskLater(PLUGIN, new Runnable () {
			public void run() {
				try {
					Mes.debug("Resetting room " + room.getNum() + " of Hotel " + room.getHotel().getName());
					room.reset();
				} catch (IOException | WorldEditException | DataException e) {
					e.printStackTrace();
				}
			}
		}, (20 * delay + secondsUntilLastTaskIsExecuted) );
		lastAdding = currentTime;
	}
}
