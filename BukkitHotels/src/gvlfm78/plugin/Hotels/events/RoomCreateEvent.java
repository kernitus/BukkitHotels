package kernitus.plugin.Hotels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Room;

public class RoomCreateEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	private Room room;

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public RoomCreateEvent(Room room){
		this.room = room;
	}
	public Room getRoom(){
		return room;
	}
	
}
