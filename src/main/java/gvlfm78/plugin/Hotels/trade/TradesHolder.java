package kernitus.plugin.Hotels.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.Room;

public class TradesHolder {

	//Two HashMaps to hold all potential buyers of hotels and rooms
	private static Map<Player, HotelBuyer> hbuyers = new HashMap<Player, HotelBuyer>();
	private static Map<Player, RoomBuyer> rbuyers = new HashMap<Player, RoomBuyer>();

	public static boolean isHotelBuyer(UUID id){
		return hbuyers.containsKey(Bukkit.getPlayer(id));
	}
	public static boolean isHotelBuyer(Player p){
		return hbuyers.containsKey(p);
	}
	public static boolean isRoomBuyer(UUID id){
		return rbuyers.containsKey(Bukkit.getPlayer(id));
	}
	public static boolean isRoomBuyer(Player p){
		return rbuyers.containsKey(p);
	}

	public static HotelBuyer getHotelBuyer(Player p){
		return hbuyers.get(p);
	}
	public static RoomBuyer getRoomBuyer(Player p){
		return rbuyers.get(p);
	}

	public static void removeHotelBuyer(Player p){
		hbuyers.remove(p);
	}
	public static void removeRoomBuyer(Player p){
		rbuyers.remove(p);
	}

	public static void removeFromAll(Player p){
		removeHotelBuyer(p);
		removeRoomBuyer(p);
	}
	public static HotelBuyer getBuyerFromHotel(Hotel hotel){
		for(HotelBuyer hb : hbuyers.values()){
			if(hb.getName().equalsIgnoreCase(hotel.getName()))
				return hb;
		}
		return null;
	}
	public static RoomBuyer getBuyerFromRoom(Room room){
		for(RoomBuyer rb : rbuyers.values()){
			if(rb.getNum()==room.getNum())
				return rb;
		}
		return null;
	}
	public static void addHotelBuyer(Player p, Hotel hotel, double price){
		if(!hbuyers.containsKey(p))
			hbuyers.put(p, new HotelBuyer(hotel, p, price));
	}
	public static void addRoomBuyer(Player p, Room room, double price){
		if(!rbuyers.containsKey(p))
			rbuyers.put(p, new RoomBuyer(room, p, price));
	}
}