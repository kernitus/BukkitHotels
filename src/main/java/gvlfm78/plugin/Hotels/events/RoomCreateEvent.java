package kernitus.plugin.Hotels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Room;

public class RoomCreateEvent extends Event{

	private Room room;
	
	public RoomCreateEvent(Room room){
		this.room = room;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Something
		return null;
	}
	
	public Room getRoom(){
		return room;
	}
}
