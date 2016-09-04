package kernitus.plugin.Hotels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Room;

public final class RoomRentEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Room room;

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public RoomRentEvent(Room room){
		this.room = room;
	}
	public Room getRoom(){
		return room;
	}
}
