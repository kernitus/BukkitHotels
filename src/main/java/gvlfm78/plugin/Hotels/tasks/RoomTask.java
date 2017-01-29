package kernitus.plugin.Hotels.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.DataException;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.exceptions.BlockNotSignException;
import kernitus.plugin.Hotels.exceptions.EventCancelledException;
import kernitus.plugin.Hotels.exceptions.RenterNonExistentException;
import kernitus.plugin.Hotels.exceptions.RoomNonExistentException;
import kernitus.plugin.Hotels.exceptions.ValuesNotMatchingException;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;

public class RoomTask extends BukkitRunnable {

	@Override
	public void run() {

		//Getting all files that end with .yml in Signs folder
		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins" + File.separator + "Hotels" + File.separator + "Signs");

		//HashSet to store unique Hotel entries that had at least one of their rooms updated/changed
		HashSet<Hotel> hotelsThatHadRoomsUpdate = new HashSet<Hotel>();

		for(String fileName : fileslist){ //Looping through all the files
			File file = HotelsConfigHandler.getFile("Signs" + File.separator + fileName);
			if(!fileName.matches("\\w+-\\d+.yml")){ file.delete(); continue; }//Delete all non-room signs in folder

			Mes.debugConsole("Checking room sign: " + file.getAbsolutePath());

			//Getting information directly out of the file
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			String hotelName = config.getString("Sign.hotel");
			String roomNumString = config.getString("Sign.room");
			World world = getWorldFromRoomSign(config);
			
			if(world==null || hotelName == null){ file.delete(); continue; }
			
			int roomNum;
			
			try{
				roomNum = Integer.parseInt(roomNumString);
			}
			catch(Exception e){
				file.delete(); continue; //TODO add debug messages so we know why it was deleted
			}
			
			Room room = new Room(world, hotelName, roomNum); //Creating room object with info from file

			try {
				room.checkRent();
				hotelsThatHadRoomsUpdate.add(room.getHotel());
			} catch (ValuesNotMatchingException | RoomNonExistentException | BlockNotSignException
					| RenterNonExistentException | EventCancelledException | IOException | DataException
					| WorldEditException e) {
				Mes.debugConsole(e.getMessage());
			}
		}

		//Update the reception signs for hotels that had their rooms changed
		for(Hotel hotel : hotelsThatHadRoomsUpdate)
			hotel.updateReceptionSigns();
	}

	private World getWorldFromRoomSign(YamlConfiguration config){
		String world = config.getString("Sign.location.world"); //Could be name or UUID

		if(world==null || world.isEmpty()) return null; //String useless, can't proceed

		World w = null;
		//Checking if it's a world name
		w = Bukkit.getWorld(world);
		if(w!=null) return w;

		//Checking if it's a world UUID
		UUID id = UUID.fromString(world);
		if(id!=null)
			w = Bukkit.getWorld(id);

		if(w!=null) return w; //Successfully got world from UUID
		
		//Failed, return null
		return null;
	}
}