package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.Room;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

public class RentExpiryEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final Room room;
	private final OfflinePlayer renter;
	private final List<UUID> friendList;
	private boolean cancel;
	
	public RentExpiryEvent(Room room, OfflinePlayer renter, List<UUID> friendList){
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
	public List<UUID> getFriendList(){
		return friendList;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
