package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.Room;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoomRentEvent extends Event implements Cancellable {
	
	private Room room;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	private Player renter;

	public RoomRentEvent(Room room, Player renter){
		this.room = room;
		this.renter = renter;
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
	public Player getRenter(){
		return renter;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	public void setRenter(Player renter){
		this.renter = renter;
	}
}
