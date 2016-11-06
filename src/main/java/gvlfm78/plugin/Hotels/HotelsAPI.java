package kernitus.plugin.Hotels;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsAPI {

	public static ArrayList<Hotel> getHotelsInWorld(World w){
		ArrayList<Hotel> hotels = new ArrayList<Hotel>();

		for(ProtectedRegion r : WorldGuardManager.getRegions(w)){
			String id = r.getId();
			if(id.matches("hotel-\\w+$")){
				String name = id.replaceFirst("hotel-", "");
				Hotel hotel = new Hotel(w, name);
				hotels.add(hotel);
			}
		}
		return hotels;
	}
	public static ArrayList<Hotel> getAllHotels(){
		ArrayList<Hotel> hotels = new ArrayList<Hotel>();
		List<World> worlds = Bukkit.getWorlds();

		for(World w : worlds)
			hotels.addAll(getHotelsInWorld(w));

		return hotels;
	}
	public static ArrayList<Hotel> getHotelsOwnedBy(UUID uuid){
		ArrayList<Hotel> hotels = getAllHotels();
		ArrayList<Hotel> owned = new ArrayList<Hotel>();

		for(Hotel hotel : hotels){
			if(hotel.isOwner(uuid))
				owned.add(hotel);
		}
		
		return owned;
	}
	public static ArrayList<Room> getRoomsRentedBy(UUID uuid){
		ArrayList<Room> rented = new ArrayList<Room>();

		for(Hotel hotel : getAllHotels()){
			for(Room room : hotel.getRooms()){
				if(room.isRenter(uuid))
					rented.add(room);
			}
		}
		return rented;
	}
	public static ArrayList<Room> getRoomsRentedByInHotel(UUID uuid, Hotel hotel){
		ArrayList<Room> rented = new ArrayList<Room>();

		for(Room room : hotel.getRooms()){
			if(room.isRenter(uuid))
				rented.add(room);
		}
		return rented;
	}
	public static int getHotelCount(){
		return getAllHotels().size();
	}
}