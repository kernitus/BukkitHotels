package kernitus.plugin.Hotels.events;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Room;

public class RentExpiryEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private final Room room;
	private final OfflinePlayer renter;
	private final List<String> friendList;
	
	public RentExpiryEvent(Room room, OfflinePlayer renter, List<String> friendList){
		this.room = room;
		this.renter = renter;
		this.friendList = friendList;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public Room getRoom(){
		return room;
	}
	public OfflinePlayer getRenter(){
		return renter;
	}
	public List<String> getFriendList(){
		return friendList;
	}
}
