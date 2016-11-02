package kernitus.plugin.Hotels.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;

public class RoomTask extends BukkitRunnable {

	@Override
	public void run() {

		//Getting all room sign files
		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins" + File.separator + "Hotels" + File.separator + "Signs");

		//HashSet to store unique Hotel entries that had at least one of their rooms updated/changed
		HashSet<Hotel> hotelsThatHadRoomsUpdate = new HashSet<Hotel>();

		for(String fileName : fileslist){ //Looping through all the files
			File file = HotelsConfigHandler.getFile("Signs" + File.separator + fileName);

			if(!fileName.matches("\\w+-\\d+.yml")){ file.delete(); }//Delete all non-room signs in folder

			Mes.debugConsole("Room sign getting checked: " + file.getName() + " Path: " + file.getAbsolutePath());

			//Getting information directly out of the file
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			String hotelName = config.getString("Sign.hotel");
			World world = Bukkit.getWorld(config.getString("Sign.location.world").toLowerCase());
			int roomNum = config.getInt("Sign.room");

			Room room = new Room(world, hotelName, roomNum); //Creating room object with info from file

			if(room.checkRent())
				hotelsThatHadRoomsUpdate.add(room.getHotel());
		}
		
		//Update the reception signs for hotels that had their rooms changed
		for(Hotel hotel : hotelsThatHadRoomsUpdate)
			hotel.updateReceptionSigns();
	}
}